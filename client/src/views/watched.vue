<template>
  <div class="watch-page">
    <!-- Page Header with Filter Controls -->
    <filter-controls
        v-if="!selectionMode"
        :ratingFilter="ratingFilter"
        :yearFilter="yearFilter"
        :monthFilter="monthFilter"
        :sortBy="sortBy"
        :availableYears="availableYears"
        :months="months"
        @update:ratingFilter="ratingFilter = $event"
        @update:yearFilter="yearFilter = $event"
        @update:monthFilter="monthFilter = $event"
        @update:sortBy="sortBy = $event"
    />

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

    <!-- Content Grid with Cards -->
    <content-grid
        :records="paginatedRecords"
        :selectionMode="selectionMode"
        :selectedRecords="selectedRecords"
        :currentPage="currentPage"
        :showAddCard="currentPage === 1 && !selectionMode"
        :filteredRecordsCount="filteredRecords.length"
        @open-record="openRecordDetails"
        @toggle-selection="toggleCardSelection"
        @add-new="openAddRecordingModal"
    />

    <!-- Add New link for pages after first page -->
    <div v-if="currentPage > 1 && !selectionMode" class="add-new-link-container">
      <button class="add-new-link" @click="goToFirstPageAndAdd">
        <span class="add-new-icon">+</span>
        Back to first page to add new recording
      </button>
    </div>

    <!-- Pagination controls -->
    <pagination
        :currentPage="currentPage"
        :totalPages="totalPages"
        @page-changed="goToPage"
    />

    <!-- Modals -->
    <add-recording-modal
        :isOpen="isAddModalOpen"
        @close="closeAddRecordingModal"
        @save="saveNewRecording"
    />

    <delete-modal
        v-if="showDeleteModal"
        :count="selectedRecords.length"
        @cancel="cancelDelete"
        @confirm="deleteSelected"
    />

    <add-to-list-modal
        v-if="showAddToListModal"
        @cancel="cancelAddToList"
        @create="handleCreateList"
    />
  </div>
</template>

<script>
import ContentCard from "../components/ContentCard.vue";
import AddRecordingModal from "../components/modal/AddRecordingModal.vue";
import DeleteModal from "../components/modal/DeleteModal.vue";
import AddToListModal from "../components/modal/AddToListModal.vue";
import Pagination from "../components/ui/Pagination.vue";
import FilterControls from "../components/FilterContols.vue";
import SelectionActionsBar from "../components/SelectionActionBar.vue";
import ContentGrid from "../components/ContentGrid.vue";

export default {
  name: 'WatchedPage',
  components: {
    ContentCard,
    AddRecordingModal,
    DeleteModal,
    AddToListModal,
    Pagination,
    FilterControls,
    SelectionActionsBar,
    ContentGrid
  },
  data() {
    return {
      sortBy: 'date-desc',
      ratingFilter: 'all',
      yearFilter: 'all',
      monthFilter: 'all',
      records: [],
      isAddModalOpen: false,
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
      ]
    }
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
    async saveNewRecording(recordingData) {
      try {
        const response = await this.$http.post('/record', recordingData);
        if (response.data && response.data.data) {
          await this.getRecords();
          this.closeAddRecordingModal();
        } else {
          console.error("Invalid response format on save", response);
        }
      } catch (error) {
        console.error("Error saving new recording:", error);
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
      }, 300);
    },
    openRecordDetails(record) {
      if (this.selectionMode) {
        this.toggleCardSelection(record);
      } else {
        console.log("Open details for record:", record.id);
        // 可在这里加入路由跳转等逻辑
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
    isSelected(record) {
      return this.selectedRecords.some(r => r.id === record.id);
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

        console.log(`Deleted ${successCount} record${successCount !== 1 ? 's' : ''}; Failed to delete ${failureCount} record${failureCount !== 1 ? 's' : ''}.`);

        await this.getRecords();
        this.cancelSelectionMode();
      } catch (error) {
        console.error("Error during batch delete:", error);
        this.showDeleteModal = false;
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
    }
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
  }
}
</script>

<style scoped>
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