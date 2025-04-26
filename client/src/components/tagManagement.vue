<template>
  <div class="tag-management">

    <!-- search and create new -->
    <div class="tag-management-header">
      <div class="search-container">
        <input
            type="text"
            v-model="searchQuery"
            placeholder="Search tags..."
            class="search-input"
        />
        <span class="search-icon">🔍</span>
      </div>
      <div class="inline-create-container">
        <input
            type="text"
            v-model="newTagName"
            placeholder="Tag name"
            class="create-input"
            @keyup.enter="createTag"
        />
        <main-btn type="highlight" @click="createTag" :disabled="!newTagName.trim()">
          Add New
        </main-btn>
      </div>
    </div>

    <!-- tag list -->
    <div class="tags-grid">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading tags...</p>
      </div>

      <div v-else-if="filteredTags.length === 0" class="empty-state">
        <p>No tags found</p>
      </div>

      <div v-else
           v-for="tag in filteredTags"
           :key="tag.id"
           class="tag-card"
      >

        <button class="delete-icon" @click="confirmDeleteTag(tag)">×</button>

        <!-- edit tagName -->
        <div class="tag-card-header">
          <template v-if="editingTagId === tag.id">
            <div class="edit-name-container">
              <input
                  type="text"
                  v-model="editingTagName"
                  class="edit-tag-input"
                  ref="editInput"
                  @keyup.enter="saveEdit(tag)"
                  @keyup.esc="cancelEdit"
              />
              <div class="edit-actions">
                <main-btn type="text" size="small" @click="saveEdit(tag)">Save</main-btn>
                <main-btn type="text" size="small" @click="cancelEdit">Cancel</main-btn>
              </div>
            </div>
          </template>
          <!-- or just show tagName -->
          <template v-else>
            <h3 class="tag-name">{{ tag.tagName }}</h3>
          </template>

          <!-- related content number -->
          <div class="tag-count">
            <span class="count-number">{{ tagContentCounts[tag.id] || 0 }}</span>
            <span class="count-label">content items</span>
          </div>
        </div>

        <div v-if="editingTagId !== tag.id" class="tag-card-actions">
          <main-btn type="text" @click="startEdit(tag)">Edit</main-btn>
        </div>
      </div>
    </div>

    <confirm-modal
        :visible="showDeleteModal"
        :title="'Delete Tag'"
        :message="`Are you sure you want to delete the tag '${currentTag.tagName}'?${
        tagContentCounts[currentTag.id] > 0
          ? ' This tag is used in ' + tagContentCounts[currentTag.id] + ' contents.'
          : ''
      }`"
        :confirm-text="'Delete'"
        :cancel-text="'Cancel'"
        type="danger"
        @confirm="deleteTag"
        @cancel="showDeleteModal = false"
    />
  </div>
</template>

<script>
import MainBtn from "./buttons/MainBtn.vue";
import ConfirmModal from "./modals/ConfirmModal.vue";

export default {
  name: 'TagManagement',
  components: {
    MainBtn,
    ConfirmModal
  },
  data() {
    return {
      tags: [],
      tagContentCounts: {},
      loading: true,
      searchQuery: '',
      newTagName: '',
      editingTagId: null,
      editingTagName: '',
      currentTag: {
        id: null,
        tagName: ''
      },
      showDeleteModal: false,
      isCreating: false
    };
  },
  computed: {
    filteredTags() {
      if (!this.searchQuery) {
        return this.tags;
      }

      const query = this.searchQuery.toLowerCase();
      return this.tags.filter(tag =>
          tag.tagName.toLowerCase().includes(query)
      );
    }
  },
  created() {
    this.fetchTags();
  },
  methods: {
    async fetchTags() {
      this.loading = true;
      try {
        const response = await this.$http.get('/tag');
        if (response && response.data && response.data.data) {
          this.tags = response.data.data;
          this.tags.forEach(tag => {
            this.fetchTagContentCount(tag.id);
          });
        }
      } catch (error) {
        console.error('Error fetching tags:', error);
      } finally {
        this.loading = false;
      }
    },

    async fetchTagContentCount(tagId) {
      try {
        const response = await this.$http.get(`/tag-content/tag/${tagId}`);
        if (response && response.data && response.data.data) {
          this.tagContentCounts = {
            ...this.tagContentCounts,
            [tagId]: response.data.data.length
          };
        }
      } catch (error) {
        console.error(`Error fetching content count for tag ${tagId}:`, error);
        this.tagContentCounts = {
          ...this.tagContentCounts,
          [tagId]: 0
        };
      }
    },

    startEdit(tag) {
      this.editingTagId = tag.id;
      this.editingTagName = tag.tagName;
      this.$nextTick(() => {
        if (this.$refs.editInput) {
          this.$refs.editInput.focus();
        }
      });
    },
    cancelEdit() {
      this.editingTagId = null;
      this.editingTagName = '';
    },
    async saveEdit(tag) {
      if (!this.editingTagName.trim() || this.isCreating) return;
      try {
        this.isCreating = true;
        const response = await this.$http.post('/tag', {
          id: tag.id,
          tagName: this.editingTagName.trim()
        });

        if (response && response.data && response.data.data) {
          const updatedTag = response.data.data;
          const index = this.tags.findIndex(t => t.id === updatedTag.id);
          if (index !== -1) {
            this.tags.splice(index, 1, updatedTag);
          }
          this.editingTagId = null;
          this.editingTagName = '';
        }
      } catch (error) {
        console.error('Error updating tag:', error);
      } finally {
        this.isCreating = false;
      }
    },
    async createTag() {
      if (!this.newTagName.trim()) return;
      try {
        const response = await this.$http.post('/tag', {
          tagName: this.newTagName.trim()
        });

        if (response && response.data && response.data.data) {
          const newTag = response.data.data;
          this.tags.push(newTag);
          this.tagContentCounts = {
            ...this.tagContentCounts,
            [newTag.id]: 0
          };
          this.newTagName = '';
        }
      } catch (error) {
        console.error('Error creating tag:', error);
      }
    },
    confirmDeleteTag(tag) {
      this.currentTag = { ...tag };
      this.showDeleteModal = true;
    },
    async deleteTag() {
      if (!this.currentTag.id) return;

      try {
        await this.$http.delete(`/tag/id/${this.currentTag.id}`);

        const index = this.tags.findIndex(t => t.id === this.currentTag.id);
        if (index !== -1) {
          this.tags.splice(index, 1);
          const newCounts = { ...this.tagContentCounts };
          delete newCounts[this.currentTag.id];
          this.tagContentCounts = newCounts;
        }

        this.showDeleteModal = false;
        this.currentTag = { id: null, tagName: '' };
      } catch (error) {
        console.error('Error deleting tag:', error);
      }
    }
  }
};
</script>
<style>
.tag-management {
  font-family: var(--font-fontFamily-primary);
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-xl);
  color: var(--text-primary);
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level1-default);
}

/* Header styling with search and create */
.tag-management-header {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
}

.search-container {
  position: relative;
  flex: 1;
  min-width: 250px;
}

.search-input {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-xl);
  font-size: var(--font-fontSize-base);
  border: 2px solid var(--border-light);
  border-radius: var(--border-radius-full);
  background-color: var(--background-base);
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: var(--highlight);
  box-shadow: 0 0 0 3px rgba(174, 202, 95, 0.2);
}

.search-icon {
  position: absolute;
  right: var(--spacing-lg);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
  pointer-events: none;
}

.inline-create-container {
  display: flex;
  gap: var(--spacing-md);
  flex: 1;
  min-width: 250px;
}

.create-input {
  flex: 1;
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--font-fontSize-base);
  border: 2px solid var(--border-light);
  border-radius: var(--border-radius-md);
  background-color: var(--background-base);
  transition: all 0.3s ease;
}

.create-input:focus {
  outline: none;
  border-color: var(--highlight);
  box-shadow: 0 0 0 3px rgba(174, 202, 95, 0.2);
}

/* Tags grid layout */
.tags-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-xl);
}

/* Tag card styling */
.tag-card {
  position: relative;
  padding: var(--spacing-xl);
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level1-default);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.tag-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background-color: var(--highlight);
  opacity: 0.7;
  transition: height 0.3s ease;
}

.tag-card:hover {
  box-shadow: var(--shadow-level2-hover);
  transform: translateY(-3px);
}

.tag-card:hover::before {
  height: 6px;
}

.tag-card-header {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.tag-name {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-xl);
  font-weight: var(--font-fontWeight-semibold);
  color: var(--text-primary);
  margin: 0;
  line-height: var(--font-lineHeight-tight);
  word-break: break-word;
}

.tag-count {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-xs);
}

.count-number {
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-bold);
  color: var(--text-primary);
}

.count-label {
  font-size: var(--font-fontSize-sm);
  color: var(--text-muted);
}

.tag-card-actions {
  margin-top: auto;
  padding-top: var(--spacing-md);
}

/* Delete button */
.delete-icon {
  position: absolute;
  top: var(--spacing-md);
  right: var(--spacing-md);
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
  border: none;
  border-radius: var(--border-radius-full);
  font-size: var(--font-fontSize-xl);
  color: var(--text-muted);
  opacity: 0.6;
  cursor: pointer;
  transition: all 0.2s ease;
}

.delete-icon:hover {
  background-color: var(--accent-error);
  color: white;
  opacity: 1;
}

/* Edit mode styling */
.edit-name-container {
  width: 100%;
}

.edit-tag-input {
  width: 100%;
  padding: var(--spacing-md);
  font-size: var(--font-fontSize-base);
  border: 2px solid var(--highlight);
  border-radius: var(--border-radius-md);
  background-color: var(--background-base);
  transition: all 0.3s ease;
}

.edit-tag-input:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(174, 202, 95, 0.2);
}

.edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
}

/* Loading state */
.loading-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl);
  color: var(--text-secondary);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(var(--primary-rgb), 0.2);
  border-top-color: var(--highlight);
  border-radius: 50%;
  animation: spin 1s infinite linear;
  margin-bottom: var(--spacing-lg);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Empty state */
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl);
  color: var(--text-secondary);
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level1-default);
  min-height: 150px;
}

.empty-state p {
  font-size: var(--font-fontSize-lg);
  color: var(--text-muted);
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .tag-management-header {
    flex-direction: column;
  }

  .tags-grid {
    grid-template-columns: 1fr;
  }
}

/* Animation for new tags */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.tag-card {
  animation: fadeIn 0.4s ease-out;
}
</style>