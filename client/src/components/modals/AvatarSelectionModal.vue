<template>
  <div class="modal-backdrop" v-if="isOpen" @click.self="closeModal">
    <div class="modal-container">
      <div class="modal-header">
        <h2 class="modal-title">Choose Your Avatar</h2>
        <button class="close-button" @click="closeModal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>

      <div class="modal-body">
        <div class="avatars-container">
          <div 
            v-for="avatar in avatarOptions" 
            :key="avatar" 
            class="avatar-option" 
            :class="{ 'selected': selectedAvatar === avatar }"
            @click="selectAvatar(avatar)"
          >
            <img 
              :src="`https://api.dicebear.com/9.x/bottts-neutral/svg?seed=${avatar}`" 
              :alt="avatar" 
              class="avatar-image" 
            />
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <main-btn type="secondary" @click="closeModal">Cancel</main-btn>
        <main-btn type="highlight" @click="saveAvatar" :disabled="!selectedAvatar">Save</main-btn>
      </div>
    </div>
  </div>
</template>

<script>
import MainBtn from "../buttons/MainBtn.vue";
import { useStore } from 'vuex';
import { useToastStore } from '../../store/toastStore.js';
import { ref, computed, onMounted } from 'vue';

export default {
  name: 'AvatarSelectionModal',
  components: { MainBtn },
  props: {
    isOpen: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close', 'avatar-updated'],
  setup(props, { emit }) {
    const store = useStore();
    const toastStore = useToastStore();
    
    const avatarOptions = [
      'Brian', 'Caleb', 'Alexander', 'Aidan',
      'Leah', 'Ryker', 'Avery', 'Easton',
      'George', 'Riley', 'Vivian', 'Sadie',
      'Adrian', 'Liam', 'Robert', 'Amaya',
      'Emery', 'Sara', 'Jade', 'Luis'
    ];
    
    const currentAvatar = computed(() => store.getters.getUserAvatar);
    const selectedAvatar = ref(currentAvatar.value || 'George');
    
    const closeModal = () => {
      emit('close');
    };
    
    const selectAvatar = (avatar) => {
      selectedAvatar.value = avatar;
    };
    
    const saveAvatar = async () => {
      if (!selectedAvatar.value) return;
      
      const result = await store.dispatch('updateAvatar', selectedAvatar.value);
      
      if (result.success) {
        toastStore.success('Avatar updated successfully!');
        emit('avatar-updated', selectedAvatar.value);
        closeModal();
      } else {
        toastStore.error('Failed to update avatar. Please try again.');
      }
    };
    
    onMounted(() => {
      selectedAvatar.value = currentAvatar.value || 'George';
    });
    
    return {
      avatarOptions,
      selectedAvatar,
      closeModal,
      selectAvatar,
      saveAvatar
    };
  }
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
  backdrop-filter: blur(4px);
}

.modal-container {
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level3-default);
  display: flex;
  flex-direction: column;
  animation: modal-appear 0.3s ease-out;
}

@keyframes modal-appear {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--border-light);
  background-color: var(--background-subtle);
  border-top-left-radius: var(--border-radius-lg);
  border-top-right-radius: var(--border-radius-lg);
}

.modal-title {
  margin: 0;
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-semibold);
  font-size: var(--font-fontSize-xl);
  color: var(--primary);
}

.close-button {
  background: transparent;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: var(--border-radius-full);
  color: var(--text-secondary);
  font-size: 1.5rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-button:hover {
  background-color: var(--interactive-hover);
  color: var(--primary-dark);
}

.modal-body {
  padding: var(--spacing-xl);
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: var(--tertiary) var(--background-subtle);
  flex: 1;
}

.modal-body::-webkit-scrollbar {
  width: 5px;
}

.modal-body::-webkit-scrollbar-track {
  background: var(--background-subtle);
  border-radius: var(--border-radius-full);
}

.modal-body::-webkit-scrollbar-thumb {
  background-color: var(--tertiary);
  border-radius: var(--border-radius-full);
}

.avatars-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: var(--spacing-lg);
  padding: var(--spacing-md);
}

.avatar-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  transition: all 0.2s ease;
  border: 2px solid transparent;
}

.avatar-option:hover {
  background-color: var(--background-subtle);
  transform: translateY(-2px);
  box-shadow: var(--shadow-level2-hover);
}

.avatar-option.selected {
  border-color: var(--primary);
  background-color: var(--interactive-active);
}

:root .theme-cyberpunk .avatar-option.selected {
  border-color: var(--secondary);
  box-shadow: 0 0 15px rgba(5, 217, 232, 0.5);
}

.avatar-image {
  width: 80px;
  height: 80px;
  border-radius: var(--border-radius-md);
  background-color: var(--background-subtle);
  margin-bottom: var(--spacing-sm);
  transition: transform 0.2s ease;
}

.avatar-option:hover .avatar-image {
  transform: scale(1.05);
}

.avatar-option.selected .avatar-image {
  transform: scale(1.1);
}

.avatar-name {
  font-size: var(--font-fontSize-sm);
  color: var(--text-secondary);
  transition: color 0.2s ease;
}

.avatar-option.selected .avatar-name {
  color: var(--primary);
  font-weight: var(--font-fontWeight-medium);
}

.modal-footer {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-top: 1px solid var(--border-light);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  background-color: var(--background-muted);
  border-bottom-left-radius: var(--border-radius-lg);
  border-bottom-right-radius: var(--border-radius-lg);
}

/* Cyberpunk theme specific */
:root .theme-cyberpunk .modal-container {
  background-color: rgba(12, 16, 22, 0.95);
  border: 1px solid var(--secondary);
  box-shadow: 0 0 20px rgba(5, 217, 232, 0.5);
}

:root .theme-cyberpunk .modal-header {
  background-color: rgba(5, 217, 232, 0.1);
  border-bottom: 1px solid var(--secondary);
}

:root .theme-cyberpunk .modal-title {
  color: var(--secondary);
  text-shadow: 0 0 5px var(--secondary);
  font-family: var(--title-font);
  letter-spacing: 1px;
}

:root .theme-cyberpunk .avatar-option:hover {
  background-color: rgba(5, 217, 232, 0.1);
}

:root .theme-cyberpunk .modal-footer {
  background-color: rgba(5, 217, 232, 0.1);
  border-top: 1px solid var(--secondary);
}

/* Media queries for responsive design */
@media (max-width: 768px) {
  .avatars-container {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  }
  
  .avatar-image {
    width: 60px;
    height: 60px;
  }
}

@media (max-width: 480px) {
  .avatars-container {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
    gap: var(--spacing-md);
  }
  
  .avatar-image {
    width: 50px;
    height: 50px;
  }
  
  .modal-footer {
    flex-direction: column;
  }
}
</style> 