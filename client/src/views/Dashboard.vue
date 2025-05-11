<template>
  <div class="dashboard-container" ref="dashboardRef">
    <div class="dashboard-header">
      <div class="dashboard-title">
        <h2>Dashboard</h2>
      </div>
      <div class="dashboard-actions">
        <main-btn
            v-if="!isEditMode"
            type="text"
            size="medium"
            @click="toggleEditMode"
        >
          Edit
        </main-btn>

        <div class="widget-selector" v-if="isEditMode">
          <label for="widget-type">Add Widget:</label>
          <select id="widget-type" v-model="selectedWidgetType" class="select-input">
            <option disabled value="">Select widget type</option>
            <option v-if="availableWidgets.length === 0" disabled class="no-widgets-option">All widgets are already on dashboard</option>
            <option v-for="type in availableWidgets" :key="type.id" :value="type.id">
              {{ type.name }}
            </option>
          </select>
          <select v-model="selectedWidgetSize" v-if="getAvailableSizes.length > 0" class="select-input">
            <option v-for="size in getAvailableSizes" :key="size" :value="size.value">
              {{ size.label }}
            </option>
          </select>
          <main-btn
              type="primary"
              size="small"
              @click="addWidget"
              :disabled="!selectedWidgetType || getAvailableSizes.length === 0 || !canAddMoreWidgets"
          >
            Add
          </main-btn>
        </div>
        <main-btn type="highlight" size="medium" @click="saveLayout" v-if="isEditMode">Save Layout</main-btn>
      </div>
    </div>

    <div class="dashboard-content">
      <grid-layout
          v-if="layout.length > 0"
          :layout="layout"
          @layout-updated="onLayoutUpdated"
          :col-num="4"
          :row-height="380"
          :is-draggable="isEditMode"
          :is-resizable="false"
          :responsive="false"
          :use-css-transforms="true"
          :vertical-compact="true"
          :margin="[10, 10]"
          :width="dashboardWidth"
          :max-rows="4"
          class="dashboard-grid"
      >
        <grid-item
            v-for="item in layout"
            :key="item.i"
            :x="item.x"
            :y="item.y"
            :w="item.w"
            :h="item.h"
            :i="item.i"
            drag-allow-from=".widget"
            drag-ignore-from=".remove-widget-btn, .widget-action-button"
            class="grid-item"
            :class="{ 'edit-mode': isEditMode }"
        >
          <component
            :is="getComponentType(item.type)"
            :id="item.i"
            :title="item.title"
            :size="item.size"
            :key="item.i"
            :prepend-icon="getWidgetIcon(item.type).html"
            :isEditMode="isEditMode"
          />
          <button v-if="isEditMode" class="remove-widget-btn" @click="removeWidget(item.i)" title="Remove widget">×</button>
        </grid-item>
      </grid-layout>
    </div>

    <div v-if="layout.length === 0" class="empty-dashboard">
      <div class="empty-message">
        <div class="empty-icon">📺</div>
        <p>Your dashboard is empty. Add some widgets to get started!</p>
      </div>
    </div>

    <div v-if="!canAddMoreWidgets && layout.length > 0 && isEditMode" class="grid-status full">
      <div class="status-message">
        <div class="status-icon">⚠️</div>
        <p>4×4 dashboard space is full! Remove existing widgets before adding more.</p>
      </div>
    </div>
  </div>

  <content-tab-modal
    v-if="contentModalConfig.isOpen"
    :isOpen="contentModalConfig.isOpen"
    v-model:visibleTabs="contentModalConfig.visibleTabs"
    :mode="contentModalConfig.mode"
    v-model:multiSelect="contentModalConfig.multiSelect"
    :targetListId="contentModalConfig.targetListId"
    @close="closeContentModal"
    @content-selected="handleContentSelected"
    @items-selected="handleItemsSelected"
  />
</template>

<script>
import {ref, computed, onMounted, onUnmounted, nextTick, provide} from 'vue'
import { GridLayout, GridItem } from 'vue3-grid-layout'
import RecentWatch from "../components/widgets/RecentWatch.vue";
import Favorite from "../components/widgets/Favorite.vue";
import Wishlist from "../components/widgets/Wishlist.vue";
import Playlists from "../components/widgets/Playlists.vue";
import ContentTabModal from "../components/modals/ContentTabModal.vue";
import { useToastStore } from "../store/toastStore.js";
import MainBtn from '../components/buttons/MainBtn.vue';

const WIDGET_SIZES = {
  small: { value: 'small', label: 'Small (1×1)', w: 1, h: 1 },
  mediumHorizontal: { value: 'mediumHorizontal', label: 'Medium (2×1)', w: 2, h: 1 },
  mediumVertical: { value: 'mediumVertical', label: 'Medium (1×2)', w: 1, h: 2 },
  large1: { value: 'large1', label: 'Large (3×1)', w: 3, h: 1 },
  large2: { value: 'large2', label: 'Large (2×2)', w: 2, h: 2 },
};

const WIDGET_CONFIG = {
  RecentWatch: {
    component: RecentWatch,
    name: 'Recently Watched',
    availableSizes: [
      WIDGET_SIZES.mediumHorizontal,
      WIDGET_SIZES.large1
    ],
    defaultTitle: 'Recently Watched'
  },
  Favorite: {
    component: Favorite,
    name: 'My Favorites',
    availableSizes: [
      WIDGET_SIZES.small,
    ],
    defaultTitle: 'My Favorites'
  },
  Wishlist: {
    component: Wishlist,
    name: 'Wishlist',
    availableSizes: [
      WIDGET_SIZES.mediumHorizontal,
      WIDGET_SIZES.large1
    ],
    defaultTitle: 'Wishlist'
  },
  Playlists: {
    component: Playlists,
    name: 'Playlists',
    availableSizes: [
      WIDGET_SIZES.mediumVertical
    ],
    defaultTitle: 'Playlists'
  }
};

export default {
  name: 'Dashboard',
  components: {
    ContentTabModal,
    RecentWatch,
    Favorite,
    Wishlist,
    Playlists,
    GridLayout,
    GridItem,
    MainBtn
  },
  setup() {
    const layout = ref([]);
    const dashboardWidth = ref(800);
    const dashboardRef = ref(null);
    const selectedWidgetType = ref('');
    const selectedWidgetSize = ref('');
    const isEditMode = ref(false);
    const toastStore = useToastStore();

    const availableWidgets = computed(() => {
      const allWidgets = Object.keys(WIDGET_CONFIG)
          .filter(key => WIDGET_CONFIG[key].component !== null)
          .map(key => ({
            id: key,
            name: WIDGET_CONFIG[key].name
          }));

      return allWidgets.filter(widget => {
        // check if this widget type already exists in the layout
        const alreadyExists = layout.value.some(item => item.type === widget.id);
        return !alreadyExists;
      });
    });

    const getAvailableSizes = computed(() => {
      if (!selectedWidgetType.value || !WIDGET_CONFIG[selectedWidgetType.value]) {
        return [];
      }

      const sizes = WIDGET_CONFIG[selectedWidgetType.value].availableSizes;
      if (sizes && sizes.length > 0 && !selectedWidgetSize.value) {
        selectedWidgetSize.value = sizes[0].value;
      }

      return sizes;
    });

    const canAddMoreWidgets = computed(() => {
      const gridMap = Array(4).fill().map(() => Array(4).fill(false));

      layout.value.forEach(item => {
        for (let y = item.y; y < item.y + item.h; y++) {
          for (let x = item.x; x < item.x + item.w; x++) {
            if (y < 4 && x < 4) {
              gridMap[y][x] = true;
            }
          }
        }
      });

      const currentSizeConfig = selectedWidgetSize.value ?
          Object.values(WIDGET_SIZES).find(s => s.value === selectedWidgetSize.value) : null;

      if (!currentSizeConfig) {
        return gridMap.some(row => row.some(cell => !cell));
      }

      const w = currentSizeConfig.w;
      const h = currentSizeConfig.h;

      for (let y = 0; y <= 4 - h; y++) {
        for (let x = 0; x <= 4 - w; x++) {
          let canPlace = true;
          for (let j = 0; j < h && canPlace; j++) {
            for (let i = 0; i < w && canPlace; i++) {
              if (gridMap[y + j][x + i]) {
                canPlace = false;
              }
            }
          }
          if (canPlace) {
            return true;
          }
        }
      }

      return false;
    });

    const generateId = () => {
      return `widget-${Date.now()}`;
    };

    const isPositionAvailable = (x, y, w, h) => {
      if (x < 0 || x + w > 4 || y < 0 || y + h > 4) {
        return false;
      }

      for (const item of layout.value) {
        if (
            x < item.x + item.w &&
            x + w > item.x &&
            y < item.y + item.h &&
            y + h > item.y
        ) {
          return false;
        }
      }

      return true;
    };

    const findAvailablePosition = (w, h) => {
      for (let y = 0; y < 4; y++) {
        for (let x = 0; x <= 4 - w; x++) {
          if (isPositionAvailable(x, y, w, h)) {
            return { x, y };
          }
        }
      }

      return null;
    };

    const addWidget = () => {
      if (!selectedWidgetType.value || !selectedWidgetSize.value || !canAddMoreWidgets.value) return;

      const widgetConfig = WIDGET_CONFIG[selectedWidgetType.value];
      if (!widgetConfig) return;

      const sizeConfig = Object.values(WIDGET_SIZES).find(s => s.value === selectedWidgetSize.value);
      if (!sizeConfig) return;

      const position = findAvailablePosition(sizeConfig.w, sizeConfig.h);
      if (!position) {
        toastStore.error('Not enough space to add this widget!');
        return;
      }

      const widgetId = generateId();

      layout.value.push({
        x: position.x,
        y: position.y,
        w: sizeConfig.w,
        h: sizeConfig.h,
        i: widgetId,
        type: selectedWidgetType.value,
        title: widgetConfig.defaultTitle,
        size: selectedWidgetSize.value
      });

      selectedWidgetType.value = '';
      selectedWidgetSize.value = '';

      nextTick(() => {
        updateDashboardWidth();
      });
    };

    const removeWidget = (id) => {
      const index = layout.value.findIndex(item => item.i === id);
      if (index !== -1) {
        layout.value.splice(index, 1);

        selectedWidgetType.value = '';
        selectedWidgetSize.value = '';
      }
    };

    const saveLayout = () => {
      localStorage.setItem('dashboard-layout', JSON.stringify(layout.value));
      toastStore.success('Layout saved successfully!');
      isEditMode.value = false;
      selectedWidgetType.value = '';
      selectedWidgetSize.value = '';
    };

    const getComponentType = (type) => {
      return WIDGET_CONFIG[type]?.component || null;
    };

    const getWidgetIcon = (type) => {
      switch(type) {
        case 'Favorite':
          return {
            html: `<div class="widget-icon favorite-icon"><svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg></div>`,
            class: 'favorite-icon'
          };
        case 'Wishlist':
          return {
            html: `<svg viewBox="0 0 24 24" class="wishlist-icon"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>`,
            class: 'wishlist-icon'
          };
        case 'Playlists':
          return {
            html: `<svg viewBox="0 0 24 24" class="playlists-icon"><line x1="8" y1="6" x2="21" y2="6"></line><line x1="8" y1="12" x2="21" y2="12"></line><line x1="8" y1="18" x2="21" y2="18"></line><line x1="3" y1="6" x2="3.01" y2="6"></line><line x1="3" y1="12" x2="3.01" y2="12"></line><line x1="3" y1="18" x2="3.01" y2="18"></line></svg>`,
            class: 'playlists-icon'
          };
        case 'RecentWatch':
          return {
            html: `<svg viewBox="0 0 24 24" class="recentwatch-icon"><path d="M12 8V12M12 16H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"></path></svg>`,
            class: 'recentwatch-icon'
          };
        default:
          return {
            html: '',
            class: ''
          };
      }
    };

    const toggleEditMode = () => {
      isEditMode.value = !isEditMode.value;
      if (!isEditMode.value) {
        selectedWidgetType.value = '';
        selectedWidgetSize.value = '';
      }
    };

    const initDefaultLayout = () => {
      layout.value = [
        {
          x: 0,
          y: 0,
          w: 2,
          h: 2,
          i: 'default-recent-watch',
          type: 'RecentWatch',
          title: WIDGET_CONFIG.RecentWatch.defaultTitle,
          size: 'large2'
        },
        {
          x: 2,
          y: 0,
          w: 2,
          h: 1,
          i: 'default-favorite',
          type: 'Favorite',
          title: WIDGET_CONFIG.Favorite.defaultTitle,
          size: 'mediumHorizontal'
        },
        {
          x: 0,
          y: 2,
          w: 1,
          h: 2,
          i: 'default-wishlist',
          type: 'Wishlist',
          title: WIDGET_CONFIG.Wishlist.defaultTitle,
          size: 'mediumVertical'
        }
      ];
    };

    const loadSavedLayout = () => {
      const savedLayout = localStorage.getItem('dashboard-layout');
      if (savedLayout) {
        try {
          const parsedLayout = JSON.parse(savedLayout);
          if (Array.isArray(parsedLayout) && parsedLayout.length > 0) {
            const validLayout = parsedLayout.filter(item =>
                item.x >= 0 && item.x + item.w <= 4 &&
                item.y >= 0 && item.y + item.h <= 4
            );

            let hasOverlap = false;
            const gridMap = Array(4).fill().map(() => Array(4).fill(false));

            for (const item of validLayout) {
              for (let y = item.y; y < item.y + item.h; y++) {
                for (let x = item.x; x < item.x + item.w; x++) {
                  if (gridMap[y][x]) {
                    hasOverlap = true;
                    break;
                  }
                  gridMap[y][x] = true;
                }
                if (hasOverlap) break;
              }
              if (hasOverlap) break;
            }

            if (validLayout.length > 0 && !hasOverlap) {
              layout.value = validLayout;
              return;
            }
          }
        } catch (e) {
          console.error('Failed to parse saved layout', e);
          localStorage.removeItem('dashboard-layout');
        }
      }

      initDefaultLayout();
    };

    const updateDashboardWidth = () => {
      if (dashboardRef.value) {
        const containerWidth = Math.max(dashboardRef.value.getBoundingClientRect().width - 30, 300);
        const cellWidth = Math.floor((containerWidth - 30) / 4);
        const newWidth = cellWidth * 4 + 30;

        if (Math.abs(dashboardWidth.value - newWidth) > 1) {
          console.log('Dashboard width updated:', newWidth);
          dashboardWidth.value = newWidth;
        }
      }
    };

    const onLayoutUpdated = (newLayout) => {
      if (JSON.stringify(layout.value) !== JSON.stringify(newLayout)) {
        console.log('Layout actually changed, updating...');

        const layoutCopy = JSON.parse(JSON.stringify(newLayout));

        nextTick(() => {
          layout.value = layoutCopy;
          localStorage.setItem('dashboard-layout', JSON.stringify(layoutCopy));
        });
      }
    };

    const contentModalConfig = ref({
      isOpen: false,
      callback: null,
      visibleTabs: ['search', 'custom', 'watched', 'wishlist'],
      mode: 'addRecord',
      multiSelect: false,
      targetListId: null
    });

    const openContentModal = (config) => {
      contentModalConfig.value = {
        isOpen: true,
        callback: config.callback,
        visibleTabs: config.visibleTabs || ['search', 'custom', 'watched', 'wishlist'],
        mode: config.mode || 'addRecord',
        multiSelect: config.multiSelect || false,
        targetListId: config.targetListId || null
      };
    };

    const closeContentModal = () => {
      contentModalConfig.value.isOpen = false;
    };

    const handleContentSelected = (content) => {
      if (contentModalConfig.value.callback) {
        contentModalConfig.value.callback(content);
      }
      closeContentModal();
    };

    const handleItemsSelected = (items) => {
      if (contentModalConfig.value.callback) {
        contentModalConfig.value.callback(items);
      }
      closeContentModal();
    };

    provide('openContentModal', openContentModal);

    onMounted(() => {
      loadSavedLayout();

      setTimeout(() => {
        updateDashboardWidth();
        window.addEventListener('resize', updateDashboardWidth);
      }, 100);
    });

    onUnmounted(() => {
      window.removeEventListener('resize', updateDashboardWidth);
    });

    return {
      layout,
      dashboardRef,
      dashboardWidth,
      availableWidgets,
      selectedWidgetType,
      selectedWidgetSize,
      getAvailableSizes,
      canAddMoreWidgets,
      addWidget,
      removeWidget,
      saveLayout,
      getComponentType,
      getWidgetIcon,
      onLayoutUpdated,
      isEditMode,
      toggleEditMode,
      contentModalConfig,
      openContentModal,
      closeContentModal,
      handleContentSelected,
      handleItemsSelected
    };
  }
}
</script>

<style scoped>
.dashboard-container {
  font-family: var(--font-fontFamily-primary);
  color: var(--text-primary);
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
}

.dashboard-title h2 {
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-semibold);
  font-size: var(--font-fontSize-xxl);
  margin: 0;
  color: var(--primary);
}

.dashboard-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.widget-selector {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  background-color: var(--background-subtle);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-light);
}

.widget-selector label {
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
  white-space: nowrap;
}

.select-input {
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  border: 1px solid var(--border-medium);
  background-color: var(--background-base);
  font-size: var(--font-fontSize-sm);
  color: var(--text-primary);
  outline: none;
  transition: all 0.2s ease;
}

.select-input:focus {
  border-color: var(--accent-info);
  box-shadow: 0 0 0 2px rgba(var(--accent-info-rgb), 0.2);
}

.dashboard-content {
  margin-top: var(--spacing-xl);
}

.dashboard-grid {
  margin: 0 auto;
}

.grid-item {
  transition: all 0.3s ease;
  border-radius: var(--border-radius-lg);
  background-color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
  overflow: hidden;
  position: relative;
}

.grid-item:hover {
  box-shadow: var(--shadow-level1-hover);
}

.grid-item.edit-mode {
  border: 2px dashed transparent;
  cursor: move !important;
  cursor: grab !important;
}

.grid-item.edit-mode * {
  cursor: inherit !important;
}

.grid-item.edit-mode:active {
  cursor: grabbing !important;
}

.grid-item.edit-mode:hover {
  border-color: var(--highlight);
}

.widget-container {
  width: 100%;
  height: 100%;
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
}

.widget-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
  border-bottom: 1px solid var(--border-light);
}

.widget-header h3 {
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-medium);
  margin: 0;
  color: var(--text-primary);
}

.remove-widget-btn {
  position: absolute;
  top: var(--spacing-xs);
  right: var(--spacing-xs);
  width: 24px;
  height: 24px;
  padding: 0;
  border: none;
  border-radius: var(--border-radius-full);
  background-color: var(--accent-error);
  color: white;
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-bold);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
  z-index: 10;
}

.grid-item.edit-mode:hover .remove-widget-btn {
  opacity: 1;
}

.remove-widget-btn:hover {
  background-color: rgba(var(--accent-error-rgb), 0.9);
}

.empty-dashboard {
  height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-message {
  text-align: center;
  padding: var(--spacing-xxl);
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-lg);
  border: 1px dashed var(--border-medium);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-lg);
}

.empty-message p {
  font-size: var(--font-fontSize-lg);
  color: var(--text-secondary);
}

.grid-status {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  background-color: var(--background-subtle);
}

.grid-status.full {
  border-left: 4px solid var(--accent-warning);
}

.status-message {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.status-icon {
  font-size: var(--font-fontSize-xl);
}

.status-message p {
  margin: 0;
  color: var(--text-secondary);
}

.no-widgets-option {
  font-style: italic;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .dashboard-actions {
    flex-direction: column;
    align-items: flex-start;
    width: 100%;
  }

  .widget-selector {
    flex-wrap: wrap;
    width: 100%;
  }
}

/* Widget icon styles */
.widget-icon {
  margin-right: var(--spacing-sm);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  position: relative;
}

.widget-icon svg {
  width: 100%;
  height: 100%;
}

.favorite-icon {
  color: var(--accent-warning);
}

.favorite-icon svg {
  fill: currentColor;
}

.wishlist-icon {
  color: var(--accent-error);
}

.wishlist-icon svg {
  fill: currentColor;
}

.playlists-icon {
  color: var(--accent-info);
}

.playlists-icon svg {
  stroke: currentColor;
  fill: none;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.recentwatch-icon {
  color: var(--primary);
}

.recentwatch-icon svg {
  stroke: currentColor;
  fill: none;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}
</style>