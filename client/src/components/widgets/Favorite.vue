<template>
  <BaseWidget
      :id="id"
      :title="title"
      :icon="icon"
      :size="size"
      :refreshable="true"
      :loading="loading"
      :error="error"
      :errorMessage="errorMessage"
      :empty="!favoriteContent"
      :emptyMessage="'No favorite content selected'"
      class="favorite-widget"
      @refresh="loadFavoriteContent"
  >
    <!-- content -->
    <div class="favorite-container">
      <div v-if="favoriteContent" class="poster-container">
        <poster-card
            :content="favoriteContent"
            @card-click="handleCardClick"
        />
      </div>
      <div v-else class="empty-state">
        <p>Select your favorite content</p>
      </div>
    </div>

    <!-- footer: Select content button -->
    <template #footer>
      <div class="widget-footer-actions">
        <button class="select-content-button" @click="openSelectModal">
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
import PosterCard from '../cards/PosterCard.vue';
import axios from 'axios';

export default {
  name: 'Favorite',
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
      default: 'My Favorite'
    },
    icon: {
      type: String,
      default: 'icon-heart'
    },
    size: {
      type: String,
      default: 'small',
      validator: (value) => ['small', 'mediumHorizontal'].includes(value)
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
        const savedFavoriteId = localStorage.getItem(`favorite-content-${props.id}`);

        if (savedFavoriteId) {
          await fetchContentDetailsWithRetry(savedFavoriteId);
        } else {
          favoriteContent.value = null;
        }
      } catch (err) {
        error.value = true;
        errorMessage.value = err.message || 'Failed to load favorite content';
        if (err.message === 'Failed to fetch content details after multiple retries') {
          localStorage.removeItem(`favorite-content-${props.id}`);
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

    const openSelectModal = () => {
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

        localStorage.setItem(`favorite-content-${props.id}`, content.id);
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
      handleContentSelected,
      handleCardClick,
      loadFavoriteContent
    };
  }
};
</script>

<style scoped>
.favorite-widget {
  width: 100%;
  height: 100%;
}

.favorite-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.poster-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 控制容器内容不溢出 */
  overflow: hidden;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: var(--text-muted);
  font-size: var(--font-fontSize-sm);
  text-align: center;
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
</style>