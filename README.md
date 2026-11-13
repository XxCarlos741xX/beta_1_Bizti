# Bizti — backend Spring Boot + frontend React (rama `integracion-front-back`)

| Carpeta | Qué es | Puerto |
|---|---|---|
| `beta_1_Bizti/` | Backend Spring Boot (rama `develop`) con las correcciones y el módulo de **usuarios** | 8080 |
| `frontend-bizti/` | Frontend React + Vite tomado de `Josesierra2/FrontBizti`, conectado a este backend | 5173 |
| `frontend/` | Frontend anterior de `develop` (sin cambios) | — |
| `Bizti-SpringBoot.postman_collection.json` | Colección de Postman para probar la API | — |
| `pruebas/prueba-front.mjs` | Prueba las funciones del front (`api.js`) contra el backend | — |
| `.github/workflows/pruebas.yml` | GitHub Actions: compila, levanta el backend y corre Postman + la prueba del front | — |

## Cómo correrlo (CMD)

Requisitos: JDK 17+ y Node.js 18+.

```cmd
cd beta_1_Bizti
mvnw spring-boot:run
```

En otra ventana:

```cmd
cd frontend-bizti
npm install
npm run dev
```

Abrir <http://localhost:5173> → **Crear cuenta** (contraseña de mínimo 6 caracteres).
Consola H2: <http://localhost:8080/h2-console> — JDBC URL `jdbc:h2:file:~/bizti-data/bizti`, usuario `sa`, sin contraseña.
Los datos quedan en `C:\Users\<usuario>\bizti-data\`; para empezar de cero, apagar el backend y borrar esa carpeta.

## Lo que se agregó: usuarios

- `models/Usuario.java`: `id` (UUID), `nombre`, `correo` (único), `contrasena`, `rol`.
  La contraseña se guarda con hash PBKDF2 y **nunca se devuelve** en el JSON.
- `repository/IRepositorioUsuario.java` (con `findByCorreo`), `services/ServiciosUsuario.java`,
  `controllers/UsuarioControlador.java` (CRUD en `/api/usuarios`).
- `controllers/AuthControlador.java`:
  - `POST /api/auth/registro` `{ nombre, correo, contrasena }` → crea el usuario con rol `usuario` (201).
  - `POST /api/auth/login` `{ correo, contrasena }` → devuelve el usuario, o 401.
- `Registro` queda relacionado con `Usuario` (`"usuario": { "id": "..." }`).
- Al editar un usuario sin enviar contraseña, se conserva la actual.

## Correcciones al código de `develop`

1. No compilaba: `Registro` usaba `Usuario`, que no existía.
2. `PrioridadControlador` tenía una clase `CategoriaControlador` anidada, recibía `Categoria` y llamaba a
   métodos inexistentes (`Modificar`, `buscarPrioridadPorId()` sin id). Se reescribió;
   `ServiciosPrioridad` no tenía `modificarPrioridad`.
3. En todos los controladores las rutas eran `"{/id}"` → `"/{id}"`, y a `buscar` le faltaba `@GetMapping`
   (no había ningún GET).
4. `Reto`: columnas `"fecha inicio"` / `"fecha fin"` con espacio → `fecha_inicio` / `fecha_fin`;
   no tenía getters/setters de `empresa`, `categoria` y `prioridad`.
5. Referencias JSON mal emparejadas (`empresa_reto` vs `reto_empresa`, `@JsonBackReference` sobre listas)
   → error 500 al listar. Ahora las listas `retos` llevan `@JsonIgnore` y el reto expone sus relaciones.
6. `Empresa`: `sector` y `contacto` eran únicos (no dejaban dos empresas del mismo sector) y `contacto`
   tenía 10 caracteres; ahora el único es `nit` y `contacto` tiene 100. `retos` no tenía getter/setter.
7. Los `modificar` de Empresa, Reto y Registro solo actualizaban 1 o 2 campos; ahora actualizan todos.
8. `ServiciosReto` / `ServiciosRegistros` buscan por `id` la empresa, categoría, prioridad, reto y usuario
   que llegan en el JSON (404 si no existen).
9. `config/ManejadorErrores.java`: dato repetido o borrar algo con datos asociados → **409** con mensaje (antes 500).
10. `config/CorsConfig.java`: permite al front en `localhost:*`.
11. `application.properties`: H2 en archivo + consola H2 + mensajes de error en la respuesta.
    Las pruebas (`src/test/resources`) usan H2 en memoria.
12. Se quitó el import sin uso `UserConfigurations` y mensajes que decían "Usuario" donde no era.

## Cómo se conectó el front

Toda la traducción está en `frontend-bizti/src/api.js`:

| Front | Spring |
|---|---|
| `empresaId`, `categoriaId`, `prioridadId` | `"empresa": { "id" }`, `"categoria": { "id" }`, `"prioridad": { "id" }` |
| `fechaInicio`, `fechaFin` | `fecha_inicio`, `fecha_fin` |
| `retoId`, `usuarioId`, `fechaRegistro` | `"reto": { "id" }`, `"usuario": { "id" }`, `fechaRegistros` |
| carga inicial | un `GET` por módulo, en paralelo |

El modo demo (sin backend) sigue disponible en el login: `admin@bizti.co` / `admin1234`.

## Pruebas

- **Postman:** importar la colección y correrla con el *Runner* en orden (los ids se guardan solos).
- **GitHub Actions:** cada push corre `.github/workflows/pruebas.yml` (compilación, `contextLoads`, Postman,
  prueba del front contra el backend real, CORS y build del front).
