import { useEffect, useState } from 'react'
import { X } from 'lucide-react'
import { etiqueta, rotulo } from './recursos.js'

// Convierte el registro (o uno vacio) en el estado del formulario
function inicial(campos, fila) {
  const v = {}
  for (const c of campos) {
    const x = fila?.[c.n]
    if (c.t === 'check') v[c.n] = x ?? true          // nueva empresa: activa por defecto
    else if (c.t === 'password') v[c.n] = ''          // la contrasena nunca viene del backend
    else v[c.n] = c.t === 'select' ? (x ?? c.opciones[0]) : (x ?? '')
  }
  return v
}

// Convierte el estado del formulario en el JSON que espera la API
function aCuerpo(campos, v) {
  const cuerpo = {}
  for (const c of campos) {
    if (c.t === 'number') cuerpo[c.n] = Number(v[c.n])
    else if (c.t === 'check') cuerpo[c.n] = Boolean(v[c.n])
    else if (c.t === 'password' && !v[c.n]) continue // vacia al editar = se conserva la actual
    else cuerpo[c.n] = v[c.n]
  }
  return cuerpo
}

export default function Formulario({ recurso, singular, todo, fila, onGuardar, onCancelar }) {
  const { campos } = recurso
  const [valores, setValores] = useState(() => inicial(campos, fila))
  const cambiar = (n, valor) => setValores((v) => ({ ...v, [n]: valor }))

  useEffect(() => {
    const esc = (e) => e.key === 'Escape' && onCancelar()
    window.addEventListener('keydown', esc)
    return () => window.removeEventListener('keydown', esc)
  }, [onCancelar])

  return (
    <div className="velo" onMouseDown={(e) => e.target === e.currentTarget && onCancelar()}>
      <aside className="cajon" role="dialog" aria-modal="true" aria-label={`${fila ? 'Editar' : 'Nuevo'} ${singular}`}>
        <header>
          <h2>{fila ? 'Editar' : 'Nuevo'} {singular}</h2>
          <button className="icono" aria-label="Cerrar" onClick={onCancelar}><X size={18} /></button>
        </header>
        <form onSubmit={(e) => { e.preventDefault(); onGuardar(aCuerpo(campos, valores)) }}>
          <div className="campos">
            {campos.map((c, i) => {
              const nombre = rotulo(c.n)
              if (c.t === 'ref') {
                const lista = todo[c.ref] || []
                return (
                  <label key={c.n}>{nombre}
                    <select required value={valores[c.n]} onChange={(e) => cambiar(c.n, e.target.value)}>
                      <option value="">{lista.length ? 'Selecciona…' : `Primero crea ${c.ref}`}</option>
                      {lista.map((o) => <option key={o.id} value={o.id}>{etiqueta(o)}</option>)}
                    </select>
                  </label>
                )
              }
              if (c.t === 'select') {
                return (
                  <label key={c.n}>{nombre}
                    <select value={valores[c.n]} onChange={(e) => cambiar(c.n, e.target.value)}>
                      {c.opciones.map((o) => <option key={o} value={o}>{o}</option>)}
                    </select>
                  </label>
                )
              }
              if (c.t === 'check') {
                return (
                  <label key={c.n} className="check">
                    <input type="checkbox" checked={Boolean(valores[c.n])} onChange={(e) => cambiar(c.n, e.target.checked)} />
                    {nombre}
                  </label>
                )
              }
              const obligatorio = c.soloCrear ? !fila : !c.opcional
              return (
                <label key={c.n}>{nombre}
                  {c.soloCrear && fila && <small>Déjala vacía para no cambiarla</small>}
                  <input
                    type={c.t} autoFocus={i === 0} value={valores[c.n]} maxLength={c.max}
                    min={c.t === 'number' ? 1 : undefined} step={c.t === 'number' ? 1 : undefined}
                    minLength={c.min} autoComplete={c.t === 'password' ? 'new-password' : undefined}
                    required={obligatorio} onChange={(e) => cambiar(c.n, e.target.value)}
                  />
                </label>
              )
            })}
          </div>
          <footer>
            <button type="button" onClick={onCancelar}>Cancelar</button>
            <button className="primario" type="submit">{fila ? 'Guardar cambios' : `Crear ${singular}`}</button>
          </footer>
        </form>
      </aside>
    </div>
  )
}
