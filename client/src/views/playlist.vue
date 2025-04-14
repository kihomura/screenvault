<template>
  <div class="lists-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">My Movie Lists</h1>
        <p class="subtitle">Organize your movies into curated collections</p>
      </div>

      <div class="header-actions">
        <div class="search-box">
          <input type="text" placeholder="Search lists..." class="search-input">
          <button class="search-button">
            <span class="search-icon">🔍</span>
          </button>
        </div>

        <button class="create-list-button">
          <span class="plus-icon">+</span>
          <span>Create New List</span>
        </button>
      </div>
    </div>

    <div class="lists-container">
      <!-- Create new list card - always shown -->
      <div class="list-card add-list-card" @click="createNewList">
        <div class="add-content">
          <div class="add-icon-wrapper">
            <span class="add-icon">+</span>
          </div>
          <h3 class="add-text">Create New List</h3>
          <p class="add-subtext">Start a new collection of movies</p>
        </div>
      </div>

      <!-- Movie list cards -->
      <div
          v-for="list in movieLists"
          :key="list.id"
          class="list-card"
          @click="openList(list.id)"
      >
        <div class="list-poster-grid">
          <div
              v-for="(poster, index) in list.recentPosters.slice(0, 4)"
              :key="index"
              class="list-poster-item"
              :style="{ opacity: 1 - (index * 0.15) }"
          >
            <img :src="poster" alt="Movie poster" class="list-poster-image">
          </div>
          <div class="movie-count" v-if="list.movieCount > 4">
            +{{ list.movieCount - 4 }}
          </div>
        </div>

        <div class="list-info">
          <h3 class="list-title">{{ list.name }}</h3>
          <div class="list-meta">
            <span class="movie-count-label">{{ list.movieCount }} movies</span>
            <span class="meta-divider">•</span>
            <span class="last-updated">Updated {{ formatDate(list.lastUpdated) }}</span>
          </div>
          <div class="list-tags" v-if="list.tags && list.tags.length">
            <span class="list-tag" v-for="tag in list.tags" :key="tag">{{ tag }}</span>
          </div>
        </div>

        <div class="list-actions">
          <button class="action-icon-button" @click.stop="shareList(list.id)">
            <span class="action-icon">↗</span>
          </button>
          <button class="action-icon-button" @click.stop="editList(list.id)">
            <span class="action-icon">✎</span>
          </button>
          <button class="action-icon-button danger" @click.stop="deleteList(list.id)">
            <span class="action-icon">🗑</span>
          </button>
        </div>
      </div>

      <!-- Empty state -->
      <div v-if="movieLists.length === 0" class="empty-state">
        <div class="empty-state-icon">📋</div>
        <h3 class="empty-state-title">No Movie Lists Yet</h3>
        <p class="empty-state-message">
          Create your first movie list to organize your favorite films by theme, genre, or any category you want!
        </p>
        <button class="empty-state-button" @click="createNewList">Create Your First List</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MovieListsPage',
  data() {
    return {
      movieLists: []
    }
  },
  methods: {
    formatDate(date) {
      // Date formatting logic would go here
      return date;
    },
    createNewList() {
      // Logic to create a new list
    },
    openList(listId) {
      // Logic to open a specific list
    },
    shareList(listId) {
      // Logic to share list
    },
    editList(listId) {
      // Logic to edit list
    },
    deleteList(listId) {
      // Logic to delete list
    }
  }
}
</script>

<style scoped>
.lists-page {
  padding: var(--spacing-xl);
  background-color: var(--background-base);
  min-height: 100vh;
  color: var(--text-primary);
  font-family: var(--fontFamily-primary);
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 2px solid var(--border-light);
}

.header-content {
  display: flex;
  flex-direction: column;
}

.page-title {
  font-weight: var(--fontWeight-bold);
  font-size: calc(var(--fontSize-xxl) * 1.5);
  color: var(--text-primary);
  margin: 0;
  position: relative;
  display: inline-block;
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 60px;
  height: 4px;
  background-color: var(--secondary);
  border-radius: var(--border-radius-full);
}

.subtitle {
  color: var(--text-secondary);
  font-size: var(--fontSize-lg);
  margin: var(--spacing-md) 0 0 0;
  font-weight: var(--fontWeight-light);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.search-box {
  position: relative;
  width: 250px;
}

.search-input {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-xl) var(--spacing-md) var(--spacing-lg);
  border-radius: var(--border-radius-full);
  border: 1px solid var(--border-medium);
  background-color: var(--background-subtle);
  color: var(--text-primary);
  font-family: var(--fontFamily-primary);
  font-size: var(--fontSize-base);
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: var(--secondary);
  box-shadow: 0 0 0 3px rgba(var(--primary-rgb), 0.2);
}

.search-button {
  position: absolute;
  right: var(--spacing-sm);
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-muted);
  transition: color 0.2s ease;
}

.search-button:hover {
  color: var(--secondary);
}

.create-list-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--secondary);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  font-weight: var(--fontWeight-medium);
  cursor: pointer;
  transition: all 0.3s ease;
}

.create-list-button:hover {
  background-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: var(--level1-hover);
}

.plus-icon {
  font-weight: var(--fontWeight-bold);
  font-size: var(--fontSize-lg);
}

.lists-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.list-card {
  display: flex;
  align-items: center;
  padding: var(--spacing-lg);
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--level1-default);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid var(--border-light);
  position: relative;
  overflow: hidden;
}

.list-card:hover {
  box-shadow: var(--level2-hover);
  transform: translateY(-3px);
  border-color: var(--border-medium);
}

.list-card:hover::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 4px;
  background-color: var(--secondary);
}

.list-poster-grid {
  display: grid;
  grid-template-columns: repeat(2, 60px);
  grid-template-rows: repeat(2, 90px);
  gap: var(--spacing-xs);
  margin-right: var(--spacing-xl);
  position: relative;
}

.list-poster-item {
  overflow: hidden;
  border-radius: var(--border-radius-md);
  box-shadow: var(--level1-default);
  transition: all 0.3s ease;
}

.list-poster-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-count {
  position: absolute;
  bottom: var(--spacing-xs);
  right: var(--spacing-xs);
  background-color: rgba(var(--background-base), 0.8);
  color: var(--text-primary);
  font-size: var(--fontSize-xs);
  font-weight: var(--fontWeight-bold);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-md);
  backdrop-filter: blur(4px);
}

.list-info {
  flex: 1;
}

.list-title {
  margin: 0 0 var(--spacing-xs) 0;
  font-size: var(--fontSize-xl);
  font-weight: var(--fontWeight-semibold);
  color: var(--text-primary);
}

.list-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-sm);
  color: var(--text-secondary);
  font-size: var(--fontSize-sm);
}

.meta-divider {
  color: var(--border-medium);
}

.list-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.list-tag {
  background-color: var(--background-muted);
  color: var(--text-secondary);
  font-size: var(--fontSize-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-full);
}

.list-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.action-icon-button {
  width: 36px;
  height: 36px;
  border-radius: var(--border-radius-full);
  border: 1px solid var(--border-light);
  background-color: var(--background-base);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-secondary);
}

.action-icon-button:hover {
  background-color: var(--interactive-hover);
  color: var(--secondary);
  border-color: var(--secondary);
}

.action-icon-button.danger:hover {
  background-color: var(--accent-error);
  color: white;
  border-color: var(--accent-error);
}

.add-list-card {
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
  background: linear-gradient(135deg, var(--background-subtle) 0%, var(--background-muted) 100%);
  border: 2px dashed var(--border-medium);
  text-align: center;
  min-height: 180px;
}

.add-list-card:hover {
  border-color: var(--secondary);
}

.add-list-card:hover::before {
  display: none;
}

.add-icon-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 70px;
  height: 70px;
  border-radius: var(--border-radius-full);
  background: linear-gradient(135deg, var(--secondary) 0%, var(--primary) 100%);
  color: white;
  font-size: 2rem;
  margin-bottom: var(--spacing-lg);
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(var(--primary-rgb), 0.3);
}

.add-list-card:hover .add-icon-wrapper {
  transform: scale(1.1) rotate(90deg);
}

.add-text {
  color: var(--text-primary);
  font-weight: var(--fontWeight-semibold);
  font-size: var(--fontSize-lg);
  margin: 0 0 var(--spacing-xs) 0;
}

.add-subtext {
  color: var(--text-secondary);
  font-size: var(--fontSize-sm);
  margin: 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xxl);
  text-align: center;
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-lg);
  border: 1px dashed var(--border-medium);
  margin-top: var(--spacing-xl);
}

.empty-state-icon {
  font-size: 4rem;
  margin-bottom: var(--spacing-lg);
  color: var(--secondary);
}

.empty-state-title {
  font-size: var(--fontSize-xl);
  font-weight: var(--fontWeight-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--spacing-md) 0;
}

.empty-state-message {
  color: var(--text-secondary);
  max-width: 500px;
  margin: 0 0 var(--spacing-xl) 0;
}

.empty-state-button {
  padding: var(--spacing-md) var(--spacing-xl);
  background-color: var(--secondary);
  color: white;
  border: none;
  border-radius: var(--border-radius-lg);
  font-weight: var(--fontWeight-medium);
  cursor: pointer;
  transition: all 0.3s ease;
}

.empty-state-button:hover {
  background-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: var(--level2-hover);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-lg);
  }

  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .search-box {
    width: 100%;
  }

  .list-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .list-poster-grid {
    margin-right: 0;
    margin-bottom: var(--spacing-lg);
    width: 100%;
    grid-template-columns: repeat(2, 1fr);
  }

  .list-actions {
    width: 100%;
    justify-content: flex-end;
    margin-top: var(--spacing-lg);
  }
}
</style>