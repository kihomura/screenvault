<template>
  <div class="poster-card" @click="$emit('card-click', content)">
    <div class="poster-wrapper">
      <div class="poster-container">
        <img
            :src="posterUrl"
            :alt="'Poster'"
            class="poster-image"
        />
      </div>
    </div>
    <div class="poster-title">
      {{ content.title || 'Loading...' }}
    </div>
  </div>
</template>

<script>
import { getContentImagePath } from "../../utils/index.js";

export default {
  name: 'PosterCard',
  props: {
    content: {
      type: Object,
      required: true
    }
  },
  computed: {
    posterUrl() {
      if (this.content) {
        return this.getContentImagePath(this.content);
      }
    }
  },
  methods: {
    getContentImagePath,
  }
};
</script>

<style scoped>
.poster-card {
  display: flex;
  flex-direction: column;
  width: 100%;
  cursor: pointer;
  transition: transform 0.2s;
  align-items: center;
  min-width: 0;
}

.poster-card:hover {
  transform: translateY(-5px);
}

.poster-wrapper {
  max-width: 150px;
  width: 100%;
}

.poster-container {
  position: relative;
  width: 100%;
  padding-top: 150%;
  overflow: hidden;
  border-radius: 8px;
  background-color: #f0f0f0;
}

.poster-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.poster-title {
  width: 100%;
  margin-top: 8px;
  font-size: 14px;
  font-weight: 500;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 0 4px;
  max-width: 150px;
}
</style>