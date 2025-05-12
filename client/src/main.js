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

// 在开发环境中使用baseURL，生产环境使用相对路径
// 这样请求会发送到前端域名，然后由nginx代理到后端
const isDev = import.meta.env.DEV;
const backendUrl = isDev ? (import.meta.env.VITE_API_URL || 'http://localhost:5555') : '';
const backendDomain = 'https://screenvault-server-production.up.railway.app';

console.log('环境:', isDev ? '开发环境' : '生产环境');
console.log('Backend URL:', backendUrl);

// 配置axios全局设置
axios.defaults.baseURL = backendUrl; // 生产环境使用相对路径
axios.defaults.withCredentials = true; // 允许跨域请求携带凭证
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.defaults.headers.common['Accept'] = 'application/json';

// 请求拦截器 - 为每个请求添加必要的头部并确保使用相对路径
axios.interceptors.request.use(function (config) {
    // 确保使用withCredentials
    config.withCredentials = true;
    
    // 强制移除硬编码的绝对URL（在生产环境中）
    if (!isDev && config.url) {
        // 如果URL以后端域名开头，则转换为相对路径
        if (config.url.startsWith(backendDomain)) {
            config.url = config.url.replace(backendDomain, '');
            console.log(`将绝对URL转换为相对路径: ${config.url}`);
        }
    }
    
    console.log(`发送请求: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
}, function (error) {
    console.error('Axios请求错误:', error);
    return Promise.reject(error);
});

// 响应拦截器 - 处理错误和CORS问题
axios.interceptors.response.use(
    response => {
        console.log(`请求成功: ${response.config.method?.toUpperCase()} ${response.config.url}`);
        return response;
    },
    error => {
        console.error(`请求失败: ${error.config?.method?.toUpperCase()} ${error.config?.url}`, error);
        if (error.message === 'Network Error') {
            console.error('网络错误 - 可能是CORS问题:', error);
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