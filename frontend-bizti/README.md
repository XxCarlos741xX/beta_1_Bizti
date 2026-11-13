# Bizti · Frontend

Frontend en React + Vite para el backend **Spring Boot** del proyecto (`beta_1_Bizti`, H2, puerto 8080).
Incluye login y registro, CRUD de empresas, categorías, prioridades, usuarios, retos y registros,
con buscador, filtros por estado/rol/relaciones y rango de fechas.

## Cómo correrlo con el backend Spring Boot

Requisitos: JDK 17+ y Node.js 18+.

1. En la carpeta `beta_1_Bizti`: `mvnw spring-boot:run` (CMD). Queda en `http://localhost:8080`.
2. En la carpeta de **este frontend**: `npm install` y `npm run dev`. Abre `http://localhost:5173`.
3. Pantalla de login → **Crear cuenta** (contraseña de mínimo 6 caracteres).

La URL del backend se cambia abajo en la barra lateral (se recuerda en el navegador).
El puerto 5173 es fijo porque `CorsConfig` del backend permite `localhost:*`.

## Modo demo (sin backend ni MySQL)

En el login marca **Modo demo (sin backend)**. Cuenta de prueba: `admin@bizti.co` / `admin1234`.
Los datos se guardan en el navegador y el simulador (`src/mock.js`) responde como el backend real,
con las mismas validaciones y errores (401, 404, 409). Sirve para probar el front sin nada encendido.

## Qué ya se probó

- Las funciones de `api.js` contra una API con las mismas respuestas que Spring (relaciones anidadas,
  `fecha_inicio`, `fechaRegistros`, contraseña que no se devuelve, 404/401/409, `DELETE` → `true`):
  registro, login, CRUD de los seis módulos, NIT y correo repetidos, relación inexistente y borrar
  algo con datos asociados.
- En modo demo: login, registro, usuarios con contraseña, empresas activas/inactivas y registros con fecha y hora.

## Conexión con el backend

El único contacto con la API está en dos archivos:

- `src/recursos.js`: módulos y campos (tipos, largos máximos y opciones de los estados).
- `src/api.js`: URL base, peticiones y el **adaptador** que traduce las filas planas del front
  (`empresaId`, `fechaInicio`…) al JSON de Spring (`"empresa": { "id" }`, `fecha_inicio`…) y de vuelta.

## Estructura

```
src/App.jsx        pantalla principal (barra lateral, avisos, confirmaciones)
src/Login.jsx      inicio de sesión y registro
src/Tabla.jsx      tabla con buscador y filtros
src/Formulario.jsx panel lateral para crear y editar
src/recursos.js    módulos y campos
src/api.js         cliente de la API
src/mock.js        backend simulado del modo demo (mismos campos que el front)
src/styles.css     todo el diseño (colores arriba, en :root)
```

El diseño está documentado en `DISENO.md`.
