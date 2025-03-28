<template>

  <div class="app-container">

    <header v-if="isAuthenticated">
      <nav>
        <h1>ScreenVault</h1>
        <div class="nav-links">
          <router-link to="/profile">Profile</router-link>
          <button @click="logout" class="logout-btn">Logout</button>
        </div>
      </nav>
    </header>

    <main>
      <router-view />
    </main>

  </div>
</template>

<script>
export default {
  computed: {
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    }
  },
  methods: {
    async logout() {
      try {
        await this.$http.post('/auth/logout', null, {
          withCredentials: true
        })
      } catch (error) {
        console.error('Logout failed:', error)
      } finally {
        this.$store.commit('setUser', null)
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style>
body, html {
  background-color: #424242;
}
</style>