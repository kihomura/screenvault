<template>
  <div class="manage-page">
    <div class="page-header">
      <div class="header-content">
        <h2>Manage {{ displayName }}</h2>
      </div>
      <div class="header-actions">
        <!-- Actions can be added here if needed -->
      </div>
    </div>

    <!-- tag management -->
    <div v-if="section === 'tags'">
      <TagManagement />
    </div>

    <!-- custom content management -->
    <div v-else-if="section === 'custom-content'">
      <CustomContentManagement />
    </div>

  </div>
</template>

<script setup>
import {computed} from 'vue';
import {useRoute} from 'vue-router';
import TagManagement from '../components/business/content/management/tagManagement.vue';
import CustomContentManagement from '../components/business/content/management/CustomContentManagement.vue';

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

/* Removing redundant page-header styles since they're defined globally now */

.page-title {
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-bold);
  font-size: 1.75rem;
  color: var(--text-primary);
  margin: 0;
}
</style>