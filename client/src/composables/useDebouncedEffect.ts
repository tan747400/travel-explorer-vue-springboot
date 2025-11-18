import { watch, type WatchSource } from "vue";

/**
 * ใช้ debounce effect เวลา deps เปลี่ยน (คล้าย useDebouncedEffect ใน React)
 *
 * @param effect ฟังก์ชันที่จะถูกเรียกหลังจาก debounce
 * @param deps   array ของ ref / getter ที่ใช้ watch
 * @param delay  หน่วงเวลา (ms)
 */
export function useDebouncedEffect(
  effect: () => void | Promise<void>,
  deps: WatchSource<unknown>[],
  delay = 300
) {
  let timer: number | undefined;

  watch(
    deps,
    () => {
      if (timer) window.clearTimeout(timer);
      timer = window.setTimeout(() => {
        effect();
      }, delay);
    },
    { immediate: true } // เรียกครั้งแรกตอน mount เหมือน useEffect 
  );
}