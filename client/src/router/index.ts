import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../pages/HomePage.vue";
import TripDetailPage from "../pages/TripDetailPage.vue";

const routes = [
  { path: "/", name: "home", component: HomePage },
  { path: "/trips/:id", name: "trip-detail", component: TripDetailPage },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;