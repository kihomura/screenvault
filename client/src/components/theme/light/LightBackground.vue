<template>
  <div class="light-background">
    <div class="subtle-gradient"></div>
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
.light-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -100;
  overflow: hidden;
  pointer-events: none;
}

.subtle-gradient {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(120deg, #f8f9fa 0%, #ffffff 100%);
  z-index: -2;
}

.blur-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.5;
  z-index: -1;
}

.circle1 {
  width: 40vw;
  height: 40vw;
  background: radial-gradient(circle, rgba(232, 244, 252, 0.8) 0%, rgba(232, 244, 252, 0) 70%);
  top: -15vw;
  right: -5vw;
  animation: floatAnimation 15s ease-in-out infinite alternate;
}

.circle2 {
  width: 50vw;
  height: 50vw;
  background: radial-gradient(circle, rgba(245, 245, 245, 0.7) 0%, rgba(245, 245, 245, 0) 70%);
  bottom: -20vw;
  left: -10vw;
  animation: floatAnimation 20s ease-in-out infinite alternate-reverse;
}

.circle3 {
  width: 30vw;
  height: 30vw;
  background: radial-gradient(circle, rgba(248, 249, 250, 0.6) 0%, rgba(248, 249, 250, 0) 70%);
  top: 40%;
  right: 30%;
  animation: floatAnimation 18s ease-in-out infinite alternate;
}

.subtle-noise {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIzMDAiIGhlaWdodD0iMzAwIj48ZmlsdGVyIGlkPSJhIiB4PSIwIiB5PSIwIj48ZmVUdXJidWxlbmNlIHR5cGU9ImZyYWN0YWxOb2lzZSIgYmFzZUZyZXF1ZW5jeT0iLjc1IiBzdGl0Y2hUaWxlcz0ic3RpdGNoIi8+PGZlQ29sb3JNYXRyaXggdHlwZT0ic2F0dXJhdGUiIHZhbHVlcz0iMCIvPjwvZmlsdGVyPjxwYXRoIGZpbHRlcj0idXJsKCNhKSIgb3BhY2l0eT0iLjAyIiBkPSJNMCAwaDMwMHYzMDBIMHoiLz48L3N2Zz4=');
  opacity: 0.3;
  z-index: -1;
}

@keyframes floatAnimation {
  0% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.05);
  }
  100% {
    transform: translateY(0) scale(1);
  }
}
</style> 