<template>
  <BaseWidget
      :id="id"
      :title="title"
      :size="size"
      :refreshable="true"
      :loading="loading"
      :error="error"
      :errorMessage="errorMessage"
      :empty="!records || records.length === 0"
      :emptyMessage="'No watch history'"
      :isEditMode="isEditMode"
      @refresh="fetchRecentWatches"
      class="recent-watch-widget"
  >
    <!-- Add icon slot -->
    <template #icon>
      <div class="recent-watch-widget-icon">
        <font-awesome-icon :icon="['fas', 'eye']" />
      </div>
    </template>

    <!-- content -->
    <div class="recent-watches-container">
      <div class="poster-grid" :class="sizeClass">
        <div class="poster-card-wrapper" v-for="record in displayedRecords" :key="record.id">
          <poster-card
              :content="record.contentDetails || record"
              @card-click="isEditMode ? undefined : handleCardClick"
          />
        </div>
      </div>
    </div>

    <!-- footer: View all option -->
    <template #footer>
      <div class="widget-footer-actions">
        <span>Showing {{ displayedRecords.length }} / {{ records.length }}</span>
        <button class="view-all-button" @click="isEditMode ? undefined : viewAllRecords">
          View All
        </button>
      </div>
    </template>
  </BaseWidget>
</template>

<script>
import BaseWidget from './BaseWidget.vue';
import PosterCard from '../cards/PosterCard.vue';

export default {
  name: 'RecentWatch',
  components: {
    BaseWidget,
    PosterCard
  },
  props: {
    id: {
      type: String,
      required: true
    },
    title: {
      type: String,
      default: 'Recent Watches'
    },
    size: {
      type: String,
      default: 'mediumHorizontal',
      validator: (value) => ['mediumHorizontal', 'large1'].includes(value)
    },
    isEditMode: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      records: [],
      loading: false,
      error: false,
      errorMessage: ''
    };
  },
  computed: {
    displayedRecords() {
      // sort records by watch date (newest first)
      let sortedRecords = [...this.records].sort((a, b) => {
        const dateA = new Date(a.watchDate).getTime();
        const dateB = new Date(b.watchDate).getTime();
        return dateB - dateA;
      });

      let displayCount = 4; // default for mediumHorizontal (2×1)
      if (this.size === 'large1') {
        displayCount = 6; // for large1 (3×1)
      }

      return sortedRecords.slice(0, displayCount);
    },
    sizeClass() {
      return `grid-size-${this.size}`;
    }
  },
  mounted() {
    this.fetchRecentWatches();
  },
  methods: {
    async fetchRecentWatches() {
      this.loading = true;
      this.error = false;

      try {
        const response = await this.$http.get('/record');
        if (response.data && response.data.data) {
          this.records = response.data.data;

          // fetch content details for each record
          for (const record of this.records) {
            if (!record.contentDetails && record.contentId) {
              await this.fetchContentDetails(record);
            }
          }
        } else {
          console.error("Invalid API response for recent watches", response);
          this.error = true;
          this.errorMessage = 'Failed to load recent watches';
        }
      } catch (error) {
        console.error("Error fetching recent watches:", error);
        this.error = true;
        this.errorMessage = error.message || 'Failed to connect to server';

      } finally {
        this.loading = false;
      }
    },
    async fetchContentDetails(record) {
      try {
        const response = await this.$http.get(`/content/id/${record.contentId}`);
        if (response.data && response.data.data) {
          record.contentDetails = response.data.data;
        }
      } catch (error) {
        console.error(`Error fetching details for content ${record.contentId}:`, error);
      }
    },
    handleCardClick(content) {
      const contentId = content.id || (content.contentDetails && content.contentDetails.id) || content.contentId;

      if (this.$router && contentId) {
        this.$router.push({
          name: 'ContentDetail',
          params: { id: contentId }
        });
      } else {
        console.log('Card clicked but missing ID or router:', content);
      }
    },
    viewAllRecords() {
      if (this.$router) {
        this.$router.push({
          name: 'watched',
          query: {
            sortBy: 'watchDate',
            sortOrder: 'desc'
          }
        });
      } else {
        console.log('View all records');
      }
    }
  }
};
</script>

<style scoped>
.recent-watch-widget {
  width: 100%;
  height: 100%;
}

.recent-watch-widget-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-primary);
  font-size: 1.2rem;
}

.recent-watches-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 var(--spacing-md);
}

.poster-grid {
  display: grid;
  gap: var(--spacing-md);
  width: 100%;
  justify-content: center;
}

.poster-card-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}

/* Grid layout based on component size */
.grid-size-mediumHorizontal {
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  max-width: 600px;
}

.grid-size-large1 {
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  max-width: 900px;
}

/* Adjust grid columns for smaller screens */
@media (max-width: 1200px) {
  .grid-size-large1 {
    grid-template-columns: repeat(5, 1fr);
  }
}

@media (max-width: 992px) {
  .grid-size-large1 {
    grid-template-columns: repeat(4, 1fr);
  }

  .grid-size-mediumHorizontal {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .grid-size-large1 {
    grid-template-columns: repeat(3, 1fr);
  }

  .grid-size-mediumHorizontal {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .grid-size-large1,
  .grid-size-mediumHorizontal {
    grid-template-columns: repeat(2, 1fr);
  }
}

.widget-footer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  font-size: var(--font-fontSize-xs);
  color: var(--text-muted);
}

.view-all-button {
  background: transparent;
  border: none;
  color: var(--accent-info);
  cursor: pointer;
  font-size: var(--font-fontSize-xs);
  font-weight: var(--font-fontWeight-medium);
  padding: 0;
}

.view-all-button:hover {
  text-decoration: underline;
  color: var(--accent-info);
  background-color: var(--interactive-hover);
}

.recent-watch-widget.widget-drag-handle .view-all-button,
.recent-watch-widget.widget-drag-handle .poster-card-wrapper * {
  pointer-events: none !important;
}
</style>