<template>
  <div class="pagination" v-if="totalPages > 1">
    <button
        class="pagination-button"
        @click="goToPage(currentPage - 1)"
        :disabled="currentPage === 1"
        :class="{ 'disabled': currentPage === 1 }"
    >
      &laquo;
    </button>

    <template v-if="totalPages <= 7">
      <button
          v-for="page in totalPages"
          :key="page"
          @click="goToPage(page)"
          class="pagination-button"
          :class="{ 'active': currentPage === page }"
      >
        {{ page }}
      </button>
    </template>

    <template v-else>
      <!-- 起始页 -->
      <button
          v-for="page in startPages"
          :key="page"
          @click="goToPage(page)"
          class="pagination-button"
          :class="{ 'active': currentPage === page }"
      >
        {{ page }}
      </button>

      <span v-if="showStartEllipsis" class="pagination-ellipsis">...</span>

      <!-- 中间页 -->
      <button
          v-for="page in middlePages"
          :key="page"
          @click="goToPage(page)"
          class="pagination-button"
          :class="{ 'active': currentPage === page }"
      >
        {{ page }}
      </button>

      <span v-if="showEndEllipsis" class="pagination-ellipsis">...</span>

      <!-- 末尾页 -->
      <button
          v-for="page in endPages"
          :key="page"
          @click="goToPage(page)"
          class="pagination-button"
          :class="{ 'active': currentPage === page }"
      >
        {{ page }}
      </button>
    </template>

    <button
        class="pagination-button"
        @click="goToPage(currentPage + 1)"
        :disabled="currentPage === totalPages"
        :class="{ 'disabled': currentPage === totalPages }"
    >
      &raquo;
    </button>
  </div>
</template>

<script>
export default {
  name: "Pagination",
  props: {
    currentPage: {
      type: Number,
      default: 1
    },
    totalPages: {
      type: Number,
      default: 1
    }
  },
  computed: {
    startPages() {
      if (this.currentPage <= 4) {
        return Array.from({ length: Math.min(3, this.totalPages) }, (_, i) => i + 1);
      }
      return [1];
    },
    middlePages() {
      if (this.totalPages <= 7) return [];
      let start = Math.max(4, this.currentPage - 1);
      let end = Math.min(this.totalPages - 3, this.currentPage + 1);

      if (this.currentPage <= 4) {
        start = 4;
        end = Math.min(5, this.totalPages - 3);
      } else if (this.currentPage >= this.totalPages - 3) {
        start = Math.max(4, this.totalPages - 4);
        end = this.totalPages - 3;
      }

      if (start > end) return [];
      return Array.from({ length: end - start + 1 }, (_, i) => start + i);
    },
    endPages() {
      if (this.totalPages <= 7) return [];
      if (this.currentPage >= this.totalPages - 3) {
        return Array.from({ length: 3 }, (_, i) => this.totalPages - 2 + i);
      }
      return [this.totalPages];
    },
    showStartEllipsis() {
      return this.totalPages > 7 && this.currentPage > 4;
    },
    showEndEllipsis() {
      return this.totalPages > 7 && this.currentPage < this.totalPages - 3;
    }
  },
  methods: {
    goToPage(page) {
      if (page < 1 || page > this.totalPages) return;
      this.$emit("page-changed", page);
    }
  }
}
</script>

<style scoped>

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-xl);
}

.pagination-button {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 36px;
  height: 36px;
  padding: 0 var(--spacing-sm);
  background-color: var(--background-subtle);
  border: 1px solid var(--border-light);
  border-radius: var(--border-radius-md);
  color: var(--text-secondary);
  font-size: var(--font-fontSize-sm);
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-button:hover:not(.active):not(.disabled) {
  background-color: var(--interactive-hover);
  border-color: var(--border-medium);
  color: var(--text-primary);
}

.pagination-button.active {
  background-color: var(--primary);
  border-color: var(--primary-dark);
  color: white;
}

.pagination-button.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-ellipsis {
  color: var(--text-muted);
  margin: 0 var(--spacing-xs);
}
</style>
