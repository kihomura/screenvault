<template>
  <div class="wishlist-tab">
    <!-- Empty state -->
    <div v-if="!wishlistContent || wishlistContent.length === 0" class="empty-state">
      <div class="empty-icon">📺</div>
      <h3>No wishlist yet</h3>
      <p>Content you want to watch will appear here</p>
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
  name: 'WishlistTab',
  components: {
    ContentItem
  },
  props: {
    wishlistContent: {
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
      listsData: {}
    };
  },
  watch: {
    wishlistContent: {
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
        return content.status !== 'WATCHED';
      }
      // For addToList mode
      else if (this.mode === 'addToList' && this.targetListId) {
        // Check if content is already in the target list
        if (this.listsData[this.targetListId] &&
            this.listsData[this.targetListId].some(item => item.contentId === content.id)) {
          return false;
        }
      }
      // For addToWishlist mode
      else if (this.mode === 'addToWishlist') {
        // already in wishlist should not be selectable
        return false;
      }
      // For selectFavorite mode - all content should be selectable
      else if (this.mode === 'selectFavorite') {
        return true;
      }
      return true;
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
      try {
        this.contents = []; // Clear contents first
        if (this.wishlistContent && this.wishlistContent.length > 0) {
          for (let i = 0; i < this.wishlistContent.length; i++) {
            const item = this.wishlistContent[i];
            try {
              const response = await this.$http.get(`content/id/${item.contentId}`);
              if (response.data && response.data.data) {
                this.contents.push(response.data.data);
              }
            } catch (err) {
              console.error(`Error fetching content id ${item.contentId}:`, err);
            }
          }
        }
      } catch (error) {
        console.error('Error in fetchContents:', error);
      }
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
    }
  }
};
</script>

<style scoped>
.wishlist-tab {
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