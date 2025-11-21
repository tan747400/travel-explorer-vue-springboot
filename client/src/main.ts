import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";

import "./style.css"; 


// Vue Toastification
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";

const toastOptions = {
  position: "top-right",
  timeout: 2500,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true,
  draggablePercent: 0.5,
  showCloseButtonOnHover: true,
  hideProgressBar: false,
  icon: true,
  transition: "Vue-Toastification__bounce",
  maxToasts: 5,
  newestOnTop: true,
};

const app = createApp(App);

// สร้าง instance ของ Pinia
const pinia = createPinia();

// ต้อง use(pinia) ก่อน router
app.use(pinia);
app.use(router);

// ติด Toastification
app.use(Toast, toastOptions);

// Mount แอป
app.mount("#app");