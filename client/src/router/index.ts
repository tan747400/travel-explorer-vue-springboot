import { createRouter, createWebHistory } from "vue-router";

// Pages
import HomePage from "../pages/HomePage.vue";
import TripDetailPage from "../pages/TripDetailPage.vue";
import LoginPage from "../pages/LoginPage.vue";
import RegisterPage from "@/pages/RegisterPage.vue";
import DashboardPage from "@/pages/DashboardPage.vue";
// Pinia auth store
import { useAuthStore } from "@/stores/authStore";

const routes = [
  { path: "/", name: "home", component: HomePage },
  { path: "/trips/:id", name: "trip-detail", component: TripDetailPage },

  { path: "/login", name: "login", component: LoginPage },
  { path: "/register", name: "register", component: RegisterPage },

  {
    path: "/dashboard",
    name: "dashboard",
    component: DashboardPage,
    meta: { requiresAuth: true }, // ต้องล็อกอินก่อนเข้า
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// =====================
// Global Navigation Guard
// =====================

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  const isLoggedIn = !!authStore.token;

  // หน้าที่ต้องล็อกอิน แต่ user ยังไม่ล็อกอิน
  if (to.meta.requiresAuth && !isLoggedIn) {
    return next({
      name: "login",
      query: { redirect: to.fullPath }, // login เสร็จจะได้กลับมาหน้านี้
    });
  }

  // ถ้าล็อกอินแล้ว แต่จะเข้า login หรือ register → redirect กลับ home
  if ((to.name === "login" || to.name === "register") && isLoggedIn) {
    return next({ name: "home" });
  }

  next();
});

export default router;