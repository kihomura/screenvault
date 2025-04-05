<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>Your Profile</h1>
      <p>Manage your personal information and security settings</p>
    </div>

    <div class="profile-content" v-if="userProfile">
      <div class="profile-section">
        <div class="section-header">
          <h2>Account Information</h2>
          <button
              class="edit-button"
              @click="toggleEditMode"
              :class="{ 'active': isEditing }"
          >
            {{ isEditing ? 'Cancel' : 'Edit Profile' }}
          </button>
        </div>

        <div class="profile-info" v-if="!isEditing">
          <div class="info-group">
            <label>Username</label>
            <p>{{ userProfile.username }}</p>
          </div>
          <div class="info-group">
            <label>Email</label>
            <p>{{ userProfile.email || "Not bound" }}</p>
          </div>
          <div class="info-group">
            <label>Nickname</label>
            <p>{{ userProfile.nickname || 'Not set' }}</p>
          </div>
          <div class="info-group">
            <label>Account Created</label>
            <p>{{ formatDate(userProfile.createdAt) }}</p>
          </div>
          <div class="info-group">
            <label>Last Updated</label>
            <p>{{ formatDate(userProfile.updatedAt) }}</p>
          </div>
        </div>

        <form @submit.prevent="updateProfile" v-else class="edit-form">
          <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" v-model="userProfile.username" disabled class="disabled-input" />
            <small>Username cannot be changed</small>
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" v-model="userProfile.email" disabled class="disabled-input" />
            <small>Not support</small>
          </div>
          <div class="form-group">
            <label for="nickname">Nickname</label>
            <input type="text" id="nickname" v-model="profileForm.nickname" />
          </div>
          <div class="form-actions">
            <button type="submit" class="save-button">Save Changes</button>
          </div>
        </form>
      </div>

      <div class="profile-section">
        <h2>Security</h2>
        <button class="change-password-button" @click="showPasswordForm = !showPasswordForm">
          {{ showPasswordForm ? 'Cancel' : 'Change Password' }}
        </button>

        <transition name="fade">
          <form @submit.prevent="changePassword" v-if="showPasswordForm" class="password-form">
            <div class="form-group">
              <label for="newPassword">New Password</label>
              <input
                  type="password"
                  id="newPassword"
                  v-model="passwordForm.newPassword"
                  @input="validatePassword"
                  @focus="handleFocus('password')"
                  @blur="handleBlur('password')"
                  required
              />
              <transition name="fade">
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
            </div>
            <div class="form-group">
              <label for="confirmPassword">Confirm New Password</label>
              <input
                  type="password"
                  id="confirmPassword"
                  v-model="passwordForm.confirmPassword"
                  @input="validateConfirmPassword"
                  @focus="handleFocus('confirmPassword')"
                  @blur="handleBlur('confirmPassword')"
                  required
              />
              <transition name="fade">
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
            <div class="form-actions">
              <button
                  type="submit"
                  class="save-button"
                  :disabled="!passwordValidation.isValid || !confirmPasswordValidation.isMatch"
              >
                Update Password
              </button>
            </div>
          </form>
        </transition>
      </div>
    </div>

    <div class="loading-container" v-else>
      <div class="loading-spinner"></div>
      <p>Loading your profile...</p>
    </div>

    <Toast :show="toast.show" :message="toast.message" :type="toast.type" />
  </div>
</template>

<script>
import { reactive } from 'vue';
import Toast from '../components/ui/Toast.vue';

export default {
  name: 'ProfileView',
  components: { Toast },
  data() {
    return {
      userProfile: null,
      isEditing: false,
      profileForm: {
        nickname: ''
      },
      passwordForm: {
        newPassword: '',
        confirmPassword: ''
      },
      showPasswordForm: false,
      passwordValidation: reactive({
        containsEightCharacters: false,
        containsNumber: false,
        containsUppercase: false,
        containsSpecialCharacter: false,
        isValid: false
      }),
      confirmPasswordValidation: reactive({
        isMatch: false
      }),
      fieldFocus: reactive({
        password: false,
        confirmPassword: false
      }),
      fieldTouched: reactive({
        password: false,
        confirmPassword: false
      }),
      toast: {
        show: false,
        message: '',
        type: 'info',
        timeout: null
      }
    };
  },
  methods: {
    async fetchUserProfile() {
      try {
        const response = await this.$http.get('/user/profile');
        this.userProfile = response.data.data;
        this.profileForm.nickname = this.userProfile.nickname || '';
      } catch (error) {
        console.error('Error fetching user profile:', error);
        this.showToast('Failed to load profile data. Please try again.', 'error');
      }
    },
    toggleEditMode() {
      if (this.isEditing) {
        // 重置表单
        this.profileForm.nickname = this.userProfile.nickname || '';
      }
      this.isEditing = !this.isEditing;
    },
    async updateProfile() {
      try {
        const response = await this.$http.put('/user/update', {
          nickname: this.profileForm.nickname
        });

        if (response.data) {
          this.userProfile = response.data.data;
          this.isEditing = false;
          this.showToast('Profile updated successfully!', 'success');
        }
      } catch (error) {
        console.error('Error updating profile:', error);
        this.showToast('Failed to update profile. Please try again.', 'error');
      }
    },
    async changePassword() {
      if (!this.passwordValidation.isValid || !this.confirmPasswordValidation.isMatch) {
        return;
      }
      try {
        await this.$http.put('/user/change-password', {
          newPassword: this.passwordForm.newPassword
        });
        this.passwordForm = {
          newPassword: '',
          confirmPassword: ''
        };
        this.showPasswordForm = false;
        this.showToast('Password changed successfully!', 'success');
      } catch (error) {
        console.error('Error changing password:', error);
        if (error.response && error.response.status === 401) {
          this.showToast('Current password is incorrect.', 'error');
        } else {
          this.showToast('Failed to update password. Please try again.', 'error');
        }
      }
    },
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date);
    },
    handleFocus(fieldName) {
      this.fieldFocus[fieldName] = true;
      this.fieldTouched[fieldName] = true;
    },
    handleBlur(fieldName) {
      this.fieldFocus[fieldName] = false;
    },
    shouldShowValidation(fieldName) {
      if (!this.fieldTouched[fieldName]) return false;
      if (this.fieldFocus[fieldName]) return true;

      if (fieldName === 'password') {
        return !this.passwordValidation.isValid;
      } else if (fieldName === 'confirmPassword') {
        return !this.confirmPasswordValidation.isMatch;
      }
      return false;
    },
    validatePassword() {
      const password = this.passwordForm.newPassword;
      const specialCharFormat = /[ !@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/;
      this.passwordValidation.containsEightCharacters = password.length >= 8;
      this.passwordValidation.containsNumber = /\d/.test(password);
      this.passwordValidation.containsUppercase = /[A-Z]/.test(password);
      this.passwordValidation.containsSpecialCharacter = specialCharFormat.test(password);
      this.passwordValidation.isValid =
          this.passwordValidation.containsEightCharacters &&
          this.passwordValidation.containsNumber &&
          this.passwordValidation.containsUppercase &&
          this.passwordValidation.containsSpecialCharacter;
      if (this.passwordForm.confirmPassword) {
        this.validateConfirmPassword();
      }
    },
    validateConfirmPassword() {
      this.confirmPasswordValidation.isMatch =
          this.passwordForm.confirmPassword === this.passwordForm.newPassword;
    },
    showToast(message, type = 'info') {
      if (this.toast.timeout) {
        clearTimeout(this.toast.timeout);
      }
      this.toast.message = message;
      this.toast.type = type;
      this.toast.show = true;
      this.toast.timeout = setTimeout(() => {
        this.toast.show = false;
      }, 3000);
    }
  },
  mounted() {
    this.fetchUserProfile();
  }
};
</script>

<style scoped>
.profile-container {
  padding: var(--spacing-xl);
  max-width: 800px;
  margin: 0 auto;
  color: var(--text-primary);
}

.profile-header {
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
}

.profile-header h1 {
  font-family: var(--font-family-secondary);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-xs);
}

.profile-header p {
  color: var(--text-secondary);
  font-size: var(--font-size-lg);
}

.profile-section {
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
  box-shadow: var(--shadow-level1-default);
  transition: box-shadow 0.3s ease;
}

.profile-section:hover {
  box-shadow: var(--shadow-level1-hover);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.section-header h2 {
  font-family: var(--font-family-secondary);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  font-size: var(--font-size-xl);
}

.profile-info {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

.info-group {
  margin-bottom: var(--spacing-md);
}

.info-group label {
  display: block;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xs);
}

.info-group p {
  font-size: var(--font-size-lg);
  color: var(--text-primary);
  padding: var(--spacing-md);
  background-color: var(--background-muted);
  border-radius: var(--border-radius-md);
  word-break: break-word;
}

.edit-button,
.change-password-button {
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--border-radius-md);
  font-weight: var(--font-weight-medium);
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  background-color: var(--background-muted);
  color: var(--text-primary);
}

.edit-button:hover,
.change-password-button:hover {
  background-color: var(--interactive-hover);
}

.edit-button.active {
  background-color: var(--accent-error);
  color: white;
}

.change-password-button {
  margin-top: var(--spacing-md);
  background-color: var(--interactive-hover);
}

.form-group {
  margin-bottom: var(--spacing-lg);
  position: relative;
}

.form-group label {
  display: block;
  margin-bottom: var(--spacing-xs);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
}

.form-group input {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border-medium);
  border-radius: var(--border-radius-md);
  background-color: var(--background-base);
  color: var(--text-primary);
  font-size: var(--font-size-base);
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2);
}

.form-group .disabled-input {
  background-color: var(--background-muted);
  cursor: not-allowed;
  opacity: 0.7;
}

.form-group small {
  display: block;
  margin-top: var(--spacing-xs);
  color: var(--text-muted);
  font-size: var(--font-size-xs);
}

.save-button {
  padding: var(--spacing-md) var(--spacing-xl);
  background-color: var(--primary);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.save-button:hover {
  background-color: var(--primary-dark, #2563eb);
}

.save-button:disabled {
  background-color: var(--background-muted);
  color: var(--text-muted);
  cursor: not-allowed;
}

.form-actions {
  margin-top: var(--spacing-xl);
  display: flex;
  justify-content: flex-end;
}

/* Password validation styles */
.password-validation-container,
.confirm-password-validation-container {
  position: relative;
  width: 100%;
  margin-top: var(--spacing-md);
  background-color: var(--background-base);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-md);
  border: 1px solid var(--border-light);
}

ul {
  padding-left: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

li {
  margin-bottom: var(--spacing-xs);
  color: var(--text-secondary);
  position: relative;
}

li:before {
  content: "";
  width: 0%;
  height: 2px;
  background: var(--accent-success);
  position: absolute;
  left: 0;
  top: 50%;
  display: block;
  transition: all .6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.is_valid {
  color: var(--text-muted);
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
  stroke: var(--accent-success);
  stroke-width: 15;
  stroke-linecap: round;
  stroke-dasharray: 180;
  stroke-dashoffset: 180;
}

.checked {
  animation: draw 0.5s ease forwards;
}

@keyframes draw {
  to {
    stroke-dashoffset: 0;
  }
}

/* Loading state */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--background-muted);
  border-top: 4px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--spacing-md);
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Animations */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .profile-container {
    padding: var(--spacing-md);
  }

  .profile-section {
    padding: var(--spacing-lg);
  }

  .profile-info {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .edit-button,
  .change-password-button {
    margin-top: var(--spacing-md);
  }
}
</style>
