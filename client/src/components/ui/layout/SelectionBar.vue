<template>
  <div class="selection-actions-bar">
    <div class="selection-info">
      <span class="selected-count">{{ selectedCount }} items selected</span>
      <main-btn type="text"
                class="selection-control-btn select-all-btn ms-2"
                @click="$emit('select-all')"
                v-if="selectedCount < totalCount"
      >
        Select All
      </main-btn>
    </div>

    <div class="selection-controls">

      <main-btn type="primary" v-if="showAddToList" class="action-btn add-to-list-btn" @click="$emit('add-to-list')">
        Add to List
      </main-btn>
      <main-btn type="danger" class="action-btn delete-btn" @click="$emit('confirm-delete')">
        Delete
      </main-btn>
      <main-btn type="secondary" class="selection-control-btn cancel-btn" @click="$emit('cancel')">
        Cancel
      </main-btn>
    </div>
  </div>
</template>

<script>
import MainBtn from "../../buttons/MainBtn.vue";

export default {
  name: 'SelectionActionsBar',
  components: {MainBtn},
  props: {
    selectedCount: {
      type: Number,
      required: true
    },
    totalCount: {
      type: Number,
      required: true
    },
    showAddToList: {
      type: Boolean,
      default: true
    }
  }
}
</script>

<style scoped>
.selection-actions-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-xl);
  box-shadow: var(--shadow-level1-default);
  border-left: 4px solid var(--accent-info);
  flex-wrap: wrap;
  gap: var(--spacing-lg);
}

.selection-info {
  display: flex;
  align-items: center;
}

.selected-count {
  font-weight: var(--font-fontWeight-semibold);
  color: var(--text-primary);
  background-color: rgba(var(--accent-info-rgb), 0.1);
  padding: 4px 10px;
  border-radius: var(--border-radius-full);
  font-size: var(--font-fontSize-sm);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: 8px 14px;
  border-radius: var(--border-radius-md);
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-medium);
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-to-list-btn {
  background-color: var(--background-base);
  color: var(--accent-info);
  border: 1px solid var(--accent-info);
}

.add-to-list-btn:hover {
  background-color: rgba(var(--accent-info-rgb), 0.1);
}

.delete-btn {
  background-color: rgba(var(--accent-error-rgb), 0.1);
  color: var(--accent-error);
}

.delete-btn:hover {
  background-color: rgba(var(--accent-error-rgb), 0.2);
}

.selection-controls {
  display: flex;
  gap: var(--spacing-md);
}

.selection-control-btn {
  padding: 6px 12px;
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: var(--font-fontSize-sm);
  cursor: pointer;
  border-radius: var(--border-radius-md);
  transition: all 0.2s ease;
}

.selection-control-btn:hover {
  background-color: var(--interactive-hover);
  color: var(--text-primary);
}

.select-all-btn {
  font-weight: var(--font-fontWeight-medium);
  color: var(--accent-info);
}

.cancel-btn {
  border: 1px solid var(--border-medium);
}
</style>