import { ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/authStore";

export function useSessionExpired() {
  const router = useRouter();
  const route = useRoute();
  const toast = useToast();
  const auth = useAuthStore();

  // กันไม่ให้เด้ง toast / redirect ซ้ำ ๆ จากหลาย request พร้อมกัน
  const isHandling = ref(false);

  function handleSessionExpired(message?: string) {
    if (isHandling.value) return;
    isHandling.value = true;

    // 1) เคลียร์ state การล็อกอิน
    auth.logout();

    // 2) แจ้งเตือนผู้ใช้
    toast.error(message || "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง");

    // 3) พาไปหน้า login + จำ path เดิมไว้ใน query "redirect"
    router
      .push({
        name: "login",
        query: {
          expired: "1",
          redirect: route.fullPath,
        },
      })
      .finally(() => {
        // ปล่อยให้ handle ใหม่ได้ในอนาคต
        isHandling.value = false;
      });
  }

  return { handleSessionExpired };
}