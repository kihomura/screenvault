<template>
  <div class="content-info-card">
    <div class="content-header">
      <h2 class="content-title">{{ content.title }}</h2>
      <h3 v-if="content.otherTitle" class="content-subtitle">{{ content.otherTitle }}</h3>
    </div>

    <div class="content-info-container">
      <div class="content-image-container">
        <img :src="imgPrefix + content.image" :alt="content.title" class="content-image" />
      </div>

      <div class="content-info">
        <div class="info-row">
          <span class="info-label">Release Date:</span>
          <span class="info-value">{{ formatDate(content.releaseDate) }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Country:</span>
          <span class="info-value">{{ content.country }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Language:</span>
          <span class="info-value">{{ content.language }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Genre:</span>
          <span class="info-value">{{ content.genre }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Category:</span>
          <span class="info-value">{{ content.category }}</span>
        </div>
      </div>
    </div>

    <div class="content-description">
      <h3>Description</h3>
      <div class="description-content">
        <p>{{ content.description }}</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ContentInfoCard',
  props: {
    content: {
      type: Object,
      required: true
    },
    imgPrefix: {
      type: String,
      default: ''
    }
  },
  methods: {
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return date.toLocaleDateString();
    }
  }
}
</script>

<style scoped>
.content-info-card {
  flex: 1;
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-level2-default);
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.3s ease;
}

.content-info-card:hover {
  box-shadow: var(--shadow-level2-hover);
}

.content-header {
  margin-bottom: var(--spacing-lg);
}

.content-title {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-xl);
  font-weight: var(--font-fontWeight-bold);
  margin: 0 0 var(--spacing-sm) 0;
  color: var(--primary-dark);
}

.content-subtitle {
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
  margin: 0;
}

.content-info-container {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.content-image-container {
  width: 45%;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-level1-default);
  position: relative;
}

.content-image {
  width: 100%;
  height: auto;
  display: block;
  transition: transform 0.3s ease;
}

.content-image:hover {
  transform: scale(1.02);
}

.content-info {
  width: 55%;
  display: flex;
  flex-direction: column;
  padding: var(--spacing-sm) 0;
}

.info-row {
  display: flex;
  margin-bottom: var(--spacing-sm);
  line-height: var(--font-lineHeight-normal);
}

.info-label {
  flex: 0 0 120px;
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
}

.info-value {
  flex: 1;
  color: var(--text-primary);
  word-break: break-word;
  padding-right: var(--spacing-xs);
}

.content-description {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-top: 1px solid var(--border-light);
  padding-top: var(--spacing-md);
}

.content-description h3 {
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-semibold);
  margin: 0 0 var(--spacing-md) 0;
  color: var(--primary-dark);
}

.description-content {
  flex: 1;
  overflow-y: auto;
  max-height: 200px;
  scrollbar-width: thin;
  scrollbar-color: var(--border-medium) transparent;
  padding-right: var(--spacing-sm);
}

.description-content p {
  line-height: var(--font-lineHeight-relaxed);
  color: var(--text-secondary);
  margin: 0;
}

.description-content::-webkit-scrollbar {
  width: 5px;
}

.description-content::-webkit-scrollbar-track {
  background: transparent;
}

.description-content::-webkit-scrollbar-thumb {
  background-color: var(--border-medium);
  border-radius: var(--border-radius-full);
}

@media (max-width: 768px) {
  .content-info-container {
    flex-direction: column;
  }

  .content-image-container,
  .content-info {
    width: 100%;
  }

  .content-image-container {
    margin-bottom: var(--spacing-lg);
  }
}
</style>