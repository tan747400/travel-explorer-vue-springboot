<template>
    <div class="w-full max-w-4xl mx-auto">
      <label class="block w-full">
        <span class="text-sm text-gray-500 mb-2 block">{{ label }}</span>
        <input
          class="w-full border-b border-gray-300 focus:border-sky-400 outline-none py-3 text-lg md:text-xl"
          type="text"
          :placeholder="placeholder"
          :value="modelValue"
          @input="onInput"
        />
      </label>
    </div>
  </template>
  
  <script setup lang="ts">
  const props = withDefaults(
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
  
  function onInput(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    emit("update:modelValue", value);
  }
  </script>  