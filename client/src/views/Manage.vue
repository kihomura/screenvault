<template>
  <div class="manage-page">
    <div class="page-header">
      <h1 class="page-title">Manage {{ displayName }}</h1>
    </div>

    <!-- tag management -->
    <div v-if="section === 'tags'">
      <TagManagement />
    </div>

    <!-- custom content management -->
    <div v-else-if="section === 'custom-content'">
      <div class="placeholder-message">
        <h3>Custom Content Management</h3>
        <p>This section is under development.</p>
      </div>
    </div>

  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import TagManagement from '../components/tagManagement.vue';

const route = useRoute();
const section = computed(() => route.params.section);
const displayName = computed(() => {
  if (section.value === 'tags') return 'Tags';
  if (section.value === 'custom-content') return 'Custom Content';
  return '';
});
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