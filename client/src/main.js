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
import './assets/css/cyberpunk-theme.css'
import CyberpunkComponents from './components/theme/cyberpunk'
import * as echarts from 'echarts'

// 根据环境设置后端URL
const backendUrl = import.meta.env.DEV ? 
    (import.meta.env.VITE_API_URL || 'http://localhost:5555') : 
    'https://screenvault-server-production.up.railway.app';

console.log('Backend URL:', backendUrl);

// 配置axios全局设置
axios.defaults.baseURL = backendUrl;
axios.defaults.withCredentials = true; // 允许跨域请求携带凭证
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.defaults.headers.common['Accept'] = 'application/json';

// 请求拦截器 - 为每个请求添加必要的头部
axios.interceptors.request.use(function (config) {
    config.withCredentials = true;
    return config;
}, function (error) {
    console.error('Axios request error:', error);
    return Promise.reject(error);
});

// 响应拦截器 - 处理错误和CORS问题
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.message === 'Network Error') {
            console.error('Network Error - This might be a CORS issue:', error);
        }
        return Promise.reject(error);
    }
);

const app = createApp(App)
app.config.globalProperties.$http = axios
app.config.globalProperties.$echarts = echarts

const pinia = createPinia();

// Font Awesome
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faGoogle } from "@fortawesome/free-brands-svg-icons";
import { faGithub } from "@fortawesome/free-brands-svg-icons";
import { faStar, faHeart, faList, faHistory, faFilm, faEye } from "@fortawesome/free-solid-svg-icons";
library.add(faGoogle, faGithub, faStar, faHeart, faList, faHistory, faFilm, faEye);
app.component("font-awesome-icon", FontAwesomeIcon);

app.use(pinia);
app.use(router)
app.use(store)
app.use(utilsPlugin)
app.use(CyberpunkComponents)

// Add link to Google Fonts for cyberpunk theme
const linkElement = document.createElement('link');
linkElement.rel = 'stylesheet';
linkElement.href = 'https://fonts.googleapis.com/css2?family=Orbitron:wght@400;500;600;700&family=Rajdhani:wght@300;400;500;600;700&family=VT323&display=swap';
document.head.appendChild(linkElement);

app.mount('#app')