<template>
  <button
      :class="[
      'main-btn',
      `main-btn--${type}`,
      { 'main-btn--loading': loading },
      { 'main-btn--disabled': disabled },
      { 'main-btn--block': block },
      `main-btn--${size}`
    ]"
      :disabled="disabled || loading"
      @click="handleClick"
  >
    <span v-if="loading" class="main-btn__loading-icon">
      <slot name="loading">
        <span class="loading-spinner"></span>
      </slot>
    </span>
    <span v-if="icon && !loading" class="main-btn__icon">
      <slot name="icon"></slot>
    </span>
    <span class="main-btn__text">
      <slot></slot>
    </span>
  </button>
</template>

<script>
export default {
  name: 'MainBtn',
  props: {
    type: {
      type: String,
      default: 'primary',
      validator: (value) => {
        return ['primary', 'secondary', 'tertiary', 'danger', 'warning', 'info', 'success', 'highlight', 'text'].includes(value);
      }
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => {
        return ['small', 'medium', 'large'].includes(value);
      }
    },
    loading: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    block: {
      type: Boolean,
      default: false
    },
    icon: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    handleClick(event) {
      if (this.disabled || this.loading) return;
      this.$emit('click', event);
    }
  }
}
</script>

<style scoped>
.main-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  font-family: var(--font-fontFamily-primary);
  font-weight: var(--font-fontWeight-medium);
  border-radius: var(--border-radius-md);
  transition: all 0.2s ease-in-out;
  cursor: pointer;
  text-align: center;
  white-space: nowrap;
  border: none;
  outline: none;
  position: relative;
  overflow: hidden;
}

/* Size variations */
.main-btn--small {
  font-size: var(--font-fontSize-xs);
  padding: var(--spacing-xs) var(--spacing-md);
  height: 32px;
}

.main-btn--medium {
  font-size: var(--font-fontSize-sm);
  padding: var(--spacing-sm) var(--spacing-lg);
  height: 40px;
}

.main-btn--large {
  font-size: var(--font-fontSize-base);
  padding: var(--spacing-md) var(--spacing-xl);
  height: 48px;
}

/* Block button */
.main-btn--block {
  display: flex;
  width: 100%;
}

/* Primary button */
.main-btn--primary {
  background-color: var(--primary);
  color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
}

.main-btn--primary:hover:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: var(--primary-dark);
  box-shadow: var(--shadow-level1-hover);
}

.main-btn--primary:active:not(.main-btn--disabled):not(.main-btn--loading) {
  transform: translateY(1px);
  box-shadow: var(--shadow-level1-default);
}

/* Secondary button */
.main-btn--secondary {
  background-color: var(--background-subtle);
  color: var(--text-primary);
  border: 1px solid var(--border-light);
}

.main-btn--secondary:hover:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: var(--background-muted);
  border-color: var(--border-medium);
}

.main-btn--secondary:active:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: var(--interactive-active);
  transform: translateY(1px);
}

/* Tertiary button */
.main-btn--tertiary {
  background-color: transparent;
  color: var(--text-secondary);
}

.main-btn--tertiary:hover:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: var(--interactive-hover);
  color: var(--text-primary);
}

.main-btn--tertiary:active:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: var(--interactive-active);
  transform: translateY(1px);
}

/* Danger button */
.main-btn--danger {
  background-color: var(--accent-error);
  color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
}

.main-btn--danger:hover:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: rgba(var(--accent-error-rgb), 0.85);
  box-shadow: var(--shadow-level1-hover);
}

.main-btn--danger:active:not(.main-btn--disabled):not(.main-btn--loading) {
  transform: translateY(1px);
  box-shadow: var(--shadow-level1-default);
}

/* Warning button */
.main-btn--warning {
  background-color: var(--accent-warning);
  color: var(--text-primary);
  box-shadow: var(--shadow-level1-default);
}

.main-btn--warning:hover:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: rgba(var(--accent-warning-rgb), 0.85);
  box-shadow: var(--shadow-level1-hover);
}

.main-btn--warning:active:not(.main-btn--disabled):not(.main-btn--loading) {
  transform: translateY(1px);
  box-shadow: var(--shadow-level1-default);
}

/* Info button */
.main-btn--info {
  background-color: var(--accent-info);
  color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
}

.main-btn--info:hover:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: rgba(var(--accent-info-rgb), 0.85);
  box-shadow: var(--shadow-level1-hover);
}

.main-btn--info:active:not(.main-btn--disabled):not(.main-btn--loading) {
  transform: translateY(1px);
  box-shadow: var(--shadow-level1-default);
}

/* Success button */
.main-btn--success {
  background-color: var(--accent-success);
  color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
}

.main-btn--success:hover:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: rgba(var(--accent-success-rgb), 0.85);
  box-shadow: var(--shadow-level1-hover);
}

.main-btn--success:active:not(.main-btn--disabled):not(.main-btn--loading) {
  transform: translateY(1px);
  box-shadow: var(--shadow-level1-default);
}

/* Highlight button */
.main-btn--highlight {
  background-color: var(--highlight);
  color: #fff;
  box-shadow: var(--shadow-level2-default);
  position: relative;
  z-index: 1;
  overflow: hidden;
  display: inline-block;
  border: none;
  outline: none;
  font-family: inherit;
  text-decoration: none;
  text-transform: uppercase;
  cursor: pointer;
  transition: all 150ms ease-out;
  letter-spacing: 1px;
}

.main-btn--highlight::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--highlight);
  z-index: -1;
  transition: opacity 0.3s ease;
  opacity: 0;
  transform: scale(1.05);
}

.main-btn--highlight:hover:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: var(--highlight);
  box-shadow: 0 0 0 0.1875rem white,
  0 0 0 0.375rem var(--highlight);
}

.main-btn--highlight:hover:not(.main-btn--disabled):not(.main-btn--loading)::before {
  opacity: 1;
}

.main-btn--highlight:active:not(.main-btn--disabled):not(.main-btn--loading) {
  transform: translateY(1px);
  box-shadow: var(--shadow-level2-default);
  background-color: var(--highlight);
  box-shadow: 0 0 0 0.1875rem var(--highlight),
  0 0 0 0.375rem var(--highlight);
  transition-duration: 75ms;
}

.main-btn--highlight::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.3) 0%, rgba(255,255,255,0) 60%);
  opacity: 0;
  transform: scale(0);
  transition: transform 0.5s ease-out, opacity 0.5s ease-out;
}

.main-btn--highlight:hover:not(.main-btn--disabled):not(.main-btn--loading)::after {
  opacity: 1;
  transform: scale(1);
  animation: highlight-ripple 1.5s infinite;
}

@keyframes highlight-ripple {
  0% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  100% {
    transform: scale(1.2);
    opacity: 0;
  }
}

/* Text button */
.main-btn--text {
  background-color: transparent;
  color: var(--accent-info);
  padding: var(--spacing-xs) var(--spacing-sm);
  height: auto;
}

.main-btn--text:hover:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: rgba(var(--accent-info-rgb), 0.1);
}

.main-btn--text:active:not(.main-btn--disabled):not(.main-btn--loading) {
  background-color: rgba(var(--accent-info-rgb), 0.2);
}

/* Disabled state */
.main-btn--disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

/* Loading state */
.main-btn--loading {
  cursor: wait;
}

.main-btn__loading-icon {
  margin-right: var(--spacing-sm);
}

.main-btn__icon {
  margin-right: var(--spacing-sm);
}

.loading-spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid currentColor;
  border-radius: 50%;
  border-right-color: transparent;
  animation: button-spinner 0.75s linear infinite;
}

@keyframes button-spinner {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>