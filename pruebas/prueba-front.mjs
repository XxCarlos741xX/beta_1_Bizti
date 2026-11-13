// Ejecuta las funciones reales de api.js del front contra la API en :8080
const F = process.argv[2]
const { cargarTodo, guardarFila, eliminarFila, iniciarSesion, registrarse } = await import(`${F}/src/api.js`)
const U = 'http://localhost:8080'
let fallos = 0
const ok = (c, m) => { console.log(`${c ? '✔' : '✘'} ${m}`); if (!c) fallos++ }
const falla = async (p, codigoMsg, m) => { try { await p; ok(false, m) } catch (e) { ok(new RegExp(codigoMsg, 'i').test(e.message), `${m} → "${e.message}"`) } }

const yo = await registrarse(U, { nombre: 'Carlos', correo: 'carlos@bizti.co', contrasena: 'clave123' })
ok(yo.id && yo.rol === 'usuario' && !('contrasena' in yo), 'registro devuelve usuario sin contraseña')
ok((await iniciarSesion(U, 'carlos@bizti.co', 'clave123')).nombre === 'Carlos', 'login correcto')
await falla(iniciarSesion(U, 'carlos@bizti.co', 'mala'), 'incorrectos', 'login malo da 401 con mensaje')
await falla(registrarse(U, { nombre: 'X', correo: 'carlos@bizti.co', contrasena: 'clave123' }), 'Ya existe', 'correo repetido da 409 con mensaje')

// Cuerpos exactamente como los arma Formulario.jsx
const e = (await guardarFila(U, 'empresas', null, { nombre: 'Acme', nit: '900-1', sector: 'TI', contacto: 'Ana', correo: 'a@acme.co', telefono: '3001234567', activa: true })).datos
ok(e.id && e.activa === true, 'crear empresa')
await falla(guardarFila(U, 'empresas', null, { nombre: 'Otra', nit: '900-1', sector: 'TI', contacto: 'B', correo: 'b@x.co', telefono: '3009999999', activa: true }), 'NIT', 'NIT repetido da 409 con mensaje claro')
await falla(guardarFila(U, 'empresas', null, { nombre: '', nit: '900-2', sector: 'TI', contacto: 'B', correo: 'malo', telefono: '12', activa: true }), 'Datos inválidos', 'campos inválidos dan 400 con la lista')
const c = (await guardarFila(U, 'categorias', null, { nombre: 'Tec', descripcion: 'd1' })).datos
const p = (await guardarFila(U, 'prioridades', null, { nombre: 'Alta', nivel: 1 })).datos
const u2 = (await guardarFila(U, 'usuarios', null, { nombre: 'Eva', correo: 'eva@x.co', rol: 'evaluador', contrasena: 'secreta1' })).datos
ok(u2.id && !('contrasena' in u2), 'crear usuario desde el CRUD')
await guardarFila(U, 'usuarios', u2.id, { nombre: 'Eva M', correo: 'eva@x.co', rol: 'evaluador' }) // sin contrasena
ok((await iniciarSesion(U, 'eva@x.co', 'secreta1')).nombre === 'Eva M', 'editar usuario sin contraseña la conserva')

const r = (await guardarFila(U, 'retos', null, { nombre: 'R1', descripcion: 'd', empresaId: e.id, categoriaId: c.id, prioridadId: p.id, fechaInicio: '2026-01-01', fechaFin: '2026-02-01', estado: 'Abierto' })).datos
ok(r.empresaId === e.id && r.categoriaId === c.id && r.prioridadId === p.id && r.fechaInicio === '2026-01-01', 'crear reto: relaciones y fechas traducidas')
const g = (await guardarFila(U, 'registros', null, { usuarioId: yo.id, retoId: r.id, fechaRegistro: '2026-01-05T10:30', estado: 'Pendiente', observacion: '' })).datos
ok(g.retoId === r.id && g.usuarioId === yo.id && g.fechaRegistro === '2026-01-05T10:30', 'crear registro: reto, usuario y fecha-hora')
await falla(guardarFila(U, 'retos', r.id, { nombre: 'R1', descripcion: '', empresaId: e.id, categoriaId: c.id, prioridadId: p.id, fechaInicio: '2026-03-01', fechaFin: '2026-01-01', estado: 'Abierto' }), 'fecha de fin', 'reto con fin antes del inicio da 409')
await guardarFila(U, 'retos', r.id, { nombre: 'R1 editado', descripcion: '', empresaId: e.id, categoriaId: c.id, prioridadId: p.id, fechaInicio: '2026-01-01', fechaFin: '2026-03-01', estado: 'Cerrado' })
await falla(guardarFila(U, 'retos', null, { nombre: 'R2', descripcion: '', empresaId: '00000000-0000-0000-0000-000000000000', categoriaId: c.id, prioridadId: p.id, fechaInicio: '2026-01-01', fechaFin: '2026-02-01', estado: 'Abierto' }), 'no encontrada', 'empresa inexistente da 404 con mensaje')

const { datos: todo } = await cargarTodo(U)
ok(Object.keys(todo).length === 6, 'cargarTodo trae las 6 tablas')
const rr = todo.retos[0]
ok(rr.nombre === 'R1 editado' && rr.fechaFin === '2026-03-01' && rr.empresaId === e.id && !('empresa' in rr), 'lista de retos queda plana para la tabla')
ok(todo.registros[0].retoId === r.id && todo.registros[0].fechaRegistro === '2026-01-05T10:30', 'lista de registros queda plana')

await falla(eliminarFila(U, 'empresas', e.id), 'retos asociados', 'borrar empresa con retos da 409')
ok((await eliminarFila(U, 'registros', g.id)).datos === null, 'DELETE responde 204 sin cuerpo y no rompe')
await eliminarFila(U, 'retos', r.id); await eliminarFila(U, 'empresas', e.id)
ok((await cargarTodo(U)).datos.empresas.length === 0, 'eliminados en cascada manual')
console.log(fallos ? `\n${fallos} FALLOS` : '\nTODO OK')
process.exit(fallos ? 1 : 0)
