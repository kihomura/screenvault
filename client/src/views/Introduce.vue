<template>
  <div class="intro-container">
    <AnimatedGrid />
    <AuthHeader />

    <main class="intro-main">
      <div class="cyber-grid-container">
        <div class="cyber-grid">
          <div class="grid-item showcase">
            <div class="image-container">
              <img src="/assets/demonstration-img.png" alt="ScreenVault dashboard demonstration" class="showcase-image">
              <div class="hologram-effect"></div>
              <div class="scan-line"></div>
            </div>
          </div>
          
          <div class="grid-item content">
            <h1 class="glitch-title" data-text="DIGITAL VIEWING ARCHIVE">DIGITAL VIEWING ARCHIVE</h1>
            <div class="terminal-container">
              <div class="terminal-header">
                <span class="terminal-dot"></span>
                <span class="terminal-dot"></span>
                <span class="terminal-dot"></span>
                <span class="terminal-title">screenvault_sys > experience.exe</span>
              </div>
              <div class="terminal-body">
                <div class="type-effect">
                  <p class="command"><span class="prompt">$</span> <span class="cmd-text">initialize ScreenVault</span></p>
                  <p class="response">Welcome to your personal cinematic universe...</p>
                  <p class="command"><span class="prompt">$</span> <span class="cmd-text">display core_features</span></p>
                  <p class="feature-item"><span class="feature-highlight">></span> Curate your viewing journey in one place</p>
                  <p class="feature-item"><span class="feature-highlight">></span> Craft personalized watchlists</p>
                  <p class="feature-item"><span class="feature-highlight">></span> Custom tags for perfect organization</p>
                  <p class="feature-item"><span class="feature-highlight">></span> Instant recall of past favorites</p>
                  <p class="command"><span class="prompt">$</span> <span class="cmd-text">echo motto</span></p>
                  <p class="response">"Every story you've lived. All securely vaulted."</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <footer class="intro-footer">
      <div class="footer-content">
        <div class="footer-links">
          <a href="#" class="footer-link">About</a>
          <a href="#" class="footer-link">Features</a>
          <a href="#" class="footer-link">Support</a>
          <a href="#" class="footer-link">Contact</a>
        </div>
        <div class="footer-copyright">
          © 2025 ScreenVault. All Rights Reserved.
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue';
import AnimatedGrid from '../components/animatedGrid.vue';
import AuthHeader from '../components/ui/AuthHeader.vue';

onMounted(() => {
  document.querySelectorAll('.glitch-text').forEach(element => {
    element.addEventListener('mouseover', () => {
      element.classList.add('active');
      setTimeout(() => element.classList.remove('active'), 1000);
    });
  });

  const typeItems = document.querySelectorAll('.type-effect p');
  typeItems.forEach((item, index) => {
    setTimeout(() => {
      item.classList.add('visible');
    }, index * 400 + 500);
  });
});

onUnmounted(() => {
  // App.vue will handle body style when navigating away from a noTheme page
});
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.intro-container {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background-color: #080b15;
  background-image:
      radial-gradient(circle at 10% 10%, rgba(178, 134, 207, 0.1), transparent 30%),
      radial-gradient(circle at 90% 90%, rgba(27, 189, 189, 0.1), transparent 30%);
}

.intro-main {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 250px);
  position: relative;
  z-index: 5;
}

.cyber-grid-container {
  width: 100%;
  max-width: 1600px;
  padding: 2rem;
}

.cyber-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  width: 100%;
}

.grid-item {
  position: relative;
  width: 100%;
}

.content {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.glitch-title {
  font-size: 1.8rem;
  font-family: 'Orbitron', sans-serif;
  color: #ffffff;
  margin-bottom: 2rem;
  text-shadow: 
    0 0 5px #05d9e8,
    0 0 10px rgba(5, 217, 232, 0.5);
  position: relative;
  letter-spacing: 2px;
  animation: titlePulse 4s infinite alternate;
}

.glitch-title::before,
.glitch-title::after {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0.8;
}

.glitch-title::before {
  color: #FF2A6D;
  z-index: -1;
  animation: glitchEffect1 3s infinite;
  text-shadow: 0 0 5px #FF2A6D;
}

.glitch-title::after {
  color: #05d9e8;
  z-index: -2;
  animation: glitchEffect2 2s infinite;
  text-shadow: 0 0 5px #05d9e8;
}

@keyframes glitchEffect1 {
  0%, 90%, 100% { transform: translate(0, 0); }
  20% { transform: translate(-3px, 3px); }
  40% { transform: translate(3px, -3px); }
  60% { transform: translate(-3px, -3px); }
}

@keyframes glitchEffect2 {
  0%, 90%, 100% { transform: translate(0, 0); }
  10% { transform: translate(3px, 3px); }
  30% { transform: translate(-3px, -3px); }
  50% { transform: translate(3px, -3px); }
  70% { transform: translate(-3px, 3px); }
}

@keyframes titlePulse {
  0% { text-shadow: 0 0 5px #05d9e8, 0 0 10px rgba(5, 217, 232, 0.5); }
  100% { text-shadow: 0 0 15px #05d9e8, 0 0 30px rgba(5, 217, 232, 0.8); }
}

.terminal-container {
  background: rgba(16, 18, 27, 0.8);
  border: 1px solid #05d9e8;
  border-radius: 8px;
  box-shadow: 
    0 0 20px rgba(5, 217, 232, 0.4),
    0 0 30px rgba(5, 217, 232, 0.2);
  overflow: hidden;
  width: 100%;
  position: relative;
}

.terminal-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    linear-gradient(90deg, rgba(255, 42, 109, 0.05) 1px, transparent 1px) 0 0 / 20px 20px,
    linear-gradient(0deg, rgba(255, 42, 109, 0.05) 1px, transparent 1px) 0 0 / 20px 20px;
  pointer-events: none;
}

.terminal-header {
  background: rgba(5, 217, 232, 0.2);
  padding: 0.75rem 1rem;
  display: flex;
  align-items: center;
  border-bottom: 1px solid rgba(5, 217, 232, 0.5);
}

.terminal-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 6px;
  background: rgba(255, 255, 255, 0.5);
}

.terminal-dot:nth-child(1) {
  background: #FF2A6D;
}

.terminal-dot:nth-child(2) {
  background: #F7971E;
}

.terminal-dot:nth-child(3) {
  background: #05d9e8;
}

.terminal-title {
  margin-left: 10px;
  font-family: 'Courier New', monospace;
  color: #ffffff;
  letter-spacing: 1px;
  font-size: 0.9rem;
}

.terminal-body {
  padding: 1rem;
  height: 380px;
  overflow-y: auto;
  font-family: 'Courier New', monospace;
}

.type-effect p {
  margin-bottom: 0.6rem;
  color: #ffffff;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.3s ease;
}

.type-effect p.visible {
  opacity: 1;
  transform: translateY(0);
}

.command {
  color: #05d9e8;
}

.prompt {
  color: #FF2A6D;
  margin-right: 8px;
}

.cmd-text {
  position: relative;
}

.cmd-text::after {
  content: '';
  display: inline-block;
  width: 8px;
  height: 15px;
  background: #05d9e8;
  margin-left: 5px;
  animation: cursorBlink 0.8s infinite;
}

.response {
  color: #aaaaaa;
  padding-left: 20px;
}

.feature-item {
  padding-left: 25px;
  position: relative;
  color: #ffffff;
}

.feature-highlight {
  color: #05d9e8;
  margin-right: 10px;
  position: absolute;
  left: 0;
}

.showcase {
  perspective: 1000px;
}

.image-container {
  position: relative;
  border: 2px solid #05d9e8;
  border-radius: 8px;
  height: 0;
  padding-bottom: 75%;
  overflow: hidden;
  transform-style: preserve-3d;
  transform: rotateY(-5deg) rotateX(3deg);
  box-shadow: 
    0 0 20px rgba(5, 217, 232, 0.4),
    0 0 40px rgba(5, 217, 232, 0.2);
  transition: transform 0.6s ease;
}

.image-container:hover {
  transform: rotateY(-2deg) rotateX(1deg) scale(1.02);
}

.showcase-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.8) contrast(1.2) saturate(1.3);
}

.hologram-effect {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    linear-gradient(90deg, rgba(5, 217, 232, 0) 0%, 
                          rgba(5, 217, 232, 0.15) 50%, 
                          rgba(5, 217, 232, 0) 100%);
  animation: hologramSweep 3s ease-in-out infinite;
  pointer-events: none;
}

@keyframes hologramSweep {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, transparent, #05d9e8, transparent);
  box-shadow: 0 0 10px #05d9e8, 0 0 20px #05d9e8;
  animation: scanLine 2s linear infinite;
  z-index: 2;
}

@keyframes scanLine {
  0% { top: 0; opacity: 0; }
  5% { opacity: 1; }
  95% { opacity: 1; }
  100% { top: 100%; opacity: 0; }
}

@keyframes cursorBlink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.intro-footer {
  background: linear-gradient(180deg, rgba(16, 18, 27, 0.5) 0%, rgba(6, 7, 27, 0.85) 100%);
  padding: 2rem 4rem;
  position: absolute;
  bottom: 0;
  width: 100%;
  z-index: 10;
  box-shadow: 0 -5px 15px rgba(255, 42, 109, 0.2);
}

.footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding-left: 30px;
  padding-right: 30px;
  box-sizing: border-box;
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
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
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
  font-size: 0.8rem;
  letter-spacing: 1px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

@media (max-width: 1024px) {
  .cyber-grid {
    grid-template-columns: 1fr;
    gap: 3rem;
  }
}

@media (max-width: 768px) {
  .cyber-grid-container {
    padding: 1.5rem;
  }
  
  .glitch-title {
    font-size: 2.2rem;
    margin-bottom: 1.5rem;
  }
  
  .terminal-body {
    height: 340px;
  }
  
  .footer-links {
    flex-wrap: wrap;
    justify-content: center;
    gap: 1.5rem;
  }
  
  .intro-footer {
    padding: 1.5rem;
  }
}

@media (max-width: 480px) {
  .cyber-grid-container {
    padding: 1rem;
  }
  
  .glitch-title {
    font-size: 1.8rem;
    margin-bottom: 1rem;
  }
  
  .terminal-body {
    height: 300px;
  }
  
  .footer-links {
    gap: 1rem;
  }
  
  .intro-footer {
    padding: 1rem;
  }
}
</style>