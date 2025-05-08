<template>
  <div class="cyberpunk-background">
    <div class="grid-layer"></div>
    <div class="scan-line-layer"></div>
    <div class="glow-layer top"></div>
    <div class="glow-layer bottom"></div>
    <div class="glow-layer left"></div>
    <div class="glow-layer right"></div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.cyberpunk-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -100;
  overflow: hidden;
  pointer-events: none;
}

.grid-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(33, 248, 246, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(33, 248, 246, 0.05) 1px, transparent 1px);
  background-size: 30px 30px;
  z-index: -2;
  animation: gridPulse 8s infinite linear;
}

.scan-line-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, transparent 50%, rgba(5, 217, 232, 0.05) 50%);
  background-size: 100% 4px;
  z-index: -1;
  animation: scanLine 10s linear infinite;
}

.glow-layer {
  position: absolute;
  z-index: -3;
  opacity: 0.15;
  filter: blur(50px);
  background: radial-gradient(circle, var(--primary) 0%, transparent 70%);
  animation: glowPulse 10s infinite alternate;
}

.glow-layer.top {
  top: -50%;
  left: 30%;
  width: 40%;
  height: 60%;
}

.glow-layer.bottom {
  bottom: -30%;
  right: 20%;
  width: 50%;
  height: 60%;
  background: radial-gradient(circle, var(--secondary) 0%, transparent 70%);
  animation-delay: 2s;
}

.glow-layer.left {
  top: 30%;
  left: -30%;
  width: 50%;
  height: 40%;
  background: radial-gradient(circle, var(--tertiary) 0%, transparent 70%);
  animation-delay: 4s;
}

.glow-layer.right {
  top: 40%;
  right: -20%;
  width: 40%;
  height: 40%;
  animation-delay: 6s;
}

@keyframes scanLine {
  0% { transform: translateY(0); }
  100% { transform: translateY(100vh); }
}

@keyframes gridPulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}

@keyframes glowPulse {
  0% { opacity: 0.05; transform: scale(1); }
  100% { opacity: 0.2; transform: scale(1.1); }
}
</style> 