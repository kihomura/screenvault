<template>
  <div class="form-group">
    <label for="rating">Rating (Only whole or half values allowed, e.g. 8 or 8.5)</label>
    <div class="rating-input">
      <!-- stars -->
      <div class="rating-stars" @mouseleave="resetHoverRating">
        <div
            v-for="i in 10"
            :key="i"
            class="star-container"
            @mousemove="handleStarHover($event, i)"
            @click="handleStarClick($event, i)"
        >
          <span class="star" :class="getStarClass(i)">★</span>
        </div>
      </div>
      <!-- number -->
      <div class="rating-number">
        <input
            type="number"
            id="rating"
            v-model.number="localRating"
            min="0"
            max="10"
            step="0.5"
            class="form-control"
            @input="validateRating"
        />
        <span class="rating-max">/10</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RatingSelector',
  props: {
    modelValue: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      hoverRating: null,
      localRating: this.modelValue
    };
  },
  watch: {
    modelValue(newValue) {
      this.localRating = newValue;
    },
    localRating(newValue) {
      this.$emit('update:modelValue', newValue);
    }
  },
  created() {
    this.localRating = this.localRating || null;
  },
  methods: {
    handleStarHover(event, starIndex) {
      const rect = event.currentTarget.getBoundingClientRect();
      const mouseX = event.clientX - rect.left;
      const halfPoint = rect.width / 2;
      if (mouseX < halfPoint) {
        this.hoverRating = starIndex - 0.5;
      } else {
        this.hoverRating = starIndex;
      }
    },
    handleStarClick(event, starIndex) {
      const rect = event.currentTarget.getBoundingClientRect();
      const mouseX = event.clientX - rect.left;
      const halfPoint = rect.width / 2;
      if (mouseX < halfPoint) {
        this.localRating = starIndex - 0.5;
      } else {
        this.localRating = starIndex;
      }
      this.hoverRating = null;
    },
    resetHoverRating() {
      this.hoverRating = null;
    },
    getStarClass(starIndex) {
      const rating = this.hoverRating !== null ? this.hoverRating : this.localRating;
      if (!rating) return 'empty';
      if (starIndex <= Math.floor(rating)) {
        return 'full';
      } else if (starIndex === Math.ceil(rating) && rating % 1 !== 0) {
        return 'half';
      } else {
        return 'empty';
      }
    },
    validateRating() {
      if (this.localRating !== null) {
        const roundedValue = Math.round(this.localRating * 2) / 2;
        if (roundedValue !== this.localRating) {
          this.localRating = roundedValue;
        }
        if (this.localRating > 10) {
          this.localRating = 10;
        } else if (this.localRating < 0) {
          this.localRating = 0;
        }
      }
    }
  }
};
</script>

<style scoped>
/* Rating stars */
.rating-stars {
  display: flex;
  margin-right: 20px;
}

.star-container {
  position: relative;
  width: 30px;
  height: 30px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}

.star {
  font-size: 28px;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--border-medium);
  position: relative;
}

.star.full {
  color: var(--accent-warning);
}

.star.half {
  position: relative;
  color: var(--border-medium);
}

.star.half::before {
  content: '★';
  position: absolute;
  overflow: hidden;
  color: var(--accent-warning);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  clip-path: inset(0 50% 0 0);
}

.star.half::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
}

/* Rating input container */
.rating-input {
  display: flex;
  align-items: center;
}

.rating-number {
  display: flex;
  align-items: center;
}

.rating-number input {
  width: 60px;
  height: 40px;
  text-align: center;
  padding: 0 8px;
  -moz-appearance: textfield;
}

.rating-number input::-webkit-outer-spin-button,
.rating-number input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.rating-max {
  color: var(--text-secondary);
  margin-left: 4px;
}

/* Form group */
.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-sm);
  display: block;
}

.form-control {
  padding: var(--spacing-md) var(--spacing-lg);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-lg);
  font-size: var(--font-fontSize-base);
  transition: all 0.2s ease;
  background-color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-info);
  box-shadow: 0 0 0 3px rgba(var(--accent-info-rgb), 0.15);
}

/* Ensuring only 0.5 increment for rating */
input[type="number"].form-control {
  step: 0.5;
}
</style>