<template>
  <div
      class="content-item"
      :class="{
      'watched': content.status === 'WATCHED',
      'wish-list': content.status === 'WANT_TO_WATCH',
      'not-selectable': !selectable,
      'selected': selected && multiSelect
    }"
      @click="handleItemClick"
  >
    <div class="result-poster">
      <img :src="getContentImagePath(content)" alt="Poster" />
    </div>

    <div class="result-details">
      <h4 class="result-title">{{ content.title }}</h4>
      <p class="result-year">{{ formattedYear }}</p>
    </div>

    <div v-if="content.status" class="status-badge">
      {{ statusLabel }}
    </div>

    <!-- multi-select checkbox -->
    <div v-if="multiSelect && selectable" class="selection-overlay">
      <div class="checkbox-container" @click.stop="handleToggleSelection">
        <div class="checkbox" :class="{ 'checked': selected }">
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { formatYear, getContentImagePath } from "../utils/index.js";

export default {
  name: 'ContentItem',
  props: {
    content: {
      type: Object,
      required: true
    },
    selectable: {
      type: Boolean,
      default: true
    },
    multiSelect: {
      type: Boolean,
      default: false
    },
    selected: {
      type: Boolean,
      default: false
    },
  },
  computed: {
    formattedYear() {
      return formatYear(this.content.releaseDate);
    },
    statusLabel() {
      switch(this.content.status) {
        case 'WATCHED':
          return 'watched';
        case 'WANT_TO_WATCH':
          return 'in wish list';
        default:
          return '';
      }
    },
  },
  methods: {
    getContentImagePath,
    handleItemClick() {
      if (!this.selectable) return;

      if (this.multiSelect) {
        this.handleToggleSelection();
      } else {
        this.$emit('select', this.content);
      }
    },
    handleToggleSelection() {
      if (this.selectable) {
        this.$emit('toggle-selection', this.content);
      }
    }
  }
};
</script>

<style scoped>
.content-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-lg);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  margin-bottom: var(--spacing-md);
  border: 1px solid transparent;
  background-color: var(--background-base);
  box-shadow: var(--shadow-level1-default);
}

.content-item:hover {
  box-shadow: var(--shadow-level2-hover);
  transform: translateY(-3px);
  border-color: rgba(var(--accent-info-rgb), 0.2);
}

.not-selectable {
  opacity: 0.6;
  cursor: not-allowed;
}

.not-selectable:hover {
  transform: none;
  box-shadow: var(--shadow-level1-default);
  border-color: transparent;
}

.result-poster {
  width: 50px;
  height: 75px;
  overflow: hidden;
  border-radius: var(--border-radius-md);
  margin-right: var(--spacing-lg);
  box-shadow: var(--shadow-level1-default);
  flex-shrink: 0;
  position: relative;
}

.result-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.content-item:hover .result-poster img {
  transform: scale(1.05);
}

.result-details {
  flex-grow: 1;
}

.result-title {
  margin: 0 0 var(--spacing-xs);
  font-weight: var(--font-fontWeight-medium);
  font-size: var(--font-fontSize-base);
  color: var(--text-primary);
}

.result-year {
  margin: 0;
  font-size: var(--font-fontSize-sm);
  color: var(--text-muted);
}

.status-badge {
  position: absolute;
  top: 20px;
  right: 10px;
  background-color: rgba(var(--tertiary-rgb), 0.5);
  color: var(--background-base);
  font-size: var(--font-fontSize-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-md);
  text-transform: uppercase;
}

.wish-list .status-badge {
  background-color: rgba(var(--accent-success-rgb), 0.5);
  color: var(--background-base);
}

/* selection overlay */
.selection-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 2;
}

/*!* checkbox *!*/
/*.checkbox-container {*/
/*  position: absolute;*/
/*  bottom: 10px;*/
/*  right: 10px;*/
/*  pointer-events: auto;*/
/*}*/

/*.checkbox {*/
/*  width: 22px;*/
/*  height: 22px;*/
/*  border-radius: var(--border-radius-full);*/
/*  border: 2px solid var(--background-muted);*/
/*  background-color: rgba(var(--background-base-rgb), 0.7);*/
/*  display: flex;*/
/*  align-items: center;*/
/*  justify-content: center;*/
/*  transition: all 0.2s ease;*/
/*}*/

/*.checkbox span {*/
/*  color: var(--background-base);*/
/*  font-size: var(--font-fontSize-xs);*/
/*  font-weight: var(--font-fontWeight-bold);*/
/*  line-height: 1;*/
/*}*/

/*.checkbox.checked {*/
/*  border: 2px solid var(--primary);*/
/*  background-color: var(--primary);*/
/*}*/

/* selected item */
.content-item.selected {
  border-color: var(--tertiary);
  border-width: 2px;
  background-color: rgba(var(--primary-rgb), 0.3);
  box-shadow: 0 0 0 1px var(--background-muted), var(--shadow-level2-hover);
}
</style>