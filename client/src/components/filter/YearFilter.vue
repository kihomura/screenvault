<template>
  <div class="year-filter">
    <div class="filter-label">Filter by Year:</div>
    <div class="select-container">
      <select
          class="year-select"
          :value="selectedYear"
          @change="$emit('year-change', $event.target.value)"
      >
        <option value="">All Years</option>
        <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
      </select>
      <div class="select-arrow"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'YearFilter',
  props: {
    availableYears: {
      type: Array,
      required: true
    },
    selectedYear: {
      type: [String, Number],
      default: ''
    }
  }
};
</script>

<style scoped>
.year-filter {
  display: flex;
  align-items: center;
}

.filter-label {
  margin-right: var(--spacing-md);
  font-size: var(--font-fontSize-sm);
  color: var(--text-secondary);
}

.select-container {
  position: relative;
}

.year-select {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  background-color: var(--background-subtle);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  color: var(--text-primary);
  cursor: pointer;
  font-family: var(--font-fontFamily-primary);
  font-size: var(--font-fontSize-sm);
  padding: var(--spacing-sm) var(--spacing-xl) var(--spacing-sm) var(--spacing-md);
  transition: all 0.2s ease;
  min-width: 120px;
}

.year-select:hover {
  border-color: var(--border-medium);
}

.year-select:focus {
  outline: none;
  border-color: var(--accent-info);
  box-shadow: 0 0 0 2px rgba(var(--accent-info-rgb), 0.2);
}

.select-arrow {
  position: absolute;
  right: var(--spacing-md);
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid var(--text-secondary);
  pointer-events: none;
}
</style>