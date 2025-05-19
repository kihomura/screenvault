import { createRouter, createWebHistory } from 'vue-router';
import store from '../store/store.js';

// views
import intro from '../views/Introduce.vue';
import login from '../views/Login.vue';
import register from '../views/Register.vue';
import dashboard from '../views/Dashboard.vue';
import watched from '../views/Watched.vue';
import playlists from '../views/Playlist.vue';
import wishlists from '../views/Wishlist.vue';
import statistics from '../views/Statistics.vue';
import profile from '../views/Profile.vue';
import contentDetail from "../views/ContentDetail.vue";
import playlistDetail from "../views/PlaylistDetail.vue";
import Manage from "../views/Manage.vue";

const routes = [
  {
    path: '/',
    name: 'intro',
    component: intro,
    meta: { requiresAuth: false, noTheme: true }
  },
  {
    path: '/login',
    name: 'login',
    component: login,
    meta: { requiresAuth: false, noTheme: true }
  },
  {
    path: '/register',
    name: 'register',
    component: register,
    meta: { requiresAuth: false, noTheme: true }
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/watched',
    name: 'watched',
    component: watched,
    meta: { requiresAuth: true }
  },
  {
    path: '/playlist',
    name: 'playlist',
    component: playlists,
    meta: { requiresAuth: true }
  },
  {
    path: '/wishlist',
    name: 'wishlist',
    component: wishlists,
    meta: { requiresAuth: true }
  },
  {
    path: '/statistics',
    name: 'statistics',
    component: statistics,
    meta: { requiresAuth: true }
  },
  {
    path: '/content/:id',
    name:'ContentDetail',
    component: contentDetail,
    meta: { requiresAuth: true }
  },
  {
    path: '/playlist/:id',
    name: 'PlaylistDetail',
    component: playlistDetail,
    meta: { requiresAuth: true }
  },
  {
    path: '/manage/:section',
    name: 'manage',
    component: Manage,
    props: true,
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory,
  routes
});

router.beforeEach(async (to, from, next) => {
  if (store.state.user === null) {
    await store.dispatch('fetchUser');
  }
  const isAuthenticated = store.getters.isAuthenticated;

  //如果未登录，重定向到login
  if (to.meta.requiresAuth && !isAuthenticated) {
    return next({ name: 'login' });
  }
  //如果已登录，重定向到Dashboard
  if ((to.name === 'login' || to.name === 'intro' || to.name === 'register') && isAuthenticated) {
    return next({ name: 'dashboard' });
  }
  next();
});

export default router;
