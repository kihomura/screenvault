<template>
  <header class="cyber-header">
    <div class="logo-container">
      <router-link to="/" class="title-link">
        <h1 class="glitch-text" data-text="SCREENVAULT">SCREENVAULT</h1>
      </router-link>
      <div class="tagline">EFFORTLESS TRACKING, SEAMLESS WATCHING, TIMELESS MEMORIES</div>
    </div>
    <div class="auth-buttons">
      <router-link to="/login">
        <CyberButton>LOGIN</CyberButton>
      </router-link>
      <router-link to="/register">
        <CyberButton>REGISTER</CyberButton>
      </router-link>
    </div>
  </header>
</template>

<script setup>
import CyberButton from './buttons/cyberButton.vue';
</script>

<style scoped>
.cyber-header {
  padding: 2rem 4rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 10;
  border-bottom: 1px solid rgba(3, 183, 197, 0.25);
  box-shadow: 0 0 20px rgba(5, 217, 232, 0.25);
  background: linear-gradient(180deg, rgba(6, 7, 27, 0.85) 0%, rgba(52, 56, 57, 0.5) 100%);
  clip-path: polygon(0 0, 100% 0, 100% 85%, 97% 100%, 0 100%);
}

.title-link {
  text-decoration: none;
}

.logo-container {
}

.glitch-text {
  font-size: 3rem;
  font-weight: 700;
  color: white;
  text-shadow:
      0 0 5px rgba(5, 217, 232, 0.62),
      0 0 10px rgba(5, 217, 232, 0.15),
      0 0 20px rgba(5, 217, 232, 0.71);
  position: relative;
  letter-spacing: 5px;
}

.glitch-text::before,
.glitch-text::after {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0.8;
}

.glitch-text::before {
  color: #FF2A6DFF;
  z-index: -1;
  animation: glitchbefore 3s infinite;
  text-shadow: 0 0 5px #FF2A6DFF;
}

.glitch-text::after {
  color: #a914ff;
  z-index: -2;
  animation: glitchafter 2s infinite;
  text-shadow: 0 0 5px #a914ff;
}

.glitch-text.active::before {
  animation: glitchbefore 0.3s infinite;
}

.glitch-text.active::after {
  animation: glitchafter 0.2s infinite;
}

@keyframes glitchbefore {
  0%, 90%, 100% { transform: translate(0, 0); }
  20% { transform: translate(-3px, 3px); }
  40% { transform: translate(3px, -3px); }
  60% { transform: translate(-3px, -3px); }
}

@keyframes glitchafter {
  0%, 90%, 100% { transform: translate(0, 0); }
  10% { transform: translate(3px, 3px); }
  30% { transform: translate(-3px, -3px); }
  50% { transform: translate(3px, -3px); }
  70% { transform: translate(-3px, 3px); }
}

.tagline {
  font-size: 1.1rem;
  font-weight: 400;
  color: #ffffff;
  letter-spacing: 3px;
  margin-top: 0.25rem;
  text-shadow: 0 0 10px rgba(5, 217, 232, 0.62);
  animation: taglinePulse 4s infinite alternate;
}

@keyframes taglinePulse {
  0% { opacity: 0.7; }
  100% { opacity: 1; }
}

.auth-buttons {
  display: flex;
  gap: 1.5rem;
}
</style>
