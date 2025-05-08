<template>
  <div
      class="list-item"
      :class="{ 'manage-mode': isManageMode }"
      draggable="true"
      @dragstart="onDragStart"
      @dragover.prevent
      @dragenter.prevent
      @drop="onDrop"
  >
    <div class="list-content" @click="handleItemClick">
      <div class="list-thumbnail">
        <img
            v-if="listThumbnail"
            :src="imgPrefix + listThumbnail"
            alt="List thumbnail"
            class="thumbnail-image"
        />
        <div v-else class="thumbnail-placeholder">
          <span>no image</span>
        </div>
      </div>

      <div class="list-info">
        <!-- Edit mode input field -->
        <div v-if="isEditing && isManageMode" class="list-name-edit-container" @click.stop>
          <input
              type="text"
              v-model="editedListName"
              class="list-name-edit-input"
              ref="editInput"
              @keyup.enter="saveListName"
              @keyup.esc="cancelEdit"
          />
          <div class="edit-actions">
            <button class="edit-action-btn save-btn" @click="saveListName">Save</button>
            <button class="edit-action-btn cancel-btn" @click="cancelEdit">Cancel</button>
          </div>
        </div>
        <!-- Regular display mode -->
        <h3 v-else class="list-name">{{ list.listName }}</h3>
        <p class="list-meta">{{ contentCount }} contents</p>
      </div>
    </div>

    <!-- manage controls -->
    <div v-if="isManageMode" class="list-actions">
      <button v-if="!isEditing" class="edit-btn" @click.stop="toggleEditMode" aria-label="Edit list name">
        <i class="edit-icon">✎</i>
      </button>
      <button class="drag-handle" aria-label="drag to order">
        <i class="drag-icon">≡</i>
      </button>
      <delete-button @click.stop="requestDelete"></delete-button>
    </div>
  </div>
</template>

<script>
import DeleteButton from "../../buttons/DeleteBtn.vue";

export default {
  name: 'ListItem',
  components: {
    DeleteButton
  },
  data() {
    return {
      contents: [],
      contentCount: 0,
      listContents: [],
      thumbnailUrl: null,
      imgPrefix: 'https://image.tmdb.org/t/p/w1280',
      isEditing: false,
      editedListName: ''
    };
  },
  props: {
    list: {
      type: Object,
      required: true
    },
    isManageMode: {
      type: Boolean,
      default: false
    },
    index: {
      type: Number,
      required: true
    }
  },
  computed: {
    listThumbnail() {
      return this.thumbnailUrl;
    }
  },
  watch: {
    isManageMode(newVal) {
      if (!newVal && this.isEditing) {
        this.isEditing = false;
      }
    }
  },
  methods: {
    async fetchContents() {
      try {
        const response = await this.$http.get(`/list-content/list/${this.list.id}`);
        const listContents = response.data.data;

        if (listContents && listContents.length > 0) {
          this.contentCount = listContents.length;
          this.listContents = listContents;

          await this.setListThumbnail();

          await listContents.forEach((listContent) => {
            this.$http.get(`/content/id/${listContent.contentId}`).then((response) => {
              this.contents.push(response.data.data)
            })
          });
        }
      } catch (error) {
        console.log("Error fetching contents: ", error)
      }
    },
    async setListThumbnail() {
      if (this.listContents && this.listContents.length > 0) {
        try {
          const imageUrl = await this.getListThumbnail();
          this.thumbnailUrl = imageUrl;
        } catch (error) {
          console.error("Error setting thumbnail:", error);
          this.thumbnailUrl = null;
        }
      }
    },
    async getListThumbnail() {
      if (!this.listContents || this.listContents.length === 0) {
        return null;
      }

      const latestAddedContent = this.listContents.reduce((latestItem, currentItem) => {
        return new Date(currentItem.addTime) > new Date(latestItem.addTime)
            ? currentItem
            : latestItem;
      }, this.listContents[0]);

      const response = await this.$http.get(`/content/id/${latestAddedContent.contentId}`);
      const content = response.data.data;

      return content && content.image ? content.image : null;
    },
    handleItemClick() {
      if (this.isManageMode) {
        if (!this.isEditing) {
          this.toggleEditMode();
        }
      } else {
        this.navigateToList();
      }
    },
    navigateToList() {
      if (!this.isManageMode) {
        this.$router.push(`/playlist/${this.list.id}`);
      }
    },
    requestDelete() {
      this.$emit('show-delete-confirm', this.list);
    },
    onDragStart(event) {
      if (this.isManageMode) {
        event.dataTransfer.setData('text/plain', this.index);
        event.dataTransfer.effectAllowed = 'move';
      }
    },
    onDrop(event) {
      if (this.isManageMode) {
        const fromIndex = parseInt(event.dataTransfer.getData('text/plain'));
        this.$emit('reorder', {fromIndex, toIndex: this.index});
      }
    },
    toggleEditMode() {
      if (this.isManageMode && !this.list.isDefault) {
        this.isEditing = true;
        this.editedListName = this.list.listName;
        this.$nextTick(() => {
          if (this.$refs.editInput) {
            this.$refs.editInput.focus();
          }
        });
      }
    },
    async saveListName() {
      if (!this.editedListName.trim()) {
        this.cancelEdit();
        return;
      }
      
      if (this.editedListName !== this.list.listName) {
        try {
          const updatedList = { ...this.list, listName: this.editedListName };
          const response = await this.$http.post('/playlist', updatedList);
          
          if (response.data && response.data.code === 200) {
            this.$emit('list-updated', response.data.data);
            // Update the local list name
            this.list.listName = this.editedListName;
          } else {
            console.error('Error updating list name:', response.data);
          }
        } catch (error) {
          console.error('Error updating list name:', error);
        }
      }
      
      this.isEditing = false;
    },
    cancelEdit() {
      this.isEditing = false;
      this.editedListName = this.list.listName;
    }
  },
  created() {
    this.fetchContents();
  }
}
</script>

<style scoped>
.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  background-color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
  transition: box-shadow 0.2s ease, transform 0.2s ease;
  margin-bottom: var(--spacing-lg);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.list-item:hover {
  box-shadow: var(--shadow-level1-hover);
}

.list-content {
  display: flex;
  align-items: center;
  flex-grow: 1;
}

.list-thumbnail {
  width: 80px;
  height: 80px;
  min-width: 80px;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  margin-right: var(--spacing-lg);
  background-color: var(--background-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.thumbnail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: var(--text-muted);
  font-size: var(--font-fontSize-xs);
  text-align: center;
  padding: var(--spacing-xs);
}

.list-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex-grow: 1;
}

.list-name {
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-semibold);
  font-size: var(--font-fontSize-lg);
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.list-meta {
  color: var(--text-muted);
  font-size: var(--font-fontSize-sm);
  margin: 0;
}

/* manage controls */
.list-item.manage-mode {
  cursor: grab;
  background-color: var(--background-subtle);
}

.list-item.manage-mode:active {
  cursor: grabbing;
  transform: scale(0.98);
}

.list-actions {
  display: flex;
  align-items: center;
}

.drag-handle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: var(--border-radius-full);
  background-color: transparent;
  border: none;
  cursor: grab;
  margin-right: var(--spacing-sm);
  color: var(--text-muted);
  opacity: 0.7;
}

.drag-handle:hover {
  opacity: 1;
  background-color: var(--background-muted);
}

.drag-icon {
  font-style: normal;
  font-size: 20px;
  line-height: 1;
}

/* Edit mode styles */
.list-name-edit-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  margin-bottom: var(--spacing-sm);
}

.list-name-edit-input {
  width: 100%;
  padding: var(--spacing-sm);
  font-size: var(--font-fontSize-lg);
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-semibold);
  border: 2px solid var(--highlight);
  border-radius: var(--border-radius-md);
  background-color: var(--background-base);
  color: var(--text-primary);
  transition: all 0.3s ease;
}

.list-name-edit-input:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(174, 202, 95, 0.2);
}

.edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  margin-top: var(--spacing-sm);
}

.edit-action-btn {
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  font-size: var(--font-fontSize-sm);
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
}

.save-btn {
  background-color: var(--highlight);
  color: white;
}

.save-btn:hover {
  background-color: var(--primary);
}

.cancel-btn {
  background-color: var(--background-subtle);
  color: var(--text-secondary);
}

.cancel-btn:hover {
  background-color: var(--background-muted);
}

.edit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: var(--border-radius-full);
  background-color: var(--background-muted);
  border: none;
  cursor: pointer;
  margin-right: var(--spacing-sm);
  transition: all 0.2s ease;
  color: var(--text-primary);
}

.edit-btn:hover {
  background-color: var(--highlight);
  color: white;
}

.edit-icon {
  font-style: normal;
  font-size: 16px;
}

@media (max-width: 640px) {
  .list-item {
    padding: var(--spacing-md);
  }

  .list-thumbnail {
    width: 60px;
    height: 60px;
    min-width: 60px;
    margin-right: var(--spacing-md);
  }

  .list-name {
    font-size: var(--font-fontSize-base);
  }
}
</style>