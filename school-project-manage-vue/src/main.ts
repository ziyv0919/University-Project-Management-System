import { createApp } from 'vue';
import { createPinia } from 'pinia';
import router from '@/router/index';
import 'normalize.css';
// tailwind模块样式文件
import './tailwind.css';
import App from '@/App.vue';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import 'element-plus/dist/index.css';

const app = createApp(App);

const pinia = createPinia();

pinia.use(piniaPluginPersistedstate);

app.use(pinia);

app.use(router);

app.mount('#app');
