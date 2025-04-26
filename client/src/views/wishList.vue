<template>
  <div class="wishlist-page">
    <!-- header -->
    <div class="page-header" v-if="!selectionMode">
      <div class="header-content">
        <h1 class="page-title">Wishlist</h1>
      </div>

      <!-- sort section -->
      <div class="header-actions">
        <div class="sort-controls">
          <select v-model="sortBy" class="sort-select">
            <option value="added-desc">Recently Added</option>
            <option value="added-asc">Oldest Added</option>
            <option value="release-desc">Newest Released</option>
            <option value="release-asc">Oldest Released</option>
          </select>
        </div>
      </div>
    </div>

    <!-- selection actions bar -->
    <selection-actions-bar
        v-if="selectionMode"
        :selectedCount="selectedContents.length"
        :totalCount="sortedContents.length"
        :show-add-to-list="false"
        @confirm-delete="confirmDelete"
        @select-all="selectAll"
        @clear-selection="clearSelection"
        @cancel="cancelSelectionMode"
    />

    <!-- contents -->
    <div class="list-contents">
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>Loading contents...</p>
      </div>

      <div v-else>
        <content-grid
            :items="sortedContents"
            itemType="content"
            :selectionMode="selectionMode"
            :selectedItems="selectedContents"
            :showAddCard="!selectionMode"
            :totalItems="sortedContents.length"
            addCardText="Add New Content"
            @open-item="viewContentDetails"
            @toggle-selection="toggleContentSelection"
            @add-new="openAddContentModal"
        />
      </div>
    </div>

    <content-tab-modal
        :is-open="isAddModalOpen"
        :visibleTabs="['search', 'custom']"
        mode="addToWishlist"
        :multiSelect="true"
        @close="closeAddContentModal"
        @items-selected="addToWishlist"
    />

    <confirm-modal
        :visible="showDeleteModal"
        :title="'Remove contents from wishlist'"
        :message="deleteMessage"
        :confirm-text="'Remove'"
        :cancel-text="'Cancel'"
        type="danger"
        @confirm="deleteSelected"
        @cancel="cancelDelete"
    />
  </div>
</template>

<script>
import ContentGrid from "../components/ui/CardGrid.vue";
import AddRecordingModal from "../components/modals/AddRecordModal.vue";
import AddToListModal from "../components/modals/AddToListModal.vue";
import Pagination from "../components/ui/Pagination.vue";
import FilterControls from "../components/FilterContols.vue";
import SelectionActionsBar from "../components/ui/SelectionBar.vue";
import MainBtn from "../components/buttons/MainBtn.vue";
import ContentTabModal from "../components/modals/ContentTabModal.vue";
import ConfirmModal from "../components/modals/ConfirmModal.vue";

export default {
  name: 'WishlistPage',
  components: {
    ConfirmModal,
    ContentTabModal,
    MainBtn,
    ContentGrid,
    AddRecordingModal,
    AddToListModal,
    Pagination,
    FilterControls,
    SelectionActionsBar
  },
  data() {
    return {
      contents: [],
      contentCount: 0,
      listContents: [],
      loading: true,
      imgPrefix: 'https://image.tmdb.org/t/p/w1280',
      sortBy: 'added-desc',
      selectionMode: false,
      selectedContents: [],
      showDeleteModal: false,
      isAddModalOpen: false,
      deleteMessage: ''
    }
  },
  computed: {
    sortedContents() {
      if (!this.contents.length) return [];

      // Create a map of contentId to addTime from listContents
      const addTimeMap = {};
      this.listContents.forEach(item => {
        addTimeMap[item.contentId] = item.addTime || new Date().toISOString();
      });

      // Make a copy of contents to sort and add type property
      let sorted = this.contents.map(content => ({
        ...content,
        type: 'content' // Add type property for ContentGrid component
      }));

      switch (this.sortBy) {
        case 'added-desc':
          return sorted.sort((a, b) => {
            return new Date(addTimeMap[b.id] || 0) - new Date(addTimeMap[a.id] || 0);
          });
        case 'added-asc':
          return sorted.sort((a, b) => {
            return new Date(addTimeMap[a.id] || 0) - new Date(addTimeMap[b.id] || 0);
          });
        case 'release-desc':
          return sorted.sort((a, b) => {
            return new Date(b.releaseDate || 0) - new Date(a.releaseDate || 0);
          });
        case 'release-asc':
          return sorted.sort((a, b) => {
            return new Date(a.releaseDate || 0) - new Date(b.releaseDate || 0);
          });
        default:
          return sorted;
      }
    }
  },
  methods: {
    async fetchWishlist() {
      try {
        const response = await this.$http.get(`/record/wishlist`);
        if (response.data.data) {
          this.listContents = response.data.data;
          this.contentCount = this.listContents.length;

          await this.fetchContentDetails();
        }
      } catch (error) {
        console.error('Error fetching wishlist: ', error);
        this.listContents = [];
        this.contentCount = 0;
      } finally {
        this.loading = false;
      }
    },
    async fetchContentDetails() {
      try {
        const promises = this.listContents.map(item =>
            this.$http.get(`/content/id/${item.contentId}`)
        );

        const responses = await Promise.all(promises);
        this.contents = responses.map(response => response.data.data)
            .filter(content => content);

      } catch (error) {
        console.error('Error fetching content details:', error);
      }
    },
    async addToWishlist(items) {
      this.loading = true;
      try {
        const addPromises = [];
        // add each item to user-content table and set their status = WANT_TO_WATCH
        for (const item of items) {
          const userContent = {
            contentId: item.id
            // status will be set at backend
          };
          addPromises.push(this.$http.post('/record/wishlist', userContent));
        }

        const responses = await Promise.all(addPromises);
        const allSuccessful = responses.every(response => response.data.code === 200);

        if (allSuccessful) {
          console.log(`Successfully added ${items.length} items to wishlist`);
          await this.fetchWishlist();
        }
      } catch (e) {
        console.error(`Error adding items to wishlist`, e)
      } finally {
        this.loading = false;
      }
    },

    viewContentDetails(content) {
      if (!this.selectionMode) {
        this.$router.push(`/content/${content.id}`);
      }
    },
    toggleContentSelection(content) {
      const index = this.selectedContents.findIndex(c => c.id === content.id);
      if (!this.selectionMode) {
        this.selectionMode = true;
      }

      if (index === -1) {
        this.selectedContents.push(content);
      } else {
        this.selectedContents.splice(index, 1);
        if (this.selectedContents.length === 0) {
          this.selectionMode = false;
        }
      }
    },
    selectAll() {
      this.selectedContents = [...this.sortedContents];
    },
    clearSelection() {
      this.selectedContents = [];
      if (this.selectedContents.length === 0) {
        this.selectionMode = false;
      }
    },
    cancelSelectionMode() {
      this.selectionMode = false;
      this.selectedContents = [];
    },
    confirmDelete() {
      if (this.selectedContents.length === 0) return;
      this.showDeleteModal = true;
      this.deleteMessage =  `Are you sure you want to remove ${this.selectedContents.length} contents from wishlist?`
    },
    cancelDelete() {
      this.showDeleteModal = false;
    },
    async deleteSelected() {
      this.showDeleteModal = false;
      this.loading = true;

      try {
        const deletePromises = [];
        for (const content of this.selectedContents) {
          deletePromises.push(this.$http.delete('/record/wishlist', {
            data: content.id,
            headers: {
              'Content-Type': 'application/json'
            },
            transformRequest: [(data) => data]
          }));
        }
        const results = await Promise.all(deletePromises);
        const allSuccessful = results.every(response => response.data.code === 200);
        if (allSuccessful) {
          console.log(`Successfully removed ${this.selectedContents.length} contents from wishlist`);
          await this.fetchWishlist();
          this.cancelSelectionMode();
        } else {
          console.error('Error deleting contents from wishlist');
          console.log(results)
        }
      } catch (error) {
        console.error('Error removing contents from wishlist');
      } finally {
        this.loading = false;
      }
    },
    openAddContentModal() {
      this.isAddModalOpen = true;
    },
    closeAddContentModal() {
      this.isAddModalOpen = false;
    }
  },
  created() {
    this.fetchWishlist();
    this.fetchContentDetails();
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
}

@media (min-width: 768px) {
  .page-header {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
  }
}

.page-title {
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-bold);
  font-size: 1.75rem;
  color: var(--text-primary);
  margin: 0;
}

.wishlist-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-lg);
  position: relative;
}
</style>