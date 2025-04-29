<template>
  <div class="manage-page">
    <div class="page-header">
      <h1 class="page-title">Statistics</h1>
    </div>


  </div>
</template>

<script setup>
</script>

<style scoped>
.manage-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-lg);
  position: relative;
}

.page-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
}

@media (min-width: 768px) {
  .page-header {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
  }
}

.page-title {
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-bold);
  font-size: 1.75rem;
  color: var(--text-primary);
  margin: 0;
}
</style>