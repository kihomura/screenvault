<template>
  <div class="profile-container">
    <h2>profile</h2>

    <div v-if="loading" class="loading">loading...</div>

    <div v-else>
      <div v-if="message" class="alert" :class="{ 'alert-error': error, 'alert-success': !error }">
        {{ message }}
      </div>

      <div class="tabs">
        <button
            @click="activeTab = 'profile'"
            :class="{ active: activeTab === 'profile' }"
        >
          personal info
        </button>
        <button
            @click="activeTab = 'password'"
            :class="{ active: activeTab === 'password' }"
        >
          change password
        </button>
      </div>

      <div v-if="activeTab === 'profile'" class="tab-content">
        <form @submit.prevent="updateProfile">
          <div class="form-group">
            <label for="username">username</label>
            <input
                type="text"
                id="username"
                v-model="profile.username"
                disabled
            />
          </div>
          <div class="form-group">
            <label for="nickname">nickname</label>
            <input
                type="text"
                id="nickname"
                v-model="profile.nickname"
                required
            />
          </div>
          <div class="form-group">
            <label>register date</label>
            <div class="static-field">{{ formatDate(profile.createdAt) }}</div>
          </div>
          <button type="submit" class="btn-primary" :disabled="updateLoading">
            {{ updateLoading ? 'updating...' : 'save' }}
          </button>
        </form>
      </div>


      <div v-if="activeTab === 'password'" class="tab-content">
        <form @submit.prevent="changePassword">
          <div class="form-group">
            <label for="oldPassword">current password</label>
            <input
                type="password"
                id="oldPassword"
                v-model="passwordForm.oldPassword"
                required
            />
          </div>
          <div class="form-group">
            <label for="newPassword">new password</label>
            <input
                type="password"
                id="newPassword"
                v-model="passwordForm.newPassword"
                required
            />
          </div>
          <div class="form-group">
            <label for="confirmNewPassword">confirm new password</label>
            <input
                type="password"
                id="confirmNewPassword"
                v-model="confirmNewPassword"
                required
            />
            <span v-if="newPasswordMismatch" class="error-text">password does not match</span>
          </div>
          <button type="submit" class="btn-primary" :disabled="passwordLoading || newPasswordMismatch">
            {{ passwordLoading ? 'updating...' : 'change password' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activeTab: 'profile',
      profile: {
        id: null,
        username: '',
        nickname: '',
        enabled: true,
        createdAt: null
      },
      passwordForm: {
        oldPassword: '',
        newPassword: ''
      },
      confirmNewPassword: '',
      loading: true,
      updateLoading: false,
      passwordLoading: false,
      message: '',
      error: false
    }
  },
  computed: {
    newPasswordMismatch() {
      return this.passwordForm.newPassword && this.confirmNewPassword &&
          this.passwordForm.newPassword !== this.confirmNewPassword
    }
  },
  created() {
    this.fetchUserProfile()
  },
  methods: {
    fetchUserProfile() {
      this.loading = true

      fetch('/user/profile', {
        credentials: 'include'
      })
          .then(response => response.json())
          .then(data => {
            this.loading = false

            if (data.code === 200) {
              this.profile = data.data
            } else {
              this.message = data.message || 'failed to get user info'
              this.error = true
            }
          })
          .catch(error => {
            this.loading = false
            this.message = 'bad request'
            this.error = true
            console.error('Fetch profile error:', error)
          })
    },

    updateProfile() {
      this.updateLoading = true
      this.message = ''

      fetch('/user/update', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          nickname: this.profile.nickname
        }),
        credentials: 'include'
      })
          .then(response => response.json())
          .then(data => {
            this.updateLoading = false

            if (data.code === 200) {
              this.message = data.message || 'success update'
              this.error = false

              const user = JSON.parse(localStorage.getItem('user') || '{}')
              user.nickname = this.profile.nickname
              localStorage.setItem('user', JSON.stringify(user))
            } else {
              this.message = data.message || 'failed update'
              this.error = true
            }
          })
          .catch(error => {
            this.updateLoading = false
            this.message = 'bad request'
            this.error = true
            console.error('Update profile error:', error)
          })
    },

    changePassword() {
      if (this.newPasswordMismatch) {
        return
      }

      this.passwordLoading = true
      this.message = ''

      fetch('/user/change-password', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(this.passwordForm),
        credentials: 'include'
      })
          .then(response => response.json())
          .then(data => {
            this.passwordLoading = false

            if (data.code === 200) {
              this.message = data.message || 'change success'
              this.error = false

              this.passwordForm.oldPassword = ''
              this.passwordForm.newPassword = ''
              this.confirmNewPassword = ''
            } else {
              this.message = data.message || 'change failed'
              this.error = true
            }
          })
          .catch(error => {
            this.passwordLoading = false
            this.message = 'bad request'
            this.error = true
            console.error('Change password error:', error)
          })
    },

    formatDate(dateString) {
      if (!dateString) return 'unknown'

      const date = new Date(dateString)
      return new Intl.DateTimeFormat('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      }).format(date)
    }
  }
}
</script>

<style>

</style>