<template>
  <div class="content-card" @click="openContentDetails">
    <div class="poster-container">
      <!-- Poster -->
      <img :src="getContentImagePath(content)"
           :alt="(content.title || 'Content') + ' poster'"
           class="content-poster">

      <!-- Category -->
      <div class="content-category">
        <span>{{content.category}}</span>
      </div>
    </div>

    <!-- Content Title and Release Date -->
    <div class="content-info">
      <h3 class="content-title">{{ content.title }}</h3>
      <span class="release-date">Release Date : {{ formatDate(content.releaseDate) }}</span>

      <!-- Source Type -->
      <div class="source-type" v-if="content.contentType">
        {{ formatContentType(content.contentType) }}
      </div>
    </div>
  </div>
</template>

<script>
import {getContentImagePath} from "../../utils/index.js";

export default {
  name: 'ContentCard',
  props: {
    content: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      imgPrefix: 'https://image.tmdb.org/t/p/w1280',
    }
  },
  methods: {
    getContentImagePath,
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return d.toLocaleDateString();
    },
    formatContentType(type) {
      if (!type) return '';
      return type
          .toLowerCase()
          .split('_')
          .map(word => word.charAt(0).toUpperCase() + word.slice(1))
          .join(' ');
    },
    openContentDetails() {
      this.$emit('card-click', this.content);
    }
  }
}
</script>

<style scoped>
.content-card {
  position: relative;
  width: 100%;
  max-width: 240px;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  background-color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
  cursor: pointer;
}

.content-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-level2-hover);
}

.content-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary), var(--secondary));
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
  z-index: 1;
}

.content-card:hover::before {
  transform: scaleX(1);
}

.poster-container {
  position: relative;
  width: 100%;
  height: 0;
  padding-bottom: 150%;
  overflow: hidden;
}

.content-poster {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.content-card:hover .content-poster {
  transform: scale(1.05);
}

.content-info {
  padding: var(--spacing-md) var(--spacing-lg);
  position: relative;
  z-index: 1;
}

.content-title {
  margin: 0;
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-base);
  font-weight: var(--font-fontWeight-semibold);
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.content-category {
  position: absolute;
  bottom: var(--spacing-sm);
  left: var(--spacing-sm);
  z-index: 2;
  padding: var(--spacing-xs);
  backdrop-filter: blur(10px);
  border-radius: var(--border-radius-md);
}

.content-category span {
  font-size: 4px;
  color: rgba(255, 255, 255, 0.76);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.11);
}

.release-date {
  display: block;
  color: var(--text-muted);
  font-size: 14px;
  margin-top: var(--spacing-xs);
}

.source-type {
  display: inline-block;
  margin-top: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-sm);
  font-size: var(--font-fontSize-sm);
  color: var(--text-secondary);
}

.content-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: var(--border-radius-lg);
  pointer-events: none;
  box-shadow: inset 0 0 0 1px rgba(var(--primary-rgb), 0.2);
  transition: box-shadow 0.3s ease;
}

.content-card:hover::after {
  box-shadow: inset 0 0 0 1px rgba(var(--primary-rgb), 0.5);
}

:root[data-theme="cyberpunk"] .content-card::before {
  height: 2px;
  background: linear-gradient(90deg,
  var(--primary),
  var(--secondary),
  var(--primary));
  background-size: 200% 100%;
  animation: gradient-shift 2s linear infinite;
}

:root[data-theme="cyberpunk"] .content-card {
  border: 1px solid var(--border-light);
}

:root[data-theme="cyberpunk"] .content-category {
  box-shadow: 0 0 10px rgba(var(--primary-rgb), 0.7);
}

@keyframes gradient-shift {
  0% {background-position: 0% 0%;}
  100% {background-position: 200% 0%;}
}
</style>