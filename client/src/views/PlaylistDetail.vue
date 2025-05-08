<template>
  <div class="list-detail-page">
    <!-- header -->
    <div class="page-header" v-if="!selectionMode">
      <div class="header-content">
        <back-button
            :to="{ name: 'playlist' }"
            aria-label="Back to playlist"
        />
        <h2>{{ list.listName }}</h2>
      </div>

      <!-- sort section -->
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
        :visibleTabs="['search', 'custom', 'watched', 'wishlist']"
        mode="addToList"
        :targetListId="list.id"
        v-model:multiSelect="isMultiSelectEnabled"
        @close="closeAddContentModal"
        @items-selected="addContentToList"
    />

    <confirm-modal
        :visible="showDeleteModal"
        :title="'Remove contents from list'"
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
import ContentGrid from '../components/ui/CardGrid.vue';
import MainBtn from "../components/buttons/MainBtn.vue";
import SelectionActionsBar from "../components/ui/SelectionBar.vue";
import AddToListModal from "../components/modals/AddToListModal.vue";
import BackButton from "../components/buttons/BackButton.vue";
import ContentTabModal from "../components/modals/ContentTabModal.vue";
import ConfirmModal from "../components/modals/ConfirmModal.vue";
import SortControls from "../components/controls/SortControls.vue";
import { useToastStore } from "../store/toastStore.js";

export default {
  name: 'ListDetail',
  components: {
    ConfirmModal,
    ContentTabModal,
    BackButton,
    MainBtn,
    ContentGrid,
    SelectionActionsBar,
    AddToListModal,
    SortControls,
  },
  data() {
    return {
      list: {},
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
      isDeleting: false,
      toastStore: null,
      isMultiSelectEnabled: false
    };
  },
  computed: {
    listId() {
      return this.$route.params.id;
    },
    sortedContents() {
      if (!this.contents.length) return [];

      const addTimeMap = {};
      this.listContents.forEach(item => {
        addTimeMap[item.contentId] = item.addTime || new Date().toISOString();
      });

      // Make a copy of contents to sort and add type property
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
    async fetchListDetails() {
      try {
        const response = await this.$http.get(`/playlist/id/${this.listId}`);
        this.list = response.data.data;
      } catch (error) {
        console.error('Error fetching list details:', error);
        this.toastStore.error('Failed to load playlist details');
      }
    },
    async fetchListContents() {
      try {
        const response = await this.$http.get(`/list-content/list/${this.listId}`);
        this.listContents = response.data.data || [];
        this.contentCount = this.listContents.length;

        await this.fetchContentDetails();
      } catch (error) {
        console.error('Error fetching list contents:', error);
        this.listContents = [];
        this.contentCount = 0;
        this.toastStore.error('Failed to load playlist contents');
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
      this.deleteMessage = `Are you sure you want to remove ${this.selectedContents.length} contents from list ${this.list.listName}?`
    },
    cancelDelete() {
      this.showDeleteModal = false;
    },
    async deleteSelected() {
      if (this.isDeleting){
        return
      }
      this.showDeleteModal = false;

      try {
        this.isDeleting = true;
        // 保存选中项的数量，以便在清空选择后仍能正确显示
        const selectedCount = this.selectedContents.length;
        
        const deletePromises = this.selectedContents.map(content => {
          const listContent = {
            listId: this.listId,
            contentId: content.id
          };
          return this.$http.delete('/list-content', { data: listContent });
        });

        const results = await Promise.all(deletePromises);
        const allSuccessful = results.every(response => response.data.code === 200);

        if (allSuccessful) {
          console.log(`Successfully removed ${selectedCount} contents from list ${this.list.listName}`);
          await this.fetchListContents();
          this.cancelSelectionMode();
          this.toastStore.success(`Removed ${selectedCount} item${selectedCount > 1 ? 's' : ''} from playlist`);
        } else {
          console.error('Some items failed to be removed from the list');
          this.toastStore.error('Failed to remove some items from playlist');
        }
      } catch (error) {
        console.error('Error removing contents from list:', error);
        this.toastStore.error('Failed to remove items from playlist');
      } finally {
        this.isDeleting = false;
      }
    },
    async addContentToList(items) {
      try {
        const listContents = items.map(item => ({
          listId: this.listId,
          contentId: item.id,
          addTime: new Date().toISOString()
        }));
        const response = await this.$http.post(`/list-content/batch`, listContents);
        if (response.data.code === 200) {
          console.log(`Successfully add ${items.length} items to list ${this.list.listName}`);
          await this.fetchListContents();
          this.toastStore.success(`Added ${items.length} item${items.length > 1 ? 's' : ''} to playlist`);
        } else {
          console.log('Failed to add to list:', response.data.code, response.data.message);
          this.toastStore.error('Failed to add items to playlist');
        }
      } catch (e) {
        console.error('Error adding content to list:', e);
        this.toastStore.error('Failed to add items to playlist');
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
    this.fetchListDetails();
    this.fetchListContents();
  }
};
</script>

<style scoped>
.list-detail-page {
  font-family: var(--font-fontFamily-primary);
  color: var(--text-primary);
  padding: var(--spacing-xl) var(--spacing-xl);
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
}

.header-actions {
  display: flex;
  gap: var(--spacing-md);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl);
  color: var(--text-muted);
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--background-muted);
  border-top: 4px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--spacing-md);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>