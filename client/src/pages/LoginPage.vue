<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 px-4">
    <div
      class="w-full max-w-md bg-white rounded-2xl shadow-lg px-6 py-7 space-y-5 border border-slate-200"
    >
      <h1 class="text-2xl font-bold text-center text-slate-800">
        เข้าสู่ระบบ
      </h1>

      <!-- Session Expired Banner -->
      <transition name="fade-slide">
        <div
          v-if="sessionExpired"
          class="relative overflow-hidden rounded-xl border border-amber-300 bg-amber-50 text-amber-800 px-4 py-3 text-sm flex items-start gap-3 shadow-sm"
        >
          <div class="w-1 bg-amber-400 rounded-full"></div>

          <div class="flex-1">
            <p class="font-semibold mb-0.5">เซสชั่นหมดอายุแล้ว</p>
            <p class="text-xs leading-snug">
              เพื่อความปลอดภัย ระบบได้ออกจากระบบให้อัตโนมัติ
              กรุณาเข้าสู่ระบบใหม่อีกครั้ง
            </p>
          </div>

          <span
            class="pointer-events-none absolute -right-6 top-0 h-full w-12 bg-gradient-to-l from-white/80 via-white/40 to-transparent opacity-0 banner-shine"
          />
        </div>
      </transition>

      <!-- Registered Success Banner -->
      <transition name="fade-slide">
        <div
          v-if="registeredSuccess"
          class="relative overflow-hidden rounded-xl border border-emerald-300 bg-emerald-50 text-emerald-800 px-4 py-3 text-sm flex items-start gap-3 shadow-sm"
        >
          <div class="w-1 bg-emerald-400 rounded-full"></div>

          <div class="flex-1">
            <p class="font-semibold mb-0.5">สมัครสมาชิกสำเร็จแล้ว</p>
            <p class="text-xs leading-snug">
              กรุณาเข้าสู่ระบบด้วยอีเมลและรหัสผ่านที่คุณสมัครไว้
            </p>
          </div>

          <span
            class="pointer-events-none absolute -right-6 top-0 h-full w-12 bg-gradient-to-l from-white/80 via-white/40 to-transparent opacity-0 banner-shine"
          />
        </div>
      </transition>

      <!-- Form / Skeleton -->
      <LoginSkeleton v-if="loading" />
      <form v-else class="space-y-4" @submit.prevent="handleSubmit">
        <div>
          <label class="block text-sm mb-1 text-slate-700">อีเมล</label>
          <input
            v-model="email"
            type="email"
            class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
            required
          />
        </div>

        <div>
          <label class="block text-sm mb-1 text-slate-700">รหัสผ่าน</label>
          <input
            v-model="password"
            type="password"
            class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
            required
          />
        </div>

        <p v-if="error" class="text-sm text-red-500">
          {{ error }}
        </p>

        <button
          type="submit"
          class="w-full bg-sky-600 hover:bg-sky-700 text-white py-2.5 rounded-lg text-sm font-medium disabled:opacity-60 disabled:cursor-not-allowed transition"
          :disabled="loading"
        >
          เข้าสู่ระบบ
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { login } from "@/services/authService";
import { useAuthStore } from "@/stores/authStore";
import { useToast } from "vue-toastification";
import LoginSkeleton from "@/components/state/LoginSkeleton.vue";

const router = useRouter();
const route = useRoute();
const toast = useToast();
const auth = useAuthStore();

const email = ref("");
const password = ref("");
const loading = ref(false);
const error = ref("");

// detect ?expired=1, ?registered=1
const sessionExpired = computed(() => route.query.expired === "1");
const registeredSuccess = computed(() => route.query.registered === "1");

onMounted(() => {
  // ไม่ต้อง toast error สำหรับ sessionExpired เพราะ useSessionExpired ทำไปแล้ว
  if (registeredSuccess.value) {
    toast.success("สมัครสมาชิกสำเร็จแล้ว กรุณาเข้าสู่ระบบ");
  }

  // ถ้า login อยู่แล้วและไม่มี redirect พิเศษ → ส่งกลับหน้าแรก
  if (auth.isLoggedIn && !route.query.redirect) {
    router.push({ name: "home" });
  }
});

interface AuthResponse {
  token: string;
  userId: number;
  email: string;
  displayName: string;
}

async function handleSubmit() {
  error.value = "";
  loading.value = true;

  try {
    const res = (await login({
      email: email.value.trim(),
      password: password.value,
    })) as AuthResponse;

    auth.setAuth(res);

    toast.success("เข้าสู่ระบบสำเร็จ 🎉");

    const redirect = (route.query.redirect as string) || null;

    if (redirect) {
      await router.push(redirect);
    } else {
      await router.push({ name: "home" });
    }
  } catch (err: any) {
    const msg =
      err?.response?.data?.message ||
      err?.message ||
      "เข้าสู่ระบบไม่สำเร็จ กรุณาลองอีกครั้ง";

    error.value = msg;
    toast.error(msg);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s ease-out;
}
.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* shine animation */
.banner-shine {
  animation: shine-move 1.6s ease-out 0.25s forwards;
}

@keyframes shine-move {
  0% {
    opacity: 0;
    transform: translateX(0);
  }
  30% {
    opacity: 1;
  }
  100% {
    opacity: 0;
    transform: translateX(-50%);
  }
}
</style>