import { createApp } from 'vue'
import axios from 'axios'
import App from './App.vue'
import router from './router'
import store from './store.js'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.js'

axios.defaults.baseURL = 'http://localhost:5555'
axios.defaults.withCredentials = true

const app = createApp(App)
app.config.globalProperties.$http = axios

// Font Awesome
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faGoogle } from "@fortawesome/free-brands-svg-icons";
import { faGithub } from "@fortawesome/free-brands-svg-icons";
library.add(faGoogle, faGithub);
app.component("font-awesome-icon", FontAwesomeIcon);

app.use(router)
app.use(store)

app.mount('#app')