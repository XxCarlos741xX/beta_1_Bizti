import { useCallback, useEffect, useState } from 'react'
import { Building2, Target, Flag, Users, Tags, ClipboardList, LogOut, Plus, CircleCheck, CircleAlert, RotateCcw } from 'lucide-react'
import { cargarTodo, guardarFila, eliminarFila, esModoDemo, fijarModoDemo, URL_POR_DEFECTO } from './api.js'
import { reiniciarDemo } from './mock.js'
import { RECURSOS, META, etiqueta } from './recursos.js'
import Tabla from './Tabla.jsx'
import Formulario from './Formulario.jsx'
import Login from './Login.jsx'

const ICONOS = { empresas: Building2, categorias: Tags, prioridades: Flag, retos: Target, usuarios: Users, registros: ClipboardList }

// La sesion se guarda en el navegador para que sobreviva a recargar la pagina
const leerSesion = () => {
  try { return JSON.parse(localStorage.getItem('bizti_sesion')) } catch { return null }
}
const leerUrl = () => {
  try { return localStorage.getItem('bizti_url') || URL_POR_DEFECTO } catch { return URL_POR_DEFECTO }
}

export default function App() {
  const [baseUrl, setBaseUrl] = useState(leerUrl)
  const [actual, setActual] = useState('empresas')
  const [todo, setTodo] = useState({}) // las seis tablas (un GET por modulo al backend Spring)
  const [form, setForm] = useState(null)       // null = cerrado, { fila } = abierto
  const [borrar, setBorrar] = useState(null)   // fila pendiente de confirmar
  const [avisos, setAvisos] = useState([])
  const [conexion, setConexion] = useState(null)
  const [usuario, setUsuario] = useState(leerSesion)
  const [demo, setDemoEstado] = useState(esModoDemo)
  const [log, setLog] = useState('Aún no se ha enviado ninguna petición.')

  const recurso = RECURSOS[actual]
  const datos = todo[actual] || []
  const { singular, desc } = META[actual]

  const avisar = useCallback((texto, tipo = 'exito') => {
    const id = Date.now() + Math.random()
    setAvisos((a) => [...a, { id, texto, tipo }])
    setTimeout(() => setAvisos((a) => a.filter((x) => x.id !== id)), 4500)
  }, [])

  useEffect(() => { try { localStorage.setItem('bizti_url', baseUrl) } catch { /* sin almacenamiento */ } }, [baseUrl])

  // Ejecuta una operacion de api.js, guarda su log y marca el estado de la conexion
  const pedir = useCallback(async (operacion, silencioso = false) => {
    try {
      const r = await operacion()
      if (!silencioso) setLog(r.log)
      setConexion(true)
      return r.datos
    } catch (e) {
      if (e.log) setLog(e.log)
      setConexion(!e.sinConexion)
      throw e
    }
  }, [])

  // Trae todas las tablas de una vez; despues de crear/editar/eliminar se recarga en silencio
  const cargar = useCallback(async (silencioso = false) => {
    try { setTodo((await pedir(() => cargarTodo(baseUrl), silencioso)) ?? {}) }
    catch (e) { setTodo({}); avisar(e.message, 'error') }
  }, [pedir, avisar, baseUrl, demo]) // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => { if (usuario) cargar() }, [cargar, usuario])  // eslint-disable-line react-hooks/exhaustive-deps

  const entrar = (u) => { localStorage.setItem('bizti_sesion', JSON.stringify(u)); setUsuario(u) }
  const salir = () => { localStorage.removeItem('bizti_sesion'); setUsuario(null); setForm(null); setConexion(null) }
  // Cambiar entre backend real y demo: las sesiones de uno no valen en el otro
  const cambiarDemo = (activo) => {
    fijarModoDemo(activo)
    setDemoEstado(activo)
    if (usuario) salir()
  }
  const restablecerDemo = () => { reiniciarDemo(); avisar('Datos de demo restablecidos'); cargar(true) }
  const cambiarTab = (k) => { setActual(k); setForm(null) }
  const cerrarForm = useCallback(() => setForm(null), [])

  const guardar = async (cuerpo) => {
    try {
      const id = form.fila?.id
      await pedir(() => guardarFila(baseUrl, actual, id, cuerpo))
      avisar(id ? `Cambios guardados` : `${singular.charAt(0).toUpperCase() + singular.slice(1)} creado`)
      setForm(null)
      cargar(true)
    } catch (e) { avisar(e.message, 'error') }
  }

  const confirmarBorrado = async () => {
    const fila = borrar
    setBorrar(null)
    try { await pedir(() => eliminarFila(baseUrl, actual, fila.id)); avisar('Eliminado') } catch (e) { avisar(e.message, 'error') }
    cargar(true)
  }

  if (!usuario) return <Login baseUrl={baseUrl} setBaseUrl={setBaseUrl} demo={demo} setDemo={cambiarDemo} onEntrar={entrar} />

  return (
    <div className="app">
      <aside className="lateral">
        <span className="logo">bizti<i /></span>
        <nav aria-label="Módulos">
          {Object.entries(RECURSOS).map(([k, r]) => {
            const Icono = ICONOS[k]
            return (
              <button key={k} className={k === actual ? 'activa' : ''} aria-current={k === actual ? 'page' : undefined} onClick={() => cambiarTab(k)}>
                <Icono size={18} aria-hidden /> {r.titulo}
              </button>
            )
          })}
        </nav>
        <div className="lateral-pie">
          <div className="yo">
            <span className="avatar" aria-hidden>{usuario.nombre?.charAt(0).toUpperCase()}</span>
            <span><strong>{usuario.nombre}</strong><small>{usuario.rol}</small></span>
            <button className="icono claro" aria-label="Cerrar sesión" title="Cerrar sesión" onClick={salir}><LogOut size={17} /></button>
          </div>
          {demo ? (
            <div className="srv">
              <span className="punto demo" />
              <span className="srv-texto">Modo demo</span>
              <button className="icono claro" aria-label="Restablecer datos de demo" title="Restablecer datos de demo" onClick={restablecerDemo}><RotateCcw size={15} /></button>
            </div>
          ) : (
            <label className="srv">
              <span className={`punto ${conexion === true ? 'ok' : conexion === false ? 'mal' : ''}`} />
              <input aria-label="URL del backend" value={baseUrl} onChange={(e) => setBaseUrl(e.target.value)} onBlur={() => cargar()} />
            </label>
          )}
          <label className="srv srv-demo">
            <input type="checkbox" checked={demo} onChange={(e) => cambiarDemo(e.target.checked)} />
            <span>Sin backend (demo)</span>
          </label>
        </div>
      </aside>

      <main className="contenido">
        {demo && <p className="banda-demo">Modo demo: los datos viven solo en este navegador y no se envían a ningún servidor.</p>}
        <header className="encabezado">
          <div>
            <h1>{recurso.titulo}</h1>
            <p>{desc}</p>
          </div>
          <button className="primario" onClick={() => setForm({ fila: null })}><Plus size={17} aria-hidden /> Nuevo {singular}</button>
        </header>

        <Tabla
          key={actual} recurso={recurso} singular={singular} datos={datos} todo={todo}
          onNuevo={() => setForm({ fila: null })} onEditar={(fila) => setForm({ fila })} onEliminar={setBorrar}
        />

        <details className="consola">
          <summary>Última petición a la API</summary>
          <pre>{log}</pre>
        </details>
      </main>

      {form && (
        <Formulario
          key={form.fila?.id ?? 'nuevo'} recurso={recurso} singular={singular} todo={todo}
          fila={form.fila} onGuardar={guardar} onCancelar={cerrarForm}
        />
      )}

      {borrar && (
        <div className="velo centro" onMouseDown={(e) => e.target === e.currentTarget && setBorrar(null)}>
          <div className="dialogo" role="alertdialog" aria-label="Confirmar eliminación">
            <h2>¿Eliminar {singular}?</h2>
            <p>Vas a eliminar {etiqueta(borrar) && borrar.nombre ? <strong>{etiqueta(borrar)}</strong> : `este ${singular}`}. Esta acción no se puede deshacer.</p>
            <div className="botones">
              <button autoFocus onClick={() => setBorrar(null)}>Cancelar</button>
              <button className="peligro-solido" onClick={confirmarBorrado}>Eliminar {singular}</button>
            </div>
          </div>
        </div>
      )}

      <div className="avisos" aria-live="polite">
        {avisos.map((a) => (
          <div key={a.id} className={`toast ${a.tipo}`}>
            {a.tipo === 'error' ? <CircleAlert size={18} /> : <CircleCheck size={18} />} {a.texto}
          </div>
        ))}
      </div>
    </div>
  )
}
