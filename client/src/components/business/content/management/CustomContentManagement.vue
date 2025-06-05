<template>
  <div class="custom-content-page">

    <!-- selection actions bar -->
    <selection-actions-bar
        v-if="selectionMode"
        :selectedCount="selectedContents.length"
        :totalCount="customContents.length"
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
            :items="customContents"
            itemType="content"
            :selectionMode="selectionMode"
            :selectedItems="selectedContents"
            :showAddCard="!selectionMode"
            :totalItems="customContents.length"
            addCardText="Add New Content"
            @open-item="viewContentDetails"
            @toggle-selection="toggleContentSelection"
            @add-new="openAddContentModal"
        />
      </div>
    </div>

    <add-custom-content-modal
        :is-open="isAddModalOpen"
        @close="closeAddContentModal"
        @content-added="addCustomContent"
    />

    <confirm-modal
        :visible="showDeleteModal"
        :title="'Delete custom content'"
        :message="deleteMessage"
        :confirm-text="'Delete'"
        :cancel-text="'Cancel'"
        type="danger"
        @confirm="deleteSelected"
        @cancel="cancelDelete"
    />
  </div>
</template>

<script>
import ContentGrid from "../../../ui/layout/CardGrid.vue";
import Pagination from "../../../ui/layout/Pagination.vue";
import SelectionActionsBar from "../../../ui/layout/SelectionBar.vue";
import MainBtn from "../../../buttons/MainBtn.vue";
import ContentTabModal from "../../../modals/ContentTabModal.vue";
import ConfirmModal from "../../../modals/ConfirmModal.vue";
import AddCustomContentModal from "../../../modals/AddCustomContentModal.vue";
import { useToastStore } from "../../../../store/toastStore.js";

export default {
  name: 'CustomContentManagement',
  components: {
    AddCustomContentModal,
    ConfirmModal,
    ContentTabModal,
    MainBtn,
    ContentGrid,
    Pagination,
    SelectionActionsBar
  },
  data() {
    return {
      customContents: [],
      loading: true,
      selectionMode: false,
      selectedContents: [],
      showDeleteModal: false,
      isAddModalOpen: false,
      deleteMessage: '',
      toastStore: null
    }
  },
  created() {
    this.toastStore = useToastStore();
    this.fetchCustomContents();
  },
  methods: {
    async fetchCustomContents() {
      try {
        this.loading = true;
        const response = await this.$http.get('/content/custom');
        if (response.data.data) {
          this.customContents = response.data.data.map(content => ({
            ...content,
            type: 'content'
          }));
        }
      } catch (error) {
        console.error('Error fetching custom contents: ', error);
        this.customContents = [];
        this.toastStore.error('Failed to load custom contents');
      } finally {
        this.loading = false;
      }
    },

    async addCustomContent(content) {
      this.loading = true;
      try {
        await this.fetchCustomContents();
        this.toastStore.success('Custom content added successfully');
      } catch (e) {
        console.error('Error adding custom content', e);
        this.toastStore.error('Failed to add custom content');
      } finally {
        this.loading = false;
        this.closeAddContentModal();
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
      this.selectedContents = [...this.customContents];
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
      this.deleteMessage = `Are you sure you want to delete ${this.selectedContents.length} custom contents? The corresponding records will be also deleted.`;
    },

    cancelDelete() {
      this.showDeleteModal = false;
    },

    async deleteSelected() {
      this.showDeleteModal = false;
      this.loading = true;

      try {
        const deletePromises = [];
        const selectedCount = this.selectedContents.length;
        
        for (const content of this.selectedContents) {
          deletePromises.push(this.$http.delete(`/content/id/${content.id}`));
        }

        const results = await Promise.all(deletePromises);
        const allSuccessful = results.every(response => response.data.code === 200);

        if (allSuccessful) {
          console.log(`Successfully deleted ${selectedCount} custom contents`);
          await this.fetchCustomContents();
          this.cancelSelectionMode();
          this.toastStore.success(`Deleted ${selectedCount} custom content${selectedCount > 1 ? 's' : ''}`);
        } else {
          console.error('Error deleting custom contents');
          console.log(results);
          this.toastStore.error('Failed to delete some custom contents');
        }
      } catch (error) {
        console.error('Error deleting custom contents', error);
        this.toastStore.error('Failed to delete custom contents');
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

.custom-content-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-lg);
  position: relative;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl) 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--border-light);
  border-radius: 50%;
  border-top-color: var(--primary);
  animation: spin 1s linear infinite;
  margin-bottom: var(--spacing-md);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>