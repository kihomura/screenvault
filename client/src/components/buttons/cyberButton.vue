<template>
  <button :class="['cyber-button', customClass]" @click="$emit('click')">
    <span class="cyber-button-text"><slot /></span>
    <span class="cyber-button-glitch"></span>
  </button>
</template>

<script setup>
const props = defineProps({
  customClass: {
    type: String,
    default: ''
  }
});
</script>

<style scoped>
.cyber-button {
  position: relative;
  padding: 0.6rem 1.8rem;
  background-color: rgba(0, 0, 0, 0.7);
  color: #05D9E8FF;
  border: 1px solid #05D9E8FF;
  font-size: 1rem;
  letter-spacing: 2px;
  text-transform: uppercase;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  clip-path: polygon(10% 0, 100% 0, 90% 100%, 0% 100%);
  font-family: 'Orbitron', sans-serif;
  text-shadow: 0 0 5px #05D9E8FF;
}

.cyber-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(5, 217, 232, 0.4), transparent);
  transition: all 0.5s ease;
}

.cyber-button:hover {
  background-color: rgba(5, 217, 232, 0.2);
  box-shadow:
      0 0 10px #05D9E8FF,
      0 0 20px #05D9E8FF;
  transform: translateY(-2px);
}

.cyber-button:hover::before {
  left: 100%;
}

/* primary button */
.cyber-button.primary {
  clip-path: polygon(0 0, 100% 0, 100% 100%, 0% 100%);
  background-color: rgba(255, 255, 255, 0.5);
  color: #e35180;
  border-color: #FF2A6DFF;
  text-shadow: 0 0 5px rgba(255, 242, 242, 0.63);
}

.cyber-button.primary:hover {
  background-color: rgba(255, 42, 109, 0.63);
  box-shadow:
      0 0 10px rgba(255, 42, 109, 0.63),
      0 0 20px rgba(255, 42, 109, 0.63);
}

/* large button */
.cyber-button.large {
  padding: 0.85rem 2.2rem;
  font-size: 1.2rem;
}

.cyber-button-glitch {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: transparent;
  z-index: -1;
}

.cyber-button:hover .cyber-button-glitch {
  animation: buttonGlitch 0.3s infinite;
}

@keyframes buttonGlitch {
  0%, 100% { transform: translate(0, 0); }
  25% { transform: translate(2px, 2px); filter: hue-rotate(90deg); }
  50% { transform: translate(-2px, -2px); filter: hue-rotate(180deg); }
  75% { transform: translate(-2px, 2px); filter: hue-rotate(270deg); }
}
</style>
