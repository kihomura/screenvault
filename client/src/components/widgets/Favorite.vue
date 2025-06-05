<template>
  <BaseWidget
      :id="id"
      :title="title"
      :size="size"
      :refreshable="true"
      :loading="loading"
      :error="error"
      :errorMessage="errorMessage"
      :empty="!favoriteContent"
      :emptyMessage="'No favorite content selected'"
      :isEditMode="isEditMode"
      class="favorite-widget"
      @refresh="loadFavoriteContent"
  >

    <template #icon>
      <div class="favorite-widget-icon">
        <font-awesome-icon :icon="['fas', 'star']" />
      </div>
    </template>
    
    <!-- content -->
    <div class="favorite-container">
      <div v-if="favoriteContent" class="poster-container" @click="isEditMode ? undefined : handleCardClick(favoriteContent)">
        <img :src="getContentImagePath(favoriteContent)" 
             :alt="favoriteContent.title" 
             class="poster-image" />
        <div class="content-overlay">
          <div class="content-title">{{ favoriteContent.title || favoriteContent.name }}</div>
        </div>
      </div>
      <div v-else class="empty-state">
        <div class="empty-icon"><i class="icon-heart-outline"></i></div>
        <p>Select your favorite content</p>
      </div>
    </div>

    <!-- footer: Select content button -->
    <template #footer>
      <div class="widget-footer-actions">
        <button class="select-content-button" @click="handleSelectButtonClick">
          {{ favoriteContent ? 'Change Content' : 'Select Content' }}
        </button>
      </div>
    </template>
  </BaseWidget>
</template>

<script>
import { ref, inject, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import BaseWidget from './BaseWidget.vue';
import axios from 'axios';
import { getContentImagePath } from "../../utils/imageUtils.js";
import { storageManager } from "../../utils/storageManager.js";

export default {
  name: 'FavoriteWidget',
  components: {
    BaseWidget
  },
  props: {
    id: {
      type: String,
      required: true
    },
    title: {
      type: String,
      default: 'My Favorite'
    },
    size: {
      type: String,
      default: 'small',
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    },
    isEditMode: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const favoriteContent = ref(null);
    const loading = ref(false);
    const error = ref(false);
    const errorMessage = ref('');
    const retryCount = ref(0);
    const MAX_RETRIES = 3;

    const openContentModal = inject('openContentModal');
    const $http = axios;

    const loadFavoriteContent = async () => {
      loading.value = true;
      error.value = false;
      errorMessage.value = '';
      retryCount.value = 0;

      try {
        const savedFavoriteId = storageManager.get(`favorite-content-${props.id}`);

        if (savedFavoriteId) {
          await fetchContentDetailsWithRetry(savedFavoriteId);
        } else {
          favoriteContent.value = null;
        }
      } catch (err) {
        error.value = true;
        errorMessage.value = err.message || 'Failed to load favorite content';
        if (err.message === 'Failed to fetch content details after multiple retries') {
          storageManager.remove(`favorite-content-${props.id}`);
          favoriteContent.value = null;
        }
      } finally {
        loading.value = false;
      }
    };

    const fetchContentDetailsWithRetry = async (contentId) => {
      while (retryCount.value < MAX_RETRIES) {
        try {
          await fetchContentDetails(contentId);
          return;
        } catch (err) {
          retryCount.value++;
          if (retryCount.value >= MAX_RETRIES) {
            throw new Error('Failed to fetch content details after multiple retries');
          }

          await new Promise(resolve => setTimeout(resolve, 1000 * retryCount.value));
        }
      }
    };

    const fetchContentDetails = async (contentId) => {
      try {
        const response = await $http.get(`/content/id/${contentId}`);

        if (response.data && response.data.data) {
          favoriteContent.value = response.data.data;
        } else {
          console.error(`[Favorite] Invalid data format received:`, response.data);
          throw new Error('Invalid data format received from API');
        }
      } catch (err) {
        console.error(`[Favorite] Error fetching details for content ${contentId}:`, err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || err.message || 'Failed to fetch content details';
        throw err;
      }
    };

    const handleSelectButtonClick = (event) => {
      console.log('Select content button clicked', {
        isEditMode: props.isEditMode,
        hasOpenContentModal: !!openContentModal
      });
      
      // stop event propagation to prevent it from being captured by the parent element
      event.stopPropagation();
      
      if (props.isEditMode) {
        console.log('Button disabled in edit mode');
        return;
      }
      
      try {
        openSelectModal();
      } catch (error) {
        console.error('Error opening select modal:', error);
      }
    };
    
    const openSelectModal = () => {
      console.log('Attempting to open content modal');
      if (typeof openContentModal !== 'function') {
        console.error('openContentModal is not a function', openContentModal);
        return;
      }
      
      openContentModal({
        callback: handleContentSelected,
        mode: 'selectFavorite',
        multiSelect: false,
        visibleTabs: ['search', 'custom', 'watched', 'wishlist']
      });
    };

    const handleContentSelected = (content) => {
      if (content && content.id) {
        favoriteContent.value = content;

        storageManager.set(`favorite-content-${props.id}`, content.id);
        error.value = false;
        errorMessage.value = '';
      } else {
        console.warn(`[Favorite] Invalid content selected:`, content);
      }
    };

    const router = useRouter();
    const handleCardClick = (content) => {
      if (router && content && content.id) {
        router.push({
          name: 'ContentDetail',
          params: {id: content.id}
        });
      }
    };

    onMounted(() => {
      loadFavoriteContent();
    });

    return {
      favoriteContent,
      loading,
      error,
      errorMessage,
      openSelectModal,
      handleSelectButtonClick,
      handleContentSelected,
      handleCardClick,
      loadFavoriteContent,
      getContentImagePath
    };
  }
};
</script>

<style scoped>
.favorite-widget {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.favorite-widget::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  z-index: 0;
  opacity: 0.6;
  pointer-events: none;
}

.favorite-widget::after {
  content: '';
  position: absolute;
  pointer-events: none;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.favorite-container {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.poster-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: var(--border-radius-lg);
  cursor: pointer;
  box-shadow: var(--shadow-level1-default);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  /* Add a accent border */
  border: 2px solid rgba(var(--primary-rgb), 0.3);
}

.poster-container:hover {
  transform: translateY(-5px) scale(1.02);
  box-shadow: var(--shadow-level2-hover);
  border: 2px solid rgba(var(--primary-rgb), 0.7);
}

.poster-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary), var(--secondary));
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
  z-index: 1;
}

.poster-container:hover::before {
  transform: scaleX(1);
}

/* Add star effect on the top-right corner */
.poster-container::after {
  content: '★';
  position: absolute;
  top: 10px;
  right: 10px;
  color: var(--primary);
  font-size: 20px;
  text-shadow: 0 0 10px rgba(var(--primary-rgb), 0.7);
  z-index: 3;
  opacity: 1;
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.poster-container:hover::after {
  transform: rotate(72deg) scale(1.3);
}

.poster-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
  /* Add slight filter to make content pop */
  filter: contrast(1.05) saturate(1.1);
}

.poster-container:hover .poster-image {
  transform: scale(1.1);
}

.content-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.85) 0%,
    rgba(0, 0, 0, 0.4) 60%,
    transparent 100%
  );
  padding: var(--spacing-md);
  z-index: 2;
  /* Add shine effect */
  border-bottom: 1px solid rgba(var(--primary-rgb), 0.3);
}

.content-title {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-base);
  font-weight: var(--font-fontWeight-semibold);
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: var(--text-muted);
}

.empty-icon {
  font-size: 2.5rem;
  margin-bottom: var(--spacing-md);
  opacity: 0.7;
  color: var(--primary);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 0.7; }
  50% { transform: scale(1.15); opacity: 1; }
  100% { transform: scale(1); opacity: 0.7; }
}

.empty-state p {
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
}

.widget-footer-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.select-content-button {
  background: transparent;
  border: none;
  color: var(--accent-info);
  cursor: pointer;
  font-size: var(--font-fontSize-xs);
  font-weight: var(--font-fontWeight-medium);
  padding: 0;
}

.select-content-button:hover {
  text-decoration: underline;
  color: var(--accent-info-hover);
}

/* Animated favorite icon */
.favorite-widget-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accent-warning);
  position: relative;
  font-size: 1.3rem;
  width: 24px;
  height: 24px;
  margin-right: 4px;
}

.favorite-widget-icon svg {
  color: inherit;
  animation: heartbeat 1.5s infinite;
  width: 100%;
  height: 100%;
}

@keyframes heartbeat {
  0% { transform: scale(1); }
  5% { transform: scale(1.2); }
  10% { transform: scale(1.1); }
  15% { transform: scale(1.2); }
  50% { transform: scale(1); }
  100% { transform: scale(1); }
}

.favorite-widget.widget-drag-handle .select-content-button,
.favorite-widget.widget-drag-handle .poster-container,
.favorite-widget.widget-drag-handle .poster-container * {
  pointer-events: none !important;
}
</style>