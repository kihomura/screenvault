<template>
  <div class="record-card" @click="openContentDetails">
    <div class="poster-container">

      <!-- Poster -->
      <img :src="getContentImagePath(contentDetails)"
           :alt="(contentDetails.title || 'Content') + ' poster'"
           class="content-poster">

      <!-- Rate -->
      <div class="rating-container">
        <div class="star-rating">
          <div class="stars-outer">
            <div class="stars-inner" :style="{ width: getStarRatingWidth() }"></div>
          </div>
        </div>
      </div>

      <!-- Category -->
      <div class="content-category">
        <span>{{contentDetails.category}}</span>
      </div>
    </div>

    <!-- Content Title and Watch Date -->
    <div class="content-info">
      <h3 class="content-title">{{ contentDetails.title }}</h3>
      <span class="watch-date">Watch Date : {{ formatDate(record.watchDate) }}</span>
    </div>
  </div>
</template>

<script>
import {getContentImagePath} from "../../utils/index.js";
export default {
  name: 'RecordCard',
  props: {
    record: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      contentDetails: {},
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
    async getContentDetails() {
      try {
        const response = await this.$http.get(`/content/id/${this.record.contentId}`);
        if (response.data && response.data.data) {
          this.contentDetails = response.data.data;
        } else {
          console.error("Invalid content API response", response);
        }
      } catch (error) {
        console.error("Error fetching content details:", error);
      }
    },
    openContentDetails() {
      this.$emit('card-click', this.record);
    },
    getStarRatingWidth() {
      const rating = this.record.rate || 0;
      const percentage = (rating / 10) * 100;
      return `${percentage}%`;
    }
  },
  mounted() {
    if (this.record.contentId) {
      this.getContentDetails();
    }
  }
}
</script>

<style scoped>
.record-card {
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

.record-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-level2-hover);
}

.record-card::before {
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

.record-card:hover::before {
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

.record-card:hover .content-poster {
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

.rating-container {
  position: absolute;
  top: var(--spacing-sm);
  right: var(--spacing-sm);
  z-index: 2;
  padding: var(--spacing-xs);
  backdrop-filter: blur(5px);
  border-radius: var(--border-radius-md);
}

.star-rating {
  display: flex;
  align-items: center;
}

.stars-outer {
  position: relative;
  display: inline-block;
  font-size: 18px;
  letter-spacing: 2px;
  color: rgba(255, 255, 255, 0.3);
}

.stars-outer::before {
  content: "★★★★★";
}

.stars-inner {
  position: absolute;
  top: 0;
  left: 0;
  white-space: nowrap;
  overflow: hidden;
  color: rgb(255, 213, 2);
}

.stars-inner::before {
  content: "★★★★★";
}

.watch-date {
  color: var(--text-muted);
  font-size: 14px;
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

.record-card::after {
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

.record-card:hover::after {
  box-shadow: inset 0 0 0 1px rgba(var(--primary-rgb), 0.5);
}

:root[data-theme="cyberpunk"] .record-card::before {
  height: 2px;
  background: linear-gradient(90deg,
  var(--primary),
  var(--secondary),
  var(--primary));
  background-size: 200% 100%;
  animation: gradient-shift 2s linear infinite;
}

:root[data-theme="cyberpunk"] .record-card {
  border: 1px solid var(--border-light);
}

:root[data-theme="cyberpunk"] .rating-container,
:root[data-theme="cyberpunk"] .content-category {
  box-shadow: 0 0 10px rgba(var(--primary-rgb), 0.7);
}

@keyframes gradient-shift {
  0% {background-position: 0% 0%;}
  100% {background-position: 200% 0%;}
}
</style>