<template>
  <div class="modal-overlay" v-if="show" @click.self="close">
    <div class="modal-container">
      <div class="modal-header">
        <h2 class="modal-title">Create New List</h2>
        <button class="close-btn" @click="close">×</button>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label for="list-name" class="form-label">List Name</label>
          <input
              id="list-name"
              v-model="listName"
              type="text"
              class="form-input"
              placeholder="My Favorite Movies"
              @keyup.enter="createList"
          />
        </div>
      </div>
      <div class="modal-footer">
        <main-btn type="secondary" @click="close">Cancel</main-btn>
        <main-btn type="highlight" @click="createList" :disabled="!listName.trim()">
          Create
        </main-btn>
      </div>
    </div>
  </div>
</template>

<script>
import MainBtn from "../buttons/MainBtn.vue";

export default {
  name: 'AddListModal',
  components: {MainBtn},
  props: {
    show: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      listName: '',
    }
  },
  methods: {
    close() {
      this.$emit('close');
      this.resetForm();
    },
    createList() {
      if (!this.listName.trim()) return;
      const newList = {
        listName: this.listName.trim(),
        isDefault: false,
        createDate: new Date().toISOString()
      };
      this.$emit('create', newList);
      this.resetForm();
      this.close();
    },
    resetForm() {
      this.listName = '';
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
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.modal-container {
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  width: 90%;
  max-width: 500px;
  box-shadow: var(--shadow-level3-default);
  overflow: hidden;
}

.modal-header {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-xl);
  font-weight: var(--font-fontWeight-semibold);
  color: var(--text-primary);
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: var(--text-muted);
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.close-btn:hover {
  color: var(--text-primary);
}

.modal-body {
  padding: var(--spacing-lg);
}

.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-label {
  display: block;
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xs);
}

.form-input, .form-textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border-medium);
  border-radius: var(--border-radius-md);
  font-family: var(--font-fontFamily-primary);
  font-size: var(--font-fontSize-base);
  color: var(--text-primary);
  background-color: var(--background-base);
  transition: border-color 0.2s ease;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: var(--accent-info);
  box-shadow: 0 0 0 2px rgba(var(--accent-info-rgb), 0.2);
}

.modal-footer {
  padding: var(--spacing-lg);
  border-top: 1px solid var(--border-light);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
}
</style>