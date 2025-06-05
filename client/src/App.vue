<template>
  <div class="app-container" :class="{ 'no-theme': isNoThemePage }">
    <cyberpunk-background v-if="themeStore.currentTheme === 'cyberpunk' && !isNoThemePage"></cyberpunk-background>
    <light-background v-if="themeStore.currentTheme === 'light' && !isNoThemePage"></light-background>
    <dark-background v-if="themeStore.currentTheme === 'dark' && !isNoThemePage"></dark-background>
    <sidebar-nav v-if="isAuthenticated && !isNoThemePage"></sidebar-nav>
    <main class="content-area" :class="{ 'full-width': isNoThemePage }">
      <router-view />
    </main>
    <toast-manager />
  </div>
</template>

<script setup>
import { useThemeStore } from './store/themeStore.js';
import { computed, onMounted, watch, nextTick } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import SidebarNav from "./components/ui/layout/Sidebar.vue";
import ToastManager from "./components/ui/feedback/ToastManager.vue";
import { 
  CyberpunkBackground, 
  LightBackground, 
  DarkBackground 
} from './components/theme';

const themeStore = useThemeStore();
const store = useStore();
const route = useRoute();

const isAuthenticated = computed(() => store.getters.isAuthenticated);
const isNoThemePage = computed(() => route.meta.noTheme === true);

watch(isAuthenticated, async (isAuth, oldIsAuth) => {
  await nextTick(); 
  if (isAuth && !oldIsAuth) {
    await themeStore.refreshTheme(); 
  } else if (!isAuth && oldIsAuth) { 
    await themeStore.refreshTheme();
  }
});

watch(() => route.path, async (newPath, oldPath) => {
  await nextTick();
  if (isNoThemePage.value) {
    document.body.classList.add('no-theme-page');
    document.body.classList.remove(`theme-${themeStore.currentTheme}`);
  } else {
    document.body.style.removeProperty('backgroundColor');
    document.body.classList.remove('no-theme-page');
    themeStore.applyTheme();                             
  }
});

onMounted(async () => {
  await themeStore.refreshTheme();

  if (isNoThemePage.value) {
    document.body.classList.add('no-theme-page');
    document.body.classList.remove(`theme-${themeStore.currentTheme}`);
  } else {
    document.body.style.removeProperty('backgroundColor');
    document.body.classList.remove('no-theme-page');
    themeStore.applyTheme(); 
  }
});
</script>

<style>
html, body {
  background-color: var(--background-muted);
}

.no-theme-page {
  background-color: #252525 !important;
}

.app-container {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  position: relative;
}

.app-container.no-theme {
  background-color: #252525;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  position: relative;
  z-index: 1;
}

.content-area.full-width {
  width: 100%;
}

/* Unified Header Styles for all pages */
.page-header,
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
}

/* Unified title styles */
.page-title h2,
.page-header h1,
.page-header h2,
.dashboard-title h2 {
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-semibold);
  font-size: var(--font-fontSize-xxl);
  margin: 0;
  color: var(--primary);
}

.theme-cyberpunk .page-title h2,
.theme-cyberpunk .page-header h1,
.theme-cyberpunk .page-header h2,
.theme-cyberpunk .dashboard-title h2 {
  font-family: var(--title-font);
}

/* Left side structure */
.page-header .header-content,
.dashboard-header .dashboard-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

/* Right side structure */
.page-header .header-actions,
.page-header .header-controls,
.dashboard-header .dashboard-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

@media (max-width: 768px) {
  .page-header,
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .page-header .header-actions,
  .page-header .header-controls,
  .dashboard-header .dashboard-actions {
    flex-direction: column;
    align-items: flex-start;
    width: 100%;
  }
}
</style>