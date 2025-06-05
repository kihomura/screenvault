<template>
  <div class="modal-container" v-if="isOpen">
    <div class="modal-backdrop" @click="closeModal"></div>
    <div class="modal-content">

      <!-- tab controls -->
      <div class="modal-header">
        <div class="modal-tabs">
          <div
              v-for="tab in filteredTabs"
              :key="tab.id"
              class="modal-tab"
              :class="{ 'active': activeTab === tab.id }"
              @click="switchTab(tab.id)"
          >
            {{ tab.label }}
          </div>
        </div>
        <!-- close button -->
        <button class="modal-close-button" @click="closeModal" aria-label="Close modal">
          <span>×</span>
        </button>
      </div>

      <div class="modal-body">

        <!-- loading state -->
        <div v-if="isLoading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>Loading content...</p>
        </div>

        <!-- error state -->
        <div v-else-if="error" class="error-container">
          <p>{{ error }}</p>
          <button class="retry-button" @click="fetchAllData">Retry</button>
        </div>

        <!-- tabs -->
        <template v-else>
          <search-tab
              v-if="activeTab === 'search' && isTabVisible('search')"
              :userRecordings="userRecordings"
              :multiSelect="multiSelect"
              :selectedItems="selectedItems"
              :mode="mode"
              :targetListId="targetListId"
              @content-selected="handleContentSelected"
              @toggle-selection="handleToggleSelection"
          />

          <custom-content-tab
              v-if="activeTab === 'custom' && isTabVisible('custom')"
              :customContents="customContents"
              :multiSelect="multiSelect"
              :selectedItems="selectedItems"
              :mode="mode"
              :targetListId="targetListId"
              :userRecordings="userRecordings"
              @content-selected="handleContentSelected"
              @toggle-selection="handleToggleSelection"
              @refresh="fetchCustomContent"
          />

          <watched-tab
              v-if="activeTab === 'watched' && isTabVisible('watched')"
              :watchedContents="watchedContents"
              :multiSelect="multiSelect"
              :selectedItems="selectedItems"
              :mode="mode"
              :targetListId="targetListId"
              @content-selected="handleContentSelected"
              @toggle-selection="handleToggleSelection"
          />

          <wishlist-tab
              v-if="activeTab === 'wishlist' && isTabVisible('wishlist')"
              :wishlistContent="wishedContents"
              :multiSelect="multiSelect"
              :selectedItems="selectedItems"
              :mode="mode"
              :targetListId="targetListId"
              @content-selected="handleContentSelected"
              @toggle-selection="handleToggleSelection"
          />
        </template>
      </div>

      <!-- multi select handle section -->
      <div class="modal-footer" v-if="multiSelect && selectedItems.length > 0">
        <span class="selected-count">{{ selectedItems.length }} items selected</span>
        <main-btn type="highlight" @click="confirmSelection">
          {{ confirmButtonText }}
        </main-btn>
      </div>

    </div>
  </div>
</template>

<script>
import SearchTab from '../business/content/tabs/SearchTab.vue';
import CustomContentTab from '../business/content/tabs/CustomContentTab.vue';
import WatchedTab from '../business/content/tabs/WatchedTab.vue';
import WishlistTab from '../business/content/tabs/WishlistTab.vue';
import MainBtn from "../buttons/MainBtn.vue";

export default {
  name: 'ContentTabModal',
  components: {
    MainBtn,
    SearchTab,
    CustomContentTab,
    WatchedTab,
    WishlistTab
  },
  emits: ['close', 'content-selected', 'items-selected', 'update:multiSelect', 'update:visibleTabs'],
  props: {
    isOpen: {
      type: Boolean,
      default: false
    },
    visibleTabs: {
      type: Array,
      default: () => ['search', 'custom', 'watched', 'wishlist']
    },
    mode: {
      // this modal is triggered in three scenarios
      type: String,
      default: 'addRecord',
      validator: value => ['addRecord', 'addToList', 'addToWishlist', 'selectFavorite'].includes(value)
    },
    targetListId: {
      type: [String, Number],
      default: null    // only used when mode = addToList
    },
    multiSelect: {
      type: Boolean,
      default: false  // true when mode = addToList or addToWishlist
    }
  },
  data() {
    return {
      activeTab: 'search',
      tabs: [
        { id: 'search', label: 'Search ALL' },
        { id: 'custom', label: 'Custom' },
        { id: 'watched', label: 'Watched' },
        { id: 'wishlist', label: 'Wish List' }
      ],
      watchedContents: [],
      wishedContents: [],
      userRecordings: [],
      customContents: [],
      selectedItems: [],
      isLoading: false,
      error: null
    };
  },
  computed: {
    filteredTabs() {
      return this.tabs.filter(tab => this.visibleTabs.includes(tab.id));
    },
    confirmButtonText() {
      switch (this.mode) {
        case 'addToList':
          return `Add to List (${this.selectedItems.length})`;
        case 'addToWishlist':
          return `Add to Wishlist (${this.selectedItems.length})`;
        default:
          return 'Confirm';
      }
    }
  },
  watch: {
    isOpen(newVal) {
      if (newVal) {
        this.fetchAllData();
        this.selectedItems = []; // reset
        if (this.filteredTabs.length > 0 && !this.isTabVisible(this.activeTab)) {
          this.activeTab = this.filteredTabs[0].id;
        }
      }
    },
    visibleTabs: {
      handler(newTabs) {
        if (newTabs.length > 0 && !newTabs.includes(this.activeTab)) {
          this.activeTab = newTabs[0];
        }
      },
      immediate: true,
      deep: true
    },
    mode: {
      handler(newMode) {
        // in addToList and addToWishlist modes, multiple contents can be selected
        if (newMode === 'addToList' || newMode === 'addToWishlist') {
          this.$emit('update:multiSelect', true);
        }

        // in addRecord mode, watched content should not be displayed
        if (newMode === 'addRecord') {
          this.$emit('update:visibleTabs', ['search', 'custom', 'wishlist']);
        }
        // content has already been added should not be displayed
        else if (newMode === 'addToWishlist') {
          this.$emit('update:visibleTabs', ['search', 'custom', 'watched']);
        }
        // for selecting favorite, show all tabs
        else if (newMode === 'selectFavorite') {
          this.$emit('update:visibleTabs', ['search', 'custom', 'watched', 'wishlist']);
        }

        this.selectedItems = []; //reset when mode change
      },
      immediate: true
    }
  },
  methods: {
    isTabVisible(tabId) {
      return Array.isArray(this.visibleTabs) && this.visibleTabs.includes(tabId);
    },

    async fetchAllData() {
      this.isLoading = true;
      this.error = null;
      
      try {
        const promises = [
          this.fetchWatchedContent(),
          this.fetchWishlistContent(),
          this.fetchCustomContent()
        ];
        
        await Promise.all(promises);
        this.userRecordings = [...this.watchedContents, ...this.wishedContents];
      } catch (err) {
        this.error = `Failed to load content: ${err.message || 'Unknown error'}`;
        console.error('Error fetching data:', err);
      } finally {
        this.isLoading = false;
      }
    },
    async fetchWatchedContent() {
      try {
        const response = await this.$http.get("/record");
        if (response.data.data) {
          this.watchedContents = response.data.data;
        }
      } catch (err) {
        console.error("Failed to fetch watched content:", err);
        throw err;
      }
    },
    async fetchWishlistContent() {
      try {
        const response = await this.$http.get("/record/wishlist");
        if (response.data.data) {
          this.wishedContents = response.data.data;
        }
      } catch (err) {
        console.error("Failed to fetch wishlist content:", err);
        throw err;
      }
    },
    async fetchCustomContent() {
      try {
        const response = await this.$http.get("content/custom");
        if (response.data.data) {
          this.customContents = response.data.data;
        }
      } catch (err) {
        console.error("Failed to fetch customContent:", err);
        throw err;
      }
    },

    switchTab(tabId) {
      this.activeTab = tabId;
    },

    closeModal() {
      this.selectedItems = [];
      this.$emit('close');
    },

    handleContentSelected(content) {
      if (!this.multiSelect) {
        // for addRecord and selectFavorite modes
        this.$emit('content-selected', content);
        this.closeModal();
      }
    },
    confirmSelection() {
      if (this.selectedItems.length > 0) {
        // for addToList & addToWishlist mode
        this.$emit('items-selected', this.selectedItems);
        this.closeModal();
      }
    },

    handleToggleSelection(content) {
      if (!this.multiSelect) return;
      // select if it's not selected, deselect if it's already selected
      const index = this.selectedItems.findIndex(item => item.id === content.id);
      if (index === -1) {
        this.selectedItems.push(content);
      } else {
        this.selectedItems.splice(index, 1);
      }
    }
  },
  mounted() {
    // Fetch data if modal is initially open
    if (this.isOpen) {
      this.fetchAllData();
    }
  }
};
</script>

<style scoped>
.modal-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  font-family: var(--font-fontFamily-primary);
}

.modal-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(var(--primary-dark-rgb), 0.6);
  backdrop-filter: blur(4px);
  z-index: 1;
}

.modal-content {
  position: relative;
  width: 600px;
  height: 650px;
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level3-default);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: modalAppear 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  z-index: 2;
}

@keyframes modalAppear {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-xl);
  border-bottom: 1px solid var(--border-light);
  background-color: var(--background-subtle);
}

.modal-tabs {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.modal-tab {
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--border-radius-full);
  font-weight: var(--font-fontWeight-medium);
  font-size: var(--font-fontSize-sm);
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-secondary);
  user-select: none;
}

.modal-tab:hover {
  background-color: var(--interactive-hover);
  color: var(--text-primary);
  box-shadow: var(--shadow-level1-hover);
}

.modal-tab.active {
  background-color: var(--accent-info);
  color: white;
  box-shadow: 0 2px 5px rgba(var(--accent-info-rgb), 0.3);
}

.modal-close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: var(--text-secondary);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-full);
  transition: all 0.2s ease;
  position: relative;
}

.modal-close-button:hover {
  background-color: var(--interactive-hover);
  color: var(--accent-error);
}

.modal-close-button:active {
  background-color: var(--interactive-active);
  transform: scale(0.95);
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg);
  position: relative;
}

/* Loading state */
.loading-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: var(--background-base);
  color: var(--text-secondary);
  font-size: var(--font-fontSize-base);
  gap: var(--spacing-lg);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-light);
  border-top: 3px solid var(--accent-info);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Error state */
.error-container {
  padding: var(--spacing-xl);
  text-align: center;
  color: var(--text-secondary);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);
}

.retry-button {
  background-color: var(--accent-info);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  padding: var(--spacing-sm) var(--spacing-xl);
  font-weight: var(--font-fontWeight-medium);
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-level1-default);
}

.retry-button:hover {
  background-color: rgba(var(--accent-info-rgb), 0.9);
  box-shadow: var(--shadow-level1-hover);
}

.retry-button:active {
  transform: scale(0.98);
}

/* Scrollbar styling */
.modal-body::-webkit-scrollbar {
  width: 8px;
}

.modal-body::-webkit-scrollbar-track {
  background: var(--background-subtle);
  border-radius: var(--border-radius-full);
}

.modal-body::-webkit-scrollbar-thumb {
  background-color: var(--border-medium);
  border-radius: var(--border-radius-full);
}

.modal-body::-webkit-scrollbar-thumb:hover {
  background-color: var(--border-dark);
}

.modal-footer {
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #eee;
}

.selected-count {
  font-weight: bold;
}
</style>