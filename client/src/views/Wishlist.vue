<template>
  <div class="wishlist-page">
    <!-- header -->
    <div class="page-header" v-if="!selectionMode">
      <div class="header-content">
        <h2>Wishlist</h2>
      </div>

      <!-- sort-->
      <div class="header-actions">
        <sort-controls
          :sortBy="sortBy"
          sortType="content"
          @update:sortBy="sortBy = $event"
        />
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
        v-model:multiSelect="isMultiSelectEnabled"
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
import ContentGrid from "../components/ui/layout/CardGrid.vue";
import AddRecordingModal from "../components/modals/AddRecordModal.vue";
import AddToListModal from "../components/modals/AddToListModal.vue";
import Pagination from "../components/ui/layout/Pagination.vue";
import FilterControls from "../components/business/controls/FilterControls.vue";
import SortControls from "../components/business/controls/SortControls.vue";
import SelectionActionsBar from "../components/ui/layout/SelectionBar.vue";
import MainBtn from "../components/buttons/MainBtn.vue";
import ContentTabModal from "../components/modals/ContentTabModal.vue";
import ConfirmModal from "../components/modals/ConfirmModal.vue";
import { useToastStore } from "../store/toastStore.js";

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
    SortControls,
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
      deleteMessage: '',
      toastStore: null,
      isMultiSelectEnabled: false
    }
  },
  computed: {
    sortedContents() {
      if (!this.contents.length) return [];

      const addTimeMap = {};
      this.listContents.forEach(item => {
        addTimeMap[item.contentId] = item.addTime || new Date().toISOString();
      });

      let sorted = this.contents.map(content => {
        const addTime = addTimeMap[content.id];
        console.log(`Debug - Content ${content.id}, addTime:`, addTime);
        return {
          ...content,
          type: 'content',
          addTime: addTime
        };
      });

      let result;
      switch (this.sortBy) {
        case 'added-desc':
          result = sorted.sort((a, b) => {
            const dateA = a.addTime ? new Date(a.addTime).getTime() : 0;
            const dateB = b.addTime ? new Date(b.addTime).getTime() : 0;
            return dateB - dateA;
          });
          break;
        case 'added-asc':
          result = sorted.sort((a, b) => {
            const dateA = a.addTime ? new Date(a.addTime).getTime() : 0;
            const dateB = b.addTime ? new Date(b.addTime).getTime() : 0;
            return dateA - dateB;
          });
          break;
        case 'release-desc':
          result = sorted.sort((a, b) => {
            const dateA = a.releaseDate ? new Date(a.releaseDate).getTime() : 0;
            const dateB = b.releaseDate ? new Date(b.releaseDate).getTime() : 0;
            return dateB - dateA;
          });
          break;
        case 'release-asc':
          result = sorted.sort((a, b) => {
            const dateA = a.releaseDate ? new Date(a.releaseDate).getTime() : 0;
            const dateB = b.releaseDate ? new Date(b.releaseDate).getTime() : 0;
            return dateA - dateB;
          });
          break;
        default:
          result = sorted;
      }

      return result;
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
        this.toastStore.error('Failed to load wishlist. Please try again.');
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
          this.toastStore.success(`Added ${items.length} item${items.length > 1 ? 's' : ''} to wishlist`);
        } else {
          this.toastStore.error('Failed to add some items to wishlist');
        }
      } catch (e) {
        console.error(`Error adding items to wishlist`, e);
        this.toastStore.error('Failed to add items to wishlist');
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
        // 保存选中项的数量，以便在清空选择后仍能正确显示
        const selectedCount = this.selectedContents.length;
        
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
          console.log(`Successfully removed ${selectedCount} contents from wishlist`);
          await this.fetchWishlist();
          this.cancelSelectionMode();
          this.toastStore.success(`Removed ${selectedCount} item${selectedCount > 1 ? 's' : ''} from wishlist`);
        } else {
          console.error('Error deleting contents from wishlist');
          console.log(results);
          this.toastStore.error('Failed to remove some items from wishlist');
        }
      } catch (error) {
        console.error('Error removing contents from wishlist');
        this.toastStore.error('Failed to remove items from wishlist');
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
    this.toastStore = useToastStore();
    this.fetchWishlist();
    this.fetchContentDetails();
  }
}
</script>

<style scoped>
/* Remove local header styles as they're defined globally now */

.wishlist-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-lg);
  position: relative;
}
</style>
