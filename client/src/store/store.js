import { createStore } from 'vuex';
import axios from 'axios';

export default createStore({
    //state存储全局状态下的user信息，默认null表示未登录
    state: {
        user: null
    },
    //mutations负责修改state，必须是同步操作
    mutations: {
        setUser(state, user) {
            if (user && !user.avatar) {
                user.avatar = localStorage.getItem('user-avatar') || "George"; // Default avatar is George
            }
            state.user = user;
        },
        updateUserAvatar(state, avatarName) {
            if (state.user) {
                state.user.avatar = avatarName;
                localStorage.setItem('user-avatar', avatarName);
            }
        }
    },
    //actions是异步操作，用于请求API
    actions: {
        async fetchUser({ commit }) {
            try {
                const { data } = await axios.get('/auth/me');
                
                // If user exists in the response, check for avatar in localStorage
                if (data) {
                    const savedAvatar = localStorage.getItem('user-avatar');
                    if (savedAvatar && !data.avatar) {
                        data.avatar = savedAvatar;
                    }
                }
                
                commit('setUser', data);
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
                // You can implement the API call here to update the avatar in the backend
                // For now, we'll just update the local state
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
            return state.user.avatar || localStorage.getItem('user-avatar') || "George"; // Default to George if no avatar
        }
    }
});