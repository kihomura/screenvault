<template>
  <button
      class="back-button"
      @click="handleBackClick"
      :aria-label="ariaLabel || 'Go back'"
  >
    <span v-if="!$slots.default" class="back-icon">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="20" height="20">
        <path d="M19 12H5M12 19l-7-7 7-7"></path>
      </svg>
    </span>
    <slot></slot>
  </button>
</template>

<script>
export default {
  name: 'BackButton',
  props: {
    // route to navigate to when clicked
    to: {
      type: [String, Object],
      default: null
    },
    steps: {
      type: Number,
      default: 1
    },
    ariaLabel: {
      type: String,
      default: 'Go back'
    }
  },
  methods: {
    handleBackClick() {
      if (this.to) {
        this.$router.push(this.to);
      } else {
        this.$router.go(-this.steps);
      }
    }
  }
}
</script>

<style scoped>
.back-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 2.5rem;
  min-width: 2.5rem;
  padding: 0 var(--spacing-md);
  font-family: var(--font-fontFamily-primary),serif;
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
  background-color: var(--background-subtle);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-level1-default);
  transition: all 0.2s ease-in-out;
  cursor: pointer;
}

.back-button:hover {
  background-color: var(--interactive-hover);
  box-shadow: var(--shadow-level1-hover);
  color: var(--text-primary);
  border-color: var(--border-medium);
}

.back-button:active {
  background-color: var(--interactive-active);
  box-shadow: var(--shadow-level1-default);
  transform: translateY(1px);
}

.back-button:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(var(--accent-info-rgb), 0.4);
}

.back-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon + * {
  margin-left: var(--spacing-sm);
}
</style>