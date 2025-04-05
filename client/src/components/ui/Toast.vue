<template>
  <div class="toast" :class="{ 'show': show }">
    <div class="toast-content" :class="type">
      <p>{{ message }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Toast',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    message: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'info'
    }
  }
}
</script>

<style scoped>
.toast {
  position: fixed;
  top: 20px; /* 距离顶部 20px */
  right: 20px; /* 距离右边 20px */
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  background-color: var(--background-base);
  box-shadow: var(--shadow-level2-default);
  transition: opacity 0.3s ease, visibility 0.3s ease;
  z-index: 10000;
  opacity: 0;
  visibility: hidden;
}

.toast.show {
  opacity: 1;
  visibility: visible;
}

.toast-content {
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--border-radius-sm);
}

.toast-content.success {
  background-color: var(--accent-success);
  color: white;
}

.toast-content.error {
  background-color: var(--accent-error);
  color: white;
}

.toast-content.info {
  background-color: var(--accent-info);
  color: white;
}
</style>
