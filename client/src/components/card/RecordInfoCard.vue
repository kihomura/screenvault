<template>
  <div class="recording-info-card">
    <div class="record-header">
      <h3>Your Watching Recording</h3>
      <main-btn type="text" class="" @click="$emit('edit')">Edit</main-btn>
    </div>

    <div class="record-info">
      <div class="info-row">
        <span class="info-label">Watch Date:</span>
        <span class="info-value">{{ formatDate(userContent.watchDate) }}</span>
      </div>

      <div class="info-row rating-row">
        <span class="info-label">Rating:</span>
        <div class="rating-display">
          <span class="rating-value">{{ userContent.rate }}/10</span>
          <div class="stars-container">
            <span
                v-for="index in 10"
                :key="index"
                class="star"
                :class="{
                'filled': index <= Math.floor(userContent.rate),
                'half-filled': index === Math.floor(userContent.rate) + 1 && hasHalfStar(userContent.rate),
                'empty': index > Math.floor(userContent.rate) + (hasHalfStar(userContent.rate) ? 1 : 0)
              }"
            >★</span>
          </div>
        </div>
      </div>
    </div>

    <div class="review-section">
      <h3>Review</h3>
      <div class="review-content">
        <div v-if="userContent.review && userContent.review.length" class="review-list">
          <div v-for="(reviewItem, index) in userContent.review" :key="index" class="review-item">
            <p>{{ reviewItem.text }}</p>
            <p class="review-date">{{ formatDate(reviewItem.date) }}</p>
          </div>
        </div>
        <div v-else class="no-review">
          No reviews added yet
        </div>
      </div>
    </div>

    <div class="tags-section">
      <h3>Tags</h3>
      <div class="tags-container">
        <div v-if="tags.length" class="tag-list">
          <span v-for="tag in tags" :key="tag.id" class="tag-item">
            {{ tag.tagName }}
          </span>
        </div>
        <div v-else class="no-tags">
          No tags added yet
        </div>
      </div>
    </div>

    <div class="lists-section">
      <h3>Lists</h3>
      <div class="lists-container">
        <div v-if="lists.length" class="list-items">
          <div v-for="list in lists" :key="list.id" class="list-item">
            {{ list.listName }}
          </div>
        </div>
        <div v-else class="no-lists">
          Not added to any lists yet
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MainBtn from "../buttons/MainBtn.vue";

export default {
  name: 'RecordingInfoCard',
  components: {MainBtn},
  props: {
    userContent: {
      type: Object,
      required: true
    },
    tags: {
      type: Array,
      default: () => []
    },
    lists: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return date.toLocaleDateString();
    },
    hasHalfStar(rating) {
      return (rating % 1) >= 0.5;
    }
  }
}
</script>

<style scoped>
.recording-info-card {
  flex: 1;
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-level2-default);
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.3s ease;
}

.recording-info-card:hover {
  box-shadow: var(--shadow-level2-hover);
}

.record-header {
  margin-bottom: var(--spacing-lg);
  position: relative;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.record-header h3 {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-xl);
  font-weight: var(--font-fontWeight-bold);
  color: var(--primary-dark);
  margin: 0 0 var(--spacing-md) 0;
  padding-bottom: var(--spacing-xs);
  position: relative;
}

.record-info {
  margin-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
  padding-bottom: var(--spacing-lg);
}

.info-row {
  display: flex;
  margin-bottom: var(--spacing-sm);
  line-height: var(--font-lineHeight-normal);
  align-items: center;
}

.info-label {
  flex: 0 0 100px;
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
}

.info-value {
  flex: 1;
  color: var(--text-primary);
}

.rating-row {
  align-items: flex-start;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.rating-value {
  font-weight: var(--font-fontWeight-bold);
  color: var(--accent-warning);
}

.stars-container {
  display: flex;
  line-height: 1;
}

.star {
  font-size: var(--font-fontSize-lg);
  display: inline-block;
  transition: color 0.2s ease;
  color: var(--border-medium);
}

.star.filled {
  color: var(--accent-warning);
}

.star.half-filled {
  position: relative;
  color: var(--border-medium);
}

.star.half-filled::before {
  content: "★";
  position: absolute;
  left: 0;
  top: 0;
  width: 50%;
  overflow: hidden;
  color: var(--accent-warning);
}

.stars-container:hover .star {
  transform: scale(1.05);
}

/* Review section */
.review-section {
  margin-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
  padding-bottom: var(--spacing-lg);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.review-section h3 {
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-semibold);
  margin: 0 0 var(--spacing-md) 0;
  color: var(--primary-dark);
}

.review-content {
  flex: 1;
  overflow-y: auto;
  max-height: 200px;
  scrollbar-width: thin;
  scrollbar-color: var(--border-medium) transparent;
  padding-right: var(--spacing-sm);
}

.review-content::-webkit-scrollbar {
  width: 5px;
}

.review-content::-webkit-scrollbar-track {
  background: transparent;
}

.review-content::-webkit-scrollbar-thumb {
  background-color: var(--border-medium);
  border-radius: var(--border-radius-full);
}

.review-item {
  background-color: var(--background-muted);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  position: relative;
}

.review-item p:first-child {
  margin-top: 0;
  line-height: var(--font-lineHeight-relaxed);
}

.review-date {
  margin-bottom: 0;
  font-size: var(--font-fontSize-sm);
  color: var(--text-muted);
  text-align: right;
  font-style: italic;
}

.no-review {
  font-style: italic;
  color: var(--text-muted);
}

/* Tags section */
.tags-section {
  margin-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
  padding-bottom: var(--spacing-lg);
}

.tags-section h3 {
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-semibold);
  margin: 0 0 var(--spacing-md) 0;
  color: var(--primary-dark);
}

.tags-container {
  overflow-x: auto;
  scrollbar-width: thin;
  scrollbar-color: var(--border-medium) transparent;
  padding-bottom: var(--spacing-xs);
}

.tags-container::-webkit-scrollbar {
  height: 5px;
}

.tags-container::-webkit-scrollbar-track {
  background: transparent;
}

.tags-container::-webkit-scrollbar-thumb {
  background-color: var(--border-medium);
  border-radius: var(--border-radius-full);
}

.tag-list {
  display: flex;
  flex-wrap: nowrap;
  gap: var(--spacing-sm);
  padding: var(--spacing-xs) 0;
}

.tag-item {
  background-color: var(--background-muted);
  color: var(--text-secondary);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-full);
  font-size: var(--font-fontSize-sm);
  transition: all 0.2s ease;
  white-space: nowrap;
}

.tag-item:hover {
  background-color: var(--accent-info);
  color: var(--background-base);
}

.no-tags, .no-lists {
  font-style: italic;
  color: var(--text-muted);
  padding: var(--spacing-xs) 0;
}

/* Lists section */
.lists-section {
  margin-bottom: var(--spacing-lg);
}

.lists-section h3 {
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-semibold);
  margin: 0 0 var(--spacing-md) 0;
  color: var(--primary-dark);
}

.lists-container {
  overflow-x: auto;
  scrollbar-width: thin;
  scrollbar-color: var(--border-medium) transparent;
  padding-bottom: var(--spacing-xs);
}

.lists-container::-webkit-scrollbar {
  height: 5px;
}

.lists-container::-webkit-scrollbar-track {
  background: transparent;
}

.lists-container::-webkit-scrollbar-thumb {
  background-color: var(--border-medium);
  border-radius: var(--border-radius-full);
}

.list-items {
  display: flex;
  gap: var(--spacing-sm);
  padding: var(--spacing-xs) 0;
}

.list-item {
  background-color: var(--background-muted);
  color: var(--text-secondary);
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--border-radius-md);
  transition: all 0.2s ease;
  white-space: nowrap;
}

.list-item:hover {
  background-color: var(--interactive-hover);
  transform: translateX(2px);
}

/* Action buttons */
.action-buttons {
  display: flex;
  justify-content: center;
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-light);
}

.edit-button {
  background-color: var(--accent-info);
  color: white;
  border: none;
  padding: var(--spacing-md) var(--spacing-xl);
  border-radius: var(--border-radius-md);
  font-weight: var(--font-fontWeight-medium);
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-level1-default);
}

.edit-button:hover {
  background-color: var(--accent-info);
  opacity: 0.9;
  box-shadow: var(--shadow-level1-hover);
  transform: translateY(-2px);
}

.edit-button:active {
  transform: translateY(0);
}

@media (max-width: 600px) {
  .info-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .info-label {
    margin-bottom: var(--spacing-xs);
  }
}
</style>