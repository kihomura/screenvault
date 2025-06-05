<template>
  <div class="empty-state">
    <div class="empty-state-icon">{{ icon }}</div>
    <h3 class="empty-state-title">{{ title }}</h3>
    <p class="empty-state-message">
      {{ message }}
    </p>
  </div>
</template>

<script>
export default {
  name: 'EmptyState',
  props: {
    title: {
      type: String,
      default: 'Your Watching Recording Is Empty'
    },
    message: {
      type: String,
      default: 'Start tracking your watching journey by adding your first recording!'
    },
    icon: {
      type: String,
      default: '🎬'
    }
  }
}
</script>

<style scoped>
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);

  border-radius: var(--border-radius-lg);
  text-align: center;
}

.empty-state-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-md);
}

.empty-state-title {
  color: var(--text-primary);
  font-size: var(--font-fontSize-xl);
  margin-bottom: var(--spacing-md);
}

.empty-state-message {
  color: var(--text-secondary);
  max-width: 400px;
}
</style>