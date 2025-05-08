<template>
  <Toast 
    :show="toastStore.visible" 
    :message="toastStore.message" 
    :type="toastStore.type" 
    :duration="toastStore.duration" 
    @close="toastStore.hideToast" 
  />
</template>

<script setup>
import Toast from './Toast.vue';
import { useToastStore } from '../../store/toastStore.js';

const toastStore = useToastStore();
</script> 