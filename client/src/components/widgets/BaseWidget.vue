<template>
  <div
      class="widget"
      :class="[`widget-${size}`, { 'widget-loading': loading, 'widget-drag-handle': isEditMode }]"
      :style="customStyle"
  >
    <!-- header -->
    <div class="widget-header">
      <div class="widget-title">
        <slot name="icon">
          <div v-if="icon" class="widget-icon">
            <i :class="icon"></i>
          </div>
        </slot>
        <h3>{{ title }}</h3>
      </div>
      <div class="widget-actions">
        <slot name="actions">
        </slot>
      </div>
    </div>

    <!-- content -->
    <div class="widget-content">
      <!-- loading state -->
      <div v-if="loading" class="widget-loader">
        <div class="loader-spinner"></div>
        <div class="loader-text">Loading...</div>
      </div>
      <!-- loading error state -->
      <div v-else-if="error" class="widget-error">
        <div class="error-icon">!</div>
        <div class="error-message">{{ errorMessage || 'Error when loading' }}</div>
        <button class="retry-button" @click="handleRefresh">Retry</button>
      </div>
      <!-- empty state -->
      <div v-else-if="empty" class="widget-empty">
        <div class="empty-icon">
          <i class="icon-empty"></i>
        </div>
        <div class="empty-message">{{ emptyMessage || 'No data yet' }}</div>
        <slot name="empty-action"></slot>
      </div>
      <!-- content from sub components -->
      <div v-else class="widget-body">
        <slot></slot>
      </div>
    </div>

    <!-- footer -->
    <div v-if="$slots.footer" class="widget-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BaseWidget',
  props: {
    id: {
      type: String,
      required: true
    },
    title: {
      type: String,
      default: 'Widget'
    },
    icon: {
      type: String,
      default: ''
    },
    size: {
      type: String,
      default: 'medium', // small, medium, large
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    },
    refreshable: {
      type: Boolean,
      default: true
    },
    customStyle: {
      type: Object,
      default: () => ({})
    },
    loading: {
      type: Boolean,
      default: false
    },
    error: {
      type: Boolean,
      default: false
    },
    errorMessage: {
      type: String,
      default: ''
    },
    empty: {
      type: Boolean,
      default: false
    },
    emptyMessage: {
      type: String,
      default: ''
    },
    isEditMode: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      refreshTimer: null
    }
  },
  mounted() {
    this.setupAutoRefresh();
  },
  beforeUnmount() {
    this.clearRefreshTimer();
  },
  methods: {
    handleRefresh() {
      if (this.loading) return;
      this.$emit('refresh');
    },
    setupAutoRefresh() {
      this.clearRefreshTimer();
      // Fixed refresh interval of 10 minutes
      this.refreshTimer = setInterval(() => {
        this.handleRefresh();
      }, 10 * 60 * 1000);
    },
    clearRefreshTimer() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer);
        this.refreshTimer = null;
      }
    },
  }
}
</script>

<style scoped>
.widget {
  display: flex;
  flex-direction: column;
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level1-default);
  transition: box-shadow 0.3s ease, transform 0.3s ease;
  overflow: hidden;
  height: 100%;
  font-family: var(--font-fontFamily-primary);
}

.widget:hover {
  box-shadow: var(--shadow-level1-hover);
}

.widget-drag-handle {
  cursor: move !important;
  cursor: grab !important;
}

.widget-drag-handle:active {
  cursor: grabbing !important;
}

.widget-drag-handle * {
  cursor: inherit !important;
}

.widget-drag-handle .widget-content,
.widget-drag-handle .widget-footer {
  pointer-events: none !important;
}

/* 确保操作按钮不触发拖拽 */
.widget-actions {
  cursor: default;
  pointer-events: auto !important;
}

/* Widget sizes */
.widget-small {
  min-height: 150px;
}

.widget-medium {
  min-height: 250px;
}

.widget-large {
  min-height: 350px;
}

/* Widget Header */
.widget-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
}

.widget-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--text-primary);
}

.widget-title h3 {
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-medium);
  margin: 0;
}

.widget-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
}

.widget-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.widget-action-button {
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  width: 30px;
  height: 30px;
  border-radius: var(--border-radius-full);
  cursor: pointer;
  color: var(--text-secondary);
  transition: background-color 0.2s ease;
}

.widget-action-button:hover {
  background-color: var(--interactive-hover);
  color: var(--text-primary);
}

.widget-action-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Widget Content */
.widget-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: auto;
  padding: var(--spacing-lg);
}

.widget-body {
  height: 100%;
}

/* Loading state */
.widget-loading {
  opacity: 0.7;
}

.widget-loader {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
}

.loader-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-light);
  border-top: 3px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--spacing-md);
}

.loader-text {
  color: var(--text-secondary);
  font-size: var(--font-fontSize-sm);
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Error state */
.widget-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  padding: var(--spacing-lg);
}

.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  border-radius: var(--border-radius-full);
  background-color: var(--accent-error);
  color: white;
  font-size: var(--font-fontSize-xl);
  font-weight: var(--font-fontWeight-bold);
  margin-bottom: var(--spacing-md);
}

.error-message {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-lg);
}

.retry-button {
  padding: var(--spacing-sm) var(--spacing-lg);
  background-color: var(--accent-info);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  cursor: pointer;
  font-weight: var(--font-fontWeight-medium);
  transition: background-color 0.2s ease;
}

.retry-button:hover {
  background-color: rgba(var(--accent-info-rgb), 0.9);
}

/* Empty state */
.widget-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  padding: var(--spacing-lg);
  color: var(--text-muted);
}

.empty-icon {
  font-size: 2rem;
  margin-bottom: var(--spacing-md);
  opacity: 0.5;
}

.empty-message {
  margin-bottom: var(--spacing-lg);
}

/* Widget footer */
.widget-footer {
  padding: var(--spacing-md) var(--spacing-lg);
  border-top: 1px solid var(--border-light);
}
</style>