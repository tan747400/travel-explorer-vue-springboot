<template>
  <div>
    <label
      class="block w-full max-w-6xl mx-auto text-gray-700 text-base mb-2 text-left"
    >
      {{ label }}
    </label>

    <input
      type="text"
      :value="modelValue"
      @input="onInput"
      :placeholder="placeholder"
      class="block w-full max-w-6xl mx-auto p-3 border-b-2 border-gray-400 placeholder:text-center text-left"
    />
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