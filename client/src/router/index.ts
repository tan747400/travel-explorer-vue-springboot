import { createRouter, createWebHistory } from "vue-router";

// Pages
import HomePage from "@/pages/HomePage.vue";
import TripDetailPage from "@/pages/TripDetailPage.vue";
import LoginPage from "@/pages/LoginPage.vue";
import RegisterPage from "@/pages/RegisterPage.vue";
import DashboardPage from "@/pages/DashboardPage.vue";
import CreateTripPage from "@/pages/CreateTripPage.vue";
import EditTripPage from "@/pages/EditTripPage.vue";

// Pinia Store
import { useAuthStore } from "@/stores/authStore";

const routes = [
  {
    path: "/",
    name: "home",
    component: HomePage,
  },

  {
    path: "/trips/:id",
    name: "trip-detail",
    component: TripDetailPage,
    props: true,
  },

  // ---------------------
  // Auth
  // ---------------------
  {
    path: "/login",
    name: "login",
    component: LoginPage,
    meta: { guestOnly: true },
  },
  {
    path: "/register",
    name: "register",
    component: RegisterPage,
    meta: { guestOnly: true },
  },

  // ---------------------
  // Protected Routes
  // ---------------------
  {
    path: "/dashboard",
    name: "dashboard",
    component: DashboardPage,
    meta: { requiresAuth: true },
  },
  {
    path: "/trips/create",
    name: "trip-create",
    component: CreateTripPage,
    meta: { requiresAuth: true },
  },
  {
    path: "/trips/:id/edit",
    name: "trip-edit",
    component: EditTripPage,
    props: true,
    meta: { requiresAuth: true },
  },

  // Fallback
  {
    path: "/:pathMatch(.*)*",
    redirect: { name: "home" },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// =============================
// Global Navigation Guard
// =============================
router.beforeEach((to, from, next) => {
  const auth = useAuthStore();
  const isLoggedIn = !!auth.token;

  // ต้อง login ก่อนเข้า (Dashboard, CreateTrip, EditTrip)
  if (to.meta.requiresAuth && !isLoggedIn) {
    return next({
      name: "login",
      query: {
        expired: "1",          // ให้ login.vue แสดง "เซสชั่นหมดอายุ"
        redirect: to.fullPath, // กลับมาหน้าเดิมหลัง login
      },
    });
  }

  // ถ้า login แล้ว ไม่ให้กลับไป login/register
  if (to.meta.guestOnly && isLoggedIn) {
    return next({ name: "home" });
  }

  next();
});

export default router;