/**
 * Storage Manager for ScreenVault Application
 * Provides centralized user-specific data storage with namespace isolation
 * Supports migration from legacy localStorage to structured storage format
 */

const APP_STORAGE_KEY = 'screen-vault-storage';
const GLOBAL_KEYS = []; // Keys that should be stored globally instead of per-user

/**
 * Main storage manager singleton
 * Handles user-specific data storage with automatic namespace switching
 */
export const storageManager = {
  /**
   * Initialize storage manager with Vuex store integration
   * Sets up user state change listeners for automatic namespace switching
   * @param {Object} store - Vuex store instance
   */
  init(store) {
    // console.log('[storageManager.js] init called');
    window.__VUEX_STORE__ = store;

    if (store && store.subscribe) {
      // Listen for user state changes in Vuex
      store.subscribe((mutation, state) => {
        if (mutation.type === 'setUser') {
          const oldNamespace = this.currentNamespace; 
          this.currentNamespace = getUserNamespace(); 
          // console.log('[storageManager.js] setUser detected in Vuex. Old namespace:', oldNamespace, 'New namespace:', this.currentNamespace);
          
          // Migrate data when user changes
          this.migrateThemeToUserNamespace();
          this.migrateExistingData();
          // console.log('[storageManager.js] Data migration complete for new user state.');
        }
      });
    } else {
      console.warn('[storageManager.js] init: store or store.subscribe is not available.');
    }
  },

  // Current user namespace for data isolation
  currentNamespace: getUserNamespace(),

  /**
   * Get a value from user-specific or global storage
   * @param {string} key - Storage key
   * @param {*} defaultValue - Default value if key doesn't exist
   * @returns {*} Stored value or default value
   */
  get(key, defaultValue = null) {
    const storage = getStorageObject();
    
    // Handle global keys
    if (isGlobalKey(key)) {
      return storage.global[key] !== undefined ? storage.global[key] : defaultValue;
    }
    
    // Handle user-specific keys
    const userNamespace = getUserNamespace();
    if (!storage.users[userNamespace]) {
      return defaultValue;
    }
    return storage.users[userNamespace][key] !== undefined
        ? storage.users[userNamespace][key]
        : defaultValue;
  },

  /**
   * Set a value in user-specific or global storage
   * @param {string} key - Storage key
   * @param {*} value - Value to store
   */
  set(key, value) {
    const storage = getStorageObject();
    
    if (isGlobalKey(key)) {
      // Store in global namespace
      storage.global[key] = value;
    } else {
      // Store in user-specific namespace
      const userNamespace = getUserNamespace();
      if (!storage.users[userNamespace]) {
        storage.users[userNamespace] = {};
      }
      storage.users[userNamespace][key] = value;
    }
    saveStorageObject(storage);
  },

  /**
   * Remove a value from storage
   * @param {string} key - Storage key to remove
   */
  remove(key) {
    const storage = getStorageObject();
    
    if (isGlobalKey(key)) {
      delete storage.global[key];
    } else {
      const userNamespace = getUserNamespace();
      if (storage.users[userNamespace]) {
        delete storage.users[userNamespace][key];
      }
    }
    saveStorageObject(storage);
  },

  /**
   * Migrate existing localStorage data to user-specific namespace
   * Handles legacy data from before namespace implementation
   */
  migrateExistingData() {
    const storage = getStorageObject();
    const userNamespace = getUserNamespace(); 
    // console.log('[storageManager.js] migrateExistingData for namespace:', userNamespace);
    
    // Ensure user namespace exists
    if (!storage.users[userNamespace]) {
      storage.users[userNamespace] = {};
    }
    
    // Define keys that should be migrated to user-specific storage
    const keysToMigrate = [
      { pattern: /^favorite-content-widget-.*$/, global: false },
      { key: 'user-avatar', global: false },
      { key: 'app-theme', global: false },
      { key: 'dashboard-layout', global: false },
      { key: 'user-playlist-order', global: false },
      { key: 'watchedPageNumber', global: false }
    ];

    // Scan localStorage for keys to migrate
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key === APP_STORAGE_KEY) continue; // Skip our structured storage key
      
      const keyConfig = keysToMigrate.find(k =>
          (k.key && k.key === key) || (k.pattern && k.pattern.test(key))
      );
      
      if (keyConfig) {
        const value = localStorage.getItem(key);
        try {
          // Try to parse as JSON
          const parsedValue = JSON.parse(value);
          if (keyConfig.global) storage.global[key] = parsedValue;
          else storage.users[userNamespace][key] = parsedValue;
        } catch (e) {
          // Store as string if JSON parsing fails
          if (keyConfig.global) storage.global[key] = value;
          else storage.users[userNamespace][key] = value;
        }
        // Remove migrated key from localStorage
        localStorage.removeItem(key);
      }
    }
    saveStorageObject(storage);
  },

  /**
   * Migrate theme settings to current user namespace
   * Handles theme migration from global or anonymous user to authenticated user
   */
  migrateThemeToUserNamespace() {
    const storage = getStorageObject();
    const userNamespace = getUserNamespace();
    // console.log('[storageManager.js] migrateThemeToUserNamespace for namespace:', userNamespace);
    
    // Ensure user namespace exists
    if (!storage.users[userNamespace]) {
      storage.users[userNamespace] = {};
    }
    
    // Migrate from global theme storage
    if (storage.global && storage.global['app-theme']) {
      const globalTheme = storage.global['app-theme'];
      // console.log('[storageManager.js] Found global app-theme:', globalTheme);
      if (!storage.users[userNamespace]['app-theme']) {
        // console.log(`[storageManager.js] Migrating theme from global to user namespace ${userNamespace}: ${globalTheme}`);
        storage.users[userNamespace]['app-theme'] = globalTheme;
      }
      delete storage.global['app-theme'];
      saveStorageObject(storage);
      // console.log('[storageManager.js] Deleted global app-theme and saved storage.');
    } 
    // Migrate from anonymous user theme storage
    else if (storage.users['anonymous'] && storage.users['anonymous']['app-theme'] && userNamespace !== 'anonymous') {
      const anonymousTheme = storage.users['anonymous']['app-theme'];
      // console.log('[storageManager.js] Found anonymous app-theme:', anonymousTheme, 'Current user ns:', userNamespace);
      if (!storage.users[userNamespace]['app-theme']) {
        // console.log(`[storageManager.js] Migrating theme from anonymous to user namespace ${userNamespace}: ${anonymousTheme}`);
        storage.users[userNamespace]['app-theme'] = anonymousTheme;
      }
      saveStorageObject(storage);
      // console.log('[storageManager.js] Migrated from anonymous (if applicable) and saved storage.');
    } else {
      // console.log('[storageManager.js] No global or anonymous theme to migrate for namespace:', userNamespace);
    }
  },
};

/**
 * Helper Functions
 */

/**
 * Get the structured storage object from localStorage
 * @returns {Object} Storage object with global and users properties
 */
function getStorageObject() {
  const storageStr = localStorage.getItem(APP_STORAGE_KEY);
  try { 
    return storageStr ? JSON.parse(storageStr) : { global: {}, users: {} }; 
  } catch (e) { 
    console.error('[storageManager.js] Error parsing storage object:', e);
    return { global: {}, users: {} }; 
  }
}

/**
 * Save the structured storage object to localStorage
 * @param {Object} obj - Storage object to save
 */
function saveStorageObject(obj) { 
  try {
    localStorage.setItem(APP_STORAGE_KEY, JSON.stringify(obj)); 
  } catch (e) {
    console.error('[storageManager.js] Error saving storage object:', e);
  }
}

/**
 * Get the current user's namespace for data isolation
 * @returns {string} User namespace (user-{username} or anonymous)
 */
function getUserNamespace() {
  try {
    const store = window.__VUEX_STORE__;
    const user = store && store.state.user;
    const namespace = (user && user.username) ? `user-${user.username}` : 'anonymous';
    return namespace;
  } catch (e) { 
    console.error('[storageManager.js] Error in getUserNamespace:', e);
    return 'anonymous'; 
  }
}

/**
 * Check if a key should be stored globally instead of per-user
 * @param {string} key - Storage key to check
 * @returns {boolean} True if key should be global
 */
function isGlobalKey(key) { 
  return GLOBAL_KEYS.includes(key) || key.startsWith('global-'); 
}

export default storageManager; 