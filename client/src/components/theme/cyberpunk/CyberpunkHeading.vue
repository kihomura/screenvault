<template>
  <component :is="tag" :class="['cyberpunk-heading', level, { 'glitch-effect': glitch }]" :data-text="text">
    {{ text }}
    <div v-if="glitch" class="glitch-line"></div>
  </component>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  level: {
    type: String,
    default: 'h1',
    validator: (value) => ['h1', 'h2', 'h3', 'h4', 'h5', 'h6'].includes(value)
  },
  text: {
    type: String,
    required: true
  },
  glitch: {
    type: Boolean,
    default: true
  }
});

const tag = computed(() => props.level);
</script>

<style scoped>
.cyberpunk-heading {
  font-family: var(--title-font);
  text-transform: uppercase;
  letter-spacing: 2px;
  position: relative;
  display: inline-block;
  margin-bottom: 1.5rem;
  padding-left: 0.5rem;
}

.cyberpunk-heading::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 3px;
  background: var(--secondary);
  transform: scaleY(0.8);
}

.h1, .h2 {
  color: var(--primary);
  text-shadow: 0 0 5px var(--primary), 0 0 10px var(--primary);
  font-size: 2.5rem;
  letter-spacing: 3px;
}

.h2 {
  font-size: 2rem;
}

.h3, .h4 {
  color: var(--secondary);
  text-shadow: 0 0 5px var(--secondary), 0 0 10px var(--secondary);
  font-size: 1.75rem;
}

.h4 {
  font-size: 1.5rem;
}

.h5, .h6 {
  color: var(--tertiary);
  text-shadow: 0 0 4px var(--tertiary);
  font-size: 1.25rem;
}

.h6 {
  font-size: 1rem;
}

.glitch-effect {
  position: relative;
  overflow: hidden;
}

.glitch-effect::before,
.glitch-effect::after {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0.8;
  animation: glitch-anim 5s infinite linear alternate-reverse;
}

.glitch-effect::before {
  left: 2px;
  color: #FF2A6DFF;
  z-index: -1;
  clip-path: polygon(0 0, 100% 0, 100% 45%, 0 45%);
  text-shadow: 0 0 5px #FF2A6DFF;
}

.glitch-effect::after {
  left: -2px;
  color: #05D9E8FF;
  z-index: -2;
  clip-path: polygon(0 55%, 100% 55%, 100% 100%, 0 100%);
  text-shadow: 0 0 5px #05D9E8FF;
}

.glitch-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 1px;
  background: rgba(255, 255, 255, 0.2);
  animation: glitch-line 5s infinite linear;
  pointer-events: none;
  z-index: 3;
}

@keyframes glitch-anim {
  0%, 70%, 90%, 100% { transform: translate(0, 0) skew(0deg); }
  20% { transform: translate(-3px, 2px) skew(1deg); }
  40% { transform: translate(3px, -2px) skew(-0.5deg); }
  60% { transform: translate(-2px, -1px) skew(0.5deg); }
  80% { transform: translate(1px, 1px) skew(-1deg); }
}

@keyframes glitch-line {
  0%, 100% { top: -100%; }
  50% { top: 200%; }
}
</style> 