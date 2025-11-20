import { createRouter, createWebHistory } from "vue-router";

// Pages
import HomePage from "@/pages/HomePage.vue";
import TripDetailPage from "@/pages/TripDetailPage.vue";
import LoginPage from "@/pages/LoginPage.vue";
import RegisterPage from "@/pages/RegisterPage.vue";
import DashboardPage from "@/pages/DashboardPage.vue";
import CreateTripPage from "@/pages/CreateTripPage.vue";

// Pinia Store
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
    meta: { requiresAuth: true },
  },

  // ⭐ Route ใหม่สำหรับ "สร้างทริป"
  {
    path: "/dashboard/create",
    name: "trip-create",
    component: CreateTripPage,
    meta: { requiresAuth: true },
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

  // ต้องล็อกอินก่อนเข้า (Dashboard + Create)
  if (to.meta.requiresAuth && !isLoggedIn) {
    return next({
      name: "login",
      query: { redirect: to.fullPath },
    });
  }

  // ไม่ให้เข้าหน้า login/register ถ้าล็อกอินแล้ว
  if ((to.name === "login" || to.name === "register") && isLoggedIn) {
    return next({ name: "home" });
  }

  next();
});

export default router;