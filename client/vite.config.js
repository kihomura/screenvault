import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
// import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  const apiUrl = env.VITE_API_URL || 'http://localhost:5555'

  return {
    plugins: [
      vue(),
      // vueDevTools(), // Commented out to prevent auto-opening dev tools
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
      port: parseInt(process.env.PORT || '5173'),
      proxy: {
        '/auth': {
          target: apiUrl,
          changeOrigin: true
        },
        '/user': {
          target: apiUrl,
          changeOrigin: true
        },
        '/oauth2': {
          target: apiUrl,
          changeOrigin: true,
          secure: false
        }
      }
    },
    preview: {
      port: parseInt(process.env.PORT || '5173'),
      host: '0.0.0.0',
    }
  }
})