# Diseño de Bizti

Todo el estilo vive en `src/styles.css`. Los colores y las tipografías están al inicio, en `:root`,
así que para reutilizarlos basta con copiar ese bloque.

## Colores

| Uso | Variable | Valor |
|---|---|---|
| Barra lateral, panel del login, textos oscuros | `--marino` | `#0B2B3A` |
| Color principal (botones, enlaces, foco) | `--verde` | `#0E6B65` |
| Botón principal al pasar el mouse | `--verde-o` | `#0A5550` |
| Detalle de marca (punto del logo, ítem activo, avatar) | `--calendula` | `#F4B63F` |
| Texto | `--tinta` | `#14233A` |
| Texto secundario | `--apagado` | `#5B6B7F` |
| Fondo de la app | `--niebla` | `#F2F5F7` |
| Tarjetas, tablas, formularios | `--papel` | `#FFFFFF` |
| Bordes | `--linea` | `#DDE3EA` |
| Error y eliminar | `--rojo` | `#C8402F` |
| Éxito | `--ok` | `#2E8B57` |

Etiquetas de estado (fondo / texto): verde `#E3F3EA` / `#1D6B41`, ámbar `#FCF0D2` / `#8A5A00`,
gris `#ECEFF2` / `#55636F`, azul `#E1EBF7` / `#23528B`, rojo `#FBEAE7` / `#8E2A1E`.

El amarillo (`--calendula`) se usa poco a propósito: solo marca lo activo o de marca.

## Tipografía

- Títulos y logo: **Bricolage Grotesque** (peso 700).
- Texto: **Figtree**.

Se instalan con npm y no necesitan internet:
`npm install @fontsource-variable/bricolage-grotesque @fontsource-variable/figtree`
y se importan en `src/main.jsx`. Los iconos son de `lucide-react`.

## Bloque para copiar

```css
:root {
  --niebla:#F2F5F7; --papel:#fff; --tinta:#14233A; --apagado:#5B6B7F; --linea:#DDE3EA;
  --verde:#0E6B65; --verde-o:#0A5550; --marino:#0B2B3A; --calendula:#F4B63F;
  --rojo:#C8402F; --ok:#2E8B57;
  --titulo:'Bricolage Grotesque Variable','Bricolage Grotesque',system-ui,sans-serif;
  --texto:'Figtree Variable','Figtree',system-ui,sans-serif;
}
```
