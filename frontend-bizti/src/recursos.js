// Descripcion de cada modulo segun el backend Spring Boot (beta_1_Bizti).
// n = nombre del campo en el front, t = tipo de input, opcional = puede ir vacio.
// t "ref" = lista desplegable con los registros de otro modulo (se envia su id).
// t "check" = casilla si/no. oculto = no se muestra en la tabla.
// soloCrear = obligatorio al crear, opcional al editar (ej. la contrasena).
// La traduccion a los nombres de Spring (fecha_inicio, "empresa": {id}...) esta en api.js.
// Los "max" son los length de las columnas en los modelos de Spring.
export const RECURSOS = {
  empresas: {
    titulo: 'Empresas',
    campos: [
      { n: 'nombre', t: 'text', max: 50 },
      { n: 'nit', t: 'text', max: 255 },
      { n: 'sector', t: 'text', max: 100 },
      { n: 'contacto', t: 'text', max: 100 },
      { n: 'correo', t: 'email', max: 150 },
      { n: 'telefono', t: 'text', max: 10 },
      { n: 'activa', t: 'check' },
    ],
  },
  categorias: {
    titulo: 'Categorías',
    campos: [
      { n: 'nombre', t: 'text', max: 100 },
      { n: 'descripcion', t: 'text' },
    ],
  },
  prioridades: {
    titulo: 'Prioridades',
    campos: [
      { n: 'nombre', t: 'text', max: 50 },
      { n: 'nivel', t: 'number' },
    ],
  },
  usuarios: {
    titulo: 'Usuarios',
    campos: [
      { n: 'nombre', t: 'text', max: 100 },
      { n: 'correo', t: 'email', max: 150 },
      { n: 'rol', t: 'select', opciones: ['usuario', 'evaluador', 'administrador'] },
      // Se envia al crear; al editar, si se deja vacia se conserva la actual. Nunca vuelve del backend.
      { n: 'contrasena', t: 'password', oculto: true, soloCrear: true, min: 6 },
    ],
  },
  retos: {
    titulo: 'Retos',
    campos: [
      { n: 'nombre', t: 'text', max: 50 },
      { n: 'descripcion', t: 'text', max: 200, opcional: true },
      { n: 'empresaId', t: 'ref', ref: 'empresas' },
      { n: 'categoriaId', t: 'ref', ref: 'categorias' },
      { n: 'prioridadId', t: 'ref', ref: 'prioridades' },
      { n: 'fechaInicio', t: 'date' },
      { n: 'fechaFin', t: 'date' },
      { n: 'estado', t: 'select', opciones: ['Abierto', 'En revisión', 'Cerrado'] },
    ],
  },
  registros: {
    titulo: 'Registros',
    campos: [
      { n: 'usuarioId', t: 'ref', ref: 'usuarios' },
      { n: 'retoId', t: 'ref', ref: 'retos' },
      { n: 'fechaRegistro', t: 'datetime-local' },
      { n: 'estado', t: 'select', opciones: ['Pendiente', 'Aprobado', 'Rechazado'] },
      { n: 'observacion', t: 'text', max: 200, opcional: true },
    ],
  },
}

// Texto que se muestra para un registro relacionado
export const etiqueta = (o) => (o ? (o.nombre ?? o.id) : '')

// Nombre del registro al que apunta un campo "ref" (ej. empresaId -> nombre de la empresa)
export const textoRef = (campo, fila, todo) =>
  etiqueta((todo?.[campo.ref] || []).find((x) => x.id === fila[campo.n]))

// Textos para la interfaz
export const META = {
  empresas:    { singular: 'empresa',   desc: 'Compañías que publican retos.' },
  categorias:  { singular: 'categoría', desc: 'Temas para clasificar los retos.' },
  prioridades: { singular: 'prioridad', desc: 'Niveles de urgencia de cada reto (1 es la más alta).' },
  retos:       { singular: 'reto',      desc: 'Desafíos abiertos por las empresas.' },
  usuarios:    { singular: 'usuario',   desc: 'Personas de la plataforma. Para iniciar sesión, cada una crea su cuenta con «Crear cuenta» o un administrador la crea aquí con contraseña.' },
  registros:   { singular: 'registro',  desc: 'Inscripción de un usuario a un reto.' },
}

const ROTULOS = {
  nit: 'NIT', empresaId: 'Empresa', categoriaId: 'Categoría', prioridadId: 'Prioridad',
  usuarioId: 'Usuario', retoId: 'Reto', fechaInicio: 'Fecha de inicio', fechaFin: 'Fecha de cierre',
  fechaRegistro: 'Fecha de registro', descripcion: 'Descripción', observacion: 'Observación',
  telefono: 'Teléfono', categoria: 'Categoría', contrasena: 'Contraseña', activa: 'Activa',
}

// "fechaInicio" -> "Fecha de inicio", "sector" -> "Sector"
export const rotulo = (n) => ROTULOS[n] || n.charAt(0).toUpperCase() + n.slice(1)
