/**
 * Main Vuex Store for ScreenVault Application
 * Manages global user authentication state and avatar persistence
 * Integrates with storageManager for user-specific data isolation
 */

import { createStore } from 'vuex';
import axios from 'axios';
import { storageManager } from '../utils/storageManager';

export default createStore({
    /**
     * Global application state
     * Stores user information and initialization status
     */
    state: {
        user: null, // User object when authenticated, null when not logged in
        _initialized: false // Flag to track if user data has been fetched
    },
    
    /**
     * Synchronous state mutations
     * All state changes must go through mutations
     */
    mutations: {
        /**
         * Set the current user and initialize storage namespace
         * @param {Object} state - Vuex state
         * @param {Object|null} user - User object or null for logout
         */
        setUser(state, user) {
            if (user) {
                const currentNamespace = getUserNamespaceFromUser(user);

                // Set default avatar if not provided
                if (!user.avatar) {
                    if (!window.__VUEX_STORE__) {
                        storageManager.init(this);
                    }
                    user.avatar = getUserAvatar(user) || "George";
                }
            }
            
            state.user = user;
            
            // Initialize storage manager with current store instance
            if (!window.__VUEX_STORE__) {
                storageManager.init(this);
            }
            storageManager.init(this); // Re-initialize namespace every time user changes
            
            // Mark as initialized on first successful user fetch
            if (user && !state._initialized) {
                state._initialized = true;
            }
        },
        
        /**
         * Update user avatar in state and persistent storage
         * @param {Object} state - Vuex state
         * @param {string} avatarName - New avatar name
         */
        updateUserAvatar(state, avatarName) {
            if (state.user) {
                state.user.avatar = avatarName;
                const namespace = getUserNamespaceFromUser(state.user);
                
                try {
                    // Directly update localStorage structure for avatar
                    const storageStr = localStorage.getItem('screen-vault-storage');
                    const storage = storageStr ? JSON.parse(storageStr) : { global: {}, users: {} };
                    if (!storage.users[namespace]) {
                        storage.users[namespace] = {};
                    }
                    storage.users[namespace]['user-avatar'] = avatarName;
                    localStorage.setItem('screen-vault-storage', JSON.stringify(storage));
                } catch (e) {
                    console.error('Error saving avatar to localStorage:', e);
                }
                
                // Also update through storage manager
                storageManager.set('user-avatar', avatarName);
            }
        }
    },

    /**
     * Asynchronous actions for API calls and complex operations
     */
    actions: {
        /**
         * Fetch current user from authentication endpoint
         * Handles OAuth login detection and page refresh logic
         * @param {Object} context - Vuex action context
         */
        async fetchUser({ commit, state }) {
            try {
                const { data } = await axios.get('/auth/me');
                const wasLoggedIn = !!state.user;

                // Restore user avatar from storage if not provided by API
                if (data) {
                    const namespace = getUserNamespaceFromUser(data);
                    try {
                        const storage = JSON.parse(localStorage.getItem('screen-vault-storage') || '{"global":{},"users":{}}');
                        const userAvatar = storage.users[namespace] && storage.users[namespace]['user-avatar'];
                        if (userAvatar && !data.avatar) {
                            data.avatar = userAvatar;
                        }
                    } catch (e) {
                        console.error('Error reading saved avatar:', e);
                    }
                }
                
                commit('setUser', data);
                
                // Handle OAuth login page refresh logic
                const isLoggedIn = !!data;
                const isOAuthLogin = !wasLoggedIn && isLoggedIn && window.location.pathname === '/dashboard';
                
                // Refresh page once after OAuth login to properly initialize all components
                if (isOAuthLogin && !localStorage.getItem('just_logged_in') && !localStorage.getItem('login_refreshed')) {
                    localStorage.setItem('login_refreshed', 'true');
                    setTimeout(() => {
                        window.location.reload();
                        setTimeout(() => {
                            localStorage.removeItem('login_refreshed');
                        }, 1000);
                    }, 100);
                }
            } catch (error) {
                // Set user to null if authentication fails
                commit('setUser', null);
            }
        },
        
        /**
         * Logout current user
         * Calls logout endpoint and clears user state
         * @param {Object} context - Vuex action context
         */
        async logout({ commit }) {
            try {
                await axios.post('/auth/logout', null, {
                    withCredentials: true
                });
            } catch (error) {
                console.error('Logout failed:', error);
            } finally {
                // Always clear user state regardless of API call result
                commit('setUser', null);
            }
        },
        
        /**
         * Update user avatar
         * @param {Object} context - Vuex action context
         * @param {string} avatarName - New avatar name
         * @returns {Object} Success/error result
         */
        async updateAvatar({ commit, state }, avatarName) {
            try {
                commit('updateUserAvatar', avatarName);
                return { success: true };
            } catch (error) {
                console.error('Update avatar failed:', error);
                return { success: false, error };
            }
        }
    },
    
    /**
     * Computed properties derived from state
     * Used for reactive data access in components
     */
    getters: {
        /**
         * Check if user is currently authenticated
         * @param {Object} state - Vuex state
         * @returns {boolean} True if user is logged in
         */
        isAuthenticated: (state) => !!state.user,
        
        /**
         * Get user avatar with fallback logic
         * @param {Object} state - Vuex state
         * @returns {string|null} Avatar name or null
         */
        getUserAvatar: (state) => {
            if (!state.user) return null;

            // Return avatar if already set
            if (state.user.avatar) {
                return state.user.avatar;
            }

            // Try to get saved avatar from storage
            const savedAvatar = getUserAvatar(state.user);
            if (savedAvatar) {
                state.user.avatar = savedAvatar;
                return savedAvatar;
            }
            
            // Use default avatar as last resort
            return "George";
        }
    }
});

/**
 * Helper Functions
 */

/**
 * Get namespace string for a user
 * @param {Object|null} user - User object
 * @returns {string} Namespace string (user-{username} or anonymous)
 */
function getUserNamespaceFromUser(user) {
    if (!user) return 'anonymous';
    return user.username ? `user-${user.username}` : 'anonymous';
}

/**
 * Get user avatar from storage with fallback logic
 * @param {Object} user - User object
 * @returns {string|null} Avatar name or null if not found
 */
function getUserAvatar(user) {
    if (!user || !user.username) return null;
    
    try {
        const namespace = getUserNamespaceFromUser(user);
        
        // Get storage object
        const storage = JSON.parse(localStorage.getItem('screen-vault-storage') || '{"global":{},"users":{}}');
        
        // Check user-specific storage first
        if (storage.users[namespace] && storage.users[namespace]['user-avatar']) {
            const avatar = storage.users[namespace]['user-avatar'];
            return avatar;
        }

        // Fallback to anonymous user storage for migration
        if (storage.users['anonymous'] && storage.users['anonymous']['user-avatar']) {
            const avatar = storage.users['anonymous']['user-avatar'];
            return avatar;
        }
        
        return null;
    } catch (e) {
        console.error('Error getting user avatar:', e);
        return null;
    }
}