<template>
  <button
      class="delete-button"
      v-bind="$attrs"
      @click="handleClick"
      :disabled="disabled"
      :class="{ 'disabled': disabled }"
      :type="type"
  >
    <svg
        class="cross-icon"
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
    >
      <line x1="18" y1="6" x2="6" y2="18"></line>
      <line x1="6" y1="6" x2="18" y2="18"></line>
    </svg>
    <slot></slot>
  </button>
</template>

<script>
export default {
  name: 'DeleteButton',
  inheritAttrs: false,
  props: {
    disabled: {
      type: Boolean,
      default: false
    },
    type: {
      type: String,
      default: 'button',
      validator: value => ['button', 'submit', 'reset'].includes(value)
    }
  },
  emits: ['click'],
  methods: {
    handleClick(event) {
      if (!this.disabled) {
        event.stopPropagation();
        this.$emit('click', event);
      }
    }
  }
}
</script>

<style scoped>
.delete-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #ff4d4f;
  border: none;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0;
  position: relative;
}

.delete-button:hover {
  background-color: #ff7875;
}

.delete-button:active {
  background-color: #d9363e;
}

.delete-button.disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

.cross-icon {
  width: 16px;
  height: 16px;
}

.delete-button.small {
  width: 24px;
  height: 24px;
}

.delete-button.large {
  width: 48px;
  height: 48px;
}
</style>