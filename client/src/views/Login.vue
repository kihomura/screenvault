<template>
  <div class="auth-page-container">
    <animated-grid />
    <auth-header />

    <div class="auth-content">
      <div class="auth-form-container">
        <h2 class="auth-title">Log In to Your Account</h2>
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
      </div>
    </div>
  </div>
</template>

<script>
import AnimatedGrid from '../components/common/animatedGrid.vue';
import AuthHeader from '../components/ui/AuthHeader.vue';
import AuthContainer from "../components/ui/auth/authContainer.vue";
import MessageDisplay from "../components/forms/message.vue";

export default {
  name: "Login",
  components: {
    AuthContainer,
    AnimatedGrid,
    AuthHeader,
    MessageDisplay,
  },
  mounted() {
  },
  unmounted() {
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
      // Use the same logic as main.js for determining backend URL
      const backendBaseUrl = import.meta.env.DEV ? 
        (import.meta.env.VITE_API_URL || 'http://localhost:5555') :
        'https://screenvault-server-production.up.railway.app';
      
      if (!backendBaseUrl) {
        console.error('Error: Unable to determine backend URL.');
        this.message = "Configuration error: Unable to determine API server address.";
        this.error = true;
        return;
      }
      
      window.location.href = `${backendBaseUrl}/oauth2/authorization/${provider}`;
    }
  }
};
</script>

<style scoped>
.auth-page-container {
  width: 100%;
  min-height: 100vh;
  position: relative;
  background-image: 
    radial-gradient(circle at 10% 10%, rgba(88, 150, 232, 0.2), transparent 25%),
    radial-gradient(circle at 90% 90%, rgba(255, 42, 109, 0.2), transparent 30%);
}

.auth-content {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  min-height: calc(100vh - 150px);
}

.auth-form-container {
  background-color: rgba(20, 25, 35, 0.7);
  border-radius: 10px;
  box-shadow: 
    0 0 20px rgba(5, 217, 232, 0.2),
    0 0 40px rgba(0, 0, 0, 0.3);
  padding: 2.5rem;
  width: 100%;
  max-width: 500px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.auth-form-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #05d9e8, #ff2a6d, transparent);
}

.auth-title {
  text-align: center;
  margin-bottom: 2rem;
  color: #ffffff;
  text-shadow: 
    0 0 7px #05d9e8,
    0 0 10px rgba(255, 42, 109, 0.4);
  font-family: 'Orbitron', sans-serif;
  font-weight: 600;
  letter-spacing: 1px;
  font-size: 1.8rem;
}

@media (max-width: 768px) {
  .auth-content {
    padding: 1.5rem;
  }
  
  .auth-form-container {
    padding: 2rem 1.5rem;
  }
  
  .auth-title {
    font-size: 1.5rem;
  }
}

@media (max-width: 480px) {
  .auth-content {
    padding: 1rem;
  }
  
  .auth-form-container {
    padding: 1.5rem 1rem;
  }
}
</style>