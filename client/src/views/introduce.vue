<template>
  <div class="cyberpunk-container">
    <AnimatedGrid />
    <AppHeader />

    <main class="cyber-main">
      <section class="hero-section">
        <div class="hero-content">
          <h2 class="section-title">YOUR DIGITAL VIEWING ARCHIVE</h2>
          <p class="hero-text">
            ScreenVault offers a highly flexible way to manage your viewing experience, helping you effortlessly track, organize, and revisit the films and shows you've watched. Your favorites are always within reach.
          </p>
          <router-link to="/register">
            <CyberButton customClass="large primary">
              START YOUR COLLECTION
            </CyberButton>
          </router-link>
        </div>
        <div class="hero-image">
          <div class="cyber-frame">
            <div class="cyber-image"></div>
            <div class="scan-line"></div>
          </div>
        </div>
      </section>
    </main>

    <footer class="cyber-footer">
      <div class="footer-content">
        <div class="footer-links">
          <a href="#" class="footer-link">XXX</a>
          <a href="#" class="footer-link">XXXX</a>
          <a href="#" class="footer-link">XXXX</a>
          <a href="#" class="footer-link">XXXX</a>
        </div>
        <div class="footer-copyright">
          © 2025 ScreenVault. All Rights Reserved.
        </div>
      </div>
    </footer>

  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import AnimatedGrid from '../components/animatedGrid.vue';
import AppHeader from '../components/appHeader.vue';
import CyberButton from '../components/buttons/cyberButton.vue';

onMounted(() => {
  document.querySelectorAll('.glitch-text').forEach(element => {
    element.addEventListener('mouseover', () => {
      element.classList.add('active');
      setTimeout(() => element.classList.remove('active'), 1000);
    });
  });
});
</script>

<style scoped>
.hero-content,
.footer-content {
  padding-left: 30px;
  padding-right: 30px;
  box-sizing: border-box;
}

:global(html), :global(body) {
  background-color: #393939;
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Blender Pro', 'Orbitron', 'Rajdhani', sans-serif;
}

@font-face {
  font-family: 'CyberFont';
  src: url('https://fonts.googleapis.com/css2?family=Orbitron:wght@400;500;700&display=swap');
}

.cyberpunk-container {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background-image:
      radial-gradient(circle at 15% 15%, rgba(178, 134, 207, 0.3), transparent 25%),
      radial-gradient(circle at 85% 85%, rgba(27, 189, 189, 0.3), transparent 25%);
}

@keyframes scanEffect {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(100%); }
}

@keyframes gridPulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}

@keyframes logoPulse {
  0% { opacity: 0.1; filter: blur(10px); }
  100% { opacity: 0.3; filter: blur(15px); }
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


@keyframes taglinePulse {
  0% { opacity: 0.7; }
  100% { opacity: 1; }
}

@keyframes buttonGlitch {
  0%, 100% { transform: translate(0, 0); }
  25% { transform: translate(2px, 2px); filter: hue-rotate(90deg); }
  50% { transform: translate(-2px, -2px); filter: hue-rotate(180deg); }
  75% { transform: translate(-2px, 2px); filter: hue-rotate(270deg); }
}

.cyber-main {
  padding: 2rem 4rem;
  position: relative;
  z-index: 5;
}

.hero-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 3rem 0;
}

.hero-content {
  flex: 1;
  position: relative;
}

.section-title {
  font-size: 2.2rem;
  margin-bottom: 1.5rem;
  color: #ffffff;
  text-shadow:
      0 0 7px #05d9e8,
      0 0 10px rgb(255, 42, 109);
  position: relative;
  display: inline-block;
  letter-spacing: 2px;
  font-family: 'Orbitron', sans-serif;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 60%;
  height: 2px;
  background: linear-gradient(90deg, #ffffff, transparent);
}

.section-title::before {
  content: '>';
  position: absolute;
  left: -40px;
  color: #ffffff;
  animation: cursorBlink 1s infinite;
}

@keyframes cursorBlink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.hero-text {
  font-size: 1.15rem;
  line-height: 1.7;
  margin-bottom: 2.5rem;
  color: #ffffff;
  text-shadow: 0 0 2px rgba(5, 217, 232, 0.31);
  position: relative;
}

.hero-text::first-letter {
  font-size: 1.8rem;
}

.hero-image {
  flex: 1;
  max-width: 700px;
}

.cyber-frame {
  border: 2px solid #05D9E8FF;
  position: relative;
  padding-top: 56.25%;
  overflow: hidden;
  box-shadow:
      0 0 15px rgba(5, 217, 232, 0.5),
      0 0 30px rgba(5, 217, 232, 0.3);
}

.cyber-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /*background-image: url("https://picsum.photos/300/200");*/
  background-size: cover;
  background-position: center;
  filter: brightness(0.8) contrast(1.2) saturate(1.8) hue-rotate(10deg);
}

.cyber-image::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, rgba(255, 42, 109, 0.2), transparent);
}

.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 10px;
  background: linear-gradient(transparent, rgba(5, 217, 232, 0.5), transparent);
  box-shadow: 0 0 10px #05D9E8FF;
  animation: scanLine 3s linear infinite;
}

@keyframes scanLine {
  0% { top: 0; opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { top: 100%; opacity: 0; }
}

.cyber-footer {
  background: linear-gradient(180deg, #3438397F 0%, #06071BD8 100%);
  padding: 2rem 4rem;
  position: absolute;
  bottom: 0;
  width: 100%;
  z-index: 10;
  clip-path: polygon(0 0, 100% 0, 100% 100%, 0 100%);
  box-shadow: 0 -5px 15px rgba(255, 42, 109, 0.3);
}

.footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.footer-links {
  display: flex;
  gap: 2.5rem;
}

.footer-link {
  color: #aaaaaa;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
  letter-spacing: 1px;
}

.footer-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 1px;
  background-color: #FF2A6DFF;
  transition: all 0.3s ease;
}

.footer-link:hover {
  color: #FF2A6DFF;
  text-shadow: 0 0 5px rgba(255, 42, 109, 0.7);
}

.footer-link:hover::after {
  width: 100%;
}

.footer-copyright {
  color: #777777;
  font-size: 0.5rem;
  letter-spacing: 1px;
}

@media (max-width: 1024px) {
  .cyber-header, .cyber-main {
    padding: 2rem;
  }

  .hero-section {
    flex-direction: column;
    gap: 2rem;
  }

  .hero-image {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .cyber-header {
    flex-direction: column;
    gap: 1.5rem;
    text-align: center;
    clip-path: none;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .footer-links {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .auth-buttons {
    flex-direction: column;
    width: 100%;
  }

  .cyber-button {
    width: 100%;
  }
}

@keyframes neonFlicker {
  0%, 19%, 21%, 23%, 25%, 54%, 56%, 100% { opacity: 1; }
  20%, 24%, 55% { opacity: 0.8; }
}

@keyframes typing {
  from { width: 0; }
  to { width: 100%; }
}

@keyframes blink {
  50% { border-color: transparent; }
}
</style>