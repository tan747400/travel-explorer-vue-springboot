import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../pages/HomePage.vue";
import TripDetailPage from "../pages/TripDetailPage.vue";
import LoginPage from "../pages/LoginPage.vue"; // 👈 เพิ่ม import

const routes = [
  { path: "/", name: "home", component: HomePage },
  { path: "/trips/:id", name: "trip-detail", component: TripDetailPage },
  { path: "/login", name: "login", component: LoginPage },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
