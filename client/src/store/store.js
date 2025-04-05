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
            state.user = user;
        }
    },
    //actions是异步操作，用于请求API
    actions: {
        async fetchUser({ commit }) {
            try {
                const { data } = await axios.get('/auth/me');
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
        }
    },
    getters: {
        isAuthenticated: (state) => !!state.user
    }
});