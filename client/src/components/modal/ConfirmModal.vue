<template>
  <div v-if="visible" class="modal-overlay" @click.self="handleCancel">
    <div class="modal-container">
      <div class="modal-header">
        <h3>{{ title }}</h3>
        <button class="close-button" @click="handleCancel" aria-label="Close">
          <span>&times;</span>
        </button>
      </div>

      <div class="modal-content">
        <p>{{ message }}</p>
      </div>

      <div class="modal-actions">
        <main-btn type="secondary"
            @click="handleCancel">
          {{ cancelText }}
        </main-btn>
        <main-btn type="danger"
            :class="confirmButtonType"
            @click="handleConfirm">
          {{ confirmText }}
        </main-btn>
      </div>
    </div>
  </div>
</template>

<script>
import MainBtn from "../buttons/MainBtn.vue";

export default {
  name: 'ConfirmModal',
  components: {MainBtn},
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: 'Confirm'
    },
    message: {
      type: String,
      required: true
    },
    confirmText: {
      type: String,
      default: 'Confirm'
    },
    cancelText: {
      type: String,
      default: 'Cancel'
    },
    type: {
      type: String,
      default: 'danger', // 'danger', 'warning', 'info', 'success'
      validator: value => ['danger', 'warning', 'info', 'success'].includes(value)
    }
  },
  computed: {
    confirmButtonType() {
      return {
        'danger': this.type === 'danger',
        'warning': this.type === 'warning',
        'info': this.type === 'info',
        'success': this.type === 'success'
      }
    }
  },
  methods: {
    handleConfirm() {
      this.$emit('confirm');
    },
    handleCancel() {
      this.$emit('cancel');
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(var(--primary-dark-rgb), 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  backdrop-filter: blur(3px);
}

.modal-container {
  width: 100%;
  max-width: 400px;
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level3-default);
  overflow: hidden;
  animation: modal-appear 0.2s ease-out;
}

@keyframes modal-appear {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-lg) var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
}

.modal-header h3 {
  margin: 0;
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-semibold);
  font-size: var(--font-fontSize-lg);
  color: var(--text-primary);
}

.close-button {
  background: transparent;
  border: none;
  font-size: var(--font-fontSize-xl);
  color: var(--text-muted);
  cursor: pointer;
  padding: var(--spacing-xs);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-full);
  transition: background-color 0.2s ease;
}

.close-button:hover {
  background-color: var(--interactive-hover);
  color: var(--text-primary);
}

.modal-content {
  padding: var(--spacing-lg);
}

.modal-content p {
  margin: 0;
  font-size: var(--font-fontSize-base);
  line-height: var(--font-lineHeight-normal);
  color: var(--text-secondary);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  padding: var(--spacing-md) var(--spacing-lg) var(--spacing-lg);
  gap: var(--spacing-sm);
}

button {
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--border-radius-md);
  font-family: var(--font-fontFamily-primary);
  font-weight: var(--font-fontWeight-medium);
  font-size: var(--font-fontSize-sm);
  transition: background-color 0.2s ease, transform 0.1s ease;
  cursor: pointer;
}

button:active {
  transform: scale(0.98);
}

.cancel-button {
  background-color: var(--background-muted);
  border: 1px solid var(--border-light);
  color: var(--text-secondary);
}

.cancel-button:hover {
  background-color: var(--interactive-hover);
}

.confirm-button {
  color: white;
  border: none;
}

.danger {
  background-color: var(--accent-error);
}

.danger:hover {
  background-color: rgba(var(--accent-error-rgb), 0.9);
}

.warning {
  background-color: var(--accent-warning);
}

.warning:hover {
  background-color: rgba(var(--accent-warning-rgb), 0.9);
}

.info {
  background-color: var(--accent-info);
}

.info:hover {
  background-color: rgba(var(--accent-info-rgb), 0.9);
}

.success {
  background-color: var(--accent-success);
}

.success:hover {
  background-color: rgba(var(--accent-success-rgb), 0.9);
}

@media (max-width: 480px) {
  .modal-container {
    max-width: 90%;
  }

  .modal-actions {
    flex-direction: column;
  }

  .cancel-button, .confirm-button {
    width: 100%;
    padding: var(--spacing-md);
  }
}
</style>