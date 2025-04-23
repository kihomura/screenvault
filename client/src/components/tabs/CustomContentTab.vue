<template>
  <div class="custom-content-tab">
    <!-- Empty state -->
    <div v-if="!customContents || customContents.length === 0" class="empty-state">
      <div class="empty-icon">📺</div>
      <h3>No custom content yet</h3>
      <p>Content you customized will appear here</p>
    </div>

    <!-- Content list -->
    <div v-else class="content-list">
      <div class="content-grid">
        <content-item
            v-for="content in customContents"
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
import ContentItem from '../ContentItem.vue';

export default {
  name: 'WishlistTab',
  components: {
    ContentItem
  },
  props: {
    customContents: {
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
      listsData: {}
    };
  },
  watch: {
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
        const listData = this.listsData?.[this.targetListId] || [];
        if (listData.some(item => item.contentId === content.id)) {
          return false;
        }
      }
      // For addToWishlist mode
      else if (this.mode === 'addToWishlist') {
        // Check if it's already in wishlist
        const isInWishlist = this.customContents.some(
            recording => recording.contentId === content.id && recording.status === 'WANT_TO_WATCH'
        );
        const isWatched = this.customContents.some(
            recording => recording.contentId === content.id && recording.status === 'WATCHED'
        );
        if (isInWishlist || isWatched) {
          return false;
        }
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
  }
};
</script>

<style scoped>
.custom-content-tab {
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