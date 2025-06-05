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
  bottom: 20px; /* 改为底部 */
  right: 20px;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  transition: opacity 0.3s ease, visibility 0.3s ease;
  z-index: 10000;
  opacity: 0;
  visibility: hidden;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.toast.show {
  opacity: 1;
  visibility: visible;
}

.toast-content {
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--border-radius-sm);
}

/* 针对 light 主题 */
.theme-light .toast {
  background-color: rgba(255, 255, 255, 0.7);
  box-shadow: var(--shadow-level2-default);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.theme-light .toast-content.success {
  background-color: rgba(76, 175, 80, 0.7);
  color: white;
}

.theme-light .toast-content.error {
  background-color: rgba(244, 67, 54, 0.7);
  color: white;
}

.theme-light .toast-content.info {
  background-color: rgba(33, 150, 243, 0.7);
  color: white;
}

/* 针对 dark 主题 */
.theme-dark .toast {
  background-color: rgba(18, 18, 18, 0.7);
  box-shadow: var(--shadow-level2-default);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.theme-dark .toast-content.success {
  background-color: rgba(102, 187, 106, 0.7);
  color: white;
}

.theme-dark .toast-content.error {
  background-color: rgba(239, 83, 80, 0.7);
  color: white;
}

.theme-dark .toast-content.info {
  background-color: rgba(66, 165, 245, 0.7);
  color: white;
}

/* 针对 cyberpunk 主题 */
.theme-cyberpunk .toast {
  background-color: rgba(0, 0, 0, 0.8);
  box-shadow: 0 0 10px var(--secondary), 0 0 20px rgba(var(--secondary-rgb), 0.3);
  border: 1px solid var(--secondary);
  clip-path: polygon(
    0 10px, 
    10px 0, 
    calc(100% - 10px) 0, 
    100% 10px, 
    100% calc(100% - 10px), 
    calc(100% - 10px) 100%, 
    10px 100%, 
    0 calc(100% - 10px)
  );
}

.theme-cyberpunk .toast-content.success {
  background-color: transparent;
  color: var(--accent-success);
  text-shadow: 0 0 5px var(--accent-success);
  border-left: 3px solid var(--accent-success);
}

.theme-cyberpunk .toast-content.error {
  background-color: transparent;
  color: var(--accent-error);
  text-shadow: 0 0 5px var(--accent-error);
  border-left: 3px solid var(--accent-error);
}

.theme-cyberpunk .toast-content.info {
  background-color: transparent;
  color: var(--secondary);
  text-shadow: 0 0 5px var(--secondary);
  border-left: 3px solid var(--secondary);
}
</style>
