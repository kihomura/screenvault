<template>
  <div class="modal-overlay" @click.self="cancel">
    <div class="add-to-list-modal">
      <div class="add-to-list-modal-header">
        <h3 class="add-to-list-modal-title">Add to List</h3>
        <button class="close-modal-btn" @click="cancel">×</button>
      </div>
      <div class="add-to-list-modal-content">
        <div class="lists-container">
          <div class="existing-lists">
            <h4 class="list-section-title">Your Lists</h4>
            <div class="empty-lists-message">
              <span class="empty-list-icon">📋</span>
              <p>No lists created yet</p>
            </div>
            <!-- 如果你已有列表数据，可循环渲染 -->
          </div>

          <div class="create-new-list">
            <h4 class="list-section-title">Create New List</h4>
            <div class="new-list-form">
              <input
                  type="text"
                  class="new-list-input"
                  placeholder="Enter list name"
                  v-model="newListName"
              >
              <button class="create-list-btn"
                      :disabled="!newListName.trim()"
                      @click="create">
                Create
              </button>
            </div>
          </div>
        </div>
      </div>
      <div class="add-to-list-modal-actions">
        <button class="cancel-add-btn" @click="cancel">Cancel</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AddToListModal',
  data() {
    return {
      newListName: ''
    }
  },
  methods: {
    cancel() {
      this.$emit('cancel');
    },
    create() {
      // 触发创建事件，同时把新列表名称传递出去
      this.$emit('create', this.newListName);
      this.newListName = '';
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
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

/* Add to List Modal */
.add-to-list-modal {
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level3-default);
  width: 90%;
  max-width: 500px;
  overflow: hidden;
  animation: modalSlideUp 0.3s ease;
}

.add-to-list-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-lg) var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
}

.add-to-list-modal-title {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-semibold);
  color: var(--text-primary);
  margin: 0;
}

.add-to-list-modal-content {
  padding: var(--spacing-lg);
}

.lists-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.list-section-title {
  font-size: var(--font-fontSize-base);
  color: var(--text-primary);
  margin: 0 0 var(--spacing-md);
  font-weight: var(--font-fontWeight-semibold);
}

.empty-lists-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xl) 0;
  color: var(--text-muted);
  text-align: center;
}

.empty-list-icon {
  font-size: 2rem;
  margin-bottom: var(--spacing-md);
}

.new-list-form {
  display: flex;
  gap: var(--spacing-md);
}

.new-list-input {
  flex: 1;
  padding: var(--spacing-md);
  border: 1px solid var(--border-medium);
  border-radius: var(--border-radius-md);
  font-size: var(--font-fontSize-sm);
  transition: all 0.2s ease;
}

.new-list-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2);
}

.create-list-btn {
  background-color: var(--primary);
  border: none;
  color: white;
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--border-radius-md);
  font-weight: var(--font-fontWeight-medium);
  cursor: pointer;
  transition: all 0.2s ease;
}

.create-list-btn:hover:not(:disabled) {
  background-color: var(--primary-dark);
}

.create-list-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.add-to-list-modal-actions {
  display: flex;
  justify-content: flex-end;
  padding: var(--spacing-md) var(--spacing-lg);
  border-top: 1px solid var(--border-light);
}

.cancel-add-btn {
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

.cancel-add-btn:hover {
  background-color: var(--interactive-hover);
  color: var(--text-primary);
}

</style>
