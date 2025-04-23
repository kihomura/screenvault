<template>
  <form @submit.prevent="onSubmit" class="auth-form">
    <div v-for="(field, index) in fields" :key="index" class="field-wrapper">
      <component
          :is="FormGroup"
          v-bind="field"
          v-model="localForm[field.name]"
          @input="onInput(field.name)"
          @focus="handleFocus(field.name)"
          @blur="handleBlur(field.name)"
      />

      <transition name="fade" v-if="validationEnabled && field.name === 'username'">
        <div class="username-validation-container" v-show="shouldShowValidation('username')">
          <ul>
            <li :class="{ 'is_valid': usernameValidation.containsValidLength }">3-20 Characters</li>
            <li :class="{ 'is_valid': usernameValidation.containsValidCharacters }">Alphanumeric Characters</li>
            <li :class="{ 'is_valid': usernameValidation.isUnique }">Username is not taken</li>
          </ul>
          <div class="checkmark_container" :class="{ 'show_checkmark': usernameValidation.isValid }">
            <svg width="50%" height="50%" viewBox="0 0 140 100">
              <path class="checkmark" :class="{ 'checked': usernameValidation.isValid }" d="M10,50 l25,40 l95,-70"/>
            </svg>
          </div>
        </div>
      </transition>

      <transition name="fade" v-if="validationEnabled && field.name === 'password'">
        <div class="password-validation-container" v-show="shouldShowValidation('password')">
          <ul>
            <li :class="{ 'is_valid': passwordValidation.containsEightCharacters }">8 Characters</li>
            <li :class="{ 'is_valid': passwordValidation.containsNumber }">Contains Number</li>
            <li :class="{ 'is_valid': passwordValidation.containsUppercase }">Contains Uppercase</li>
            <li :class="{ 'is_valid': passwordValidation.containsSpecialCharacter }">Contains Special Character</li>
          </ul>
          <div class="checkmark_container" :class="{ 'show_checkmark': passwordValidation.isValid }">
            <svg width="50%" height="50%" viewBox="0 0 140 100">
              <path class="checkmark" :class="{ 'checked': passwordValidation.isValid }" d="M10,50 l25,40 l95,-70"/>
            </svg>
          </div>
        </div>
      </transition>

      <transition name="fade" v-if="validationEnabled && field.name === 'confirmPassword'">
        <div class="confirm-password-validation-container" v-show="shouldShowValidation('confirmPassword')">
          <ul>
            <li :class="{ 'is_valid': confirmPasswordValidation.isMatch }">Passwords match</li>
          </ul>
          <div class="checkmark_container" :class="{ 'show_checkmark': confirmPasswordValidation.isMatch }">
            <svg width="50%" height="50%" viewBox="0 0 140 100">
              <path class="checkmark" :class="{ 'checked': confirmPasswordValidation.isMatch }" d="M10,50 l25,40 l95,-70"/>
            </svg>
          </div>
        </div>
      </transition>

    </div>
    <login-button class="mt-4" :disabled="!isFormValid">
      {{ submitText }}
    </login-button>
  </form>
</template>

<script setup>
import { reactive, computed } from 'vue'
import FormGroup from '../../form/inputGroup.vue'
import LoginButton from "../../buttons/loginButton.vue";

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

const emit = defineEmits(['submit'])

const localForm = reactive({})
props.fields.forEach(field => {
  localForm[field.name] = field.value || ''
})

const usernameValidation = reactive({
  containsValidLength: false,
  containsValidCharacters: false,
  isUnique: false,
  isValid: false
})

const passwordValidation = reactive({
  containsEightCharacters: false,
  containsNumber: false,
  containsUppercase: false,
  containsSpecialCharacter: false,
  isValid: false
})

const confirmPasswordValidation = reactive({
  isMatch: false
})

const fieldFocus = reactive({
  username: false,
  password: false,
  confirmPassword: false
})
const fieldTouched = reactive({
  username: false,
  password: false,
  confirmPassword: false
})

const handleFocus = (fieldName) => {
  fieldFocus[fieldName] = true
  fieldTouched[fieldName] = true
}

const handleBlur = (fieldName) => {
  fieldFocus[fieldName] = false
}

const shouldShowValidation = (fieldName) => {
  if (!props.validationEnabled) return false;
  if (!fieldTouched[fieldName]) return false
  if (fieldFocus[fieldName]) return true
  if (fieldName === 'username') {
    return !usernameValidation.isValid
  } else if (fieldName === 'password') {
    return !passwordValidation.isValid
  } else if (fieldName === 'confirmPassword') {
    return !confirmPasswordValidation.isMatch
  }
  return false
}

const checkUsernameExists = async (username) => {
  try {
    const res = await fetch(`/auth/check_username?username=${encodeURIComponent(username)}`);
    const data = await res.json();
    return data.exists;
  } catch (error) {
    console.error(error);
    return false;
  }
}

const validateUsername = (username) => {
  usernameValidation.containsValidLength = username.length >= 3 && username.length <= 20;
  usernameValidation.containsValidCharacters = /^[a-zA-Z0-9_]+$/.test(username);
  usernameValidation.isUnique = false;
  checkUsernameExists(username).then(exists => {
    usernameValidation.isUnique = !exists;
    usernameValidation.isValid =
        usernameValidation.containsValidLength &&
        usernameValidation.containsValidCharacters &&
        usernameValidation.isUnique;
  });
}

const validatePassword = (password) => {
  const specialCharFormat = /[ !@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/;
  passwordValidation.containsEightCharacters = password.length >= 8;
  passwordValidation.containsNumber = /\d/.test(password);
  passwordValidation.containsUppercase = /[A-Z]/.test(password);
  passwordValidation.containsSpecialCharacter = specialCharFormat.test(password);
  passwordValidation.isValid =
      passwordValidation.containsEightCharacters &&
      passwordValidation.containsNumber &&
      passwordValidation.containsUppercase &&
      passwordValidation.containsSpecialCharacter;
}

const validateConfirmPassword = (confirmPassword) => {
  confirmPasswordValidation.isMatch = confirmPassword === localForm.password;
}

const onInput = (fieldName) => {
  if(props.validationEnabled) {
    validateField(fieldName)
  }
}

const validateField = (fieldName) => {
  if (fieldName === 'username') {
    validateUsername(localForm.username);
  }
  if (fieldName === 'password') {
    validatePassword(localForm.password);
    if (localForm.confirmPassword !== undefined) {
      validateConfirmPassword(localForm.confirmPassword);
    }
  }
  if (fieldName === 'confirmPassword') {
    validateConfirmPassword(localForm.confirmPassword);
  }
}

const isFormValid = computed(() => {
  const hasPasswordField = props.fields.some(f => f.name === 'password');
  const hasUsernameField = props.fields.some(f => f.name === 'username');
  const hasConfirmPasswordField = props.fields.some(f => f.name === 'confirmPassword');

  if(!props.validationEnabled) return true;

  return (!hasPasswordField || passwordValidation.isValid) &&
      (!hasUsernameField || usernameValidation.isValid) &&
      (!hasConfirmPasswordField || confirmPasswordValidation.isMatch);
})

const onSubmit = () => {
  if (isFormValid.value) {
    emit('submit', { ...localForm });
  }
}
</script>

<style scoped>
.auth-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.field-wrapper {
  position: relative;
  width: 100%;
  margin-top: 10px;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
.fade-enter-to, .fade-leave-from {
  opacity: 1;
}

ul {
  padding-left: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

li {
  margin-bottom: 6px;
  color: #525f7f;
  position: relative;
}

li:before {
  content: "";
  width: 0%;
  height: 2px;
  background: #8af1d9;
  position: absolute;
  left: 0;
  top: 50%;
  display: block;
  transition: all .6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.is_valid {
  color: rgba(136, 152, 170, 0.8);
}

.is_valid:before {
  width: 100%;
}

.checkmark_container {
  position: absolute;
  top: -15px;
  right: -15px;
  width: 50px;
  height: 50px;
  visibility: hidden;
  opacity: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: opacity .4s ease;
}

.show_checkmark {
  visibility: visible;
  opacity: 1;
}

.checkmark {
  width: 100%;
  height: 100%;
  fill: none;
  stroke: white;
  stroke-width: 15;
  stroke-linecap: round;
  stroke-dasharray: 180;
  stroke-dashoffset: 180;
}

.checked {
  animation: draw 0.5s ease forwards;
}

.username-validation-container,
.password-validation-container,
.confirm-password-validation-container {
  position: relative;
  width: 100%;
  margin-top: 10px;
}

@keyframes draw {
  to {
    stroke-dashoffset: 0;
  }
}
</style>
