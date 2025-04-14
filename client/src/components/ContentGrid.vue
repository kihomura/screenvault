<!-- src/components/ContentGrid.vue -->
<template>
  <div class="contents-container">
    <!-- Add new card - only shown on the first page and not in selection mode -->
    <div v-if="showAddCard" class="contents-card add-card" @click="$emit('add-new')">
      <div class="add-content">
        <div class="add-icon-wrapper">
          <span class="add-icon">+</span>
        </div>
        <p class="add-subtext">Track your latest watch</p>
      </div>
    </div>

    <!-- Content cards -->
    <div
        v-for="record in records"
        :key="record.id"
        class="content-card-wrapper"
        @click="$emit('open-record', record)"
        @mouseover="hoveredCard = record.id"
        @mouseleave="hoveredCard = null"
    >
      <content-card
          :record="record"
          :class="{ 'content-card-blur': selectionMode && !isSelected(record) }"
      />

      <!-- Hoverable selection checkbox -->
      <div
          class="hover-selection-checkbox"
          :class="{
          'visible': hoveredCard === record.id || isSelected(record) || selectionMode,
          'selected': isSelected(record)
        }"
          @click.stop="$emit('toggle-selection', record)"
      >
        <div class="selection-indicator">
          <span class="checkmark" v-if="isSelected(record)">✓</span>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <empty-state v-if="filteredRecordsCount === 0" />
  </div>
</template>

<script>
import ContentCard from "./ContentCard.vue";
import EmptyState from "./EmptyState.vue";

export default {
  name: 'ContentGrid',
  components: {
    ContentCard,
    EmptyState
  },
  props: {
    records: {
      type: Array,
      required: true
    },
    selectionMode: {
      type: Boolean,
      default: false
    },
    selectedRecords: {
      type: Array,
      default: () => []
    },
    currentPage: {
      type: Number,
      required: true
    },
    showAddCard: {
      type: Boolean,
      default: false
    },
    filteredRecordsCount: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      hoveredCard: null
    }
  },
  methods: {
    isSelected(record) {
      return this.selectedRecords.some(r => r.id === record.id);
    }
  }
}
</script>

<style scoped>
.contents-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

@media (min-width: 640px) {
  .contents-container {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 768px) {
  .contents-container {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (min-width: 1024px) {
  .contents-container {
    grid-template-columns: repeat(6, 1fr);
  }
}

.content-card-wrapper {
  position: relative;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: var(--shadow-level1-default);
}

.content-card-wrapper:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-level2-hover);
}

.content-card-wrapper.selectable {
  cursor: pointer;
}

.content-card-blur {
  filter: brightness(0.9);
  transition: filter 0.3s ease;
  position: relative;
}

.content-card-blur::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.35);
  pointer-events: none;
  z-index: 10;
}

.hover-selection-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 24px;
  height: 24px;
  background-color: rgba(var(--background-base-rgb), 0.8);
  border: 2px solid rgba(var(--border-medium-rgb), 0.8);
  border-radius: var(--border-radius-full);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s ease, background-color 0.2s ease, border-color 0.2s ease, transform 0.2s ease;
  z-index: 10;
}

.hover-selection-checkbox.visible {
  opacity: 1;
}

.hover-selection-checkbox:hover {
  transform: scale(1.1);
  border-color: var(--accent-info);
}

.hover-selection-checkbox.selected {
  background-color: var(--accent-info);
  border-color: var(--accent-info);
}

.selection-indicator {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.checkmark {
  color: white;
  font-size: var(--font-fontSize-sm);
  font-weight: var(--font-fontWeight-bold);
}

.add-card {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--background-subtle);
  color: var(--text-primary);
  border: 2px dashed var(--border-medium);
  height: 100%;
  min-height: 220px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-card:hover {
  background-color: var(--interactive-hover);
  border-color: var(--primary);
}

.add-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.add-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  border-radius: var(--border-radius-full);
  background-color: var(--background-muted);
}

.add-icon {
  font-size: 24px;
  color: var(--text-primary);
}

.add-subtext {
  color: var(--text-secondary);
  font-size: var(--font-fontSize-sm);
  margin: 0;
}
</style>