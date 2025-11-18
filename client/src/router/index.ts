import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../pages/HomePage.vue";

const routes = [
  { path: "/", name: "home", component: HomePage },
  // อนาคต /trips/:id 
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
