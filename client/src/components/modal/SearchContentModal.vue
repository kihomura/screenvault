<template>
  <div>
    <div class="custom-content-option">
      <p @click="openCustomContentModal" class="custom-content-link">
        Didn't find it? Add your own content here
      </p>
    </div>

    <div class="modal-search">
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

    <div class="loading-container" v-if="isLoading">
      <div class="loading-spinner"></div>
      <p class="loading-text">Searching...</p>
    </div>

    <div class="search-results" v-if="!isLoading && searchSubmitted && searchResults.length > 0">
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
        <!-- TODO: already added to watched or want to watch -->
          already added
        </div>
      </div>
    </div>

    <div class="empty-search" v-if="!isLoading && searchSubmitted && searchResults.length === 0">
      <p>No results found for "{{ searchQuery }}"</p>
    </div>

    <add-custom-content-modal
        v-if="isCustomContentModalOpen"
        :isOpen="isCustomContentModalOpen"
        @close="closeCustomContentModal"
        @save="handleCustomContentSaved"
    />
  </div>
</template>

<script>
import AddCustomContentModal from './AddCustomContentModal.vue';

export default {
  name: 'SearchContentModal',
  components: {
    AddCustomContentModal
  },
  props: {
    // to verify is this content already added
    userRecordings: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      searchQuery: '',
      searchResults: [],
      searchSubmitted: false,
      isLoading: false,
      // TODO: use imgPrefix only when content's source_type = 'OFFICIAL_DATA'
      imgPrefix: 'https://image.tmdb.org/t/p/w1280',
      isCustomContentModalOpen: false
    };
  },
  computed: {
    sortedSearchResults() {
      // set 'already added' item in the bottom
      const results = [...this.searchResults];
      return results.sort((a, b) => {
        if (a.alreadyAdded && !b.alreadyAdded) return 1;
        if (!a.alreadyAdded && b.alreadyAdded) return -1;
        return 0;
      });
    }
  },
  methods: {
    async fetchSearchResults() {
      if (this.searchQuery.length >= 2) {
        this.searchSubmitted = true;
        this.isLoading = true;
        try {
          const response = await this.$http.get(`/content/title/${this.searchQuery}`);
          // TODO: Verify is the item's status is WATCHED or WANT_TO_WATCH
          if (response && response.data && response.data.data) {
            const results = response.data.data;
            results.forEach(result => {
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
      this.$emit('content-selected', content);
      this.searchResults = [];
      this.searchQuery = content.title;
      this.searchSubmitted = false;
    },
    formatYear(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.getFullYear();
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
</style>