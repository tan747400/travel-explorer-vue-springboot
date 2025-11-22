<template>
  <div>
    <!-- Label -->
    <label
      class="block w-full max-w-6xl mx-auto text-gray-700 text-base mb-2 text-left"
    >
      {{ label }}
    </label>

    <!-- Input Search -->
    <input
      type="text"
      :value="modelValue"
      @input="onInput"
      :placeholder="placeholder"
      class="block w-full max-w-6xl mx-auto p-3 border-b-2 border-gray-400
             placeholder:text-center text-left"
    />

    <!-- ช่องให้รับแท็กที่คลิก -->
    <slot name="tag-list" :onTagSelected="onTagSelected"></slot>
  </div>
</template>

<script setup lang="ts">
/**
 * props
 * (ไม่ต้องเก็บในตัวแปร props เพื่อตัด warning unused variable)
 */
withDefaults(
  defineProps<{
    modelValue: string;
    label?: string;
    placeholder?: string;
  }>(),
  {
    label: "",
    placeholder: "",
  }
);

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
}>();

/**
 * handle text input typing
 */
function onInput(event: Event) {
  const value = (event.target as HTMLInputElement).value;
  emit("update:modelValue", value);
}

/**
 * handle when user clicked a tag
 * — เคลียร์ค่าเดิม แล้วใส่แท็กใหม่ทันที
 */
function onTagSelected(tag: string) {
  emit("update:modelValue", tag);
}
</script>