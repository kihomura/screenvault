<template>
  <div class="auth-container">
    <auth-form
        :fields="fields"
        :submitText="submitText"
        :validationEnabled="validationEnabled"
        @submit="onSubmit"
    />
    <p class="m-3">or</p>

    <!-- OAuth2 -->
    <form-button @click="handleOAuth('google')" class="oauth-button google m-2">
      <font-awesome-icon :icon="['fab', 'google']"/> Continue with Google
    </form-button>
    <form-button @click="handleOAuth('github')" class="oauth-button github m-2">
      <font-awesome-icon :icon="['fab', 'github']"/> Continue with GitHub
    </form-button>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import FormButton from "../../buttons/formButton.vue";
import AuthForm from "./authForm.vue";

const props = defineProps({
  fields: {
    type: Array,
    required: true,
  },
  submitText: {
    type: String,
    required: true,
  },
  validationEnabled: {
    type: Boolean,
    default: true,
  }
})

const emit = defineEmits(['submit', 'oauth-login'])

const onSubmit = (formData) => {
  emit('submit', formData)
}
const handleOAuth = (provider) => {
  emit('oauth-login', provider)
}
</script>

<style scoped>
.auth-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 400px;
}
</style>
