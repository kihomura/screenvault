<template>
  <div class="watch-page">
    <div class="page-header">
      <div class="header-content">
        <h2>Profile</h2>
      </div>
      <div class="header-actions">
        <main-btn 
            :type="isEditing ? 'danger' : 'secondary'" 
            @click="toggleEditMode"
        >
          {{ isEditing ? 'Cancel' : 'Edit Profile' }}
        </main-btn>
      </div>
    </div>

    <div class="profile-container" v-if="userProfile">
      <div class="profile-card">
        <div class="card-header">
          <h2 class="card-title">Account Information</h2>
        </div>

        <div class="profile-info" v-if="!isEditing">
          <div class="info-column personal-info">
            <div class="avatar-section">
              <label>Avatar</label>
              <div class="avatar-container">
                <img :src="avatarUrl" alt="User avatar" class="profile-avatar" />
                <button class="change-avatar-btn" @click="openAvatarModal">Change</button>
              </div>
            </div>
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
          </div>
          <div class="info-column date-info">
            <div class="info-group">
              <label>Account Created</label>
              <p>{{ formatDate(userProfile.createdAt) }}</p>
            </div>
            <div class="info-group">
              <label>Last Updated</label>
              <p>{{ formatDate(userProfile.updatedAt) }}</p>
            </div>
          </div>
        </div>

        <form @submit.prevent="updateProfile" v-else class="edit-form">
          <div class="form-columns">
            <div class="form-column">
              <div class="avatar-section">
                <label>Avatar</label>
                <div class="avatar-container">
                  <img :src="avatarUrl" alt="User avatar" class="profile-avatar" />
                  <button type="button" class="change-avatar-btn" @click="openAvatarModal">Change Avatar</button>
                </div>
              </div>
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
            </div>
            <div class="form-column date-column">
              <div class="info-group">
                <label>Account Created</label>
                <p>{{ formatDate(userProfile.createdAt) }}</p>
              </div>
              <div class="info-group">
                <label>Last Updated</label>
                <p>{{ formatDate(userProfile.updatedAt) }}</p>
              </div>
            </div>
          </div>
          <div class="form-actions">
            <main-btn type="primary" @click="updateProfile">Save Changes</main-btn>
          </div>
        </form>
      </div>

      <div class="profile-card">
        <div class="card-header">
          <h2 class="card-title">Security</h2>
          <main-btn 
              :type="showPasswordForm ? 'danger' : 'info'"
              @click="showPasswordForm = !showPasswordForm"
          >
            {{ showPasswordForm ? 'Cancel' : 'Change Password' }}
          </main-btn>
        </div>

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
                  <ul class="validation-list">
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
                  <ul class="validation-list">
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
              <main-btn 
                  type="primary"
                  :disabled="!passwordValidation.isValid || !confirmPasswordValidation.isMatch"
                  @click="changePassword"
              >
                Update Password
              </main-btn>
            </div>
          </form>
        </transition>
      </div>
    </div>

    <div class="loading-container" v-else>
      <div class="loading-spinner"></div>
      <p>Loading your profile...</p>
    </div>
    
    <avatar-selection-modal
      :is-open="showAvatarModal"
      @close="closeAvatarModal"
      @avatar-updated="handleAvatarUpdated"
    />
  </div>
</template>

<script>
import { reactive, computed } from 'vue';
import MainBtn from '../components/buttons/MainBtn.vue';
import AvatarSelectionModal from '../components/modals/AvatarSelectionModal.vue';
import { useToastStore } from '../store/toastStore.js';
import { useStore } from 'vuex';

export default {
  name: 'ProfileView',
  components: { 
    MainBtn,
    AvatarSelectionModal
  },
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
      showAvatarModal: false,
      toastStore: null,
      store: null
    };
  },
  created() {
    this.toastStore = useToastStore();
    this.store = useStore();
  },
  computed: {
    avatarUrl() {
      const avatar = this.store.getters.getUserAvatar || 'George';
      return `https://api.dicebear.com/9.x/bottts-neutral/svg?seed=${avatar}`;
    }
  },
  methods: {
    async fetchUserProfile() {
      try {
        const response = await this.$http.get('/user/profile');
        this.userProfile = response.data.data;
        this.profileForm.nickname = this.userProfile.nickname || '';
      } catch (error) {
        console.error('Error fetching user profile:', error);
        this.toastStore.error('Failed to load profile data. Please try again.');
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
          this.toastStore.success('Profile updated successfully!');
        }
      } catch (error) {
        console.error('Error updating profile:', error);
        this.toastStore.error('Failed to update profile. Please try again.');
      }
    },
    async changePassword() {
      if (!this.passwordValidation.isValid || !this.confirmPasswordValidation.isMatch) {
        return;
      }

      try {
        await this.$http.put('/user/update-password', {
          newPassword: this.passwordForm.newPassword
        });

        this.passwordForm.newPassword = '';
        this.passwordForm.confirmPassword = '';
        this.showPasswordForm = false;
        
        this.toastStore.success('Password updated successfully!');
      } catch (error) {
        console.error('Error changing password:', error);
        this.toastStore.error('Failed to update password. Please try again.');
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
      if (type === 'success') {
        this.toastStore.success(message);
      } else if (type === 'error') {
        this.toastStore.error(message);
      } else {
        this.toastStore.info(message);
      }
    },
    openAvatarModal() {
      this.showAvatarModal = true;
    },
    closeAvatarModal() {
      this.showAvatarModal = false;
    },
    async handleAvatarUpdated(avatar) {
      // The avatar is already updated in the store by the modal component
      this.toastStore.success('Avatar updated successfully!');
    }
  },
  mounted() {
    this.fetchUserProfile();
  }
};
</script>

<style scoped>
.watch-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-lg);
  font-family: var(--font-fontFamily-primary);
  color: var(--text-primary);
}

.page-subtitle {
  color: var(--text-secondary);
  font-size: var(--font-fontSize-base);
  margin: 0;
}

.profile-container {
  max-width: 900px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
}

.profile-card {
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level1-default);
  overflow: hidden;
  transition: box-shadow 0.3s ease;
}

.profile-card:hover {
  box-shadow: var(--shadow-level1-hover);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
  background-color: var(--background-subtle);
}

.card-title {
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-semibold);
  color: var(--text-primary);
  font-size: var(--font-fontSize-xl);
  margin: 0;
}

.profile-info {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: var(--spacing-xl);
  padding: var(--spacing-lg);
}

.info-column {
  display: flex;
  flex-direction: column;
}

.personal-info {
  border-right: 1px solid var(--border-light);
  padding-right: var(--spacing-lg);
}

.date-info {
  padding-left: var(--spacing-md);
}

.info-group {
  margin-bottom: var(--spacing-lg);
}

.info-group label {
  display: block;
  font-size: var(--font-fontSize-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xs);
  font-weight: var(--font-fontWeight-medium);
}

.info-group p {
  font-size: var(--font-fontSize-base);
  color: var(--text-primary);
  padding: var(--spacing-md);
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-md);
  word-break: break-word;
  margin: 0;
}

.edit-form,
.password-form {
  padding: var(--spacing-lg);
}

.form-columns {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: var(--spacing-xl);
}

.form-column {
  display: flex;
  flex-direction: column;
}

.date-column {
  padding-left: var(--spacing-md);
  border-left: 1px solid var(--border-light);
}

.form-group {
  margin-bottom: var(--spacing-lg);
  position: relative;
}

.form-group label {
  display: block;
  margin-bottom: var(--spacing-xs);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
  font-size: var(--font-fontSize-sm);
}

.form-group input {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border-medium);
  border-radius: var(--border-radius-md);
  background-color: var(--background-base);
  color: var(--text-primary);
  font-size: var(--font-fontSize-base);
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
  font-size: var(--font-fontSize-xs);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-lg);
}

/* Password validation styles */
.password-validation-container,
.confirm-password-validation-container {
  position: relative;
  width: 100%;
  margin-top: var(--spacing-md);
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-md);
  border: 1px solid var(--border-light);
}

.validation-list {
  padding-left: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
  margin: 0;
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
  .watch-page {
    padding: var(--spacing-md);
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .header-actions {
    width: 100%;
    margin-top: var(--spacing-md);
  }

  .profile-info,
  .form-columns {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
  }

  .personal-info {
    border-right: none;
    padding-right: 0;
    border-bottom: 1px solid var(--border-light);
    padding-bottom: var(--spacing-lg);
  }

  .date-info {
    padding-left: 0;
    padding-top: var(--spacing-md);
  }

  .date-column {
    padding-left: 0;
    border-left: none;
    padding-top: var(--spacing-md);
    border-top: 1px solid var(--border-light);
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .card-header button {
    width: 100%;
    margin-top: var(--spacing-xs);
  }
}

/* Add avatar-related styles */
.avatar-section {
  margin-bottom: var(--spacing-md);
}

.avatar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--spacing-sm);
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: var(--border-radius-md);
  border: 2px solid var(--border-medium);
  background-color: var(--background-subtle);
}

.change-avatar-btn {
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--background-subtle);
  color: var(--text-primary);
  border: 1px solid var(--border-medium);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: var(--font-fontSize-sm);
}

.change-avatar-btn:hover {
  background-color: var(--background-muted);
  border-color: var(--primary);
}
</style>
