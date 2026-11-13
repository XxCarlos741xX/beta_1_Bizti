// Backend simulado para el MODO DEMO (sin servidor). Usa los mismos campos que el front
// envia al backend Spring Boot (antes de la traduccion de api.js). Guarda en localStorage.
const CLAVE = 'bizti_demo_db_v2' // v2: campos del backend Spring
export const CREDENCIALES_DEMO = { correo: 'admin@bizti.co', contrasena: 'admin1234' }

const ENTIDADES = {
  empresas: ['nombre', 'nit', 'sector', 'contacto', 'correo', 'telefono', 'activa'],
  categorias: ['nombre', 'descripcion'],
  prioridades: ['nombre', 'nivel'],
  usuarios: ['nombre', 'correo', 'rol', 'contrasena'],
  retos: ['nombre', 'descripcion', 'empresaId', 'categoriaId', 'prioridadId', 'fechaInicio', 'fechaFin', 'estado'],
  registros: ['usuarioId', 'retoId', 'fechaRegistro', 'observacion', 'estado'],
}
const REQUERIDOS = {
  empresas: ['nombre', 'nit', 'sector', 'contacto', 'correo', 'telefono'], categorias: ['nombre', 'descripcion'],
  prioridades: ['nombre', 'nivel'], usuarios: ['nombre', 'correo', 'rol'],
  retos: ['nombre', 'empresaId', 'categoriaId', 'prioridadId', 'fechaInicio', 'fechaFin', 'estado'],
  registros: ['usuarioId', 'retoId', 'fechaRegistro', 'estado'],
}
const OPCIONES = {
  retos: { estado: ['Abierto', 'En revisión', 'Cerrado'] },
  usuarios: { rol: ['usuario', 'evaluador', 'administrador'] },
  registros: { estado: ['Pendiente', 'Aprobado', 'Rechazado'] },
}
const POR_DEFECTO = { empresas: { activa: true }, retos: { descripcion: '' }, registros: { observacion: '' } }
// campo -> tabla a la que apunta (clave foranea)
const FK = { retos: { empresaId: 'empresas', categoriaId: 'categorias', prioridadId: 'prioridades' }, registros: { usuarioId: 'usuarios', retoId: 'retos' } }
// quien usa a cada tabla: no se puede borrar mientras haya filas que la referencian
const USADA_POR = {
  empresas: [['retos', 'empresaId']], categorias: [['retos', 'categoriaId']], prioridades: [['retos', 'prioridadId']],
  usuarios: [['registros', 'usuarioId']], retos: [['registros', 'retoId']],
}
const CORREO = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const id = () => crypto.randomUUID()
const ahora = () => new Date().toISOString().slice(0, 19).replace('T', ' ')

function semilla() {
  const t = (n) => `2026-10-${String(30 - n).padStart(2, '0')} 09:00:00`
  const base = (extra, n) => ({ ...extra, createdAt: t(n), updatedAt: t(n) })
  const e = ['e1', 'e2', 'e3'], c = ['c1', 'c2', 'c3'], p = ['p1', 'p2', 'p3'], u = ['u1', 'u2', 'u3'], r = ['r1', 'r2', 'r3']
  return {
    credenciales: { u1: CREDENCIALES_DEMO.contrasena },
    tablas: {
      empresas: [
        base({ id: e[0], nombre: 'Andina Tech', nit: '901456781-2', sector: 'Tecnología', contacto: 'Mariana Gómez', correo: 'alianzas@andinatech.demo', telefono: '3004567812', activa: true }, 1),
        base({ id: e[1], nombre: 'EcoFuturo SAS', nit: '901234567-1', sector: 'Sostenibilidad', contacto: 'Carlos Rojas', correo: 'contacto@ecofuturo.demo', telefono: '3012345678', activa: true }, 2),
        base({ id: e[2], nombre: 'NovaLogística', nit: '901112233-4', sector: 'Logística', contacto: 'Felipe Castro', correo: 'retos@novalogistica.demo', telefono: '3151122334', activa: false }, 3),
      ],
      categorias: [
        base({ id: c[0], nombre: 'Tecnología', descripcion: 'Soluciones digitales y transformación tecnológica.' }, 1),
        base({ id: c[1], nombre: 'Sostenibilidad', descripcion: 'Proyectos con impacto ambiental y social.' }, 2),
        base({ id: c[2], nombre: 'Operaciones', descripcion: 'Optimización de procesos y productividad.' }, 3),
      ],
      prioridades: [
        base({ id: p[0], nombre: 'Alta', nivel: 1 }, 1), base({ id: p[1], nombre: 'Media', nivel: 2 }, 2), base({ id: p[2], nombre: 'Baja', nivel: 3 }, 3),
      ],
      usuarios: [
        base({ id: u[0], nombre: 'Administrador Demo', correo: CREDENCIALES_DEMO.correo, rol: 'administrador' }, 1),
        base({ id: u[1], nombre: 'Valentina Pérez', correo: 'valentina.perez@ejemplo.demo', rol: 'usuario' }, 2),
        base({ id: u[2], nombre: 'Camila López', correo: 'camila.lopez@ejemplo.demo', rol: 'evaluador' }, 3),
      ],
      retos: [
        base({ id: r[0], nombre: 'Reducir la huella de carbono', descripcion: 'Propuesta digital para disminuir las emisiones de los centros de distribución.', empresaId: e[1], categoriaId: c[1], prioridadId: p[0], fechaInicio: '2026-10-01', fechaFin: '2026-11-30', estado: 'Abierto' }, 1),
        base({ id: r[1], nombre: 'Analítica para decisiones comerciales', descripcion: 'Tablero con indicadores clave para el equipo comercial.', empresaId: e[0], categoriaId: c[0], prioridadId: p[0], fechaInicio: '2026-09-10', fechaFin: '2026-10-31', estado: 'En revisión' }, 2),
        base({ id: r[2], nombre: 'Automatización de inventarios', descripcion: 'Flujo de inventario ágil con herramientas de bajo costo.', empresaId: e[2], categoriaId: c[2], prioridadId: p[1], fechaInicio: '2026-08-15', fechaFin: '2026-09-30', estado: 'Cerrado' }, 3),
      ],
      registros: [
        base({ id: 'g1', usuarioId: u[1], retoId: r[0], fechaRegistro: '2026-10-02T09:30', observacion: 'Interesada en el componente de datos.', estado: 'Aprobado' }, 1),
        base({ id: 'g2', usuarioId: u[2], retoId: r[1], fechaRegistro: '2026-10-05T14:00', observacion: '', estado: 'Pendiente' }, 2),
        base({ id: 'g3', usuarioId: u[0], retoId: r[2], fechaRegistro: '2026-09-01T08:15', observacion: 'Entrega final revisada.', estado: 'Rechazado' }, 3),
      ],
    },
  }
}

let memoria = null // respaldo si el navegador no deja usar localStorage
function leer() {
  try { const d = JSON.parse(localStorage.getItem(CLAVE)); if (d?.tablas?.registros && d.credenciales) return d } catch { /* se crea de nuevo */ }
  if (memoria) return memoria
  const d = semilla(); guardar(d); return d
}
function guardar(d) {
  memoria = d
  try { localStorage.setItem(CLAVE, JSON.stringify(d)) } catch { /* solo en memoria */ }
}
export function reiniciarDemo() { guardar(semilla()) }

const ok = (body, status = 200) => ({ status, body })
const err = (status, message) => ({ status, body: { error: { message } } })
const invalido = (m) => { throw { status: 400, message: m } } // eslint-disable-line no-throw-literal

// Misma validacion que el backend del profe
function validar(entidad, body) {
  const campos = ENTIDADES[entidad]
  if (!body || typeof body !== 'object' || Array.isArray(body)) invalido('El cuerpo de la solicitud debe ser un objeto JSON.')
  const extra = Object.keys(body).filter((c) => !campos.includes(c))
  if (extra.length) invalido(`Campos no permitidos: ${extra.join(', ')}.`)
  const v = {}
  for (const c of campos) {
    if (body[c] === undefined) {
      if (REQUERIDOS[entidad].includes(c)) invalido(`El campo "${c}" es obligatorio.`)
      if (POR_DEFECTO[entidad]?.[c] !== undefined) v[c] = POR_DEFECTO[entidad][c]
      continue
    }
    const x = body[c]
    if (typeof x === 'string') {
      v[c] = x.trim()
      if (REQUERIDOS[entidad].includes(c) && !v[c]) invalido(`El campo "${c}" no puede estar vacío.`)
    } else if (typeof x === 'boolean' && c === 'activa') {
      v[c] = x
    } else if (entidad === 'prioridades' && c === 'nivel') {
      const n = Number(x)
      if (!Number.isInteger(n) || n < 1) invalido('El nivel debe ser un número entero mayor que cero.')
      v[c] = n
    } else invalido(`El campo "${c}" debe ser texto.`)
    const opc = OPCIONES[entidad]?.[c]
    if (opc && !opc.includes(v[c])) invalido(`El valor de "${c}" no es válido.`)
  }
  if (v.correo && !CORREO.test(v.correo)) invalido('Ingresa un correo electrónico válido.')
  if (v.fechaRegistro && !/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}/.test(v.fechaRegistro)) invalido('La fecha de registro debe tener fecha y hora.')
  for (const c of ['fechaInicio', 'fechaFin']) {
    const f = v[c]
    const d = f && new Date(`${f}T00:00:00.000Z`)
    if (f && (!/^\d{4}-\d{2}-\d{2}$/.test(f) || Number(f.slice(0, 4)) < 1000 || Number.isNaN(d.getTime()) || d.toISOString().slice(0, 10) !== f)) {
      invalido(`El campo "${c}" debe tener formato YYYY-MM-DD.`)
    }
  }
  if (entidad === 'retos' && v.fechaInicio && v.fechaFin && v.fechaFin < v.fechaInicio) invalido('La fecha de cierre no puede ser anterior a la fecha de inicio.')
  return v
}

// Restricciones que en el backend real aplica MySQL (llaves foraneas y unicas)
function restricciones(db, entidad, fila) {
  for (const [campo, destino] of Object.entries(FK[entidad] || {})) {
    if (!db.tablas[destino].some((x) => x.id === fila[campo])) return err(400, 'Una de las entidades relacionadas no existe.')
  }
  const repetido = (campos) => db.tablas[entidad].some((x) => x.id !== fila.id && campos.every((c) => x[c] === fila[c]))
  const unicos = { empresas: [['nit'], ['correo'], ['telefono']], usuarios: [['correo']], registros: [['usuarioId', 'retoId']] }[entidad] || []
  if (unicos.some(repetido)) return err(409, 'Ya existe un registro con ese valor único.')
  return null
}

function auth(db, ruta, body) {
  if (ruta === 'registro' || ruta === 'register') {
    const extra = Object.keys(body || {}).filter((c) => !['nombre', 'correo', 'contrasena'].includes(c))
    if (extra.length) return err(400, `Campos no permitidos: ${extra.join(', ')}.`)
    const nombre = typeof body?.nombre === 'string' ? body.nombre.trim() : ''
    const correo = typeof body?.correo === 'string' ? body.correo.trim().toLowerCase() : ''
    if (!nombre) return err(400, 'El campo "nombre" es obligatorio.')
    if (!CORREO.test(correo)) return err(400, 'Ingresa un correo electrónico válido.')
    if (typeof body.contrasena !== 'string' || body.contrasena.length < 6) {
      return err(400, 'La contrasena debe tener al menos 6 caracteres')
    }
    if (db.tablas.usuarios.some((u) => u.correo === correo)) return err(409, 'Ya existe un registro con ese valor único.')
    const u = { id: id(), nombre, correo, rol: 'usuario', createdAt: ahora(), updatedAt: ahora() }
    db.tablas.usuarios.unshift(u); db.credenciales[u.id] = body.contrasena; guardar(db)
    const { createdAt, updatedAt, ...user } = u
    return ok({ user, token: 'token-demo', tokenType: 'Bearer', expiresIn: 3600 }, 201)
  }
  if (ruta === 'login') {
    const { correo, contrasena } = body ?? {}
    if (typeof correo !== 'string' || typeof contrasena !== 'string') return err(400, 'Debes enviar "correo" y "contrasena".')
    const u = db.tablas.usuarios.find((x) => x.correo === correo.trim().toLowerCase())
    if (!u || db.credenciales[u.id] !== contrasena) return err(401, 'Correo o contraseña incorrectos.')
    const { createdAt, updatedAt, ...user } = u
    return ok({ user, token: 'token-demo', tokenType: 'Bearer', expiresIn: 3600 })
  }
  return err(404, 'Ruta no encontrada.')
}

// Punto de entrada: simula una peticion a /api/<ruta>
export function servirDemo(metodo, ruta, cuerpo) {
  const db = leer()
  const [a, b] = ruta.split('/')
  try {
    if (a === 'auth' && metodo === 'POST') return auth(db, b, cuerpo)
    if (a === 'data' && metodo === 'GET') return ok(db.tablas)
    const filas = db.tablas[a]
    if (!filas) return err(404, 'Entidad no encontrada.')
    const actual = b ? filas.find((x) => x.id === b) : null

    if (metodo === 'GET') return b ? (actual ? ok(actual) : err(404, 'Registro no encontrado.')) : ok(filas)

    if (metodo === 'POST' && !b) {
      const { contrasena, ...valores } = validar(a, cuerpo)
      if (a === 'usuarios' && (!contrasena || contrasena.length < 6)) return err(400, 'La contrasena debe tener al menos 6 caracteres')
      const fila = { id: id(), ...valores, createdAt: ahora(), updatedAt: ahora() }
      const r = restricciones(db, a, fila); if (r) return r
      filas.unshift(fila)
      if (a === 'usuarios') db.credenciales[fila.id] = contrasena
      guardar(db)
      return ok(fila, 201)
    }
    if (metodo === 'PUT' && b) {
      const { contrasena, ...valores } = validar(a, cuerpo)
      if (!actual) return err(404, 'Registro no encontrado.')
      if (a === 'usuarios' && contrasena && contrasena.length < 6) return err(400, 'La contrasena debe tener al menos 6 caracteres')
      const r = restricciones(db, a, { ...actual, ...valores }); if (r) return r
      Object.assign(actual, valores, { updatedAt: ahora() })
      if (a === 'usuarios' && contrasena) db.credenciales[b] = contrasena // vacia = se conserva
      guardar(db)
      return ok(actual)
    }
    if (metodo === 'DELETE' && b) {
      if (!actual) return err(404, 'Registro no encontrado.')
      if ((USADA_POR[a] || []).some(([t, c]) => db.tablas[t].some((x) => x[c] === b))) return err(409, 'No se puede eliminar: existen registros relacionados.')
      db.tablas[a] = filas.filter((x) => x.id !== b); delete db.credenciales[b]; guardar(db)
      return { status: 204, body: null }
    }
    return err(404, 'Ruta no encontrada.')
  } catch (e) {
    if (e?.status) return err(e.status, e.message)
    throw e
  }
}
