<template>
  <aside class="sidebar">
    <div class="sidebar-header">
      <h2 class="sidebar-title">ScreenVault</h2>
    </div>

    <div class="user-profile">
      <div class="avatar" @click="openAvatarModal">
        <img :src="avatarUrl" alt="User avatar" />
        <div class="avatar-overlay">
          <span class="change-avatar-text">Change</span>
        </div>
      </div>
      <div class="user-info" @click="navigateToProfile">
        <p class="user-name">{{ userName }}</p>
      </div>
    </div>

    <nav class="nav-menu">
      <router-link to="/dashboard" class="nav-item">
        <i class="nav-icon dashboard-icon"></i>
        <span>Dashboard</span>
      </router-link>
      <router-link to="/watched" class="nav-item">
        <i class="nav-icon watched-icon"></i>
        <span>Watched</span>
      </router-link>
      <router-link to="/playlist" class="nav-item">
        <i class="nav-icon list-icon"></i>
        <span>List</span>
      </router-link>
      <router-link to="/wishlist" class="nav-item">
        <i class="nav-icon wishlist-icon"></i>
        <span>Wish List</span>
      </router-link>

      <div class="nav-section">
        <div class="section-header" @click="toggleManageSection">
          <div class="nav-item-wrapper">
            <i class="nav-icon manage-icon"></i>
            <span class="me-5">Manage</span>
            <i class="toggle-icon ms-5" :class="{ 'expanded': isManageSectionOpen }"></i>
          </div>
        </div>

        <transition name="slide">
          <div v-show="isManageSectionOpen" class="section-content">
            <router-link to="/manage/tags" class="nav-item sub-nav-item" :class="{ 'router-link-active': isActiveRoute('/manage/tags') }">
              <i class="nav-icon tag-icon"></i>
              <span>Tags</span>
            </router-link>
            <router-link to="/manage/custom-content" class="nav-item sub-nav-item" :class="{ 'router-link-active': isActiveRoute('/manage/custom-content') }">
              <i class="nav-icon custom-content-icon"></i>
              <span>Custom Content</span>
            </router-link>
          </div>
        </transition>
      </div>

      <router-link to="/statistics" class="nav-item">
        <i class="nav-icon stats-icon"></i>
        <span>Statistics</span>
      </router-link>
      <router-link to="/profile" class="nav-item">
        <i class="nav-icon profile-icon"></i>
        <span>Profile</span>
      </router-link>
    </nav>

    <div class="sidebar-footer">
      <div class="theme-selector">
        <label for="theme-select">Theme</label>
        <select
            id="theme-select"
            v-model="selectedTheme"
            @change="changeTheme"
            class="theme-select"
        >
          <option value="light">Light</option>
          <option value="dark">Dark</option>
          <option value="cyberpunk">Cyberpunk</option>
        </select>
      </div>

      <button @click="logout" class="logout-btn">
        <i class="nav-icon logout-icon"></i>
        <span>Logout</span>
      </button>
    </div>
    
    <!-- Avatar Selection Modal -->
    <avatar-selection-modal
      :is-open="showAvatarModal"
      @close="closeAvatarModal"
      @avatar-updated="handleAvatarUpdated"
    />
  </aside>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useThemeStore } from '../../store/themeStore.js';
import { useStore } from 'vuex';
import AvatarSelectionModal from '../modals/AvatarSelectionModal.vue';

const router = useRouter();
const route = useRoute();
const themeStore = useThemeStore();
const store = useStore();
const isManageSectionOpen = ref(false);
const showAvatarModal = ref(false);

const isActiveManage = computed(() => route.path.startsWith('/manage'));

onMounted(() => {
  if (isActiveManage.value) {
    isManageSectionOpen.value = true;
  }
});

function isActiveRoute(path) {
  return route.path === path;
}

function toggleManageSection() {
  isManageSectionOpen.value = !isManageSectionOpen.value;
}

const selectedTheme = ref(themeStore.currentTheme);

const userName = computed(() => {
  return store.state.user?.username || 'Guest User';
});

const userAvatar = computed(() => {
  return store.state.user?.avatar || 'George';
});

const avatarUrl = computed(() => {
  return `https://api.dicebear.com/9.x/bottts-neutral/svg?seed=${userAvatar.value}`;
});

function openAvatarModal() {
  showAvatarModal.value = true;
}

function closeAvatarModal() {
  showAvatarModal.value = false;
}

function handleAvatarUpdated(avatarName) {
  console.log('Avatar updated to:', avatarName);
}

function changeTheme() {
  themeStore.setTheme(selectedTheme.value);
}

function navigateToProfile() {
  router.push('/profile');
}

async function logout() {
  try {
    await store.dispatch('logout');
    await router.push('/login');
  } catch (error) {
    console.error('Logout failed:', error);
  }
}

onMounted(() => {
  selectedTheme.value = themeStore.currentTheme;
});
</script>

<style scoped>
.sidebar {
  width: 280px;
  height: 100vh;
  background: var(--background-base);
  color: var(--text-primary);
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-level2-default);
  transition: all 0.3s ease;
  overflow-y: auto;
}

.sidebar-header {
  padding: var(--spacing-lg) var(--spacing-lg) var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
}

.sidebar-title {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-xl);
  font-weight: var(--font-fontWeight-bold);
  margin: 0;
  letter-spacing: 0.5px;
  position: relative;
  display: inline-block;
}

:root .theme-light .sidebar-title {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 1px 1px rgba(0,0,0,0.1);
}

:root .theme-dark .sidebar-title {
  color: var(--primary);
  text-shadow: 0 0 8px rgba(96, 165, 250, 0.6);
  border-bottom: 2px solid var(--secondary);
  padding-bottom: var(--spacing-xs);
}

:root .theme-cyberpunk .sidebar-title {
  font-family: var(--title-font);
  color: var(--primary);
  text-shadow: 0 0 5px var(--primary), 0 0 10px var(--secondary);
  text-transform: uppercase;
  letter-spacing: 2px;
  border-left: 3px solid var(--secondary);
  padding-left: var(--spacing-sm);
  position: relative;
  overflow: hidden;
}

:root .theme-cyberpunk .sidebar-title::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(5, 217, 232, 0.2), transparent);
  animation: titleGlow 3s linear infinite;
}

:root .theme-cyberpunk .sidebar {
  background: rgba(12, 16, 22, 0.9);
  border-right: 1px solid var(--secondary);
  position: relative;
  overflow: hidden;
}

:root .theme-cyberpunk .sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 1px;
  background: var(--secondary);
  box-shadow: 0 0 10px var(--secondary);
  z-index: 10;
}

:root .theme-cyberpunk .nav-item {
  border-left: 2px solid transparent;
  transition: all 0.3s ease;
  margin-bottom: 0.5rem;
  background: transparent;
  clip-path: polygon(0 0, 97% 0, 100% 100%, 0% 100%);
  font-family: var(--body-font);
}

:root .theme-cyberpunk .nav-item:hover {
  background: rgba(5, 217, 232, 0.1);
  border-left-color: var(--secondary);
  color: var(--secondary);
  text-shadow: 0 0 5px var(--secondary);
  box-shadow: inset 0 0 10px rgba(5, 217, 232, 0.1);
}

:root .theme-cyberpunk .router-link-active {
  background: rgba(255, 42, 109, 0.1) !important;
  border-left: 2px solid var(--primary) !important;
  color: var(--primary) !important;
  text-shadow: 0 0 5px var(--primary) !important;
  box-shadow: inset 0 0 15px rgba(255, 42, 109, 0.2) !important;
}

:root .theme-cyberpunk .sidebar-footer {
  border-top: 1px solid rgba(5, 217, 232, 0.3);
  background: rgba(12, 16, 22, 0.8);
}

:root .theme-cyberpunk .theme-select {
  background: rgba(0, 0, 0, 0.6);
  color: var(--secondary);
  border: 1px solid var(--secondary);
  font-family: var(--font-fontFamily-mono);
  letter-spacing: 1px;
  text-shadow: 0 0 3px var(--secondary);
  clip-path: polygon(5px 0, 100% 0, calc(100% - 5px) 100%, 0 100%);
}

:root .theme-cyberpunk .theme-select:focus {
  box-shadow: 0 0 0 2px rgba(5, 217, 232, 0.5);
  border-color: var(--secondary);
}

:root .theme-cyberpunk .theme-selector label {
  color: var(--text-primary);
  font-family: var(--body-font);
  letter-spacing: 1px;
  text-transform: uppercase;
  font-size: 0.8rem;
}

:root .theme-cyberpunk .logout-btn {
  background: rgba(0, 0, 0, 0.6);
  color: var(--primary);
  border: 1px solid var(--primary);
  font-family: var(--button-font);
  text-transform: uppercase;
  letter-spacing: 1px;
  text-shadow: 0 0 3px var(--primary);
  clip-path: polygon(5px 0, 100% 0, calc(100% - 5px) 100%, 0 100%);
  transition: all 0.3s ease;
}

:root .theme-cyberpunk .logout-btn:hover {
  background: rgba(255, 42, 109, 0.2);
  box-shadow: 0 0 10px rgba(255, 42, 109, 0.5);
}

:root .theme-cyberpunk .user-profile {
  background: rgba(12, 16, 22, 0.8);
  border-bottom: 1px solid rgba(5, 217, 232, 0.3);
}

:root .theme-cyberpunk .user-profile:hover {
  background: rgba(5, 217, 232, 0.05);
}

:root .theme-cyberpunk .avatar {
  border: 2px solid var(--secondary);
  box-shadow: 0 0 10px rgba(5, 217, 232, 0.5);
  overflow: hidden;
  position: relative;
}

:root .theme-cyberpunk .avatar::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(5, 217, 232, 0.4), transparent);
  animation: avatarGlow 3s infinite;
}

:root .theme-cyberpunk .user-name {
  color: var(--primary);
  font-family: var(--body-font);
  text-shadow: 0 0 5px rgba(255, 42, 109, 0.5);
  letter-spacing: 1px;
}

@keyframes avatarGlow {
  0% { left: -100%; }
  100% { left: 200%; }
}

@keyframes titleGlow {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.user-profile {
  display: flex;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: background 0.2s;
}

.user-profile:hover {
  background: var(--background-subtle);
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: var(--border-radius-full);
  overflow: hidden;
  background: var(--background-muted);
  margin-right: var(--spacing-md);
  border: 2px solid var(--border-medium);
  position: relative;
  cursor: pointer;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.avatar:hover .avatar-overlay {
  opacity: 1;
}

.change-avatar-text {
  color: white;
  font-size: var(--font-fontSize-xs);
  font-weight: var(--font-fontWeight-medium);
}

.user-info {
  flex: 1;
}

.user-name {
  font-weight: var(--font-fontWeight-medium);
  margin: 0 0 var(--spacing-xs);
  font-size: var(--font-fontSize-base);
}

.nav-menu {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  padding: var(--spacing-lg) var(--spacing-md);
}

.nav-item {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: var(--text-secondary);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  margin-bottom: var(--spacing-sm);
  transition: all 0.2s;
}

.nav-item:hover {
  background: var(--interactive-hover);
  color: var(--text-primary);
  transform: translateX(4px);
}

.nav-item.router-link-active {
  background: var(--interactive-active);
  color: var(--primary);
  font-weight: var(--font-fontWeight-medium);
}

.nav-section {
  margin-bottom: var(--spacing-sm);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  padding: var(--spacing-xs) 0;
}

.nav-item-wrapper {
  display: flex;
  align-items: center;
  color: var(--text-secondary);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  transition: all 0.2s;
  flex: 1;
}

.section-header:hover .nav-item-wrapper {
  background: var(--interactive-hover);
  color: var(--text-primary);
  transform: translateX(4px);
}

.section-content {
  padding-left: var(--spacing-md);
  overflow: hidden;
}

.sub-nav-item {
  padding-left: var(--spacing-lg);
  margin-bottom: var(--spacing-xs);
}

.toggle-icon {
  width: 16px;
  height: 16px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;
  transition: transform 0.3s ease;
  margin-right: var(--spacing-md);
}

.toggle-icon.expanded {
  transform: rotate(180deg);
}

.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
  max-height: 100px;
}

.slide-enter-from,
.slide-leave-to {
  max-height: 0;
  opacity: 0;
}

.nav-icon {
  width: 20px;
  height: 20px;
  margin-right: var(--spacing-md);
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;
  filter: invert(var(--icon-invert, 0.5));
  opacity: 0.9;
  transition: filter 0.2s ease, opacity 0.2s ease;
}

.nav-item:hover .nav-icon {
  filter: invert(var(--icon-invert-hover, 0.7));
  opacity: 1;
}

.nav-item.router-link-active .nav-icon {
  filter: invert(var(--icon-invert-active, 0.9));
  opacity: 1;
}

:root .theme-light {
  --icon-invert: 0.4;
  --icon-invert-hover: 0.6;
  --icon-invert-active: 0.2;
}

:root .theme-dark {
  --icon-invert: 0.7;
  --icon-invert-hover: 0.8;
  --icon-invert-active: 1;
}

:root .theme-cyberpunk {
  --icon-invert: 0.8;
  --icon-invert-hover: 0.9;
  --icon-invert-active: 1;
}

.dashboard-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='3' y='3' width='7' height='7'/%3E%3Crect x='14' y='3' width='7' height='7'/%3E%3Crect x='14' y='14' width='7' height='7'/%3E%3Crect x='3' y='14' width='7' height='7'/%3E%3C/svg%3E");
}

.watched-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z'/%3E%3Ccircle cx='12' cy='12' r='3'/%3E%3C/svg%3E");
}

.list-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cline x1='8' y1='6' x2='21' y2='6'/%3E%3Cline x1='8' y1='12' x2='21' y2='12'/%3E%3Cline x1='8' y1='18' x2='21' y2='18'/%3E%3Cline x1='3' y1='6' x2='3.01' y2='6'/%3E%3Cline x1='3' y1='12' x2='3.01' y2='12'/%3E%3Cline x1='3' y1='18' x2='3.01' y2='18'/%3E%3C/svg%3E");
}

.wishlist-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z'/%3E%3C/svg%3E");
}

.profile-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'/%3E%3Ccircle cx='12' cy='7' r='4'/%3E%3C/svg%3E");
}

.stats-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cline x1='18' y1='20' x2='18' y2='10'/%3E%3Cline x1='12' y1='20' x2='12' y2='4'/%3E%3Cline x1='6' y1='20' x2='6' y2='14'/%3E%3C/svg%3E");
}

.logout-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4'/%3E%3Cpolyline points='16 17 21 12 16 7'/%3E%3Cline x1='21' y1='12' x2='9' y2='12'/%3E%3C/svg%3E");
}

.manage-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z'/%3E%3Cpolyline points='14 2 14 8 20 8'/%3E%3Cline x1='16' y1='13' x2='8' y2='13'/%3E%3Cline x1='16' y1='17' x2='8' y2='17'/%3E%3Cpolyline points='10 9 9 9 8 9'/%3E%3C/svg%3E");
}

.tag-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z'/%3E%3Cline x1='7' y1='7' x2='7.01' y2='7'/%3E%3C/svg%3E");
}

.custom-content-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7'/%3E%3Cpath d='M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z'/%3E%3C/svg%3E");
}

.sidebar-footer {
  padding: var(--spacing-lg);
  border-top: 1px solid var(--border-light);
}

.theme-selector {
  margin-bottom: var(--spacing-lg);
}

.theme-selector label {
  display: block;
  margin-bottom: var(--spacing-xs);
  color: var(--text-secondary);
  font-size: var(--font-fontSize-sm);
}

.theme-select {
  width: 100%;
  padding: var(--spacing-sm);
  background-color: var(--background-subtle);
  color: var(--text-primary);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  font-size: var(--font-fontSize-sm);
  outline: none;
  cursor: pointer;
  transition: all 0.2s;
}

.theme-select:hover {
  border-color: var(--border-medium);
}

.theme-select:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2);
}

.logout-btn {
  display: flex;
  align-items: center;
  width: 100%;
  padding: var(--spacing-md);
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.2s;
  font-size: var(--font-fontSize-base);
}

.logout-btn:hover {
  background: var(--accent-error);
  color: white;
  border-color: var(--accent-error);
}

.logout-btn .nav-icon {
  margin-right: var(--spacing-md);
}
</style>