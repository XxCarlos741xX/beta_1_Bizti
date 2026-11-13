import { useState } from 'react'
import { iniciarSesion, registrarse } from './api.js'
import { CREDENCIALES_DEMO } from './mock.js'

// Pantalla de inicio de sesion y registro
export default function Login({ baseUrl, setBaseUrl, demo, setDemo, onEntrar }) {
  const [modo, setModo] = useState('login') // 'login' | 'registro'
  const [form, setForm] = useState({ nombre: '', correo: '', contrasena: '' })
  const [error, setError] = useState('')
  const [info, setInfo] = useState('')
  const [cargando, setCargando] = useState(false)
  const [sinConexion, setSinConexion] = useState(false)
  const cambiar = (n, v) => setForm((f) => ({ ...f, [n]: v }))
  const esLogin = modo === 'login'

  const cambiarModo = () => { setModo(esLogin ? 'registro' : 'login'); setError(''); setInfo('') }

  const enviar = async (e) => {
    e.preventDefault()
    setError(''); setInfo(''); setSinConexion(false); setCargando(true)
    try {
      // Spring: POST /api/auth/registro y /api/auth/login devuelven el usuario (sin contrasena)
      const usuario = esLogin
        ? await iniciarSesion(baseUrl, form.correo, form.contrasena)
        : await registrarse(baseUrl, form)
      onEntrar(usuario) // el registro deja la sesion iniciada
    } catch (err) {
      setError(err.message)
      setSinConexion(Boolean(err.sinConexion))
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="entrada">
      <div className="entrada-marca">
        <span className="logo">bizti<i /></span>
        <div>
          <h1>Los retos de tus empresas, todos en un mismo lugar.</h1>
          <p>Registra empresas, abre retos y sigue cada avance sin perder el hilo.</p>
        </div>
        <div className="barras" aria-hidden>
          <span style={{ '--w': '86%' }} /><span style={{ '--w': '58%' }} /><span style={{ '--w': '72%' }} /><span style={{ '--w': '34%' }} />
        </div>
      </div>

      <main className="entrada-form">
        <form onSubmit={enviar}>
          <h2>{esLogin ? 'Inicia sesión' : 'Crea tu cuenta'}</h2>
          <p className="sub">{esLogin ? 'Usa el correo con el que te registraste.' : 'Solo te toma un minuto.'}</p>

          {!esLogin && (
            <label>Nombre
              <input value={form.nombre} maxLength={100} required autoFocus onChange={(e) => cambiar('nombre', e.target.value)} />
            </label>
          )}
          <label>Correo
            <input type="email" value={form.correo} maxLength={150} required autoFocus={esLogin} onChange={(e) => cambiar('correo', e.target.value)} />
          </label>
          <label>Contraseña{!esLogin && <small>Mínimo 6 caracteres</small>}
            <input
              type="password" value={form.contrasena} required minLength={esLogin ? undefined : 6}
              autoComplete={esLogin ? 'current-password' : 'new-password'}
              onChange={(e) => cambiar('contrasena', e.target.value)}
            />
          </label>

          {error && <p className="aviso error" role="alert">{error}</p>}
          {sinConexion && !demo && (
            <button type="button" className="texto" onClick={() => { setDemo(true); setError('') }}>
              Probar sin backend (modo demo)
            </button>
          )}
          {info && <p className="aviso exito" role="status">{info}</p>}

          <button className="primario grande" type="submit" disabled={cargando}>
            {cargando ? 'Un momento…' : esLogin ? 'Entrar' : 'Crear cuenta'}
          </button>
          <button type="button" className="texto" onClick={cambiarModo}>
            {esLogin ? '¿Aún no tienes cuenta? Regístrate' : 'Ya tengo cuenta'}
          </button>

          <label className="check demo-check">
            <input type="checkbox" checked={demo} onChange={(e) => { setDemo(e.target.checked); setError(''); setInfo('') }} />
            Modo demo (sin backend)
          </label>
          {demo ? (
            <p className="sub demo-ayuda">
              Los datos se guardan solo en este navegador. Cuenta de prueba:<br />
              <strong>{CREDENCIALES_DEMO.correo}</strong> / <strong>{CREDENCIALES_DEMO.contrasena}</strong>
            </p>
          ) : (
            <details className="servidor">
              <summary>Servidor</summary>
              <label>URL del backend
                <input value={baseUrl} onChange={(e) => setBaseUrl(e.target.value)} />
              </label>
            </details>
          )}
        </form>
      </main>
    </div>
  )
}
