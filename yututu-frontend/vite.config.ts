import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  // // 127.0.0.1:5011/api -> 127.0.0.1:8011/api
  // server: {
  //   proxy: {
  //     '/api': 'http://127.0.0.1:8011',
  //   },
  // },
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
