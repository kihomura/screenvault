<template>
  <div class="cyber-grid"></div>
</template>
<script setup>
</script>

<style scoped>
.cyber-grid {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
      linear-gradient(#05D9E826 1px, transparent 1px),
      linear-gradient(#05D9E826 1px, transparent 1px);
  background-size: 20px 20px;
  z-index: 0;
  animation: gridPulse 8s infinite linear;
  opacity: 0.7;
}

.cyber-grid::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
      repeating-linear-gradient(90deg, transparent, transparent 2px, rgba(5, 217, 232, 0.04) 2px, rgba(5, 217, 232, 0.04) 4px);
  animation: scanEffect 15s linear infinite;
}

@keyframes scanEffect {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(100%); }
}

@keyframes gridPulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}
</style>
