<template>
  <div class="modal-overlay" @click.self="cancel">
    <div class="delete-modal">
      <div class="delete-modal-header">
        <h3 class="delete-modal-title">Delete Recordings</h3>
        <button class="close-modal-btn" @click="cancel">×</button>
      </div>
      <div class="delete-modal-content">
        <p class="delete-modal-message">
          Are you sure you want to delete {{ count }} recording<span v-if="count > 1">s</span>?
          <br>
          <span class="delete-warning">This action cannot be undone.</span>
        </p>
      </div>
      <div class="delete-modal-actions">
        <button class="cancel-delete-btn" @click="cancel">Cancel</button>
        <button class="confirm-delete-btn" @click="confirm">Delete</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DeleteModal',
  props: {
    count: {
      type: Number,
      default: 0
    }
  },
  methods: {
    cancel() {
      this.$emit('cancel');
    },
    confirm() {
      this.$emit('confirm');
    }
  }
}
</script>

<style scoped>

/* Delete Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.delete-modal {
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level3-default);
  width: 90%;
  max-width: 450px;
  overflow: hidden;
  animation: modalSlideUp 0.3s ease;
}

@keyframes modalSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.delete-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-lg) var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
}

.delete-modal-title {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-semibold);
  color: var(--text-primary);
  margin: 0;
}

.close-modal-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: var(--text-secondary);
  cursor: pointer;
  line-height: 1;
  padding: 0;
  margin-left: var(--spacing-lg);
}

.close-modal-btn:hover {
  color: var(--text-primary);
}

.delete-modal-content {
  padding: var(--spacing-lg);
}

.delete-modal-message {
  font-size: var(--font-fontSize-base);
  color: var(--text-secondary);
  text-align: center;
  margin: 0;
  line-height: var(--font-lineHeight-relaxed);
}

.delete-warning {
  color: var(--accent-error);
  font-weight: var(--font-fontWeight-medium);
  display: inline-block;
  margin-top: var(--spacing-md);
}

.delete-modal-actions {
  display: flex;
  justify-content: flex-end;
  padding: var(--spacing-md) var(--spacing-lg);
  border-top: 1px solid var(--border-light);
  gap: var(--spacing-md);
}

.cancel-delete-btn {
  background-color: var(--background-subtle);
  border: 1px solid var(--border-medium);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-sm) var(--spacing-xl);
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-delete-btn:hover {
  background-color: var(--interactive-hover);
  color: var(--text-primary);
}

.confirm-delete-btn {
  background-color: var(--accent-error);
  border: 1px solid transparent;
  border-radius: var(--border-radius-md);
  padding: var(--spacing-sm) var(--spacing-xl);
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-medium);
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
}

.confirm-delete-btn:hover {
  background-color: rgba(var(--accent-error-rgb), 0.9);
}

</style>
