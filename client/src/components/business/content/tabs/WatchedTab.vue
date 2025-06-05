<template>
  <div class="watched-tab">
    <!-- Empty state -->
    <div v-if="!watchedContents || watchedContents.length === 0" class="empty-state">
      <div class="empty-icon">📺</div>
      <h3>No watched content yet</h3>
      <p>Content you've watched will appear here</p>
    </div>

    <!-- Content list -->
    <div v-else class="content-list">
      <div class="content-grid">
        <content-item
            v-for="content in contents"
            :key="content.id"
            :content="content"
            :selectable="isContentSelectable(content)"
            :selected="isContentSelected(content)"
            :multiSelect="multiSelect"
            @select="selectContent"
            @toggle-selection="toggleSelection"
        />
      </div>
    </div>
  </div>
</template>

<script>
import ContentItem from '../../../common/ContentItem.vue';

export default {
  name: 'WatchedTab',
  components: {
    ContentItem
  },
  props: {
    watchedContents: {
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
      contents: [],
      listsData: {} // for storing list content data
    };
  },
  watch: {
    watchedContents: {
      immediate: true,
      handler() {
        this.fetchContents();
      }
    },
    targetListId: {
      immediate: true,
      handler(newId) {
        if (newId && this.mode === 'addToList') {
          this.fetchListContent(newId);
        }
      }
    }
  },
  methods: {
    isContentSelected(content) {
      return this.selectedItems.some(item => item.id === content.id);
    },
    isContentSelectable(content) {
      // For addRecord mode - all watched content is not selectable
      if (this.mode === 'addRecord') {
        return false;
      }
      // For addToList mode
      else if (this.mode === 'addToList' && this.targetListId) {
        // Check if content is already in the target list
        if (this.listsData[this.targetListId] &&
            this.listsData[this.targetListId].some(item => item.contentId === content.id)) {
          return false;
        }
      }
      // For addToWishlist mode - watched content should be selectable
      else if (this.mode === 'addToWishlist') {
        return true;
      }
      // For selectFavorite mode - all content should be selectable
      else if (this.mode === 'selectFavorite') {
        return true;
      }
      return true;
    },
    async fetchListContent(listId) {
      try {
        const response = await this.$http.get(`/list-content/list/${listId}`);
        if (response && response.data && response.data.data) {
          this.listsData[listId] = response.data.data;
        }
      } catch (error) {
        console.error('Error fetching list content:', error);
      }
    },
    selectContent(content) {
      if (!this.multiSelect) {
        this.$emit('content-selected', content);
      }
    },
    toggleSelection(content) {
      this.$emit('toggle-selection', content);
    },
    async fetchContents() {
      this.contents = [];
      if (this.watchedContents && this.watchedContents.length > 0) {
        try {
          // Fetch full content details for each watched content
          for (let i = 0; i < this.watchedContents.length; i++) {
            const watchedItem = this.watchedContents[i];
            if (watchedItem.status === 'WATCHED') { // Only include actually watched content
              try {
                const response = await this.$http.get(`content/id/${watchedItem.contentId}`);
                if (response.data.data) {
                  const content = response.data.data;
                  content.status = 'WATCHED'; // Mark with status
                  this.contents.push(content);
                }
              } catch (err) {
                console.error(`Error fetching content id ${watchedItem.contentId}:`, err);
              }
            }
          }
        } catch (error) {
          console.error("Error in fetchContents:", error);
        }
      }
    }
  }
};
</script>

<style scoped>
.watched-tab {
  padding: var(--spacing-md) 0;
}

.content-list {
  display: flex;
  flex-direction: column;
}

.content-grid {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl);
  text-align: center;
  color: var(--text-secondary);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-lg);
}

.empty-state h3 {
  margin: 0 0 var(--spacing-sm);
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-medium);
}

.empty-state p {
  margin: 0;
  font-size: var(--font-fontSize-base);
  color: var(--text-muted);
}
</style>