import { createApp } from 'vue'
import axios from 'axios'
import App from './App.vue'
import router from './router'
import store from './store/store.js'
import utilsPlugin from './plugins/utils.js'
import { createPinia } from 'pinia';
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.js'
import './assets/css/custom-colors.css'
import * as echarts from 'echarts'

const backendUrl = import.meta.env.DEV ? 
    (import.meta.env.VITE_API_URL || 'http://localhost:5555') : 
    'https://screenvault-server-production.up.railway.app';

axios.defaults.baseURL = backendUrl;
axios.defaults.withCredentials = true;

axios.interceptors.request.use(function (config) {
    config.withCredentials = true;
    return config;
}, function (error) {
    return Promise.reject(error);
});

const app = createApp(App)
app.config.globalProperties.$http = axios
app.config.globalProperties.$echarts = echarts

const pinia = createPinia();

// Font Awesome
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faGoogle } from "@fortawesome/free-brands-svg-icons";
import { faGithub } from "@fortawesome/free-brands-svg-icons";
library.add(faGoogle, faGithub);
app.component("font-awesome-icon", FontAwesomeIcon);

app.use(pinia);
app.use(router)
app.use(store)
app.use(utilsPlugin)

app.mount('#app')