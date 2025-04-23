<template>
  <div class="list-detail-page">
    <!-- header -->
    <div class="page-header" v-if="!selectionMode">
      <back-button
          :to="{ name: 'playlist' }"
          aria-label="Back to playlist"
      />
      <h1 class="page-title">{{ list.listName }}</h1>

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
        :visibleTabs="['search', 'custom', 'watched', 'wishlist']"
        mode="addToList"
        :targetListId="list.id"
        :multiSelect="true"
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
      isDeleting: false
    };
  },
  computed: {
    listId() {
      return this.$route.params.id;
    },
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
    async fetchListDetails() {
      try {
        const response = await this.$http.get(`/playlist/id/${this.listId}`);
        this.list = response.data.data;
      } catch (error) {
        console.error('Error fetching list details:', error);
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
          console.log(`Successfully removed ${this.selectedContents.length} contents from list ${this.list.listName}`);
          await this.fetchListContents();
          this.cancelSelectionMode();
        } else {
          console.error('Some items failed to be removed from the list');
        }
      } catch (error) {
        console.error('Error removing contents from list:', error);
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
          console.log(`Successfully add ${items.length}items to list ${this.list.listName}`);
          await this.fetchListContents();
        } else {
          console.log('Failed to add to list:', response.data.code, response.data.message);
        }
      } catch (e) {
        console.error('Error adding content to list:', e);
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

.page-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xxl);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
}

.page-title {
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-bold);
  font-size: var(--font-fontSize-xxl);
  color: var(--text-primary);
  margin: 0;
}

@media (min-width: 768px) {
  .page-header {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
  }
}

.header-actions {
  display: flex;
  gap: var(--spacing-md);
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.sort-select {
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  border: 1px solid var(--border-light);
  background-color: var(--background-subtle);
  color: var(--text-primary);
  font-size: var(--font-fontSize-sm);
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