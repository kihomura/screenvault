<template>
  <div class="modal-backdrop" v-if="isOpen" @click.self="closeModal">
    <div class="modal-container">

      <!-- header -->
      <div class="modal-header">
        <h2 class="modal-title">Add New Recording</h2>
        <button class="close-button" @click="closeModal">&times;</button>
      </div>

      <!-- selected content -->
      <div class="content-form">
        <div class="selected-content-header">
          <div class="selected-content-info">
            <img
                :src="selectedContent.image ? imgPrefix + selectedContent.image : '/placeholder-poster.jpg'"
                alt="Poster"
                class="selected-content-poster"
            />
            <div class="selected-content-text">
              <h3>{{ selectedContent.title }}</h3>
              <p>{{ formatYear(selectedContent.releaseDate) }}</p>
            </div>
          </div>
        </div>

        <div class="form-container">
          <!-- Watch Date -->
          <div class="form-group">
            <label for="watchDate">Watch Date</label>
            <input
                type="date"
                id="watchDate"
                v-model="formData.watchDate"
                class="form-control"
                :max="currentDate"
            />
          </div>

          <!-- Rating -->
          <div class="form-group">
            <label for="rating">Rating (Only whole or half values allowed, e.g. 8 or 8.5)</label>
            <div class="rating-input">

              <!-- stars -->
              <div class="rating-stars" @mouseleave="resetHoverRating">
                <div
                    v-for="i in 10"
                    :key="i"
                    class="star-container"
                    @mousemove="handleStarHover($event, i)"
                    @click="handleStarClick($event, i)"
                >
                  <span class="star" :class="getStarClass(i)">★</span>
                </div>
              </div>
              <!-- number -->
              <div class="rating-number">
                <input
                    type="number"
                    id="rating"
                    v-model.number="formData.rate"
                    min="0"
                    max="10"
                    step="0.5"
                    class="form-control"
                    @input="validateRating"
                />
                <span class="rating-max">/10</span>
              </div>
            </div>
          </div>

          <!-- Tag -->
          <div class="form-group">
            <label>Tags</label>
            <!-- select existed tags -->
            <div class="tags-container">
              <div class="tags-list">
                <div
                    v-for="tag in availableTags"
                    :key="tag.id"
                    :class="['tag-item', { 'tag-selected': selectedTags.some(t => t.id === tag.id) }]"
                    @click="toggleTag(tag)"
                >
                  {{ tag.tagName }}
                </div>
              </div>
              <!-- or add new tag -->
              <div class="add-tag-input">
                <input
                    type="text"
                    v-model="newTagName"
                    placeholder="Add new tag"
                    class="form-control"
                    @keyup.enter="createNewTag"
                />
                <button class="add-tag-button" @click="createNewTag" :disabled="!newTagName.trim()">+</button>
              </div>
            </div>
          </div>

          <!-- Review -->
          <div class="form-group">
            <label for="reviewText">Review</label>
            <!-- review text -->
            <textarea
                id="reviewText"
                v-model="formData.reviews[0].text"
                placeholder="Your thoughts about this title..."
                class="form-control"
                rows="4"
            ></textarea>
            <!-- review date (watch date as default) -->
            <p class="review-date-selector">
              <label>
                <input type="checkbox" v-model="formData.reviews[0].useCustomDate" />
                Use a different date than the actual viewing time
              </label>
              <input
                  v-if="formData.reviews[0].useCustomDate"
                  type="date"
                  v-model="formData.reviews[0].date"
                  class="form-control small-date"
                  :max="currentDate"
              />
            </p>
          </div>

          <div v-if="!showMultipleReviews" class="multiple-reviews-prompt">
            <p @click="showMultipleReviews = true" class="custom-content-link">
              Seen it more than once? Click to add more reviews...
            </p>
          </div>

          <!-- add multiple reviews -->
          <div
              v-for="(review, index) in formData.reviews.slice(1)"
              :key="index + 1"
              class="form-group additional-review"
          >
            <div class="review-header">
              <label>Additional Review</label>
              <button class="remove-review-button" @click="removeReview(index + 1)">&times;</button>
            </div>
            <textarea
                v-model="review.text"
                placeholder="Your additional thoughts at a different time..."
                class="form-control"
                rows="4"
            ></textarea>
            <p class="review-date-selector">
              <label>
                <input type="checkbox" v-model="review.useCustomDate" />
                Use a different date than the actual viewing time
              </label>
              <input
                  v-if="review.useCustomDate"
                  type="date"
                  v-model="review.date"
                  class="form-control small-date"
                  :max="currentDate"
              />
            </p>
          </div>

          <div v-if="showMultipleReviews" class="add-another-review">
            <button class="add-review-button" @click="addReview">Add Another Review</button>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <main-btn type="secondary" @click="closeModal">Cancel</main-btn>
        <div class="save-buttons">
          <main-btn type="highlight" @click="saveRecording(false)">Save</main-btn>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MainBtn from "../buttons/MainBtn.vue";
import {formatYear} from "../../utils/index.js";

export default {
  name: 'AddRecordModal',
  components: {
    MainBtn
  },
  props: {
    isOpen: {
      type: Boolean,
      default: false
    },
    selectedContent: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      currentDate: new Date().toISOString().split('T')[0],
      // TODO: use imgPrefix only when content's source_type = 'OFFICIAL_DATA'
      imgPrefix: 'https://image.tmdb.org/t/p/w1280',
      showMultipleReviews: false,
      availableTags: [],
      selectedTags: [],
      newTagName: '',
      hoverRating: null,
      formData: {
        contentId: null,
        rate: null,
        status: 'WATCHED',
        watchDate: new Date().toISOString().split('T')[0],
        reviews: [
          {
            text: '',
            date: new Date().toISOString().split('T')[0],
            useCustomDate: false
          }
        ]
      }
    };
  },
  watch: {
    isOpen(newVal) {
      if (newVal && this.selectedContent) {
        this.formData.contentId = this.selectedContent.id;
      }
    },
    selectedContent(newContent) {
      if (newContent) {
        this.formData.contentId = newContent.id;
      }
    }
  },
  created() {
    this.fetchTags();
  },
  methods: {
    formatYear,
    async fetchTags() {
      try {
        const response = await this.$http.get('/tag');
        if (response && response.data && response.data.data) {
          this.availableTags = response.data.data;
        }
      } catch (error) {
        console.error('Error fetching tags:', error);
      }
    },
    toggleTag(tag) {
      const index = this.selectedTags.findIndex(t => t.id === tag.id);
      if (index > -1) {
        this.selectedTags.splice(index, 1);
      } else {
        this.selectedTags.push(tag);
      }
    },
    async createNewTag() {
      if (!this.newTagName.trim()) return;
      try {
        const response = await this.$http.post('/tag', {
          tagName: this.newTagName.trim()
        });
        if (response && response.data && response.data.data) {
          const newTag = response.data.data;
          this.availableTags.push(newTag);
          this.selectedTags.push(newTag);
          this.newTagName = '';
        }
      } catch (error) {
        console.error('Error creating tag:', error);
        alert('Failed to create tag');
      }
    },
    addReview() {
      this.formData.reviews.push({
        text: '',
        date: this.formData.watchDate,
        useCustomDate: false
      });
    },
    removeReview(index) {
      this.formData.reviews.splice(index, 1);
    },
    closeModal() {
      this.resetForm();
      this.$emit('close');
    },
    resetForm() {
      this.selectedTags = [];
      this.showMultipleReviews = false;
      this.formData = {
        contentId: this.selectedContent ? this.selectedContent.id : null,
        rate: null,
        status: 'WATCHED',
        watchDate: new Date().toISOString().split('T')[0],
        reviews: [
          {
            text: '',
            date: new Date().toISOString().split('T')[0],
            useCustomDate: false
          }
        ]
      };
    },
    saveRecording(addAnother) {
      if (!this.formData.contentId) {
        alert('Content ID is missing');
        return;
      }
      if (!this.formData.watchDate) {
        alert('Please enter a watch date');
        return;
      }
      const reviewList = this.formData.reviews
          .filter(review => review.text.trim() !== '')
          .map(review => {
            return {
              text: review.text.trim(),
              date: review.useCustomDate ? review.date : this.formData.watchDate
            };
          });

      const recordingData = {
        contentId: this.formData.contentId,
        rate: this.formData.rate,
        status: this.formData.status,
        watchDate: this.formData.watchDate,
        review: reviewList,
      };

      const tagData = {
        tags: this.selectedTags.map(tag => ({
          tagId: tag.id,
          contentId: this.formData.contentId
        }))
      };

      this.$emit('save', {
        recordingData: recordingData,
        tagData: tagData
      });

      this.closeModal();
    },
    handleStarHover(event, starIndex) {
      const rect = event.currentTarget.getBoundingClientRect();
      const mouseX = event.clientX - rect.left;
      const halfPoint = rect.width / 2;
      if (mouseX < halfPoint) {
        this.hoverRating = starIndex - 0.5;
      } else {
        this.hoverRating = starIndex;
      }
    },
    handleStarClick(event, starIndex) {
      const rect = event.currentTarget.getBoundingClientRect();
      const mouseX = event.clientX - rect.left;
      const halfPoint = rect.width / 2;
      if (mouseX < halfPoint) {
        this.formData.rate = starIndex - 0.5;
      } else {
        this.formData.rate = starIndex;
      }
      this.hoverRating = null;
    },
    resetHoverRating() {
      this.hoverRating = null;
    },
    getStarClass(starIndex) {
      const rating = this.hoverRating !== null ? this.hoverRating : this.formData.rate;
      if (!rating) return 'empty';
      if (starIndex <= Math.floor(rating)) {
        return 'full';
      } else if (starIndex === Math.ceil(rating) && rating % 1 !== 0) {
        return 'half';
      } else {
        return 'empty';
      }
    },
    validateRating() {
      if (this.formData.rate !== null) {
        const roundedValue = Math.round(this.formData.rate * 2) / 2;
        if (roundedValue !== this.formData.rate) {
          this.formData.rate = roundedValue;
        }
        if (this.formData.rate > 10) {
          this.formData.rate = 10;
        } else if (this.formData.rate < 0) {
          this.formData.rate = 0;
        }
      }
    }
  }
};
</script>

<style scoped>
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { transform: translateY(30px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-5px); }
  100% { transform: translateY(0px); }
}

/* Modal base */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(var(--primary-dark-rgb), 0.7);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-out;
  font-family: var(--font-fontFamily-primary);
  color: var(--text-primary);
  padding: var(--spacing-lg);
}

.modal-container {
  background-color: var(--background-base);
  width: min(90%, 580px);
  max-height: 90vh;
  border-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-level3-default);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
}

/* Modal header */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-xl) var(--spacing-xl);
  background: linear-gradient(to right, var(--background-subtle), var(--background-base));
  border-bottom: 1px solid var(--border-light);
}

.modal-title {
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-semibold);
  font-size: var(--font-fontSize-xl);
  color: var(--primary-dark);
  margin: 0;
  position: relative;
  letter-spacing: 0.3px;
}

.close-button {
  background: transparent;
  border: none;
  font-size: 1.5rem;
  color: var(--text-muted);
  cursor: pointer;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-full);
  transition: all 0.2s ease;
}

.close-button:hover {
  color: var(--accent-error);
}

.content-form {
  padding: var(--spacing-xl);
  overflow-y: auto;
  max-height: 85vh;
  scrollbar-width: thin;
  scrollbar-color: var(--tertiary) var(--background-subtle);
}

.content-form::-webkit-scrollbar {
  width: 5px;
}

.content-form::-webkit-scrollbar-track {
  background: var(--background-subtle);
  border-radius: var(--border-radius-full);
}

.content-form::-webkit-scrollbar-thumb {
  background-color: var(--tertiary);
  border-radius: var(--border-radius-full);
}

.selected-content-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
}

.selected-content-info {
  display: flex;
  align-items: center;
}

.selected-content-poster {
  width: 60px;
  height: 90px;
  border-radius: var(--border-radius-md);
  margin-right: var(--spacing-lg);
  box-shadow: var(--shadow-level2-default);
  object-fit: cover;
}

.selected-content-text h3 {
  margin: 0 0 var(--spacing-xs);
  font-weight: var(--font-fontWeight-semibold);
  font-size: var(--font-fontSize-lg);
  color: var(--text-primary);
}

.selected-content-text p {
  margin: 0;
  font-size: var(--font-fontSize-sm);
  color: var(--text-secondary);
}

.change-selection-button {
  background-color: transparent;
  border: 1px solid var(--border-medium);
  border-radius: var(--border-radius-full);
  padding: var(--spacing-xs) var(--spacing-lg);
  color: var(--text-secondary);
  font-size: var(--font-fontSize-sm);
  cursor: pointer;
  transition: all 0.2s ease;
}

.change-selection-button:hover {
  color: var(--accent-info);
  border-color: var(--accent-info);
}

/* Form styles */
.form-container {
  display: grid;
  gap: var(--spacing-xl);
}

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

/* Rating stars */
.rating-stars {
  display: flex;
  margin-right: 20px;
}

.star-container {
  position: relative;
  width: 30px;
  height: 30px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}

.star {
  font-size: 28px;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--border-medium);
  position: relative;
}

.star.full {
  color: var(--accent-warning);
}

.star.half {
  position: relative;
  color: var(--border-medium);
}

.star.half::before {
  content: '★';
  position: absolute;
  overflow: hidden;
  color: var(--accent-warning);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  clip-path: inset(0 50% 0 0);
}

.star.half::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
}

/* Rating input container */
.rating-input {
  display: flex;
  align-items: center;
}

.rating-number {
  display: flex;
  align-items: center;
}

.rating-number input {
  width: 60px;
  height: 40px;
  text-align: center;
  padding: 0 8px;
  -moz-appearance: textfield;
}

.rating-number input::-webkit-outer-spin-button,
.rating-number input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.rating-max {
  color: var(--text-secondary);
  margin-left: 4px;
}

/* Rating hint */
.rating-hint {
  margin: 0 0 var(--spacing-sm);
  font-size: var(--font-fontSize-sm);
  color: var(--text-secondary);
}

/* Ensuring only 0.5 increment for rating */
input[type="number"].form-control {
  step: 0.5;
}

/* Tags */
.tags-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.tag-item {
  padding: var(--spacing-sm) var(--spacing-lg);
  background-color: var(--background-subtle);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-full);
  font-size: var(--font-fontSize-sm);
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-level1-default);
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-level1-hover);
}

.tag-selected {
  background-color: rgba(var(--accent-info-rgb), 0.1);
  border-color: var(--accent-info);
  color: var(--accent-info);
  box-shadow: 0 0 0 2px rgba(var(--accent-info-rgb), 0.15);
}

.add-tag-input {
  display: flex;
  margin-top: var(--spacing-md);
}

.add-tag-input input {
  flex: 1;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.add-tag-button {
  background-color: var(--accent-info);
  color: white;
  border: none;
  min-width: 36px;
  height: 100%;
  border-top-right-radius: var(--border-radius-lg);
  border-bottom-right-radius: var(--border-radius-lg);
  cursor: pointer;
  font-size: var(--font-fontSize-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.add-tag-button:hover:not(:disabled) {
  background-color: rgba(var(--accent-info-rgb), 0.8);
}

.add-tag-button:disabled {
  background-color: var(--border-medium);
  cursor: not-allowed;
}

/* Reviews */
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

.multiple-reviews-prompt {
  text-align: center;
  margin-top: var(--spacing-sm);
}

.multiple-reviews-prompt .custom-content-link {
  display: inline-block;
  color: var(--accent-info);
  cursor: pointer;
  transition: color 0.2s ease;
  position: relative;
}

.multiple-reviews-prompt .custom-content-link::after {
  content: '';
  position: absolute;
  width: 0;
  height: 1px;
  bottom: -2px;
  left: 0;
  background-color: var(--accent-info);
  transition: width 0.2s ease;
}

.multiple-reviews-prompt .custom-content-link:hover {
  color: var(--primary-dark);
}

.multiple-reviews-prompt .custom-content-link:hover::after {
  width: 100%;
}

.add-another-review {
  display: flex;
  justify-content: center;
  margin-top: var(--spacing-lg);
}

.add-review-button {
  background-color: transparent;
  border: 1px dashed var(--border-medium);
  color: var(--accent-info);
  padding: var(--spacing-md) var(--spacing-xl);
  border-radius: var(--border-radius-full);
  cursor: pointer;
  font-size: var(--font-fontSize-base);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.add-review-button::before {
  content: '+';
  font-size: 1.2em;
  font-weight: var(--font-fontWeight-bold);
}

.add-review-button:hover {
  border-color: var(--accent-info);
  color: var(--accent-info);
  transform: translateY(-2px);
}

/* Form actions */
.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-xl) var(--spacing-xl);
  background: linear-gradient(to right, var(--background-subtle), var(--background-base));
  border-top: 1px solid var(--border-light);
  margin-top: var(--spacing-lg);
}

.save-buttons {
  display: flex;
  gap: var(--spacing-md);
}


input[type="date"].form-control {
  display: block;
  width: 100%;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  position: relative;
}

</style>