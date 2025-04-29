<template>
  <div class="modal-backdrop" v-if="isOpen" @click.self="closeModal">
    <div class="modal-container">

      <!-- header -->
      <div class="modal-header">
        <h2 class="modal-title">{{ isEditMode ? 'Edit Record' : 'Add Record' }}</h2>
        <button class="close-button" @click="closeModal">&times;</button>
      </div>

      <!-- selected content -->
      <div class="content-form">
        <div class="selected-content-header">
          <div class="selected-content-info">
            <img
                :src="getContentImagePath(selectedContent)"
                alt="Poster"
                class="selected-content-poster"
            />
            <div class="selected-content-text">
              <h3>{{ selectedContent.title }}</h3>
              <p>{{ formatYear(selectedContent.releaseDate) }}</p>
            </div>
          </div>
        </div>

        <!-- record form -->
        <div class="form-container">

          <!-- watch date -->
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

          <!-- rating -->
          <rating-selector v-model="formData.rate"></rating-selector>

          <!-- tags -->
          <tag-selector v-model="selectedTags"></tag-selector>

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

          <div v-if="!showMultipleReviews && formData.reviews.length === 1" class="multiple-reviews-prompt">
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

          <div v-if="showMultipleReviews || formData.reviews.length > 1" class="add-another-review">
            <button class="add-review-button" @click="addReview">Add Another Review</button>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <main-btn type="secondary" @click="closeModal">Cancel</main-btn>
        <div class="save-buttons">
          <main-btn type="highlight" @click="saveRecording">{{ isEditMode ? 'Update' : 'Save' }}</main-btn>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MainBtn from "../buttons/MainBtn.vue";
import RatingSelector from "../RatingStars.vue";
import TagSelector from "../TagSelector.vue";
import {formatYear, getContentImagePath} from "../../utils/index.js";

export default {
  name: 'AddRecordModal',
  components: {
    MainBtn,
    RatingSelector,
    TagSelector
  },
  props: {
    isOpen: {
      type: Boolean,
      default: false
    },
    selectedContent: {
      type: Object,
      default: null
    },
    existingRecord: {
      type: Object,
      default: null
    },
    existingTags: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      currentDate: new Date().toISOString().split('T')[0],
      showMultipleReviews: false,
      selectedTags: [],
      formData: {
        contentId: null,
        rate: null,
        status: 'WATCHED',
        watchDate: this.currentDate,
        reviews: [
          {
            text: '',
            date: this.currentDate,
            useCustomDate: false
          }
        ]
      },
      isEditMode: false
    };
  },
  watch: {
    isOpen(newVal) {
      if (newVal) {
        this.initializeForm();
      }
    },
    existingRecord(newRecord) {
      if (newRecord && this.isOpen) {
        this.initializeForm();
      }
    }
  },
  methods: {
    getContentImagePath,
    formatYear,
    initializeForm() {
      this.showMultipleReviews = false;

      // set whether in edit or add mode
      this.isEditMode = !!this.existingRecord;

      if (this.selectedContent) {
        this.formData.contentId = this.selectedContent.id;
      }

      // initialize form with existing data if in edit mode
      if (this.isEditMode && this.existingRecord) {
        this.formData = {
          contentId: this.existingRecord.contentId || this.selectedContent?.id,
          rate: this.existingRecord.rate,
          status: this.existingRecord.status || 'WATCHED',
          watchDate: this.existingRecord.watchDate || this.currentDate,
          reviews: []
        };

        if (this.existingRecord.review && this.existingRecord.review.length > 0) {
          this.formData.reviews = this.existingRecord.review.map(rev => {
            const useCustomDate = rev.date !== this.existingRecord.watchDate;
            return {
              text: rev.text || '',
              date: rev.date || this.existingRecord.watchDate,
              useCustomDate: useCustomDate
            };
          });

          if (this.existingRecord.review.length > 1) {
            this.showMultipleReviews = true;
          }
        } else {
          this.formData.reviews = [
            {
              text: '',
              date: this.formData.watchDate,
              useCustomDate: false
            }
          ];
        }
        this.selectedTags = [...this.existingTags];
      } else {
        // initialize empty form for add mode
        this.formData = {
          contentId: this.selectedContent ? this.selectedContent.id : null,
          rate: null,
          status: 'WATCHED',
          watchDate: this.currentDate,
          reviews: [
            {
              text: '',
              date: this.currentDate,
              useCustomDate: false
            }
          ]
        };
        this.selectedTags = [];
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
      this.$emit('close');
    },
    saveRecording() {
      if (!this.formData.contentId || !this.formData.watchDate) {
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

      const originalTags = this.isEditMode ? this.existingTags.map(t => ({
        tagId: t.id,
        contentId: this.formData.contentId,
      })) : [];
      const newTags = this.selectedTags.map(t => ({
        tagId: t.id,
        contentId: this.formData.contentId,
      }));

      this.$emit('save', {
        recordingData: recordingData,
        originalTags: originalTags,
        newTags: newTags,
        isEdit: this.isEditMode
      });

      this.closeModal();
    }
  }
};
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes float {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
  100% {
    transform: translateY(0px);
  }
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

.form-control::placeholder {
  color: var(--text-muted);
  opacity: 0.7;
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-info);
  box-shadow: 0 0 0 3px rgba(var(--accent-info-rgb), 0.15);
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


input[type="date"].form-control {
  display: block;
  width: 100%;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  position: relative;
}
</style>