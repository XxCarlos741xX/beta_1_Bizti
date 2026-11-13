# Bizti — backend Spring Boot + frontend React (rama `integracion-front-back`)

| Carpeta | Qué es | Puerto |
|---|---|---|
| `beta_1_Bizti/` | API REST Spring Boot 4 (Java 17, H2) | 8080 |
| `frontend-bizti/` | Frontend React + Vite (tomado de `Josesierra2/FrontBizti`) conectado a la API | 5173 |
| `frontend/` | Frontend anterior de `develop` (sin cambios) | — |
| `Bizti-SpringBoot.postman_collection.json` | Colección de Postman de la API v1 | — |
| `pruebas/` | Prueba del front (`api.js`) y casos de validación contra el backend real | — |
| `.github/workflows/pruebas.yml` | GitHub Actions: compila, corre JUnit, levanta la API y ejecuta Postman, las pruebas del front y los casos | — |

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

- Frontend: <http://localhost:5173> → **Crear cuenta** (contraseña de mínimo 6 caracteres).
- Swagger: <http://localhost:8080/swagger-ui.html>
- Consola H2: <http://localhost:8080/h2-console> — JDBC URL `jdbc:h2:file:~/bizti-data/bizti`, usuario `sa`, sin contraseña.
  Los datos quedan en `C:\Users\<usuario>\bizti-data\`; para empezar de cero, apagar el backend y borrar esa carpeta.

### Variables de entorno (opcionales)

| Variable | Por defecto | Para qué |
|---|---|---|
| `BIZTI_DB_URL` | `jdbc:h2:file:~/bizti-data/bizti` | URL de la base |
| `BIZTI_DB_USUARIO` / `BIZTI_DB_CLAVE` | `sa` / vacía | Credenciales de la base |
| `BIZTI_CORS_ORIGENES` | `http://localhost:*,http://127.0.0.1:*` | Orígenes permitidos (en producción, el dominio del front; nunca `*`) |
| `BIZTI_H2_CONSOLA` | `true` | Desactivar la consola H2 fuera de desarrollo |

## API v1

Todos los recursos están en `/api/v1/{recurso}` con `empresas`, `categorias`, `prioridades`, `usuarios`, `retos` y `registros`:

| Verbo | Ruta | Éxito | Errores |
|---|---|---|---|
| GET | `/api/v1/{recurso}?page=0&size=20&sort=nombre,asc` | 200, página `{ content, page }` | — |
| GET | `/api/v1/{recurso}/{id}` | 200 | 400 (id no es UUID), 404 |
| POST | `/api/v1/{recurso}` | **201** + `Location` | 400 (validación), 404 (relación inexistente), 409 (duplicado o regla) |
| PUT | `/api/v1/{recurso}/{id}` | 200 | 400, 404, 409 |
| DELETE | `/api/v1/{recurso}/{id}` | **204** | 404, 409 (tiene datos asociados) |
| POST | `/api/v1/auth/registro` `{ nombre, correo, contrasena }` | 201 (rol `usuario`) | 400, 409 |
| POST | `/api/v1/auth/login` `{ correo, contrasena }` | 200 | 400, 401 |

Formato de error (igual para todos):

```json
{
  "timestamp": "2026-11-12T19:50:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Datos inválidos. correo: no es un correo válido",
  "path": "/api/v1/empresas",
  "errors": [ { "campo": "correo", "mensaje": "no es un correo válido" } ]
}
```

Los cuerpos JSON y los ejemplos de cada recurso están en Swagger y en la colección de Postman.

## Estructura del backend

```
controllers/  EmpresaControlador ... AuthControlador   HTTP: @Valid, códigos, Location
services/     EmpresaService (interfaz) + EmpresaServiceImpl ...   negocio y @Transactional
repository/   IRepositorioEmpresa, IRepositorioReto ...  JPA (@EntityGraph en retos y registros)
models/       entidades JPA (solo mapean tablas; relaciones LAZY)
dto/          XRequestDto (con validación) y XResponseDto (lo que sale)
mapper/       Entidad <-> DTO
exception/    RecursoNoEncontrado (404), ReglaNegocio (409), CredencialesInvalidas (401)
config/       ManejadorErrores (único @RestControllerAdvice), CorsConfig
```

Flujo: Controlador → Servicio (interfaz) → Repositorio. Las entidades nunca salen de la API; la contraseña se guarda con hash PBKDF2 y no aparece en ningún DTO de salida.

## Reglas de negocio

- Empresa: NIT, correo y teléfono únicos; teléfono de 10 dígitos.
- Usuario: correo único; contraseña de 6 a 72 caracteres; rol `usuario`, `evaluador` o `administrador`. Al editar sin contraseña se conserva la actual.
- Prioridad: `nivel` desde 1.
- Reto: `fecha_fin` no puede ser anterior a `fecha_inicio`.
- No se puede eliminar una empresa, categoría o prioridad con retos, ni un reto o usuario con registros (409 con el motivo).

## Cambios frente a `develop`

**Integración y módulo de usuarios**
- `Usuario` (modelo, repositorio, servicio, controlador) y `AuthControlador` (registro/login).
- `frontend-bizti` conectado a la API; traducción de campos en `frontend-bizti/src/api.js`.

**Correcciones de compilación y funcionamiento de `develop`**
- `Registro` usaba `Usuario`, que no existía; `PrioridadControlador` tenía una clase anidada y métodos inexistentes.
- Rutas `"{/id}"` → `"/{id}"`; faltaba `@GetMapping` (no había ningún GET).
- Columnas `"fecha inicio"`/`"fecha fin"` con espacio; referencias JSON mal emparejadas (error 500 al listar).
- `sector`/`contacto` de empresa eran únicos; los `modificar` solo actualizaban 1 o 2 campos.

**Buenas prácticas (revisión de la API)**
- DTOs de entrada y salida con Bean Validation; las entidades ya no se exponen.
- Servicios con interfaz, inyección por constructor, `@Transactional` (`readOnly` en consultas), `orElseThrow`.
- Excepciones propias y manejador global con formato uniforme; 400 con la lista de campos; sin stack trace.
- `/api/v1`, `GET /{id}`, paginación, 201 + `Location`, 204.
- Relaciones LAZY + `@EntityGraph` (sin N+1) y `open-in-view` desactivado.
- Swagger (springdoc), CORS y credenciales por variables de entorno, mensajes de validación en español.
- Nombres: `IReporsitorioReto` → `IRepositorioReto`, `iRepositorioEmpresa` → `IRepositorioEmpresa`, `ServiciosX` → `XService`/`XServiceImpl`.

## Pruebas

- **JUnit:** `EmpresaServiceImplTest` (Mockito) y `EmpresaControladorTest` (`@WebMvcTest` + `MockMvc`), más `contextLoads`.
  Se corren con `mvnw test` (usan H2 en memoria).
- **Postman:** importar la colección y correrla con el *Runner* en orden (los ids se guardan solos).
- **GitHub Actions:** en cada push corre todo lo anterior más `pruebas/prueba-front.mjs` (funciones del front contra la API real),
  `pruebas/prueba-casos.mjs` (26 casos: válidos, obligatorios, límites, formatos, tipos y reglas), Swagger, CORS y el build del front.
