import { createStore } from 'vuex';
import axios from 'axios';
import { storageManager } from '../utils/storageManager';

export default createStore({
    // State stores global user information, null by default indicates not logged in
    state: {
        user: null,
        _initialized: false
    },
    mutations: {
        setUser(state, user) {
            if (user) {
                const currentNamespace = getUserNamespaceFromUser(user);

                if (!user.avatar) {
                    if (!window.__VUEX_STORE__) {
                        storageManager.init(this);
                    }
                    user.avatar = getUserAvatar(user) || "George";
                }
            }
            state.user = user;
            
            if (!window.__VUEX_STORE__) {
                storageManager.init(this);
            }
            storageManager.init(this); // Re-initialize namespace every time user changes
            
            if (user && !state._initialized) {
                state._initialized = true;
            }
        },
        updateUserAvatar(state, avatarName) {
            if (state.user) {
                state.user.avatar = avatarName;
                const namespace = getUserNamespaceFromUser(state.user);
                try {
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
                storageManager.set('user-avatar', avatarName);
            }
        }
    },

    actions: {
        async fetchUser({ commit, state }) {
            try {
                const { data } = await axios.get('/auth/me');
                const wasLoggedIn = !!state.user;

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
                
                const isLoggedIn = !!data;
                const isOAuthLogin = !wasLoggedIn && isLoggedIn && window.location.pathname === '/dashboard';
                
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
                commit('setUser', null);
            }
        },
        async logout({ commit }) {
            try {
                await axios.post('/auth/logout', null, {
                    withCredentials: true
                });
            } catch (error) {
                console.error('Logout failed:', error);
            } finally {
                commit('setUser', null);
            }
        },
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
    getters: {
        isAuthenticated: (state) => !!state.user,
        getUserAvatar: (state) => {
            if (!state.user) return null;

            if (state.user.avatar) {
                return state.user.avatar;
            }

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

function getUserNamespaceFromUser(user) {
    if (!user) return 'anonymous';
    return user.username ? `user-${user.username}` : 'anonymous';
}

function getUserAvatar(user) {
    if (!user || !user.username) return null;
    
    try {
        const namespace = getUserNamespaceFromUser(user);
        
        const storage = JSON.parse(localStorage.getItem('screen-vault-storage') || '{"global":{},"users":{}}');
        
        if (storage.users[namespace] && storage.users[namespace]['user-avatar']) {
            const avatar = storage.users[namespace]['user-avatar'];
            return avatar;
        }

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