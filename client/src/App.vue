<template>
  <div class="app-container">
    <sidebar-nav v-if="isAuthenticated"></sidebar-nav>
    <main class="content-area">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useThemeStore } from './store/themeStore.js';
import { computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import SidebarNav from "./components/ui/Sidebar.vue";

const themeStore = useThemeStore();
const store = useStore();

const isAuthenticated = computed(() => {
  return store.getters.isAuthenticated;
});

onMounted(() => {
  themeStore.applyTheme();
});
</script>

<style>
html, body {
  background-color: var(--background-muted);
}

.app-container {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}
</style>