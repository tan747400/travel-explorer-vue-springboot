<template>
  <div class="min-h-screen bg-slate-50">
    <!-- ==== Navbar อยู่ทุกหน้า ==== -->
    <header class="border-b bg-white">
      <div class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
        <div class="font-bold text-xl text-sky-700">
          Travel Explorer
        </div>

        <nav class="flex items-center gap-3">
          <button
            type="button"
            class="text-sm px-3 py-1.5 rounded-md border border-sky-500 text-sky-600 hover:bg-sky-50"
          >
            Login
          </button>

          <button
            type="button"
            class="text-sm px-3 py-1.5 rounded-md bg-sky-600 text-white hover:bg-sky-700"
          >
            Register
          </button>
        </nav>
      </div>
    </header>

    <!-- ==== ตรงนี้คือเนื้อหาแต่ละหน้า ==== -->
    <router-view />
  </div>
</template>

<script setup>
</script>