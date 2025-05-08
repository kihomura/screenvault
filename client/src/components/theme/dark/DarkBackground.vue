<template>
  <div class="dark-background">
    <div class="deep-gradient"></div>
    <div class="blur-circle circle1"></div>
    <div class="blur-circle circle2"></div>
    <div class="blur-circle circle3"></div>
    <div class="subtle-noise"></div>
  </div>
</template>

<script setup>
// No props or logic needed for this component
</script>

<style scoped>
.dark-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -100;
  overflow: hidden;
  pointer-events: none;
}

.deep-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(125deg, #121212 0%, #1a1a1a 50%, #121212 100%);
  z-index: -2;
}

.blur-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  z-index: -1;
}

.circle1 {
  width: 45vw;
  height: 45vw;
  background: radial-gradient(circle, rgba(90, 90, 90, 0.2) 0%, rgba(50, 50, 50, 0) 70%);
  top: -20vw;
  right: -10vw;
  animation: floatAnimationDark 20s ease-in-out infinite alternate;
}

.circle2 {
  width: 60vw;
  height: 60vw;
  background: radial-gradient(circle, rgba(70, 70, 70, 0.15) 0%, rgba(40, 40, 40, 0) 70%);
  bottom: -30vw;
  left: -15vw;
  animation: floatAnimationDark 25s ease-in-out infinite alternate-reverse;
}

.circle3 {
  width: 35vw;
  height: 35vw;
  background: radial-gradient(circle, rgba(80, 80, 80, 0.1) 0%, rgba(60, 60, 60, 0) 70%);
  top: 35%;
  right: 25%;
  animation: floatAnimationDark 22s ease-in-out infinite alternate;
}

.subtle-noise {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzMDAiIGhlaWdodD0iMzAwIj48ZmlsdGVyIGlkPSJhIiB4PSIwIiB5PSIwIj48ZmVUdXJidWxlbmNlIHR5cGU9ImZyYWN0YWxOb2lzZSIgYmFzZUZyZXF1ZW5jeT0iLjc1IiBzdGl0Y2hUaWxlcz0ic3RpdGNoIi8+PGZlQ29sb3JNYXRyaXggdHlwZT0ic2F0dXJhdGUiIHZhbHVlcz0iMCIvPjwvZmlsdGVyPjxwYXRoIGZpbHRlcj0idXJsKCNhKSIgb3BhY2l0eT0iLjAzIiBkPSJNMCAwaDMwMHYzMDBIMHoiLz48L3N2Zz4=');
  opacity: 0.4;
  z-index: -1;
}

@keyframes floatAnimationDark {
  0% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-15px) scale(1.03);
  }
  100% {
    transform: translateY(0) scale(1);
  }
}
</style> 