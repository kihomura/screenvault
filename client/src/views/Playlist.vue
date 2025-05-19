<template>
  <div class="list-page">
    <!-- header -->
    <div class="page-header">
      <div class="header-content">
        <h2>Play Lists</h2>
      </div>
      <div class="header-actions">
        <main-btn type="secondary" @click="toggleManageMode">
          {{ isManageMode ? 'Complete' : 'Manage' }}
        </main-btn>
      </div>
    </div>

    <!-- Inline InputButton for creating new lists -->
    <div class="create-list-container">
      <input-button
          id="create-list-input"
          placeholder="Enter list name"
          buttonLabel="Create"
          successText="Created!"
          @button-click="addNewList"
      />
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
            @list-updated="handleListUpdated"
        />
      </div>
    </div>

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
import InputButton from "../components/form/InputWithBtn.vue";
import MainBtn from "../components/buttons/MainBtn.vue";
import ConfirmModal from "../components/modals/ConfirmModal.vue";
import { useToastStore } from "../store/toastStore.js";
import { storageManager } from "../utils/storageManager.js";

export default {
  name: 'playlist',
  components: {
    MainBtn,
    ListItem,
    InputButton,
    ConfirmModal
  },
  data() {
    return {
      isManageMode: false,
      isToggling: false,
      lists: [],
      // store the custom order of lists
      storageKey: 'user-playlist-order',
      deleteModalVisible: false,
      listToDelete: null,
      deleteMessage: '',
      isDeleting: null,
      toastStore: null
    }
  },
  created() {
    this.toastStore = useToastStore();
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
    async addNewList(listName) {
      try {
        // create new list with the name from input
        const newList = { listName };
        const response = await this.$http.post(`/playlist`, newList);
        if (response.data && response.data.data) {
          this.lists.unshift(response.data.data);
          this.saveListOrderToLocalStorage();
          this.toastStore.success(`List "${listName}" created successfully`);
        }
      } catch (error) {
        console.log('Error adding new list: ',error);
        this.toastStore.error('Failed to create list. Please try again.');
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
        const listName = this.listToDelete.listName;
        this.isDeleting = true;
        const response = await this.$http.delete(`/playlist/id/${listToDeleteId}`);

        if (response.data && response.data.code === 200) {
          this.lists = this.lists.filter(list => list.id !== listToDeleteId);
          this.saveListOrderToLocalStorage();
          this.toastStore.success(`List "${listName}" deleted successfully`);
        } else {
          console.error('Server returned error:', response.data);
          this.toastStore.error('Failed to delete list. Please try again.');
        }
      } catch (error) {
        console.log('Error deleting list: ', error);
        this.toastStore.error('Failed to delete list. Please try again.');
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
        this.toastStore.error('Failed to load your lists. Please refresh the page.');
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
      storageManager.set(this.storageKey, listIds);
    },
    getListOrderFromLocalStorage() {
      const savedOrder = storageManager.get(this.storageKey);
      return savedOrder ? (Array.isArray(savedOrder) ? savedOrder : JSON.parse(savedOrder)) : null;
    },
    handleListUpdated(updatedList) {
      // Find the list in the array and update it
      const index = this.lists.findIndex(list => list.id === updatedList.id);
      if (index !== -1) {
        this.lists[index] = { ...this.lists[index], ...updatedList };
      }
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

.header-actions {
  display: flex;
  gap: var(--spacing-md);
}

.create-list-container {
  margin-bottom: var(--spacing-xl);
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