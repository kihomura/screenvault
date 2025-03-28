import { createRouter, createWebHistory } from 'vue-router';
import store from '../store';
import intro from '../views/introduce.vue';
import login from '../views/login.vue';
import register from '../views/register.vue';
import profile from '../views/profile.vue';

const routes = [
  {
    path: '/',
    name: 'intro',
    component: intro,
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'login',
    component: login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'register',
    component: register,
    meta: { requiresAuth: false }
  },
  {
    path: '/profile',
    name: 'profile',
    component: profile,
    meta: { requiresAuth: true }
  },
  // {
  //   path: '/dashboard',
  //   name: 'dashboard',
  //   component: dashboard,
  //   meta: { requiresAuth: true }
  // }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
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
    return next({ name: 'profile' });
  }
  next();
});

export default router;
