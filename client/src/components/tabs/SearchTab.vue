<template>
  <div class="search-tab">
    <div class="search-container-wrapper">
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

    <div class="search-results-container">
      <div class="loading-container" v-if="isLoading">
        <div class="loading-spinner"></div>
        <p class="loading-text">Searching...</p>
      </div>

      <div class="search-results" v-if="!isLoading && searchSubmitted && searchResults.length > 0">
        <content-item
            v-for="result in sortedSearchResults"
            :key="result.id"
            :content="result"
            :selectable="isContentSelectable(result)"
            :selected="isContentSelected(result)"
            :multiSelect="multiSelect"
            :imgPrefix="imgPrefix"
            @select="selectContent"
            @toggle-selection="toggleSelection"
        />
      </div>

      <div class="empty-search" v-if="!isLoading && searchSubmitted && searchResults.length === 0">
        <p>No results found for "{{ searchQuery }}"</p>
      </div>
    </div>
  </div>
</template>

<script>
import ContentItem from '../ContentItem.vue';
import {formatYear} from "../../utils/index.js";

export default {
  name: 'SearchContentTab',
  components: {
    ContentItem
  },
  props: {
    userRecordings: {
      type: Array,
      default: () => []
    },
    multiSelect: {
      type: Boolean,
      default: false
    },
    selectedItems: {
      type: Array,
      default: () => []
    },
    mode: {
      type: String,
      default: 'addRecord'
    },
    targetListId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      searchQuery: '',
      searchResults: [],
      searchSubmitted: false,
      isLoading: false,
      imgPrefix: 'https://image.tmdb.org/t/p/w1280',
      listsData: {} // for addToList mode
    };
  },
  computed: {
    sortedSearchResults() {
      const results = [...this.searchResults];

      // in addRecord mode, watched content goes to bottom (and unselectable)
      if (this.mode === 'addRecord') {
        return results.sort((a, b) => {
          if (a.status === 'WATCHED' && b.status !== 'WATCHED') return 1;
          if (a.status !== 'WATCHED' && b.status === 'WATCHED') return -1;
          return 0;
        });
      }
      // in addToWishlist mode, content already added goes to bottom (and unselectable)
      else if (this.mode === 'addToWishlist') {
        return results.sort((a, b) => {
          if (a.status === 'WANT_TO_WATCH' && b.status !== 'WANT_TO_WATCH') return 1;
          if (a.status === 'WANT_TO_WATCH' && b.status === 'WANT_TO_WATCH') return -1;
          return 0;
        });
      }
      // keep original order with no special sorting
      else {
        return results;
      }
    }
  },
  watch:  {
    targetListId: {
      // fetch list data when targetListId changes
      immediate: true,
      handler(newId) {
        if (newId && this.mode === 'addToList') {
          this.fetchListContent(newId)
        }
      }
    }
  },
  created() {
    this.restoreSearchState();
  },
  beforeUnmount() {
    this.saveSearchState();
  },
  methods: {
    formatYear,
    isContentSelectable(content) {
      // in addRecord mode, content has watched are unselectable
      if (this.mode === 'addRecord') {
        if (content.status === 'WATCHED') {
          return false;
        }
      }
      // in addToList & addToWishlist mode, content already added are unselectable
      else if (this.mode === 'addToList' && this.targetListId) {
        if (this.listsData[this.targetListId] &&
            this.listsData[this.targetListId].some(item => item.contentId === content.id)) {
          return false;
        }
      }
      else if (this.mode === 'addToWishlist') {
        if (
            this.userRecordings.some(recording => recording.contentId === content.id && recording.status === 'WANT_TO_WATCH') ||
            this.userRecordings.some(recording => recording.contentId === content.id && recording.status === 'WATCHED',)){
          return false;
        }
      }
      return true;
    },
    isContentSelected(content) {
      return this.selectedItems.some(item => item.id === content.id);
    },
    async fetchListContent(listId) {
      try {
        const response = await this.$http.get(`/list-content/list/${listId}`);
        if (response && response.data && response.data.data) {
          this.listsData[listId] = response.data.data;
        }
      } catch (error) {
        console.error('Error fetching list content', error);
      }
    },
    async fetchSearchResults() {
      if (this.searchQuery.length >= 2) {
        this.searchSubmitted = true;
        this.isLoading = true;
        try {
          const response = await this.$http.get(`/content/title/${this.searchQuery}`);
          if (response && response.data && response.data.data) {
            const results = response.data.data;
            this.updateSearchResultsStatus(this.userRecordings, results);
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

      this.saveSearchState();
    },
    updateSearchResultsStatus(recordings, results = null) {
      const searchResults = results || this.searchResults;

      searchResults.forEach(result => {
        // check if content exists in user recordings
        const existingRecording = recordings.find(
            recording => recording.contentId === result.id
        );
        if (existingRecording) {
          result.status = existingRecording.status;
        }
      });
    },
    saveSearchState() {
      const searchState = {
        query: this.searchQuery,
        results: this.searchResults,
        submitted: this.searchSubmitted
      };

      // use mode and targetListId to create a unique key for this modal instance
      const stateKey = `searchTab_${this.mode}_${this.targetListId || 'default'}`;
      sessionStorage.setItem(stateKey, JSON.stringify(searchState));
    },
    restoreSearchState() {
      try {
        const stateKey = `searchTab_${this.mode}_${this.targetListId || 'default'}`;
        const savedState = sessionStorage.getItem(stateKey);

        if (savedState) {
          const { query, results, submitted } = JSON.parse(savedState);
          this.searchQuery = query;
          this.searchResults = results;
          this.searchSubmitted = submitted;

          // update status of restored search results
          if (results.length > 0 && this.userRecordings.length > 0) {
            this.updateSearchResultsStatus(this.userRecordings);
          }
        }
      } catch (error) {
        console.error('Error restoring search state:', error);
      }
    },
    selectContent(content) {
      if (!this.multiSelect) {
        this.$emit('content-selected', content);
        this.searchResults = [];
        this.searchQuery = '';
        this.searchSubmitted = false;
      }
    },
    toggleSelection(content) {
      this.$emit('toggle-selection', content);
    }
  }
};
</script>

<style scoped>
@keyframes spin {
  to { transform: rotate(360deg); }
}

.search-tab {
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
}

.search-container-wrapper {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: var(--background-base);
  padding: var(--spacing-md) 0;
  padding-bottom: var(--spacing-lg);
}

.search-container {
  display: flex;
  position: relative;
  box-shadow: var(--shadow-level1-default);
  border-radius: var(--border-radius-full);
}

.search-results-container {
  flex: 1;
  overflow-y: auto;
  padding-bottom: var(--spacing-lg);
}

.search-results-container::-webkit-scrollbar {
  width: 8px;
}

.search-results-container::-webkit-scrollbar-track {
  background: var(--background-subtle);
  border-radius: var(--border-radius-full);
}

.search-results-container::-webkit-scrollbar-thumb {
  background-color: var(--border-medium);
  border-radius: var(--border-radius-full);
}

.search-results-container::-webkit-scrollbar-thumb:hover {
  background-color: var(--border-dark);
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

.search-results {
  padding: var(--spacing-md) var(--spacing-lg);
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

.empty-search {
  display: flex;
  justify-content: center;
  padding: var(--spacing-xl);
  color: var(--text-secondary);
  font-size: var(--font-fontSize-base);
}
</style>