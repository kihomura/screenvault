/**
 * Storage Manager with namespace support
 * Enables storing data for multiple users in localStorage without conflicts
 * Simplified version that only uses username as identifier
 */

const APP_STORAGE_KEY = 'screen-vault-storage';

// Keys that should be stored in global namespace (shared between users)
const GLOBAL_KEYS = [];

/**
 * Get the current storage object or initialize a new one
 */
function getStorageObject() {
  const storageStr = localStorage.getItem(APP_STORAGE_KEY);
  if (storageStr) {
    try {
      return JSON.parse(storageStr);
    } catch (e) {
      console.error('Failed to parse storage, resetting', e);
      return { global: {}, users: {} };
    }
  }
  return { global: {}, users: {} };
}

/**
 * Save the storage object back to localStorage
 */
function saveStorageObject(obj) {
  localStorage.setItem(APP_STORAGE_KEY, JSON.stringify(obj));
}

/**
 * Get the user namespace using username
 * Falls back to 'anonymous' if no user is logged in
 */
function getUserNamespace() {
  try {
    const store = window.__VUEX_STORE__;
    const user = store && store.state.user;

    if (user && user.username) {
      return `user-${user.username}`;
    }

    return 'anonymous';
  } catch (e) {
    console.error('Error getting user namespace:', e);
    return 'anonymous';
  }
}

function isGlobalKey(key) {
  return GLOBAL_KEYS.includes(key) || key.startsWith('global-');
}

export const storageManager = {

  init(store) {
    window.__VUEX_STORE__ = store;

    const namespace = getUserNamespace();
    this.migrateThemeToUserNamespace();

    if (store && store.subscribe) {
      store.subscribe((mutation, state) => {
        if (mutation.type === 'setUser') {
          const newNamespace = getUserNamespace();
          if (namespace !== newNamespace) {

            setTimeout(() => {
              try {
                if (window.__PINIA_INSTANCE__) {
                  const themeStore = window.__PINIA_INSTANCE__.state.value.theme;
                  if (themeStore) {
                    themeStore.refreshTheme && themeStore.refreshTheme();
                  }
                }
              } catch (e) {
                console.error('Error refreshing theme after user change:', e);
              }
            }, 100);
          }
        }
      });
    }
  },

  /**
   * Get a value from storage
   * @param {string} key - The storage key
   * @param {*} defaultValue - Default value if key doesn't exist
   */
  get(key, defaultValue = null) {
    const storage = getStorageObject();

    // Check if this is a global key
    if (isGlobalKey(key)) {
      return storage.global[key] !== undefined ? storage.global[key] : defaultValue;
    }

    // Get user-specific value
    const userNamespace = getUserNamespace();
    if (!storage.users[userNamespace]) {
      return defaultValue;
    }

    return storage.users[userNamespace][key] !== undefined
        ? storage.users[userNamespace][key]
        : defaultValue;
  },

  /**
   * Set a value in storage
   * @param {string} key - The storage key
   * @param {*} value - The value to store
   */
  set(key, value) {
    const storage = getStorageObject();

    // Store in global namespace if it's a global key
    if (isGlobalKey(key)) {
      storage.global[key] = value;
    } else {
      // Store in user namespace
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

    if (!storage.users[userNamespace]) {
      storage.users[userNamespace] = {};
    }

    const keysToMigrate = [
      // Favorite widget keys - require special handling
      { pattern: /^favorite-content-widget-.*$/, global: false },
      { key: 'user-avatar', global: false },
      { key: 'app-theme', global: false },
      { key: 'dashboard-layout', global: false },
      { key: 'user-playlist-order', global: false },
      { key: 'watchedPageNumber', global: false }
    ];

    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);

      // Skip the new storage key itself
      if (key === APP_STORAGE_KEY) continue;

      // Check if this key should be migrated
      const keyConfig = keysToMigrate.find(k =>
          (k.key && k.key === key) || (k.pattern && k.pattern.test(key))
      );

      if (keyConfig) {
        const value = localStorage.getItem(key);

        try {
          const parsedValue = JSON.parse(value);

          if (keyConfig.global) {
            storage.global[key] = parsedValue;
          } else {
            storage.users[userNamespace][key] = parsedValue;
          }
        } catch (e) {
          if (keyConfig.global) {
            storage.global[key] = value;
          } else {
            storage.users[userNamespace][key] = value;
          }
        }

        localStorage.removeItem(key);
      }
    }

    saveStorageObject(storage);
  },

  /**
   * Migrate theme settings from global namespace to user namespace
   * Only needs to be executed once during initialization
   */
  migrateThemeToUserNamespace() {
    const storage = getStorageObject();
    const userNamespace = getUserNamespace();
    
    // Ensure user namespace exists
    if (!storage.users[userNamespace]) {
      storage.users[userNamespace] = {};
    }
    
    // Check if theme setting exists in global storage
    if (storage.global && storage.global['app-theme']) {
      // If user namespace doesn't have theme setting, use the global one
      if (!storage.users[userNamespace]['app-theme']) {
        storage.users[userNamespace]['app-theme'] = storage.global['app-theme'];
      }
      
      // Delete global theme setting
      delete storage.global['app-theme'];
      saveStorageObject(storage);
    }
  },
};

export default storageManager;