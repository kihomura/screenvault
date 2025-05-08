<template>
  <div class="filter-controls">
    <!-- Rating filter -->
    <div class="filter-dropdown">
      <label for="rating-filter">Filter by rating:</label>
      <div class="custom-select">
        <select
            id="rating-filter"
            :value="ratingFilter"
            @change="$emit('update:ratingFilter', $event.target.value)"
            class="filter-select"
        >
          <option value="all">All ratings</option>
          <option value="10-plus">10</option>
          <option value="9-plus">9+</option>
          <option value="8-plus">8+</option>
          <option value="7-plus">7+</option>
          <option value="6-plus">6+</option>
          <option value="5-plus">5+</option>
          <option value="4-plus">4+</option>
          <option value="3-plus">3+</option>
          <option value="2-plus">2+</option>
        </select>
        <div class="select-arrow">▼</div>
      </div>
    </div>

    <!-- Year filter -->
    <div class="filter-dropdown">
      <label for="year-filter">Year:</label>
      <div class="custom-select">
        <select
            id="year-filter"
            :value="yearFilter"
            @change="$emit('update:yearFilter', $event.target.value)"
            class="filter-select"
        >
          <option value="all">All years</option>
          <option v-for="year in availableYears" :key="year" :value="year">
            {{ year }}
          </option>
        </select>
        <div class="select-arrow">▼</div>
      </div>
    </div>

    <!-- Month filter -->
    <div class="filter-dropdown">
      <label for="month-filter">Month:</label>
      <div class="custom-select">
        <select
            id="month-filter"
            :value="monthFilter"
            @change="$emit('update:monthFilter', $event.target.value)"
            class="filter-select"
            :disabled="yearFilter === 'all'"
        >
          <option value="all">All months</option>
          <option v-for="month in months" :key="month.value" :value="month.value">
            {{ month.label }}
          </option>
        </select>
        <div class="select-arrow">▼</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FilterControls',
  props: {
    ratingFilter: {
      type: String,
      required: true
    },
    yearFilter: {
      type: String,
      required: true
    },
    monthFilter: {
      type: String,
      required: true
    },
    availableYears: {
      type: Array,
      required: true
    },
    months: {
      type: Array,
      required: true
    }
  }
}
</script>

<style scoped>
.filter-controls {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.filter-dropdown {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.filter-dropdown label {
  font-size: var(--font-fontSize-sm);
  color: var(--text-secondary);
  white-space: nowrap;
}

.custom-select {
  position: relative;
  min-width: 140px;
}

.filter-select {
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

.filter-select:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.filter-select:hover:not(:disabled) {
  border-color: var(--border-medium);
  background-color: var(--interactive-hover);
}

.filter-select:focus {
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