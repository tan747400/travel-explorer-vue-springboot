import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";

import "./style.css"; // ถ้าคุณมีไฟล์สไตล์อยู่แล้ว ให้คงบรรทัดนี้ไว้

const app = createApp(App);

// สร้าง instance ของ Pinia ก่อน
const pinia = createPinia();

// ต้อง use(pinia) ก่อน use(router)
app.use(pinia);
app.use(router);

app.mount("#app");