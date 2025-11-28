<template>
  <div class="min-h-screen flex flex-col bg-slate-50">
    <!-- Navbar -->
    <Navbar />

    <!-- Main content -->
    <main class="flex-1">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import Navbar from "@/components/Navbar.vue";
</script>

<style>
</style>
