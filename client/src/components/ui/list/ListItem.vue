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
    <div class="list-content" @click="navigateToList">
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
        <h3 class="list-name">{{ list.listName }}</h3>
        <p class="list-meta">{{ contentCount }} contents</p>
      </div>
    </div>

    <!-- manage controls -->
    <div v-if="isManageMode" class="list-actions">
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
      imgPrefix: 'https://image.tmdb.org/t/p/w1280'
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
  height: 40px;
  width: 40px;
  background: transparent;
  border: none;
  cursor: grab;
  color: var(--text-muted);
  margin-right: var(--spacing-sm);
}

.drag-handle:active {
  cursor: grabbing;
}

.drag-icon {
  font-style: normal;
  font-size: 1.5rem;
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