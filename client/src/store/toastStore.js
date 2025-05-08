import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useToastStore = defineStore('toast', () => {
  const visible = ref(false);
  const message = ref('');
  const type = ref('info');
  const duration = ref(3000);
  let timeoutId = null;

  function showToast(options) {
    // Clear any existing timers
    if (timeoutId) {
      clearTimeout(timeoutId);
    }

    // Set toast properties
    message.value = options.message || 'Operation completed';
    type.value = options.type || 'info';
    duration.value = options.duration || 3000;
    
    // Show toast
    visible.value = true;
    
    // Auto-hide toast after duration
    if (duration.value > 0) {
      timeoutId = setTimeout(() => {
        hideToast();
      }, duration.value);
    }
  }

  function hideToast() {
    visible.value = false;
    if (timeoutId) {
      clearTimeout(timeoutId);
      timeoutId = null;
    }
  }

  function success(msg, options = {}) {
    showToast({
      message: msg,
      type: 'success',
      ...options
    });
  }

  function error(msg, options = {}) {
    showToast({
      message: msg,
      type: 'error',
      ...options
    });
  }

  function info(msg, options = {}) {
    showToast({
      message: msg,
      type: 'info',
      ...options
    });
  }

  return {
    visible,
    message,
    type,
    duration,
    showToast,
    hideToast,
    success,
    error,
    info
  };
}); 