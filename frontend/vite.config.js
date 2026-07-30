import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Proxying /api to the Spring app keeps every request same-origin,
// so the backend needs no CORS configuration.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: 'http://localhost:9090', changeOrigin: true },
      '/swagger-ui': { target: 'http://localhost:9090', changeOrigin: true },
      '/v3/api-docs': { target: 'http://localhost:9090', changeOrigin: true },
    },
  },
});
