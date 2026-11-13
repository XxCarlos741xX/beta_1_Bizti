import { useMemo, useState } from 'react'
import { Search, Pencil, Trash2, SearchX, Plus } from 'lucide-react'
import { rotulo, textoRef } from './recursos.js'

function valorCelda(campo, fila, todo) {
  const v = fila[campo.n]
  if (campo.t === 'ref') return textoRef(campo, fila, todo)
  if (campo.t === 'check') return v ? 'Sí' : 'No'
  if (campo.t === 'datetime-local') return String(v ?? '').slice(0, 16).replace('T', ' ')
  return v ?? ''
}

// Color de la etiqueta segun el significado del valor
function tono(campo, texto) {
  const t = String(texto).toLowerCase()
  if (campo.t === 'check') return t === 'sí' ? 'verde' : 'gris'
  if (campo.n === 'rol') return t.startsWith('admin') ? 'azul' : 'gris'
  if (/abiert|activ|nuev|aprob/.test(t)) return 'verde'
  if (/curso|pend|revis/.test(t)) return 'ambar'
  if (/rechaz/.test(t)) return 'rojo'
  if (/cerr|inactiv|cancel/.test(t)) return 'gris'
  return 'azul'
}

const filtrable = (c) => c.t === 'ref' || c.t === 'check' || c.n === 'estado' || c.n === 'rol'
const conEtiqueta = (c) => c.t === 'check' || c.n === 'estado' || c.n === 'rol'

export default function Tabla({ recurso, singular, datos, todo, onNuevo, onEditar, onEliminar }) {
  const columnas = recurso.campos.filter((c) => !c.oculto)
  const camposFiltro = columnas.filter(filtrable)
  const campoFecha = columnas.find((c) => c.t === 'date' || c.t === 'datetime-local')

  const [busqueda, setBusqueda] = useState('')
  const [filtros, setFiltros] = useState({})
  const [desde, setDesde] = useState('')
  const [hasta, setHasta] = useState('')

  const opciones = useMemo(() => {
    const o = {}
    for (const c of camposFiltro) o[c.n] = [...new Set(datos.map((f) => String(valorCelda(c, f, todo))).filter(Boolean))].sort()
    return o
  }, [datos, camposFiltro, todo])

  const visibles = useMemo(() => {
    const q = busqueda.trim().toLowerCase()
    return datos.filter((f) => {
      if (q && !columnas.some((c) => String(valorCelda(c, f, todo)).toLowerCase().includes(q))) return false
      for (const c of camposFiltro) if (filtros[c.n] && String(valorCelda(c, f, todo)) !== filtros[c.n]) return false
      if (campoFecha) {
        const d = String(f[campoFecha.n] ?? '').slice(0, 10)
        if (desde && (!d || d < desde)) return false
        if (hasta && (!d || d > hasta)) return false
      }
      return true
    })
  }, [datos, todo, busqueda, filtros, desde, hasta, columnas, camposFiltro, campoFecha])

  const hayFiltros = busqueda || desde || hasta || Object.values(filtros).some(Boolean)
  const limpiar = () => { setBusqueda(''); setFiltros({}); setDesde(''); setHasta('') }

  return (
    <section className="tarjeta">
      <div className="filtros">
        <label className="buscador">
          <Search size={16} aria-hidden />
          <input type="search" placeholder={`Buscar ${singular}…`} aria-label="Buscar" value={busqueda} onChange={(e) => setBusqueda(e.target.value)} />
        </label>
        {camposFiltro.map((c) => (
          <label key={c.n} className="filtro">{rotulo(c.n)}
            <select value={filtros[c.n] || ''} onChange={(e) => setFiltros({ ...filtros, [c.n]: e.target.value })}>
              <option value="">Todos</option>
              {opciones[c.n].map((o) => <option key={o} value={o}>{o}</option>)}
            </select>
          </label>
        ))}
        {campoFecha && (
          <>
            <label className="filtro">{rotulo(campoFecha.n)}: desde<input type="date" value={desde} onChange={(e) => setDesde(e.target.value)} /></label>
            <label className="filtro">hasta<input type="date" value={hasta} onChange={(e) => setHasta(e.target.value)} /></label>
          </>
        )}
        {hayFiltros && <button className="texto" onClick={limpiar}>Limpiar filtros</button>}
      </div>

      {!datos.length ? (
        <div className="vacio">
          <Plus size={28} aria-hidden />
          <h3>Aún no hay {recurso.titulo.toLowerCase()}</h3>
          <p>Crea la primera para empezar a trabajar.</p>
          <button className="primario" onClick={onNuevo}>Crear {singular}</button>
        </div>
      ) : !visibles.length ? (
        <div className="vacio">
          <SearchX size={28} aria-hidden />
          <h3>Sin resultados</h3>
          <p>Ningún {singular} coincide con la búsqueda o los filtros.</p>
          <button onClick={limpiar}>Limpiar filtros</button>
        </div>
      ) : (
        <>
          <div className="tabla-wrap">
            <table>
              <thead>
                <tr>{columnas.map((c) => <th key={c.n}>{rotulo(c.n)}</th>)}<th className="acc-col"><span className="sr">Acciones</span></th></tr>
              </thead>
              <tbody>
                {visibles.map((f) => (
                  <tr key={f.id} title={f.id}>
                    {columnas.map((c, i) => {
                      const v = valorCelda(c, f, todo)
                      return (
                        <td key={c.n} className={i === 0 ? 'principal' : ''}>
                          {v === '' ? <span className="nada">—</span> : conEtiqueta(c) ? <span className={`badge ${tono(c, v)}`}>{v}</span> : v}
                        </td>
                      )
                    })}
                    <td className="acc">
                      <button className="icono" aria-label={`Editar ${singular}`} title="Editar" onClick={() => onEditar(f)}><Pencil size={16} /></button>
                      <button className="icono peligro" aria-label={`Eliminar ${singular}`} title="Eliminar" onClick={() => onEliminar(f)}><Trash2 size={16} /></button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <p className="pie">Mostrando {visibles.length} de {datos.length}</p>
        </>
      )}
    </section>
  )
}
