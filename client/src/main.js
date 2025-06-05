/**
 * Main entry point for the ScreenVault Vue.js application
 * Configures global settings, plugins, and initializes the application
 */

import { createApp } from 'vue'
import axios from 'axios'
import App from './App.vue'
import router from './router'
import store from './store/store.js'
import utilsPlugin from './plugins/utils.js'
import storagePlugin from './plugins/storagePlugin.js'
import { createPinia } from 'pinia';
import { storageManager } from './utils/storageManager.js'

// Bootstrap CSS and JavaScript imports
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.js'

// Custom CSS imports
import './assets/css/custom-colors.css'
import './assets/css/cyberpunk-theme.css'

// Theme components and chart library
import CyberpunkComponents from './components/theme/cyberpunk'
import * as echarts from 'echarts'

// FontAwesome icon library setup
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faGoogle } from "@fortawesome/free-brands-svg-icons";
import { faGithub } from "@fortawesome/free-brands-svg-icons";
import { faStar, faHeart, faList, faHistory, faFilm, faEye } from "@fortawesome/free-solid-svg-icons";

/**
 * Configure backend API URL based on environment
 * Priority: VITE_API_URL > development/production defaults
 */
const backendUrl = import.meta.env.VITE_API_URL ||
    (import.meta.env.DEV ?
        'http://localhost:5555' :
        'https://screenvault-server-production.up.railway.app');

/**
 * Global Axios configuration for API requests
 * Sets up base URL, credentials, and default headers
 */
console.log('[main.js] Backend URL:', backendUrl);
console.log('[main.js] DEV mode:', import.meta.env.DEV);
console.log('[main.js] VITE_API_URL:', import.meta.env.VITE_API_URL);
axios.defaults.baseURL = backendUrl;
axios.defaults.withCredentials = true; // Enable cross-origin credential sharing
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.defaults.headers.common['Accept'] = 'application/json';

/**
 * Axios request interceptor
 * Ensures credentials are included in all requests
 */
axios.interceptors.request.use(function (config) {
    config.withCredentials = true;
    return config;
}, function (error) {
    // console.error('Axios request error:', error); // Keep this if useful for general debugging
    return Promise.reject(error);
});

/**
 * Axios response interceptor
 * Handles network errors and CORS issues
 */
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.message === 'Network Error') {
            // console.error('Network Error - This might be a CORS issue:', error); // Keep this if useful
        }
        return Promise.reject(error);
    }
);

// Create Vue application instance
const app = createApp(App)

// Make Axios and ECharts available globally
app.config.globalProperties.$http = axios
app.config.globalProperties.$echarts = echarts

// Configure FontAwesome icon library
library.add(faGoogle, faGithub, faStar, faHeart, faList, faHistory, faFilm, faEye);
app.component("font-awesome-icon", FontAwesomeIcon);

// Initialize storage manager for user data persistence
console.log('[main.js] Initializing storageManager...');
storageManager.init(store);

// Setup Pinia state management
const pinia = createPinia();
window.__PINIA_INSTANCE__ = pinia;
app.use(pinia);

// Install Vue plugins and components
app.use(router)
app.use(store)
app.use(utilsPlugin)
app.use(storagePlugin)
app.use(CyberpunkComponents)

/**
 * Load Google Fonts for cyberpunk theme
 * Dynamically adds font links to document head
 */
const linkElement = document.createElement('link');
linkElement.rel = 'stylesheet';
linkElement.href = 'https://fonts.googleapis.com/css2?family=Orbitron:wght@400;500;600;700&family=Rajdhani:wght@300;400;500;600;700&family=VT323&display=swap';
document.head.appendChild(linkElement);

/**
 * Application initialization
 * Fetches user authentication state before mounting the app
 */
(async () => {
    try {
        await store.dispatch('fetchUser');
    } catch (e) {
        console.error('Failed to fetch user data on init:', e); // Keep this important error log
    } finally {
        app.mount('#app');
    }
})();