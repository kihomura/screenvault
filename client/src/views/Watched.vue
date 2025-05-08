<template>
  <div class="watch-page">
    <!-- Header with Filter Controls -->
    <div class="page-header" v-if="!selectionMode">
      <div class="header-content">
        <h2>Watching Recordings</h2>
      </div>
      <div class="header-controls">
        <filter-controls
            :ratingFilter="ratingFilter"
            :yearFilter="yearFilter"
            :monthFilter="monthFilter"
            :availableYears="availableYears"
            :months="months"
            @update:ratingFilter="ratingFilter = $event"
            @update:yearFilter="yearFilter = $event"
            @update:monthFilter="monthFilter = $event"
        />
        <sort-controls
            :sortBy="sortBy"
            sortType="record"
            @update:sortBy="sortBy = $event"
        />
      </div>
    </div>

    <!-- Selection Actions Bar (replaces filters when in selection mode) -->
    <selection-actions-bar
        v-if="selectionMode"
        :selectedCount="selectedRecords.length"
        :totalCount="filteredRecords.length"
        @add-to-list="addToList"
        @confirm-delete="confirmDelete"
        @select-all="selectAll"
        @clear-selection="clearSelection"
        @cancel="cancelSelectionMode"
    />

    <!-- Content Grid with ContentCards -->
    <content-grid
        :items="paginatedRecordsWithType"
        itemType="record"
        :selectionMode="selectionMode"
        :selectedItems="selectedRecords"
        :showAddCard="currentPage === 1 && !selectionMode"
        :totalItems="filteredRecords.length"
        addCardText="Track your latest watch"
        @open-item="openRecordDetails"
        @toggle-selection="toggleCardSelection"
        @add-new="openAddRecordingModal"
    />

    <!-- Add New link for pages after first page -->
    <div v-if="currentPage > 1 && !selectionMode" class="add-new-link-container">
      <main-btn type="text" class="add-new-link" @click="goToFirstPageAndAdd">
        <span class="add-new-icon">+</span>
        Back to first page to add new recording
      </main-btn>
    </div>

    <!-- Pagination controls -->
    <pagination
        :currentPage="currentPage"
        :totalPages="totalPages"
        @page-changed="goToPage"
    />

    <!-- ContentTabModal for selecting content -->
    <content-tab-modal
        :isOpen="isAddModalOpen"
        :visibleTabs="['search', 'custom', 'wishlist']"
        mode="addRecord"
        v-model:multiSelect="isMultiSelectEnabled"
        @close="closeAddRecordingModal"
        @content-selected="handleContentSelected"
    />

    <!-- AddRecordModal for adding details to selected content -->
    <add-recording-modal
        :isOpen="isRecordDetailsModalOpen"
        :selectedContent="selectedContent"
        @close="closeRecordDetailsModal"
        @save="saveNewRecording"
    />

    <confirm-modal
        :visible="showDeleteModal"
        :title="'Delete Record'"
        :message="deleteMessage"
        :confirm-text="'Delete'"
        :cancel-text="'Cancel'"
        type="danger"
        @confirm="deleteSelected"
        @cancel="cancelDelete"
    />

    <add-to-list-modal
        v-if="showAddToListModal"
        @cancel="cancelAddToList"
        @create="handleCreateList"
    />
  </div>
</template>

<script>
import ContentGrid from "../components/ui/CardGrid.vue";
import AddRecordingModal from "../components/modals/AddRecordModal.vue";
import AddToListModal from "../components/modals/AddToListModal.vue";
import Pagination from "../components/ui/Pagination.vue";
import FilterControls from "../components/controls/FilterControls.vue";
import SortControls from "../components/controls/SortControls.vue";
import SelectionActionsBar from "../components/ui/SelectionBar.vue";
import MainBtn from "../components/buttons/MainBtn.vue";
import ContentTabModal from "../components/modals/ContentTabModal.vue";
import ConfirmModal from "../components/modals/ConfirmModal.vue";
import { useToastStore } from "../store/toastStore.js";

export default {
  name: 'WatchedPage',
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
      sortBy: 'date-desc',
      ratingFilter: 'all',
      yearFilter: 'all',
      monthFilter: 'all',
      records: [],
      isAddModalOpen: false,
      isRecordDetailsModalOpen: false,
      selectedContent: null,
      currentPage: 1,
      firstPageItems: 17,
      regularPageItems: 18,
      maxPagesToShow: 7,
      selectionMode: false,
      selectedRecords: [],
      showDeleteModal: false,
      showAddToListModal: false,
      newListName: '',
      hoveredCard: null,
      deleteMessage: '',
      isAdding: false,
      months: [
        { value: '01', label: 'January' },
        { value: '02', label: 'February' },
        { value: '03', label: 'March' },
        { value: '04', label: 'April' },
        { value: '05', label: 'May' },
        { value: '06', label: 'June' },
        { value: '07', label: 'July' },
        { value: '08', label: 'August' },
        { value: '09', label: 'September' },
        { value: '10', label: 'October' },
        { value: '11', label: 'November' },
        { value: '12', label: 'December' }
      ],
      toastStore: null,
      isMultiSelectEnabled: false
    }
  },
  created() {
    this.toastStore = useToastStore();
  },
  computed: {
    availableYears() {
      if (!this.records.length) return [];
      const years = new Set();
      this.records.forEach(record => {
        const date = new Date(record.watchDate);
        years.add(date.getFullYear());
      });
      return Array.from(years).sort((a, b) => b - a);
    },
    filteredRecords() {
      if (!this.records.length) return [];
      let filtered = [...this.records];

      if (this.ratingFilter !== 'all') {
        const minRating = parseInt(this.ratingFilter.split('-')[0]);
        filtered = filtered.filter(record => record.rate >= minRating);
      }

      if (this.yearFilter !== 'all') {
        filtered = filtered.filter(record => {
          const date = new Date(record.watchDate);
          return date.getFullYear() === parseInt(this.yearFilter);
        });
      }

      if (this.yearFilter !== 'all' && this.monthFilter !== 'all') {
        filtered = filtered.filter(record => {
          const date = new Date(record.watchDate);
          const month = String(date.getMonth() + 1).padStart(2, '0');
          return month === this.monthFilter;
        });
      }

      switch (this.sortBy) {
        case 'date-desc':
          return filtered.sort((a, b) => new Date(b.watchDate) - new Date(a.watchDate));
        case 'date-asc':
          return filtered.sort((a, b) => new Date(a.watchDate) - new Date(b.watchDate));
        case 'rating-desc':
          return filtered.sort((a, b) => b.rate - a.rate);
        case 'rating-asc':
          return filtered.sort((a, b) => a.rate - b.rate);
        default:
          return filtered;
      }
    },
    paginatedRecords() {
      const pageSize = this.currentPage === 1 ? this.firstPageItems : this.regularPageItems;
      let startIndex = this.currentPage === 1
          ? 0
          : this.firstPageItems + (this.currentPage - 2) * this.regularPageItems;
      return this.filteredRecords.slice(startIndex, startIndex + pageSize);
    },
    paginatedRecordsWithType() {
      // Add type property to each record for ContentGrid component
      return this.paginatedRecords.map(record => ({
        ...record,
        type: 'record'
      }));
    },
    totalPages() {
      if (this.filteredRecords.length <= this.firstPageItems) return 1;
      const remainingItems = this.filteredRecords.length - this.firstPageItems;
      return 1 + Math.ceil(remainingItems / this.regularPageItems);
    }
  },
  methods: {
    openAddRecordingModal() {
      this.isAddModalOpen = true;
    },
    closeAddRecordingModal() {
      this.isAddModalOpen = false;
    },
    handleContentSelected(content) {
      this.selectedContent = content;
      this.isAddModalOpen = false;
      this.isRecordDetailsModalOpen = true;
    },
    closeRecordDetailsModal() {
      this.isRecordDetailsModalOpen = false;
      this.selectedContent = null;
    },
    async getRecords() {
      try {
        const response = await this.$http.get('/record');
        if (response.data && response.data.data) {
          this.records = response.data.data;
        } else {
          console.error("Invalid response format", response);
        }
      } catch (error) {
        console.error("Error fetching records:", error);
      }
    },
    async saveNewRecording(data)  {
      if (this.isAdding) return;
      try {
        this.isAdding = true;
        const recordResponse = await this.$http.post('/record', data.recordingData);
        if (recordResponse.data && recordResponse.data.data) {
          for (const tag of data.newTags) {
            try {
              const tagResponse = await this.$http.post('/tag-content', tag);
              console.log('Tag added successfully:', tagResponse.data);
            } catch (error) {
              console.error('Failed to add tag:', tag, error);
            }
          }
          await this.getRecords();
          this.closeRecordDetailsModal();
          this.toastStore.success('New recording added successfully!');
        } else {
          console.error('Invalid response format when saving record:', recordResponse);
          this.toastStore.error('Failed to add new recording. Please try again.');
        }
      } catch (error) {
        console.error('Error saving new record:', error);
        this.toastStore.error('Failed to add new recording. Please try again.');
      } finally {
        this.isAdding = false;
      }
    },
    goToPage(page) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    goToFirstPageAndAdd() {
      this.currentPage = 1;
      window.scrollTo({ top: 0, behavior: 'smooth' });
      setTimeout(() => {
        this.openAddRecordingModal();
      }, 500);
    },
    openRecordDetails(record) {
      if (this.selectionMode) {
        this.toggleCardSelection(record);
      } else {
        localStorage.setItem('watchedPageNumber', this.currentPage)
        this.$router.push({ name: 'ContentDetail', params: { id: record.contentId } });
      }
    },
    toggleCardSelection(record) {
      const index = this.selectedRecords.findIndex(r => r.id === record.id);
      if (!this.selectionMode) this.selectionMode = true;
      if (index === -1) {
        this.selectedRecords.push(record);
      } else {
        this.selectedRecords.splice(index, 1);
        if (this.selectedRecords.length === 0) this.selectionMode = false;
      }
    },
    cancelSelectionMode() {
      this.selectionMode = false;
      this.selectedRecords = [];
    },
    selectAll() {
      this.selectedRecords = [...this.filteredRecords];
    },
    clearSelection() {
      this.selectedRecords = [];
      if (this.selectedRecords.length === 0) this.selectionMode = false;
    },
    confirmDelete() {
      if (this.selectedRecords.length === 0) return;
      this.showDeleteModal = true;
      this.deleteMessage = `Are you sure you want to delete ${this.selectedRecords.length} records?`;
    },
    cancelDelete() {
      this.showDeleteModal = false;
    },
    async deleteSelected() {
      try {
        let successCount = 0;
        let failureCount = 0;

        for (const record of this.selectedRecords) {
          try {
            const response = await this.$http.delete(`/record/id/${record.id}`);
            if (response.data && response.data.code === 200) {
              successCount++;
            } else {
              failureCount++;
            }
          } catch (error) {
            console.error(`Error deleting record ${record.id}:`, error);
            failureCount++;
          }
        }

        this.showDeleteModal = false;
        await this.getRecords();
        this.cancelSelectionMode();
        
        if (successCount > 0 && failureCount === 0) {
          this.toastStore.success(`Successfully deleted ${successCount} recording${successCount > 1 ? 's' : ''}.`);
        } else if (successCount > 0 && failureCount > 0) {
          this.toastStore.info(`Deleted ${successCount} recording${successCount > 1 ? 's' : ''}, but failed to delete ${failureCount}.`);
        } else {
          this.toastStore.error('Failed to delete recordings. Please try again.');
        }
      } catch (error) {
        console.error("Error during batch delete:", error);
        this.showDeleteModal = false;
        this.toastStore.error('An error occurred while deleting recordings.');
      }
    },
    addToList() {
      if (this.selectedRecords.length === 0) return;
      this.showAddToListModal = true;
    },
    cancelAddToList() {
      this.showAddToListModal = false;
      this.newListName = '';
    },
    handleCreateList(newListName) {
      console.log("Create new list:", newListName);
      this.cancelAddToList();
    },
  },
  watch: {
    ratingFilter() {
      this.currentPage = 1;
    },
    yearFilter() {
      this.monthFilter = 'all';
      this.currentPage = 1;
    },
    monthFilter() {
      this.currentPage = 1;
    },
    sortBy() {
      this.currentPage = 1;
    }
  },
  mounted() {
    this.getRecords();

    // restore page number from localstorage
    // for when come back from RecordDetail page
    const savedPage = localStorage.getItem('watchedPageNumber');
    if (savedPage) {
      this.currentPage = parseInt(savedPage);
      localStorage.setItem('watchedPageNumber', '');
    }
  }
}
</script>

<style scoped>
/* Remove local header styles as they're defined globally now */

.header-controls {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.watch-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-lg);
  position: relative;
}

.add-new-link-container {
  display: flex;
  justify-content: center;
  margin-bottom: var(--spacing-xl);
}

.add-new-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-lg);
  background-color: var(--background-subtle);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  color: var(--text-primary);
  font-size: var(--font-fontSize-sm);
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-new-link:hover {
  background-color: var(--interactive-hover);
  border-color: var(--border-medium);
}

.add-new-icon {
  font-size: var(--font-fontSize-base);
}
</style>