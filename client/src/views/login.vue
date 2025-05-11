<template>
  <animated-grid />
  <app-header />

  <wrapper class="m-5">
    <h2>Login your ScreenVault account</h2>
    <message-display v-if="message" :message="message" :error="error" />

    <auth-container
        :fields="[
          { id: 'username', label: 'username', name: 'username' },
          { id: 'password', label: 'password', name: 'password', type: 'password' }
        ]"
        submitText="LOGIN"
        :validationEnabled="false"
        @submit="login"
        @oauth-login="loginWithOAuth"
    />
  </wrapper>
</template>

<script>
import Wrapper from "../components/form/wrapper.vue";
import AnimatedGrid from '../components/animatedGrid.vue';
import AppHeader from '../components/ui/Header.vue';
import AuthContainer from "../components/ui/auth/authContainer.vue";
import MessageDisplay from "../components/form/message.vue";

export default {
  name: "Login",
  components: {
    AuthContainer,
    AnimatedGrid,
    AppHeader,
    Wrapper,
    MessageDisplay,
  },
  mounted() {
    document.body.classList.add('auth-page');
  },
  unmounted() {
    document.body.classList.remove('auth-page');
  },
  data() {
    return {
      message: "",
      error: false
    };
  },
  methods: {
    async login(formData) {
      this.message = "";
      try {
        const params = new URLSearchParams();
        params.append("username", formData.username);
        params.append("password", formData.password);

        const response = await this.$http.post("/auth/login", params, {
          withCredentials: true,
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
        });

        if (response.data.code === 200) {
          this.$store.commit('setUser', response.data.data);
          this.message = response.data.message;
          this.error = false;

          this.$router.push("/dashboard");
        } else {
          this.message = "Incorrect username or password";
          this.error = true;
        }
      } catch (error) {
        this.message = "Login failed, please try later";
        this.error = true;
        console.error("Login error:", error);
      }
    },
    loginWithOAuth(provider) {
      window.location.href = `/oauth2/authorization/${provider}`;
    }
  }
};
</script>

<style>
.auth-page {
  background-color: #252525 !important;
}
</style>

<style scoped>
h2 {
  text-align: center;
  margin-bottom: 50px;
  color: #ffffff;
  text-shadow: 0 0 7px #05d9e8,
  0 0 10px rgba(255, 42, 109, 0.65);
  display: inline-block;
}
</style>