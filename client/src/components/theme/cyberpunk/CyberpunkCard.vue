<template>
  <div :class="['cyberpunk-card', { 'holo-effect': holo }]">
    <div class="cyberpunk-card-header" v-if="$slots.header">
      <slot name="header"></slot>
    </div>
    <div class="cyberpunk-card-body">
      <slot></slot>
    </div>
    <div class="cyberpunk-card-footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
    <div class="card-glitch-effect"></div>
    <div class="card-border"></div>
  </div>
</template>

<script setup>
const props = defineProps({
  holo: {
    type: Boolean,
    default: false
  }
});
</script>

<style scoped>
.cyberpunk-card {
  background: rgba(24, 24, 40, 0.85);
  border: 1px solid var(--secondary);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(5px);
  box-shadow: 0 0 20px rgba(var(--secondary-rgb), 0.3);
  padding: 1px;
  margin-bottom: 1.5rem;
  transition: all 0.3s ease;
}

.cyberpunk-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, var(--primary), var(--secondary), var(--tertiary));
  background-size: 200% 100%;
  animation: gradient-shift 3s linear infinite;
  z-index: 1;
}

.cyberpunk-card-header {
  padding: 1rem;
  border-bottom: 1px solid rgba(var(--secondary-rgb), 0.2);
  position: relative;
  background: rgba(var(--secondary-rgb), 0.05);
  font-family: var(--title-font);
}

.cyberpunk-card-body {
  padding: 1.5rem;
  position: relative;
  z-index: 2;
  font-family: var(--body-font);
}

.cyberpunk-card-footer {
  padding: 1rem;
  border-top: 1px solid rgba(var(--secondary-rgb), 0.2);
  position: relative;
  background: rgba(var(--secondary-rgb), 0.05);
  font-family: var(--body-font);
}

.card-glitch-effect {
  position: absolute;
  top: 0;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(var(--secondary-rgb), 0.2),
    transparent
  );
  transition: 0.5s;
  z-index: 1;
}

.cyberpunk-card:hover .card-glitch-effect {
  left: 100%;
}

.card-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 1px solid var(--secondary);
  z-index: 0;
  pointer-events: none;
  clip-path: polygon(
    0% 5px, 
    5px 0%, 
    calc(100% - 5px) 0%, 
    100% 5px, 
    100% calc(100% - 5px), 
    calc(100% - 5px) 100%, 
    5px 100%, 
    0% calc(100% - 5px)
  );
}

.cyberpunk-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 25px rgba(var(--secondary-rgb), 0.5);
}

/* Holographic effect */
.holo-effect {
  background: linear-gradient(
    135deg,
    rgba(var(--primary-rgb), 0.05) 0%,
    rgba(var(--secondary-rgb), 0.1) 25%,
    rgba(var(--tertiary-rgb), 0.1) 50%,
    rgba(var(--secondary-rgb), 0.1) 75%,
    rgba(var(--primary-rgb), 0.05) 100%
  );
  background-size: 400% 400%;
  animation: holoBg 8s ease infinite;
}

.holo-effect .card-border {
  border-color: rgba(var(--secondary-rgb), 0.3);
  box-shadow: 
    0 0 5px rgba(var(--secondary-rgb), 0.3),
    inset 0 0 5px rgba(var(--secondary-rgb), 0.3);
}

@keyframes gradient-shift {
  0% { background-position: 0% 0%; }
  100% { background-position: 200% 0%; }
}

@keyframes holoBg {
  0% { background-position: 0% 0%; }
  50% { background-position: 100% 100%; }
  100% { background-position: 0% 0%; }
}
</style> 