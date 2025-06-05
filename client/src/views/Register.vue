<template>
  <div class="auth-page-container">
    <animated-grid />
    <auth-header />

    <div class="auth-content">
      <div class="auth-form-container">
        <h2 class="auth-title">Create Your Account</h2>
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
  name: "Register",
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
        console.log("Sending register request to:", this.$http.defaults.baseURL + "/auth/register");
        
        const response = await this.$http.post("/auth/register", {
          username: formData.username,
          password: formData.password,
          confirmPassword: formData.confirmPassword
        }, {
          withCredentials: true,
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        });

        console.log("Register response:", response);

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
        console.error("Register error details:", error);
        if (error.response) {
          console.error("Error response:", error.response.data);
          this.message = error.response.data.message || 'Registration failed: Server error';
        } else if (error.request) {
          console.error("No response received:", error.request);
          this.message = 'No response from server. Please check your connection.';
        } else {
          console.error("Error setting up request:", error.message);
          this.message = 'Error setting up request: ' + error.message;
        }
        this.error = true;
      }
    },
    loginWithOAuth(provider) {
      const backendBaseUrl = import.meta.env.VITE_API_URL;
      if (!backendBaseUrl) {
        console.error('Error: VITE_API_BASE_URL is not defined. Please set it in your .env file.');
        this.message = "Configuration error: Unable to determine API server address.";
        this.error = true;
        return;
      }
      window.location.href = `${backendBaseUrl}/oauth2/authorization/${provider}`;
    }
  }
}
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