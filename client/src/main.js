import { createApp } from 'vue'
import axios from 'axios'
import App from './App.vue'
import router from './router'
import store from './store/store.js'
import utilsPlugin from './plugins/utils.js'
import storagePlugin from './plugins/storagePlugin.js'
import { createPinia } from 'pinia';
import { storageManager } from './utils/storageManager.js'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.js'
import './assets/css/custom-colors.css'
import './assets/css/cyberpunk-theme.css'
import CyberpunkComponents from './components/theme/cyberpunk'
import * as echarts from 'echarts'
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faGoogle } from "@fortawesome/free-brands-svg-icons";
import { faGithub } from "@fortawesome/free-brands-svg-icons";
import { faStar, faHeart, faList, faHistory, faFilm, faEye } from "@fortawesome/free-solid-svg-icons";

// set API request url based on the production/development environment
const backendUrl = import.meta.env.DEV ? 
    (import.meta.env.VITE_API_URL || 'http://localhost:5555') : 
    'https://screenvault-server-production.up.railway.app';

// global settings for Axios
axios.defaults.baseURL = backendUrl;
axios.defaults.withCredentials = true; // allow cross-origin requests to carry credentials
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.defaults.headers.common['Accept'] = 'application/json';

// add necessary headers to each request (request interceptor)
axios.interceptors.request.use(function (config) {
    config.withCredentials = true;
    return config;
}, function (error) {
    // console.error('Axios request error:', error); // Keep this if useful for general debugging
    return Promise.reject(error);
});

// handle error and CORS issue (response interceptor)
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.message === 'Network Error') {
            // console.error('Network Error - This might be a CORS issue:', error); // Keep this if useful
        }
        return Promise.reject(error);
    }
);

const app = createApp(App)
app.config.globalProperties.$http = axios
app.config.globalProperties.$echarts = echarts

library.add(faGoogle, faGithub, faStar, faHeart, faList, faHistory, faFilm, faEye);
app.component("font-awesome-icon", FontAwesomeIcon);

console.log('[main.js] Initializing storageManager...');
storageManager.init(store);

const pinia = createPinia();
window.__PINIA_INSTANCE__ = pinia;
app.use(pinia);

app.use(router)
app.use(store)
app.use(utilsPlugin)
app.use(storagePlugin)
app.use(CyberpunkComponents)

// Add link to Google Fonts for cyberpunk theme
const linkElement = document.createElement('link');
linkElement.rel = 'stylesheet';
linkElement.href = 'https://fonts.googleapis.com/css2?family=Orbitron:wght@400;500;600;700&family=Rajdhani:wght@300;400;500;600;700&family=VT323&display=swap';
document.head.appendChild(linkElement);

(async () => {
  try {
    await store.dispatch('fetchUser');
  } catch (e) {
    console.error('Failed to fetch user data on init:', e); // Keep this important error log
  } finally {
    app.mount('#app');
  }
})();