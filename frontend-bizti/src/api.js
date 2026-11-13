import { servirDemo } from './mock.js'

// URL por defecto del backend Spring Boot (beta_1_Bizti) y version de la API
export const URL_POR_DEFECTO = 'http://localhost:8080'
const API = '/api/v1'

// Modo demo: no se llama a ningun servidor, se usa el backend simulado de mock.js
const CLAVE_MODO = 'bizti_modo_demo'
export const esModoDemo = () => {
  try { return localStorage.getItem(CLAVE_MODO) === '1' } catch { return false }
}
export const fijarModoDemo = (activo) => {
  try { activo ? localStorage.setItem(CLAVE_MODO, '1') : localStorage.removeItem(CLAVE_MODO) } catch { /* sin almacenamiento */ }
}

// Cada llamada devuelve { datos, log }: el log es el texto de la peticion y la
// respuesta, que la pantalla muestra en "Ultima peticion a la API".
export class ErrorApi extends Error {
  constructor(mensaje, log, sinConexion = false) {
    super(mensaje)
    this.log = log
    this.sinConexion = sinConexion
  }
}

// Saca el mensaje de error de cualquiera de los formatos posibles:
//  - Spring (ResponseStatusException): { status, error: "Not Found", message: "Empresa no encontrada" }
//  - ManejadorErrores (409):           { error: "Conflicto de datos: ..." }
//  - Modo demo:                        { error: { message: "..." } }
function mensajeDeError(json, status) {
  if (json?.error?.message) return json.error.message
  if (json?.message) return json.message
  if (typeof json?.error === 'string') return json.error
  return `Error ${status}`
}

// Peticion "cruda" a /api/<ruta>
export async function llamar(baseUrl, metodo, ruta, cuerpo) {
  const demo = esModoDemo()
  const url = demo ? `demo://api/${ruta}` : `${baseUrl.replace(/\/+$/, '')}${API}/${ruta}`
  let log = `${metodo} ${url}${demo ? ' (modo demo, sin backend)' : ''}` + (cuerpo ? `\n${JSON.stringify(cuerpo, null, 2)}` : '')
  let status, estadoTexto = '', crudo
  if (demo) {
    const r = servirDemo(metodo, ruta, cuerpo)
    status = r.status
    crudo = r.body == null ? '' : JSON.stringify(r.body)
  } else {
    try {
      const r = await fetch(url, {
        method: metodo,
        headers: cuerpo ? { 'Content-Type': 'application/json' } : {},
        body: cuerpo ? JSON.stringify(cuerpo) : undefined,
      })
      status = r.status; estadoTexto = r.statusText; crudo = await r.text()
    } catch {
      log += '\n\n← Sin respuesta. ¿Está encendido el backend Spring Boot (mvnw spring-boot:run) en el puerto 8080?'
      throw new ErrorApi('No se pudo conectar con el backend', log, true)
    }
  }
  let json = null
  try { json = crudo ? JSON.parse(crudo) : null } catch { /* no era JSON */ }
  log += `\n\n← ${status} ${estadoTexto}\n${json != null ? JSON.stringify(json, null, 2) : crudo}`.trimEnd()
  if (status >= 400) throw new ErrorApi(mensajeDeError(json, status), log)
  // Spring devuelve el objeto, o una pagina { content, page } en los listados; DELETE responde 204 sin cuerpo.
  // El modo demo envuelve algunas respuestas como { data }.
  const envuelto = json && typeof json === 'object' && !Array.isArray(json) && 'data' in json
  return { datos: envuelto ? json.data : json, log }
}

// ---------------------------------------------------------------------------
// Adaptador front <-> Spring Boot
// El front trabaja con filas "planas" (empresaId, fechaInicio...). Spring usa
// objetos anidados ("empresa": { "id": ... }) y otros nombres (fecha_inicio,
// fechaRegistros). Aqui se traduce en las dos direcciones.
// ---------------------------------------------------------------------------
const ref = (id) => (id ? { id } : null)
const fechaHora = (v) => (v && v.length === 16 ? `${v}:00` : v || null) // "2026-01-01T10:00" -> "...:00"

// Fila del front -> JSON que espera Spring
const A_SPRING = {
  retos: ({ fechaInicio, fechaFin, empresaId, categoriaId, prioridadId, ...resto }) => ({
    ...resto,
    fecha_inicio: fechaInicio || null,
    fecha_fin: fechaFin || null,
    empresa: ref(empresaId),
    categoria: ref(categoriaId),
    prioridad: ref(prioridadId),
  }),
  registros: ({ fechaRegistro, retoId, usuarioId, observacion, ...resto }) => ({
    ...resto,
    fechaRegistros: fechaHora(fechaRegistro),
    observacion: observacion ?? '',
    reto: ref(retoId),
    usuario: ref(usuarioId),
  }),
  usuarios: ({ contrasena, ...resto }) => (contrasena ? { ...resto, contrasena } : resto),
}

// JSON de Spring -> fila del front
const DE_SPRING = {
  retos: ({ fecha_inicio, fecha_fin, empresa, categoria, prioridad, ...resto }) => ({
    ...resto,
    fechaInicio: fecha_inicio ?? '',
    fechaFin: fecha_fin ?? '',
    empresaId: empresa?.id ?? '',
    categoriaId: categoria?.id ?? '',
    prioridadId: prioridad?.id ?? '',
  }),
  registros: ({ fechaRegistros, reto, usuario, ...resto }) => ({
    ...resto,
    fechaRegistro: fechaRegistros ? String(fechaRegistros).slice(0, 16) : '',
    retoId: reto?.id ?? '',
    usuarioId: usuario?.id ?? '',
  }),
}

const aSpring = (recurso, fila) => (A_SPRING[recurso] ? A_SPRING[recurso](fila) : fila)
const deSpring = (recurso, fila) => (fila && DE_SPRING[recurso] ? DE_SPRING[recurso](fila) : fila)

export const RUTAS = ['empresas', 'categorias', 'prioridades', 'usuarios', 'retos', 'registros']

// Trae las seis tablas. Spring no tiene /api/data, asi que se piden en paralelo.
// Los listados son paginados: se pide una pagina grande y se toma "content".
const TAMANO_PAGINA = 1000
export async function cargarTodo(baseUrl) {
  if (esModoDemo()) return llamar(baseUrl, 'GET', 'data')
  const respuestas = await Promise.all(RUTAS.map((r) => llamar(baseUrl, 'GET', `${r}?size=${TAMANO_PAGINA}`)))
  const datos = {}
  RUTAS.forEach((r, i) => {
    const d = respuestas[i].datos
    const filas = Array.isArray(d) ? d : d?.content || []
    datos[r] = filas.map((f) => deSpring(r, f))
  })
  const log = RUTAS.map((r, i) => `GET ${baseUrl.replace(/\/+$/, '')}${API}/${r}?size=${TAMANO_PAGINA}  ← 200 (${datos[r].length} filas)`).join('\n')
  return { datos, log }
}

// Crear (sin id) o modificar (con id)
export async function guardarFila(baseUrl, recurso, id, fila) {
  const demo = esModoDemo()
  const cuerpo = demo ? fila : aSpring(recurso, fila)
  const r = await llamar(baseUrl, id ? 'PUT' : 'POST', id ? `${recurso}/${id}` : recurso, cuerpo)
  return { ...r, datos: demo ? r.datos : deSpring(recurso, r.datos) }
}

export const eliminarFila = (baseUrl, recurso, id) => llamar(baseUrl, 'DELETE', `${recurso}/${id}`)

// Login y registro. Spring devuelve el usuario directamente; el demo lo devuelve en { user }.
export async function iniciarSesion(baseUrl, correo, contrasena) {
  const { datos } = await llamar(baseUrl, 'POST', 'auth/login', { correo, contrasena })
  return datos?.user ?? datos
}

export async function registrarse(baseUrl, { nombre, correo, contrasena }) {
  const { datos } = await llamar(baseUrl, 'POST', 'auth/registro', { nombre, correo, contrasena })
  return datos?.user ?? datos
}
