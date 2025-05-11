<template>
  <BaseWidget
      :id="id"
      :title="title"
      :size="size"
      :refreshable="true"
      :loading="loading"
      :error="error"
      :errorMessage="errorMessage"
      :empty="!playlists || playlists.length === 0"
      :emptyMessage="'No playlists found'"
      :isEditMode="isEditMode"
      @refresh="fetchPlaylists"
      class="playlist-widget"
  >
    <!-- Add icon slot -->
    <template #icon>
      <div class="playlist-widget-icon">
        <font-awesome-icon :icon="['fas', 'list']" />
      </div>
    </template>

    <!-- content -->
    <div class="playlists-container">
      <div class="list-vertical-container" :class="sizeClass">
        <div
            v-for="(playlist, index) in playlists"
            :key="playlist.id"
            class="list-item-wrapper"
        >
          <list-item
              :list="playlist"
              :is-manage-mode="false"
              :index="index"
              :disabled="isEditMode"
          />
        </div>
      </div>
    </div>

    <!-- footer -->
    <template #footer>
      <div class="widget-footer-actions">
        <button class="view-all-button" @click="isEditMode ? undefined : viewAllPlaylists">
          View All Playlists
        </button>
      </div>
    </template>
  </BaseWidget>
</template>

<script>
import BaseWidget from './BaseWidget.vue';
import ListItem from '../ui/list/ListItem.vue';

export default {
  name: 'Playlist',
  components: {
    BaseWidget,
    ListItem
  },
  props: {
    id: {
      type: String,
      required: true
    },
    title: {
      type: String,
      default: 'My Playlists'
    },
    size: {
      type: String,
      default: 'mediumVertical',
      validator: (value) => ['mediumVertical', 'largeVertical'].includes(value)
    },
    isEditMode: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      playlists: [],
      loading: false,
      error: false,
      errorMessage: ''
    };
  },
  computed: {
    sizeClass() {
      return `container-size-${this.size}`;
    }
  },
  mounted() {
    this.fetchPlaylists();
  },
  methods: {
    async fetchPlaylists() {
      this.loading = true;
      this.error = false;

      try {
        const response = await this.$http.get('/playlist');
        if (response.data && response.data.data) {
          this.playlists = response.data.data;
        } else {
          console.error("Invalid API response for playlists", response);
          this.error = true;
          this.errorMessage = 'Failed to load playlists';
        }
      } catch (error) {
        console.error("Error fetching playlists:", error);
        this.error = true;
        this.errorMessage = error.message || 'Failed to connect to server';
      } finally {
        this.loading = false;
      }
    },
    viewAllPlaylists() {
      if (this.$router) {
        this.$router.push({
          name: 'playlist'
        });
      } else {
        console.log('View all playlists');
      }
    }
  }
};
</script>

<style scoped>
.playlist-widget {
  width: 100%;
  height: 100%;
}

.playlist-widget-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-primary);
  font-size: 1.2rem;
}

.playlists-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.list-vertical-container {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding-right: var(--spacing-md);
}

/* Customize scrollbar */
.list-vertical-container::-webkit-scrollbar {
  width: 6px;
}

.list-vertical-container::-webkit-scrollbar-track {
  background: var(--background-muted);
  border-radius: var(--border-radius-full);
}

.list-vertical-container::-webkit-scrollbar-thumb {
  background: var(--border-medium);
  border-radius: var(--border-radius-full);
}

.list-vertical-container::-webkit-scrollbar-thumb:hover {
  background: var(--border-dark);
}

.list-item-wrapper {
  margin-bottom: var(--spacing-md);
}

.list-item-wrapper:last-child {
  margin-bottom: 0;
}

/* Size configurations for vertical layouts */
.container-size-mediumVertical {
  max-height: 400px;
}

.container-size-largeVertical {
  max-height: 600px;
}

.widget-footer-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
  font-size: var(--font-fontSize-xs);
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

/* Responsive adjustments */
@media (max-width: 768px) {
  .container-size-mediumVertical,
  .container-size-largeVertical {
    max-height: 350px;
  }
}

@media (max-width: 576px) {
  .container-size-mediumVertical,
  .container-size-largeVertical {
    max-height: 300px;
  }
}

.playlist-widget.widget-drag-handle .view-all-button,
.playlist-widget.widget-drag-handle .list-item-wrapper * {
  pointer-events: none !important;
}
</style>