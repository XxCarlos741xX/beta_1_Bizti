import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Puerto 5173: es el que permite el CorsConfig del backend (localhost:*)
export default defineConfig({
  plugins: [react()],
  server: { port: 5173, strictPort: true },
})
