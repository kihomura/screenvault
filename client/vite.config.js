import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  base: '/',
  build: {
    outDir: 'dist',
  },
  css: {
    preprocessorOptions: {
      scss: {
      }
    }
  },
  server: {
    host: '0.0.0.0',
    port: process.env.PORT || 5173,
    proxy: {
      '/auth': {
        target: 'http://localhost:5555',
        changeOrigin: true
      },
      '/user': {
        target: 'http://localhost:5555',
        changeOrigin: true
      },
      '/oauth2': {
        target: 'http://localhost:5555',
        changeOrigin: true,
        secure: false
      }
    }
  },
  preview: {
    port: process.env.PORT || 5173,
    host: '0.0.0.0',
  }
})