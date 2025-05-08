<template>
  <div class="custom-content-tab">
    <!-- Add More button -->
    <div class="add-button-container">
      <main-btn type="text" class="add-content-button" @click="openAddContentModal">
        Add More Custom Content
      </main-btn>
    </div>

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

    <!-- Add Custom Content Modal -->
    <add-custom-content-modal
        :isOpen="isAddModalOpen"
        @close="closeAddContentModal"
        @content-added="refreshTab"
    />
  </div>
</template>

<script>
import ContentItem from '../ContentItem.vue';
import MainBtn from '../buttons/MainBtn.vue';
import AddCustomContentModal from '../modals/AddCustomContentModal.vue';

export default {
  name: 'CustomContentTab',
  components: {
    ContentItem,
    MainBtn,
    AddCustomContentModal
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
    },
    userRecordings: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      listsData: {},
      isAddModalOpen: false
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
        // Check if content is already in the wishlist or watched list
        if (this.userRecordings && this.userRecordings.length > 0) {
          // Check if content is already in wishlist or already watched
          if (this.userRecordings.some(item => 
              item.contentId === content.id && 
              (item.status === 'WANT_TO_WATCH' || item.status === 'WATCHED')
          )) {
            return false;
          }
        }
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
    openAddContentModal() {
      this.isAddModalOpen = true;
    },
    closeAddContentModal() {
      this.isAddModalOpen = false;
    },
    refreshTab() {
      this.$emit('refresh');
    }
  }
};
</script>

<style scoped>
.custom-content-tab {
  padding: var(--spacing-md) 0;
}

.add-button-container {
  display: flex;
  justify-content: center;
  margin-bottom: var(--spacing-xl);
  padding: 0 var(--spacing-md);
}

.add-content-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  transition: transform 0.2s ease;
}

.add-content-button:hover {
  transform: translateY(-2px);
}

.add-icon {
  font-size: 1.2em;
  font-weight: bold;
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