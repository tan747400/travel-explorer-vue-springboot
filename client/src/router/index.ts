import { createRouter, createWebHistory } from "vue-router";

// Pages
import HomePage from "@/pages/HomePage.vue";
import TripDetailPage from "@/pages/TripDetailPage.vue";
import LoginPage from "@/pages/LoginPage.vue";
import RegisterPage from "@/pages/RegisterPage.vue";
import DashboardPage from "@/pages/DashboardPage.vue";
import CreateTripPage from "@/pages/CreateTripPage.vue";
import EditTripPage from "@/pages/EditTripPage.vue";
import ProfilePage from "@/pages/ProfilePage.vue"; 

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
    path: "/profile",
    name: "profile",
    component: ProfilePage,
    meta: { requiresAuth: true }, // ⭐ ต้อง login เท่านั้น
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

  // ต้อง login
  if (to.meta.requiresAuth && !isLoggedIn) {
    return next({
      name: "login",
      query: {
        expired: "1",
        redirect: to.fullPath,
      },
    });
  }

  // ถ้า login แล้ว ไม่ให้เข้าหน้า login/register
  if (to.meta.guestOnly && isLoggedIn) {
    return next({ name: "home" });
  }

  next();
});

export default router;