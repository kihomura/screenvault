const APP_STORAGE_KEY = 'screen-vault-storage';
const GLOBAL_KEYS = [];

// getStorageObject, saveStorageObject, getUserNamespace, isGlobalKey functions are assumed to be defined above and correct

export const storageManager = {
  init(store) {
    // console.log('[storageManager.js] init called');
    window.__VUEX_STORE__ = store;

    if (store && store.subscribe) {
      store.subscribe((mutation, state) => {
        if (mutation.type === 'setUser') {
          const oldNamespace = this.currentNamespace; 
          this.currentNamespace = getUserNamespace(); 
          // console.log('[storageManager.js] setUser detected in Vuex. Old namespace:', oldNamespace, 'New namespace:', this.currentNamespace);
          
          this.migrateThemeToUserNamespace();
          this.migrateExistingData();
          // console.log('[storageManager.js] Data migration complete for new user state.');
        }
      });
    } else {
      console.warn('[storageManager.js] init: store or store.subscribe is not available.');
    }
  },

  currentNamespace: getUserNamespace(),

  get(key, defaultValue = null) {
    const storage = getStorageObject();
    if (isGlobalKey(key)) {
      return storage.global[key] !== undefined ? storage.global[key] : defaultValue;
    }
    const userNamespace = getUserNamespace();
    if (!storage.users[userNamespace]) {
      return defaultValue;
    }
    return storage.users[userNamespace][key] !== undefined
        ? storage.users[userNamespace][key]
        : defaultValue;
  },

  set(key, value) {
    const storage = getStorageObject();
    if (isGlobalKey(key)) {
      storage.global[key] = value;
    } else {
      const userNamespace = getUserNamespace();
      if (!storage.users[userNamespace]) {
        storage.users[userNamespace] = {};
      }
      storage.users[userNamespace][key] = value;
    }
    saveStorageObject(storage);
  },

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

  migrateExistingData() {
    const storage = getStorageObject();
    const userNamespace = getUserNamespace(); 
    // console.log('[storageManager.js] migrateExistingData for namespace:', userNamespace);
    if (!storage.users[userNamespace]) {
      storage.users[userNamespace] = {};
    }
    const keysToMigrate = [
      { pattern: /^favorite-content-widget-.*$/, global: false },
      { key: 'user-avatar', global: false },
      { key: 'app-theme', global: false },
      { key: 'dashboard-layout', global: false },
      { key: 'user-playlist-order', global: false },
      { key: 'watchedPageNumber', global: false }
    ];

    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key === APP_STORAGE_KEY) continue;
      const keyConfig = keysToMigrate.find(k =>
          (k.key && k.key === key) || (k.pattern && k.pattern.test(key))
      );
      if (keyConfig) {
        const value = localStorage.getItem(key);
        try {
          const parsedValue = JSON.parse(value);
          if (keyConfig.global) storage.global[key] = parsedValue;
          else storage.users[userNamespace][key] = parsedValue;
        } catch (e) {
          if (keyConfig.global) storage.global[key] = value;
          else storage.users[userNamespace][key] = value;
        }
        localStorage.removeItem(key);
      }
    }
    saveStorageObject(storage);
  },

  migrateThemeToUserNamespace() {
    const storage = getStorageObject();
    const userNamespace = getUserNamespace();
    // console.log('[storageManager.js] migrateThemeToUserNamespace for namespace:', userNamespace);
    
    if (!storage.users[userNamespace]) {
      storage.users[userNamespace] = {};
    }
    
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
    } else if (storage.users['anonymous'] && storage.users['anonymous']['app-theme'] && userNamespace !== 'anonymous') {
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

// Assume getStorageObject, saveStorageObject, getUserNamespace, isGlobalKey are defined
// For example (these need to be complete based on your original file):
function getStorageObject() {
  const storageStr = localStorage.getItem(APP_STORAGE_KEY);
  try { return storageStr ? JSON.parse(storageStr) : { global: {}, users: {} }; }
  catch (e) { 
    console.error('[storageManager.js] Error parsing storage object:', e);
    return { global: {}, users: {} }; 
  }
}
function saveStorageObject(obj) { 
  try {
    localStorage.setItem(APP_STORAGE_KEY, JSON.stringify(obj)); 
  } catch (e) {
    console.error('[storageManager.js] Error saving storage object:', e);
  }
}
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
function isGlobalKey(key) { return GLOBAL_KEYS.includes(key) || key.startsWith('global-'); }

export default storageManager; 