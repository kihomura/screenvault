<template>
  <div class="list-page">
    <!-- header -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">Play Lists</h1>
      </div>
      <div class="header-actions">
        <main-btn type="highlight" @click="showAddModal = true">
          Create
        </main-btn>
        <main-btn type="secondary" @click="toggleManageMode">
          {{ isManageMode ? 'Complete' : 'Manage' }}
        </main-btn>
      </div>
    </div>

    <div class="lists-container">
      <!-- no list state -->
      <div v-if="lists.length === 0" class="empty-state">
        <div class="empty-icon">📋</div>
        <h3 class="empty-title">No Lists Yet</h3>
        <p class="empty-text">Create your first list to get start!</p>
      </div>

      <div v-else class="lists-wrapper">
        <!-- list items -->
        <list-item
            v-for="(list, index) in lists"
            :key="list.id"
            :list="list"
            :index="index"
            :isManageMode="isManageMode"
            @reorder="handleReorder"
            @show-delete-confirm="showConfirmModal"
        />
      </div>
    </div>

    <!-- create new list -->
    <add-list-modal
        :show="showAddModal"
        @close="showAddModal = false"
        @create="addNewList"
    />

    <!-- confirm delete modal -->
    <confirm-modal
        :visible="deleteModalVisible"
        :title="'Delete List'"
        :message="deleteMessage"
        :confirm-text="'Delete'"
        :cancel-text="'Cancel'"
        type="danger"
        @confirm="deleteList"
        @cancel="deleteModalVisible = false"
    />
  </div>
</template>

<script>
import ListItem from '../components/ui/list/ListItem.vue';
import AddListModal from "../components/modals/AddListModal.vue";
import MainBtn from "../components/buttons/MainBtn.vue";
import ConfirmModal from "../components/modals/ConfirmModal.vue";

export default {
  name: 'playlist',
  components: {
    MainBtn,
    ListItem,
    AddListModal,
    ConfirmModal
  },
  data() {
    return {
      isManageMode: false,
      showAddModal: false,
      isToggling: false,
      lists: [],
      // store the custom order of lists
      storageKey: 'user-playlist-order',
      deleteModalVisible: false,
      listToDelete: null,
      deleteMessage: '',
      isDeleting: null
    }
  },
  methods: {
    toggleManageMode() {
      if (this.isToggling) return;
      this.isToggling = true;
      this.isManageMode = !this.isManageMode;
      console.log('Manage mode:', this.isManageMode);
      setTimeout(() => {
        this.isToggling = false;
      }, 300);
    },
    async addNewList(newList) {
      try {
        const response = await this.$http.post(`/playlist`, newList);
        if (response.data && response.data.data) {
          this.lists.unshift(response.data.data);
          this.saveListOrderToLocalStorage();
        }
      } catch (error) {
        console.log('Error adding new list: ',error);
      }
    },
    showConfirmModal(list) {
      this.listToDelete = list;
      this.deleteMessage = `Are you sure you want to delete list '${list.listName}'?`;
      this.deleteModalVisible = true;
    },
    async deleteList() {
      if (!this.listToDelete || this.isDeleting) return;
      try {
        const listToDeleteId = this.listToDelete.id;
        this.isDeleting = true;
        const response = await this.$http.delete(`/playlist/id/${listToDeleteId}`);

        if (response.data && response.data.code === 200) {
          this.lists = this.lists.filter(list => list.id !== listToDeleteId);
          this.saveListOrderToLocalStorage();
        } else {
          console.error('Server returned error:', response.data);
        }
      } catch (error) {
        console.log('Error deleting list: ', error);
      } finally {
        this.deleteModalVisible = false;
        this.listToDelete = null;
        this.isDeleting = false;
      }
    },
    async fetchAllLists() {
      try {
        const response = await this.$http.get(`/playlist`);
        let fetchedLists = response.data.data;

        const savedOrder = this.getListOrderFromLocalStorage();

        if (savedOrder && savedOrder.length > 0) {
          fetchedLists = this.reorderListsBasedOnSavedOrder(fetchedLists, savedOrder);
        }

        this.lists = fetchedLists;
        this.saveListOrderToLocalStorage();
      } catch (error) {
        console.error(`Error getting lists: `, error);
      }
    },
    reorderListsBasedOnSavedOrder(fetchedLists, savedOrder) {
      const orderedLists = [];

      // add the list in the preserved order
      savedOrder.forEach(id => {
        const foundList = fetchedLists.find(list => list.id === id);
        if (foundList) {
          orderedLists.push(foundList);
        }
      });
      // add any list that might be new and not in the preserved order
      fetchedLists.forEach(list => {
        if (!savedOrder.includes(list.id)) {
          orderedLists.push(list);
        }
      });

      return orderedLists;
    },
    handleReorder({ fromIndex, toIndex }) {
      if (fromIndex !== toIndex) {
        const listToMove = this.lists[fromIndex];

        // delete from the original position
        this.lists.splice(fromIndex, 1);
        // insert into new position
        this.lists.splice(toIndex, 0, listToMove);

        this.saveListOrderToLocalStorage();
      }
    },
    saveListOrderToLocalStorage() {
      // store list id order to localStorage
      const listIds = this.lists.map(list => list.id);
      localStorage.setItem(this.storageKey, JSON.stringify(listIds));
    },
    getListOrderFromLocalStorage() {
      const savedOrder = localStorage.getItem(this.storageKey);
      return savedOrder ? JSON.parse(savedOrder) : null;
    }
  },
  mounted() {
    this.fetchAllLists();
  }
}
</script>

<style scoped>
.list-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-lg);
  position: relative;
}

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

.header-actions {
  display: flex;
  gap: var(--spacing-md);
}

.lists-container {
  min-height: 300px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl) 0;
  text-align: center;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: var(--spacing-lg);
}

.empty-title {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-xl);
  font-weight: var(--font-fontWeight-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--spacing-md) 0;
}

.empty-text {
  font-size: var(--font-fontSize-base);
  color: var(--text-secondary);
  margin: 0 0 var(--spacing-xl) 0;
  max-width: 320px;
}

.lists-wrapper {
  display: flex;
  flex-direction: column;
}
</style>