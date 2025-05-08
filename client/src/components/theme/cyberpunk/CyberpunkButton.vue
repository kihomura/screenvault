<template>
  <button 
    :class="['cyberpunk-button', variant, size, { 'glitch-effect': glitch }]" 
    @click="$emit('click')"
    :disabled="disabled"
  >
    <span class="button-text"><slot></slot></span>
    <span v-if="glitch" class="button-glitch"></span>
    <span class="button-border-effect"></span>
    <div class="button-noise"></div>
  </button>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'tertiary', 'outline', 'ghost'].includes(value)
  },
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['sm', 'md', 'lg'].includes(value)
  },
  glitch: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  }
});

defineEmits(['click']);
</script>

<style scoped>
.cyberpunk-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.75rem 1.5rem;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  border: 1px solid var(--secondary);
  font-family: var(--button-font);
  font-size: var(--font-fontSize-base);
  font-weight: var(--font-fontWeight-medium);
  letter-spacing: 2px;
  text-transform: uppercase;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  clip-path: polygon(6px 0, 100% 0, calc(100% - 6px) 100%, 0 100%);
  box-shadow: 0 0 10px rgba(var(--secondary-rgb), 0.3);
  text-shadow: 0 0 5px var(--secondary);
  z-index: 1;
}

.cyberpunk-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(120deg, transparent, rgba(var(--secondary-rgb), 0.3), transparent);
  transform: translateX(-100%);
  transition: 0.5s;
  z-index: -1;
}

.cyberpunk-button:hover::before {
  transform: translateX(100%);
}

.button-border-effect {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -2;
}

.button-border-effect::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 2px;
  height: 100%;
  background: var(--secondary);
  transition: all 0.3s ease;
  opacity: 0.5;
}

.button-border-effect::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: var(--secondary);
  transition: all 0.3s ease;
  opacity: 0.5;
}

.cyberpunk-button:hover .button-border-effect::before,
.cyberpunk-button:hover .button-border-effect::after {
  opacity: 1;
  box-shadow: 0 0 8px var(--secondary);
}

.button-noise {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzMDAiIGhlaWdodD0iMzAwIj48ZmlsdGVyIGlkPSJhIiB4PSIwIiB5PSIwIj48ZmVUdXJidWxlbmNlIHR5cGU9ImZyYWN0YWxOb2lzZSIgYmFzZUZyZXF1ZW5jeT0iLjc1IiBzdGl0Y2hUaWxlcz0ic3RpdGNoIi8+PGZlQ29sb3JNYXRyaXggdHlwZT0ic2F0dXJhdGUiIHZhbHVlcz0iMCIvPjwvZmlsdGVyPjxwYXRoIGZpbHRlcj0idXJsKCNhKSIgb3BhY2l0eT0iLjA1IiBkPSJNMCAwaDMwMHYzMDBIMHoiLz48L3N2Zz4=');
  opacity: 0.05;
  z-index: -3;
  pointer-events: none;
}

/* Variants */
.primary {
  background: linear-gradient(90deg, rgba(var(--primary-rgb), 0.3), rgba(var(--primary-rgb), 0.5));
  border-color: var(--primary);
  color: white;
  text-shadow: 0 0 5px var(--primary);
  box-shadow: 0 0 10px rgba(var(--primary-rgb), 0.3);
}

.primary:hover {
  background: linear-gradient(90deg, rgba(var(--primary-rgb), 0.5), rgba(var(--primary-rgb), 0.7));
  box-shadow: 0 0 15px rgba(var(--primary-rgb), 0.5);
}

.primary .button-border-effect::before,
.primary .button-border-effect::after {
  background: var(--primary);
}

.secondary {
  background: linear-gradient(90deg, rgba(var(--secondary-rgb), 0.3), rgba(var(--secondary-rgb), 0.5));
  border-color: var(--secondary);
  color: white;
  text-shadow: 0 0 5px var(--secondary);
  box-shadow: 0 0 10px rgba(var(--secondary-rgb), 0.3);
}

.secondary:hover {
  background: linear-gradient(90deg, rgba(var(--secondary-rgb), 0.5), rgba(var(--secondary-rgb), 0.7));
  box-shadow: 0 0 15px rgba(var(--secondary-rgb), 0.5);
}

.tertiary {
  background: linear-gradient(90deg, rgba(var(--tertiary-rgb), 0.3), rgba(var(--tertiary-rgb), 0.5));
  border-color: var(--tertiary);
  color: black;
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.5);
  box-shadow: 0 0 10px rgba(var(--tertiary-rgb), 0.3);
}

.tertiary:hover {
  background: linear-gradient(90deg, rgba(var(--tertiary-rgb), 0.5), rgba(var(--tertiary-rgb), 0.7));
  box-shadow: 0 0 15px rgba(var(--tertiary-rgb), 0.5);
}

.tertiary .button-border-effect::before,
.tertiary .button-border-effect::after {
  background: var(--tertiary);
}

.outline {
  background: transparent;
  border-color: var(--secondary);
  color: var(--secondary);
}

.outline:hover {
  background: rgba(var(--secondary-rgb), 0.1);
}

.ghost {
  background: transparent;
  border-color: transparent;
  box-shadow: none;
  color: var(--secondary);
}

.ghost:hover {
  background: rgba(var(--secondary-rgb), 0.05);
  border-color: rgba(var(--secondary-rgb), 0.2);
}

/* Sizes */
.sm {
  padding: 0.5rem 1rem;
  font-size: var(--font-fontSize-sm);
}

.md {
  padding: 0.75rem 1.5rem;
  font-size: var(--font-fontSize-base);
}

.lg {
  padding: 1rem 2rem;
  font-size: var(--font-fontSize-lg);
}

/* Disabled state */
.cyberpunk-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  filter: grayscale(0.8);
}

.cyberpunk-button:disabled::before {
  display: none;
}

/* Glitch effect */
.glitch-effect:hover .button-glitch {
  display: block;
  animation: glitch 0.3s cubic-bezier(.25, .46, .45, .94) both infinite;
}

.button-glitch {
  display: none;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(var(--secondary-rgb), 0.8);
  z-index: -1;
}

@keyframes glitch {
  0% {
    transform: translate(0);
    opacity: 0.8;
  }
  20% {
    transform: translate(-5px, 5px);
    opacity: 0.2;
  }
  40% {
    transform: translate(-5px, -5px);
    opacity: 0.8;
  }
  60% {
    transform: translate(5px, 5px);
    opacity: 0.2;
  }
  80% {
    transform: translate(5px, -5px);
    opacity: 0.8;
  }
  100% {
    transform: translate(0);
    opacity: 0.8;
  }
}
</style> 