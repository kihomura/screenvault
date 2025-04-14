<template>
  <div class="modal-backdrop" v-if="isOpen" @click.self="closeModal">
    <div class="modal-container">
      <div class="modal-header">
        <h2 class="modal-title">Add New Recording</h2>
        <button class="close-button" @click="closeModal">&times;</button>
      </div>

      <div class="custom-content-option" v-if="!selectedContent">
        <p @click="openCustomContentModal" class="custom-content-link">
          Didn't find it? Add your own content here
        </p>
      </div>

      <div class="modal-search" v-if="!selectedContent">
        <div class="search-container">
          <input
              type="text"
              v-model="searchQuery"
              placeholder="Search for movies, shows, etc."
              class="search-input"
              @keyup.enter="fetchSearchResults"
          />
          <button class="search-button" @click="fetchSearchResults">
            <span v-if="isLoading" class="loader"></span>
            <span v-else class="search-icon">🔍</span>
          </button>
        </div>
      </div>

      <!-- Loading indicator -->
      <div class="loading-container" v-if="isLoading">
        <div class="loading-spinner"></div>
        <p class="loading-text">Searching...</p>
      </div>

      <!-- Search results: shown after search completes with results -->
      <div class="search-results" v-if="!isLoading && searchSubmitted && searchResults.length > 0 && !selectedContent">
        <div
            v-for="result in sortedSearchResults"
            :key="result.id"
            class="search-result-item"
            :class="{ 'already-added': result.alreadyAdded }"
            @click="result.alreadyAdded ? null : selectContent(result)"
        >
          <div class="result-poster">
            <img :src="result.image ? imgPrefix + result.image : '/placeholder-poster.jpg'" alt="Poster" />
          </div>
          <div class="result-details">
            <h4 class="result-title">{{ result.title }}</h4>
            <p class="result-year">{{ formatYear(result.releaseDate) }}</p>
          </div>
          <div v-if="result.alreadyAdded" class="already-added-badge">
            already added
          </div>
        </div>
      </div>

      <!-- No results message -->
      <div class="empty-search" v-if="!isLoading && searchSubmitted && searchResults.length === 0 && !selectedContent">
        <p>No results found for "{{ searchQuery }}"</p>
      </div>

      <!-- Selected content form -->

      <div class="content-form" v-if="selectedContent">
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
          <button class="change-selection-button" @click="resetSelection">Change</button>
        </div>

        <div class="form-container">
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

          <div class="form-group">
            <label for="rating">Rating (Only whole or half values allowed, e.g. 8 or 8.5)</label>
            <div class="rating-input">
              <div class="rating-stars" @mouseleave="resetHoverRating">
                <div
                    v-for="i in 10"
                    :key="i"
                    class="star-container"
                    @mousemove="handleStarHover($event, i)"
                    @click="handleStarClick($event, i)"
                >
                  <!-- Using a single star with a clip-path for half stars -->
                  <span class="star" :class="getStarClass(i)">★</span>
                </div>
              </div>
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


          <!-- Tags Section -->
          <div class="form-group">
            <label>Tags</label>
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

          <!-- Reviews Section -->
          <div class="form-group">
            <label for="reviewText">Review</label>
            <textarea
                id="reviewText"
                v-model="formData.reviews[0].text"
                placeholder="Your thoughts about this title..."
                class="form-control"
                rows="4"
            ></textarea>
            <p class="review-date-selector">
              <label>
                <input type="checkbox" v-model="formData.reviews[0].useCustomDate">
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

          <!-- Additional reviews -->
          <div v-if="!showMultipleReviews" class="multiple-reviews-prompt">
            <p @click="showMultipleReviews = true" class="custom-content-link">
              Seen it more than once? Click to add more reviews...
            </p>
          </div>

          <div v-for="(review, index) in formData.reviews.slice(1)" :key="index + 1" class="form-group additional-review">
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
                <input type="checkbox" v-model="review.useCustomDate">
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

      <div class="form-actions" v-if="selectedContent">
        <button class="cancel-button" @click="closeModal">Cancel</button>
        <div class="save-buttons">
          <button class="save-button" @click="saveRecording(false)">Save</button>
        </div>
      </div>

    </div>
  </div>

  <add-custom-content-modal
      v-if="isCustomContentModalOpen"
      :isOpen="isCustomContentModalOpen"
      @close="closeCustomContentModal"
      @save="handleCustomContentSaved"
  />
</template>

<script>
import AddCustomContentModal from './AddCustomContentModal.vue';

export default {
  name: 'AddRecordingModal',
  components: {
    AddCustomContentModal
  },
  props: {
    isOpen: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      searchQuery: '',
      searchResults: [],
      searchSubmitted: false,
      selectedContent: null,
      currentDate: new Date().toISOString().split('T')[0],
      imgPrefix: 'https://image.tmdb.org/t/p/w1280',
      isLoading: false,
      isCustomContentModalOpen: false,
      hoverRating: null,
      showMultipleReviews: false,
      availableTags: [],
      selectedTags: [],
      newTagName: '',
      userRecordings: [], // Store the user's existing recordings
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
    }
  },
  computed: {
    sortedSearchResults() {
      // Create a copy of search results to avoid mutating the original
      const results = [...this.searchResults];

      // Sort results so that already added items appear at the bottom
      return results.sort((a, b) => {
        if (a.alreadyAdded && !b.alreadyAdded) return 1;
        if (!a.alreadyAdded && b.alreadyAdded) return -1;
        return 0;
      });
    }
  },
  created() {
    this.fetchTags();
    this.fetchUserRecordings();
  },
  watch: {
    isOpen(newVal) {
      if (newVal) {
        // Refresh user recordings whenever modal is opened
        this.fetchUserRecordings();
      }
    }
  },
  methods: {
    async fetchUserRecordings() {
      try {
        const response = await this.$http.get('/record');
        if (response && response.data && response.data.data) {
          this.userRecordings = response.data.data;
        }
      } catch (error) {
        console.error('Error fetching user recordings:', error);
      }
    },
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
      this.searchQuery = '';
      this.searchResults = [];
      this.searchSubmitted = false;
      this.selectedContent = null;
      this.selectedTags = [];
      this.showMultipleReviews = false;
      this.formData = {
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
      };
    },
    async fetchSearchResults() {
      if (this.searchQuery.length >= 2) {
        this.searchSubmitted = true;
        this.isLoading = true;

        try {
          const response = await this.$http.get(`/content/title/${this.searchQuery}`);
          if (response && response.data && response.data.data) {
            // Get search results
            const results = response.data.data;

            // Check each result if it exists in user's content
            results.forEach(result => {
              // Check if this content is already in user's recordings
              result.alreadyAdded = this.userRecordings.some(
                  recording => recording.contentId === result.id
              );
            });

            this.searchResults = results;
          } else {
            this.searchResults = [];
          }
        } catch (error) {
          console.error('Search error:', error);
          this.searchResults = [];
        } finally {
          this.isLoading = false;
        }
      } else {
        this.searchSubmitted = true;
        this.searchResults = [];
      }
    },
    selectContent(content) {
      if (content.alreadyAdded) return; // Prevent selecting already added content

      this.selectedContent = content;
      this.formData.contentId = content.id;
      // Clear search results and set search box content to selected content title
      this.searchResults = [];
      this.searchQuery = content.title;
    },
    resetSelection() {
      this.selectedContent = null;
      this.formData.contentId = null;
      this.searchQuery = '';
      this.searchSubmitted = false;
      this.searchResults = [];
    },
    openCustomContentModal() {
      this.isCustomContentModalOpen = true;
    },
    closeCustomContentModal() {
      this.isCustomContentModalOpen = false;
    },
    handleCustomContentSaved(newContent) {
      this.selectContent(newContent);
      this.closeCustomContentModal();
    },
    saveRecording(addAnother) {
      if (!this.formData.contentId) {
        alert('Please select content');
        return;
      }
      if (!this.formData.watchDate) {
        alert('Please enter a watch date');
        return;
      }

      // Process reviews, filtering out empty ones and setting correct dates
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
        tags: this.selectedTags.map(tag => ({
          tagId: tag.id,
          contentId: this.formData.contentId
        }))
      };

      // Pass data through event
      this.$emit('save', recordingData);
      this.closeModal();
    },

    handleStarHover(event, starIndex) {
      const rect = event.currentTarget.getBoundingClientRect();
      const mouseX = event.clientX - rect.left;
      const halfPoint = rect.width / 2;

      // If mouse is on the left half of the star, set half-star rating
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

      // If clicked on the left half of the star, set half-star rating
      if (mouseX < halfPoint) {
        this.formData.rate = starIndex - 0.5;
      } else {
        this.formData.rate = starIndex;
      }
    },

    resetHoverRating() {
      this.hoverRating = null;
    },

    getStarClass(starIndex) {
      // Use hover rating if available, otherwise use form rating
      const rating = this.hoverRating !== null ? this.hoverRating : this.formData.rate;

      if (!rating) return 'empty';

      if (starIndex <= Math.floor(rating)) {
        return 'full'; // full star
      } else if (starIndex === Math.ceil(rating) && rating % 1 !== 0) {
        return 'half'; // half star
      } else {
        return 'empty'; // empty star
      }
    },

    validateRating() {
      if (this.formData.rate !== null) {
        // Ensure the value is a multiple of 0.5
        const roundedValue = Math.round(this.formData.rate * 2) / 2;
        if (roundedValue !== this.formData.rate) {
          this.formData.rate = roundedValue;
        }
        // Ensure the value is between 0 and 10
        if (this.formData.rate > 10) {
          this.formData.rate = 10;
        } else if (this.formData.rate < 0) {
          this.formData.rate = 0;
        }
      }
    },

    formatYear(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.getFullYear();
    }
  }
}
</script>

<style scoped>
/* Base styles and animations */
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

/* Custom content option */
.custom-content-option {
  text-align: center;
  padding: var(--spacing-xs);
}

.custom-content-link {
  color: var(--accent-info);
  cursor: pointer;
  font-size: var(--font-fontSize-sm);
  transition: color 0.2s ease;
  display: inline-block;
  text-decoration: none;
  position: relative;
}

p.custom-content-link {
  margin-bottom: 0;
  margin-top: 0;
}

.custom-content-link::after {
  content: '';
  position: absolute;
  width: 0;
  height: 1px;
  bottom: -2px;
  left: 0;
  background-color: var(--accent-info);
  transition: width 0.2s ease;
}

.custom-content-link:hover {
  color: var(--primary-dark);
}

.custom-content-link:hover::after {
  width: 100%;
}

/* Search area */
.modal-search {
  padding: var(--spacing-xs) var(--spacing-xl) var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
}

.search-container {
  display: flex;
  position: relative;
  box-shadow: var(--shadow-level1-default);
  border-radius: var(--border-radius-full);
}

.search-input {
  flex: 1;
  padding: var(--spacing-lg) var(--spacing-xl);
  padding-right: 50px;
  border: 1px solid var(--border-light);
  color: var(--text-primary);
  border-radius: var(--border-radius-full);
  font-size: var(--font-fontSize-base);
  transition: all 0.25s ease;
  background-color: var(--background-base);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-info);
  box-shadow: 0 0 0 3px rgba(var(--accent-info-rgb), 0.15);
}

.search-button {
  position: absolute;
  right: 5px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-full);
  transition: all 0.2s ease;
}

.search-button:hover {
  background-color: rgba(var(--accent-info-rgb), 0.1);
}

.search-icon {
  font-size: var(--font-fontSize-lg);
}

/* Loading indicators */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl) 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(var(--accent-info-rgb), 0.15);
  border-top-color: var(--accent-info);
  border-radius: 50%;
  animation: spin 1s ease-in-out infinite;
}

.loading-text {
  margin-top: var(--spacing-lg);
  color: var(--text-secondary);
  font-size: var(--font-fontSize-base);
  letter-spacing: 0.5px;
}

.loader {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 2px solid rgba(var(--accent-info-rgb), 0.3);
  border-radius: 50%;
  border-top-color: var(--accent-info);
  animation: spin 0.8s linear infinite;
}

/* Search results */
.search-results {
  max-height: 50vh;
  overflow-y: auto;
  padding: var(--spacing-md) var(--spacing-lg);
  scrollbar-width: thin;
  scrollbar-color: var(--tertiary) var(--background-subtle);
}

.search-results::-webkit-scrollbar {
  width: 5px;
}

.search-results::-webkit-scrollbar-track {
  background: var(--background-subtle);
  border-radius: var(--border-radius-full);
}

.search-results::-webkit-scrollbar-thumb {
  background-color: var(--tertiary);
  border-radius: var(--border-radius-full);
}

.search-result-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  margin-bottom: var(--spacing-md);
  border: 1px solid transparent;
  background-color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
}

.search-result-item:hover {
  box-shadow: var(--shadow-level2-hover);
  transform: translateY(-3px);
  border-color: rgba(var(--accent-info-rgb), 0.2);
}

.already-added {
  opacity: 0.6;
  cursor: not-allowed;
}

.already-added:hover {
  transform: none;
  box-shadow: var(--shadow-level1-default);
  border-color: transparent;
}

.result-poster {
  width: 50px;
  height: 75px;
  overflow: hidden;
  border-radius: var(--border-radius-md);
  margin-right: var(--spacing-lg);
  box-shadow: var(--shadow-level1-default);
  flex-shrink: 0;
}

.result-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.search-result-item:hover .result-poster img {
  transform: scale(1.05);
}

.result-details {
  flex-grow: 1;
}

.result-title {
  margin: 0 0 var(--spacing-xs);
  font-weight: var(--font-fontWeight-medium);
  font-size: var(--font-fontSize-base);
  color: var(--text-primary);
}

.result-year {
  margin: 0;
  font-size: var(--font-fontSize-sm);
  color: var(--text-muted);
}

.already-added-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: var(--border-medium);
  color: var(--text-secondary);
  font-size: var(--font-fontSize-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-full);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.empty-search {
  display: flex;
  justify-content: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
  font-size: var(--font-fontSize-base);
}

/* Selected content form */
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

/* Rating stars with precision decimal display */
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
  color: var(--border-medium); /* Default empty star color */
  position: relative;
}

/* Full star - completely filled */
.star.full {
  color: var(--accent-warning);
}

/* Half star - uses pseudo element with precise clipping */
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
  /* This clip-path ensures only the left half is visible */
  clip-path: inset(0 50% 0 0);
}

/* Make sure the half star's background is empty */
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

.cancel-button {
  background-color: transparent;
  border: 1px solid var(--border-dark);
  color: var(--text-secondary);
  padding: var(--spacing-md) var(--spacing-xl);
  border-radius: var(--border-radius-full);
  font-size: var(--font-fontSize-base);
  font-weight: var(--font-fontWeight-medium);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  overflow: hidden;
}

.cancel-button::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background-color: rgba(var(--tertiary-rgb), 0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.5s, height 0.5s;
}

.cancel-button:hover {
  color: var(--text-primary);
}

.cancel-button:hover::after {
  width: 200%;
  height: 200%;
}

.save-button {
  background: linear-gradient(135deg, var(--accent-success), rgba(var(--accent-success-rgb), 0.8));
  color: white;
  border: none;
  padding: var(--spacing-md) var(--spacing-xxl);
  border-radius: var(--border-radius-full);
  font-size: var(--font-fontSize-base);
  font-weight: var(--font-fontWeight-medium);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: var(--shadow-level2-default), 0 0 0 0 rgba(var(--accent-success-rgb), 0.3);
  position: relative;
  overflow: hidden;
}

.save-button::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: linear-gradient(to right, transparent, rgba(255, 255, 255, 0.2), transparent);
  transform: translateX(-100%);
}

.save-button:hover {
  box-shadow: var(--shadow-level2-hover), 0 0 0 5px rgba(var(--accent-success-rgb), 0.15);
  transform: translateY(-2px);
}

.save-button:hover::after {
  transform: translateX(100%);
  transition: transform 0.6s ease-in-out;
}

/* Additional UI improvements */
input[type="date"].form-control {
  display: block;
  width: 100%;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  position: relative;
}

/* JavaScript will be needed to properly implement half-star ratings
   This CSS provides the visual foundation */
.star.active-0-5::before { width: 50%; }
.star.active-1-0::before { width: 100%; }
.star.active-1-5::before { width: 50%; }
.star.active-2-0::before { width: 100%; }
.star.active-2-5::before { width: 50%; }
.star.active-3-0::before { width: 100%; }
.star.active-3-5::before { width: 50%; }
.star.active-4-0::before { width: 100%; }
.star.active-4-5::before { width: 50%; }
.star.active-5-0::before { width: 100%; }
.star.active-5-5::before { width: 50%; }
.star.active-6-0::before { width: 100%; }
.star.active-6-5::before { width: 50%; }
.star.active-7-0::before { width: 100%; }
.star.active-7-5::before { width: 50%; }
.star.active-8-0::before { width: 100%; }
.star.active-8-5::before { width: 50%; }
.star.active-9-0::before { width: 100%; }
.star.active-9-5::before { width: 50%; }
.star.active-10-0::before { width: 100%; }
</style>