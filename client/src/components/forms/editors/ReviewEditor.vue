<template>
  <div class="review-form">
    <!-- Main Review -->
    <div class="form-group" v-if="isMainReview">
      <label for="reviewText">Review</label>
      <textarea
          id="reviewText"
          v-model="reviewData.text"
          placeholder="Your thoughts about this title..."
          class="form-control"
          rows="4"
      ></textarea>
      <p class="review-date-selector">
        <label>
          <input type="checkbox" v-model="reviewData.useCustomDate" />
          Use a different date than the actual viewing time
        </label>
        <input
            v-if="reviewData.useCustomDate"
            type="date"
            v-model="reviewData.date"
            class="form-control small-date"
            :max="currentDate"
        />
      </p>
    </div>

    <!-- Additional Review -->
    <div class="form-group additional-review" v-else>
      <div class="review-header">
        <label>Additional Review</label>
        <button class="remove-review-button" @click="$emit('remove')">×</button>
      </div>
      <textarea
          v-model="reviewData.text"
          placeholder="Your additional thoughts at a different time..."
          class="form-control"
          rows="4"
      ></textarea>
      <p class="review-date-selector">
        <label>
          <input type="checkbox" v-model="reviewData.useCustomDate" />
          Use a different date than the actual viewing time
        </label>
        <input
            v-if="reviewData.useCustomDate"
            type="date"
            v-model="reviewData.date"
            class="form-control small-date"
            :max="currentDate"
        />
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ReviewForm',
  props: {
    value: {
      type: Object,
      required: true
    },
    isMainReview: {
      type: Boolean,
      default: false
    },
    watchDate: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      currentDate: new Date().toISOString().split('T')[0],
      reviewData: {
        text: this.value.text || '',
        date: this.value.date || this.watchDate || new Date().toISOString().split('T')[0],
        useCustomDate: this.value.useCustomDate || false
      }
    };
  },
  watch: {
    value: {
      handler(newValue) {
        this.reviewData.text = newValue.text || '';
        this.reviewData.date = newValue.date || this.watchDate || new Date().toISOString().split('T')[0];
        this.reviewData.useCustomDate = newValue.useCustomDate || false;
      },
      deep: true
    },
    reviewData: {
      handler(newValue) {
        this.$emit('input', newValue);
      },
      deep: true
    },
    watchDate(newDate) {
      if (!this.reviewData.useCustomDate) {
        this.reviewData.date = newDate;
      }
    }
  }
};
</script>

<style scoped>
/* Review form styles */
.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-sm);
  display: block;
}

.form-control {
  padding: var(--spacing-md) var(--spacing-lg);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-lg);
  font-size: var(--font-fontSize-base);
  transition: all 0.2s ease;
  background-color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-info);
  box-shadow: 0 0 0 3px rgba(var(--accent-info-rgb), 0.15);
}

textarea.form-control {
  resize: vertical;
  min-height: 120px;
  line-height: var(--font-lineHeight-relaxed);
}

.review-date-selector {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
  font-size: var(--font-fontSize-sm);
  color: var(--text-secondary);
}

.small-date {
  width: auto;
  padding: var(--spacing-xs) var(--spacing-md);
  margin-left: var(--spacing-md);
}

.additional-review {
  background: linear-gradient(to bottom right, var(--background-subtle), var(--background-base));
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  position: relative;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-level1-default);
  margin-top: var(--spacing-md);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.review-header label {
  margin-bottom: 0;
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-primary);
  font-size: var(--font-fontSize-base);
}

.remove-review-button {
  background: transparent;
  border: none;
  color: var(--text-muted);
  font-size: 1.2rem;
  cursor: pointer;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-full);
  transition: all 0.2s ease;
}

.remove-review-button:hover {
  color: var(--accent-error);
}
</style>