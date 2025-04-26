<template>
  <div class="form-group">
    <label>Tags</label>
    <!-- select existed tags -->
    <div class="tags-container">
      <div class="tags-list">
        <div
            v-for="tag in availableTags"
            :key="tag.id"
            :class="['tag-item', { 'tag-selected': selectedTags.some(t => t.id === tag.id) }]"
            @click="toggleTag(tag)"
        >
          {{ tag.tagName }}
        </div>
      </div>
      <!-- or add new tag -->
      <div class="add-tag-input">
        <input
            type="text"
            v-model="newTagName"
            placeholder="Add new tag"
            class="form-control"
            @keyup.enter="createNewTag"
        />
        <button class="add-tag-button" @click="createNewTag" :disabled="!newTagName.trim()">+</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TagSelector',
  props: {
    modelValue: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      availableTags: [],
      selectedTags: [],
      newTagName: '',
    };
  },
  watch: {
    modelValue: {
      immediate: true,
      handler(newValue) {
        if (newValue && JSON.stringify(newValue) !== JSON.stringify(this.selectedTags)) {
          this.selectedTags = JSON.parse(JSON.stringify(newValue));
        }
      }
    },
    selectedTags: {
      deep: true,
      handler(newValue) {
        if (JSON.stringify(newValue) !== JSON.stringify(this.modelValue)) {
          this.$emit('update:modelValue', JSON.parse(JSON.stringify(newValue)));
        }
      }
    }
  },
  created() {
    this.fetchTags();
  },
  methods: {
    async fetchTags() {
      try {
        const response = await this.$http.get('/tag');
        if (response && response.data && response.data.data) {
          this.availableTags = response.data.data;
        }
      } catch (error) {
        console.error('Error fetching tags:', error);
      }
    },
    toggleTag(tag) {
      const index = this.selectedTags.findIndex(t => t.id === tag.id);
      if (index > -1) {
        this.selectedTags.splice(index, 1);
      } else {
        this.selectedTags.push(tag);
      }
    },
    async createNewTag() {
      if (!this.newTagName.trim()) return;
      try {
        const response = await this.$http.post('/tag', {
          tagName: this.newTagName.trim()
        });
        if (response && response.data && response.data.data) {
          const newTag = response.data.data;
          this.availableTags.push(newTag);
          this.selectedTags.push(newTag);
          this.newTagName = '';
        }
      } catch (error) {
        console.error('Error creating tag:', error);
      }
    }
  }
};
</script>

<style scoped>
/* Tags */
.tags-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.tag-item {
  padding: var(--spacing-sm) var(--spacing-lg);
  background-color: var(--background-subtle);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-full);
  font-size: var(--font-fontSize-sm);
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-level1-default);
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-level1-hover);
}

.tag-selected {
  background-color: rgba(var(--accent-info-rgb), 0.1);
  border-color: var(--accent-info);
  color: var(--accent-info);
  box-shadow: 0 0 0 2px rgba(var(--accent-info-rgb), 0.15);
}

.add-tag-input {
  display: flex;
  margin-top: var(--spacing-md);
}

.add-tag-input input {
  flex: 1;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.add-tag-button {
  background-color: var(--accent-info);
  color: white;
  border: none;
  min-width: 36px;
  height: 100%;
  border-top-right-radius: var(--border-radius-lg);
  border-bottom-right-radius: var(--border-radius-lg);
  cursor: pointer;
  font-size: var(--font-fontSize-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.add-tag-button:hover:not(:disabled) {
  background-color: rgba(var(--accent-info-rgb), 0.8);
}

.add-tag-button:disabled {
  background-color: var(--border-medium);
  cursor: not-allowed;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-sm);
  display: block;
}

.form-control {
  padding: var(--spacing-md) var(--spacing-lg);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-lg);
  font-size: var(--font-fontSize-base);
  transition: all 0.2s ease;
  background-color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-info);
  box-shadow: 0 0 0 3px rgba(var(--accent-info-rgb), 0.15);
}
</style>