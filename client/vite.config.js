import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  css: {
    preprocessorOptions: {
      scss: {
      }
    }
  },
  server: {
    host: '0.0.0.0',
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
  }
})