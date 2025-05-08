<template>
  <div class="sort-controls">
    <label for="sort-by" class="sort-label">Sort by:</label>
    <div class="custom-select">
      <select
          id="sort-by"
          :value="sortBy"
          @change="$emit('update:sortBy', $event.target.value)"
          class="sort-select"
      >
        <option 
          v-for="option in availableSortOptions" 
          :key="option.value" 
          :value="option.value"
        >
          {{ option.label }}
        </option>
      </select>
      <div class="select-arrow">▼</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SortControls',
  props: {
    sortBy: {
      type: String,
      required: true
    },
    sortType: {
      type: String,
      default: 'content',
      validator: (value) => ['content', 'record'].includes(value)
    }
  },
  computed: {
    availableSortOptions() {
      if (this.sortType === 'record') {
        // Options for watched page (watchDate and rating)
        return [
          { value: 'date-desc', label: 'Date (Newest First)' },
          { value: 'date-asc', label: 'Date (Oldest First)' },
          { value: 'rating-desc', label: 'Rating (High to Low)' },
          { value: 'rating-asc', label: 'Rating (Low to High)' }
        ];
      } else {
        // Options for playlist, wishlist, customContentManagement
        return [
          { value: 'added-desc', label: 'Recently Added' },
          { value: 'added-asc', label: 'Oldest Added' },
          { value: 'release-desc', label: 'Newest Released' },
          { value: 'release-asc', label: 'Oldest Released' }
        ];
      }
    }
  }
}
</script>

<style scoped>
.sort-controls {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.sort-label {
  font-size: var(--font-fontSize-sm);
  color: var(--text-secondary);
  white-space: nowrap;
}

.custom-select {
  position: relative;
  min-width: 160px;
}

.sort-select {
  appearance: none;
  background-color: var(--background-subtle);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-sm) var(--spacing-md) var(--spacing-sm) var(--spacing-md);
  font-size: var(--font-fontSize-sm);
  color: var(--text-primary);
  cursor: pointer;
  width: 100%;
  transition: all 0.2s ease;
}

.sort-select:hover {
  border-color: var(--border-medium);
  background-color: var(--interactive-hover);
}

.sort-select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2);
}

.select-arrow {
  position: absolute;
  right: var(--spacing-md);
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  font-size: 10px;
  color: var(--text-muted);
}
</style> 