<template>
  <animated-grid />
  <app-header />

  <wrapper class="m-5">
    <h2>Create your ScreenVault account</h2>
    <message-display v-if="message" :message="message" :error="error" />

    <auth-container
        :fields="[
          { id: 'username', label: 'username', name: 'username' },
          { id: 'password', label: 'password', name: 'password', type: 'password' },
          { id: 'confirmPassword', label: 'confirmPassword', name: 'confirmPassword', type: 'password' }
        ]"
        submitText="REGISTER"
        @submit="register"
        @oauth-login="loginWithOAuth"
    />
  </wrapper>
</template>

<script>
import Wrapper from "../components/form/wrapper.vue";
import AnimatedGrid from '../components/animatedGrid.vue';
import AppHeader from '../components/appHeader.vue';
import AuthContainer from "../components/auth/authContainer.vue";
import MessageDisplay from "../components/form/message.vue";

export default {
  name: "Register",
  components: {
    AuthContainer,
    AnimatedGrid,
    AppHeader,
    Wrapper,
    MessageDisplay,
  },
  data() {
    return {
      message: '',
      error: false
    }
  },
  methods: {
    async register(formData) {
      this.message = '';

      if (formData.password !== formData.confirmPassword) {
        this.message = 'Passwords do not match';
        this.error = true;
        return;
      }

      try {
        const response = await this.$http.post("/auth/register", {
          username: formData.username,
          password: formData.password,
          confirmPassword: formData.confirmPassword
        }, {
          withCredentials: true
        });

        if (response.data.code === 200) {
          this.message = response.data.message;
          this.error = false;
          setTimeout(() => {
            this.$router.push("/login");
          }, 1500);
        } else {
          this.message = response.data.message || 'Sign up failed. Please try again';
          this.error = true;
        }
      } catch (error) {
        this.message = 'Sign up failed. Please try again';
        this.error = true;
        console.error("Register error:", error);
      }
    },
    loginWithOAuth(provider) {
      window.location.href = `/oauth2/authorization/${provider}`;
    }
  }
}
</script>

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