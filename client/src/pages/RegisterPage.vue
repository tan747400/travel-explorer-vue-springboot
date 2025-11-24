<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 px-4">
    <div
      class="w-full max-w-md bg-white rounded-2xl shadow px-6 py-7 space-y-5 border border-slate-200"
    >
      <h1 class="text-2xl font-bold text-center text-slate-800">
        สมัครสมาชิก
      </h1>

      <!-- ฟอร์มสมัครสมาชิก / Skeleton -->
      <RegisterSkeleton v-if="loading" />
      <form
        v-else
        @submit.prevent="handleRegister"
        class="space-y-4"
      >
        <!-- Display Name -->
        <div>
          <label class="block text-sm mb-1 text-slate-700">
            ชื่อที่แสดง <span class="text-red-500">*</span>
          </label>
          <input
            v-model="displayName"
            type="text"
            class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm 
                   focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
            placeholder="เช่น Traveler123"
            required
          />
        </div>

        <!-- Email -->
        <div>
          <label class="block text-sm mb-1 text-slate-700">
            อีเมล <span class="text-red-500">*</span>
          </label>
          <input
            v-model="email"
            type="email"
            class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm
                   focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
            placeholder="you@example.com"
            required
          />
        </div>

        <!-- Password -->
        <div>
          <label class="block text-sm mb-1 text-slate-700">
            รหัสผ่าน <span class="text-red-500">*</span>
          </label>
          <input
            v-model="password"
            type="password"
            class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm
                   focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
            placeholder="อย่างน้อย 8 ตัวอักษร"
            required
          />
          <p class="text-xs text-slate-500 mt-1">
            แนะนำให้มีทั้งตัวอักษรและตัวเลข
          </p>
        </div>

        <!-- Confirm Password -->
        <div>
          <label class="block text-sm mb-1 text-slate-700">
            ยืนยันรหัสผ่าน <span class="text-red-500">*</span>
          </label>
          <input
            v-model="confirmPassword"
            type="password"
            class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm
                   focus:ring-2 focus:ring-sky-400 focus:border-sky-400"
            placeholder="กรอกรหัสผ่านซ้ำ"
            required
          />
        </div>

        <!-- Error message -->
        <p v-if="error" class="text-sm text-red-500">
          {{ error }}
        </p>

        <!-- Submit -->
        <button
          type="submit"
          class="w-full bg-sky-600 hover:bg-sky-700 text-white py-2.5 rounded-lg 
                 text-sm font-medium disabled:opacity-60 disabled:cursor-not-allowed transition"
          :disabled="loading"
        >
          สมัครสมาชิก
        </button>
      </form>

      <p class="text-sm text-center mt-2 text-slate-600">
        มีบัญชีอยู่แล้ว?
        <router-link class="text-sky-600 hover:underline" to="/login">
          เข้าสู่ระบบ
        </router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { register } from "@/services/authService";
import RegisterSkeleton from "@/components/state/RegisterSkeleton.vue";

const router = useRouter();
const toast = useToast();

const displayName = ref("");
const email = ref("");
const password = ref("");
const confirmPassword = ref("");

const loading = ref(false);
const error = ref("");

// helper เช็ก email format
function isValidEmail(val: string): boolean {
  return /\S+@\S+\.\S+/.test(val);
}

async function handleRegister() {
  error.value = "";

  const display = displayName.value.trim();
  const mail = email.value.trim();
  const pass = password.value.trim();
  const pass2 = confirmPassword.value.trim();

  // === Validation ฝั่งหน้าเว็บ ===
  if (!display) {
    error.value = "กรุณากรอกชื่อที่แสดง";
    return;
  }

  if (!isValidEmail(mail)) {
    error.value = "รูปแบบอีเมลไม่ถูกต้อง";
    return;
  }

  if (pass.length < 8) {
    error.value = "รหัสผ่านต้องยาวอย่างน้อย 8 ตัวอักษร";
    return;
  }

  if (pass !== pass2) {
    error.value = "รหัสผ่านและยืนยันรหัสผ่านไม่ตรงกัน";
    return;
  }

  loading.value = true;

  try {
    await register({
      displayName: display,
      email: mail,
      password: pass,
    });

    toast.success("สมัครสมาชิกสำเร็จ 🎉 กรุณาเข้าสู่ระบบ");

    // สมัครสำเร็จ → ไปหน้า Login
    router.push({
      name: "login",
      query: { registered: "1" },
    });
  } catch (err: any) {
    console.error(err);

    const msg =
      err?.response?.data?.message ||
      err?.message ||
      "สมัครสมาชิกไม่สำเร็จ กรุณาลองใหม่อีกครั้ง";

    error.value = msg;
    toast.error(msg);
  } finally {
    loading.value = false;
  }
}
</script>