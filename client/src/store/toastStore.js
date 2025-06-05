/**
 * Toast Store using Pinia
 * Manages global toast notification state and display logic
 * Provides convenient methods for showing different types of messages
 */

import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useToastStore = defineStore('toast', () => {
  // Reactive state for toast component
  const visible = ref(false);    // Controls toast visibility
  const message = ref('');       // Toast message content
  const type = ref('info');      // Toast type (info, success, error)
  const duration = ref(3000);    // Auto-hide duration in milliseconds
  let timeoutId = null;          // Timeout reference for auto-hide

  /**
   * Show a toast notification with custom options
   * @param {Object} options - Toast configuration
   * @param {string} options.message - Message to display
   * @param {string} options.type - Toast type (info, success, error)
   * @param {number} options.duration - Auto-hide duration (0 = no auto-hide)
   */
  function showToast(options) {
    // Clear any existing auto-hide timers
    if (timeoutId) {
      clearTimeout(timeoutId);
    }

    // Configure toast properties with defaults
    message.value = options.message || 'Operation completed';
    type.value = options.type || 'info';
    duration.value = options.duration || 3000;
    
    // Make toast visible
    visible.value = true;
    
    // Set up auto-hide timer if duration is specified
    if (duration.value > 0) {
      timeoutId = setTimeout(() => {
        hideToast();
      }, duration.value);
    }
  }

  /**
   * Hide the current toast notification
   * Clears any active auto-hide timers
   */
  function hideToast() {
    visible.value = false;
    if (timeoutId) {
      clearTimeout(timeoutId);
      timeoutId = null;
    }
  }

  /**
   * Show a success toast notification
   * @param {string} msg - Success message to display
   * @param {Object} options - Additional toast options
   */
  function success(msg, options = {}) {
    showToast({
      message: msg,
      type: 'success',
      ...options
    });
  }

  /**
   * Show an error toast notification
   * @param {string} msg - Error message to display
   * @param {Object} options - Additional toast options
   */
  function error(msg, options = {}) {
    showToast({
      message: msg,
      type: 'error',
      ...options
    });
  }

  /**
   * Show an info toast notification
   * @param {string} msg - Info message to display
   * @param {Object} options - Additional toast options
   */
  function info(msg, options = {}) {
    showToast({
      message: msg,
      type: 'info',
      ...options
    });
  }

  // Return reactive state and methods for use in components
  return {
    // State
    visible,
    message,
    type,
    duration,
    
    // Methods
    showToast,
    hideToast,
    success,
    error,
    info
  };
}); 