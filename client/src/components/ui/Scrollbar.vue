<template>
  <div :class="['custom-scrollbar', customClass]" :style="computedStyle">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'CustomScrollbar',
  props: {
    maxHeight: {
      type: String,
      default: 'auto'
    },
    customClass: {
      type: String,
      default: ''
    }
  },
  computed: {
    computedStyle() {
      return {
        maxHeight: this.maxHeight,
        overflowY: 'auto'
      }
    }
  }
}
</script>

<style scoped>
.custom-scrollbar {
  scrollbar-width: thin; /* Firefox */
  scrollbar-color: var(--tertiary) var(--background-subtle);
}

/* Chrome, Edge, Safari */
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: var(--background-subtle);
  border-radius: var(--border-radius-full);
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: var(--tertiary);
  border-radius: var(--border-radius-full);
}
</style>
