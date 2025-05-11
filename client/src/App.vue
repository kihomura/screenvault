<template>
  <div class="app-container">
    <cyberpunk-background v-if="currentTheme === 'cyberpunk'"></cyberpunk-background>
    <light-background v-if="currentTheme === 'light'"></light-background>
    <dark-background v-if="currentTheme === 'dark'"></dark-background>
    <sidebar-nav v-if="isAuthenticated"></sidebar-nav>
    <main class="content-area">
      <router-view />
    </main>
    <toast-manager />
  </div>
</template>

<script setup>
import { useThemeStore } from './store/themeStore.js';
import { computed, onMounted, watch, ref, nextTick } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import SidebarNav from "./components/ui/Sidebar.vue";
import ToastManager from "./components/ui/ToastManager.vue";
import { 
  CyberpunkBackground, 
  LightBackground, 
  DarkBackground 
} from './components/theme';

const themeStore = useThemeStore();
const store = useStore();
const route = useRoute();
const currentTheme = ref(themeStore.currentTheme);

const isAuthenticated = computed(() => {
  return store.getters.isAuthenticated;
});

// Watch for authentication changes
watch(() => isAuthenticated.value, (newValue) => {
  nextTick(() => {
    // When authentication state changes, ensure theme is properly applied
    document.body.style.removeProperty('backgroundColor');
    document.body.classList.remove('auth-page');
    themeStore.applyTheme();
  });
});

// Watch for theme changes
watch(() => themeStore.currentTheme, (newTheme) => {
  currentTheme.value = newTheme;
});

// Watch for route changes to ensure theme is properly applied
watch(() => route.path, () => {
  nextTick(() => {
    // Remove any inline background color style that might have been set by other components
    if (document.body.style.backgroundColor) {
      document.body.style.removeProperty('backgroundColor');
    }
    // Re-apply the theme
    themeStore.applyTheme();
  });
});

onMounted(() => {
  // Clear any inline styles that might interfere with theme
  document.body.style.removeProperty('backgroundColor');
  // Apply the theme
  themeStore.applyTheme();
  currentTheme.value = themeStore.currentTheme;
});
</script>

<style>
html, body {
  background-color: var(--background-muted);
}

.auth-page {
  background-color: #252525 !important;
}

.app-container {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  position: relative;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  position: relative;
  z-index: 1;
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