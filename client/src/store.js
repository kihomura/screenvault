import { createStore } from 'vuex';
import axios from 'axios';

export default createStore({
    state: {
        user: null
    },
    mutations: {
        setUser(state, user) {
            state.user = user;
        }
    },
    actions: {
        async fetchUser({ commit }) {
            try {
                //判断用户登录状态
                const { data } = await axios.get('/auth/me');
                commit('setUser', data);
            } catch (error) {
                commit('setUser', null);
            }
        }
    },
    getters: {
        isAuthenticated: (state) => !!state.user
    }
});
