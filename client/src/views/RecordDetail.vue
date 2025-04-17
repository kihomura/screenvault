<template>
  <div class="record-detail-page">
    <div class="page-header">
      <back-button
          :to="{ name: 'watched' }"
          aria-label="Back to recordings"
      />
      <h1 class="page-title">Watching Recordings</h1>
    </div>

    <div v-if="loading" class="loading-state">
      Loading content details...
    </div>

    <div v-else-if="error" class="error-state">
      {{ error }}
    </div>

    <div v-else class="record-detail-content">
      <div class="content-detail-container">
        <!-- Content Detail -->
        <content-info-card :content="content" :img-prefix="imgPrefix" />

        <!-- User's Record Detail -->
        <recording-info-card
            :user-content="userContent"
            :tags="tags"
            :lists="lists"
            @edit="openEditModal"
        />
      </div>
    </div>

    <!-- Edit modal (didnt apply) -->
    <!-- <edit-recording-modal
        v-if="showEditModal"
        :record="userContent"
        @close="closeEditModal"
        @save="saveUpdatedRecord"
    /> -->
  </div>
</template>

<script>
import BackButton from "../components/buttons/BackButton.vue";
import ContentInfoCard from "../components/ContentInfoCard.vue";
import RecordingInfoCard from "../components/RecordInfoCard.vue";

export default {
  name: 'RecordDetailPage',
  components: {
    BackButton,
    ContentInfoCard,
    RecordingInfoCard
  },
  data() {
    return {
      recordId: null,
      userContent: null,
      content: null,
      tags: [],
      lists: [],
      loading: true,
      error: null,
      showEditModal: false,
      // TODO: use imgPrefix only when content's source_type = 'OFFICIAL_DATA'
      imgPrefix: 'https://image.tmdb.org/t/p/w1280'
    }
  },
  methods: {
    openEditModal() {
      this.showEditModal = true;
    },
    closeEditModal() {
      this.showEditModal = false;
    },
    async fetchRecordDetails() {
      this.loading = true;
      this.error = null;
      try {
        const userContentResponse = await this.$http.get(`/record/id/${this.recordId}`);
        if (userContentResponse.data && userContentResponse.data.data) {
          this.userContent = userContentResponse.data.data;
          // use contentId in user_content to get content detail (include tag and list)
          await this.fetchContentDetails(this.userContent.contentId);
          await this.fetchTags(this.userContent.contentId);
          await this.fetchLists(this.userContent.contentId);
        } else {
          this.error = "Failed to load record details";
        }
      } catch (error) {
        console.error("Error fetching record details:", error);
        this.error = "Error loading record details. Please try again.";
      } finally {
        this.loading = false;
      }
    },
    async fetchContentDetails(contentId) {
      try {
        const contentResponse = await this.$http.get(`/content/id/${contentId}`);
        if (contentResponse.data && contentResponse.data.data) {
          this.content = contentResponse.data.data;
        } else {
          console.error("Invalid content response format", contentResponse);
          this.error = "Failed to load content details";
        }
      } catch (error) {
        console.error("Error fetching content details:", error);
        this.error = "Error loading content details";
      }
    },
    async fetchTags(contentId) {
      try {
        const tagIdsResponse = await this.$http.get(`/tag-content/content/${contentId}`);
        if (tagIdsResponse.data && tagIdsResponse.data.data) {
          const tagIds = tagIdsResponse.data.data;
          // get each tag's details
          const tagPromises = tagIds.map(tagId => this.$http.get(`/tag/id/${tagId}`));
          const tagResponses = await Promise.all(tagPromises);
          this.tags = tagResponses
              .filter(response => response.data && response.data.data)
              .map(response => response.data.data);
        }
      } catch (error) {
        console.error("Error fetching tags:", error);
      }
    },
    async fetchLists(contentId) {
      try {
        const listContentResponse = await this.$http.get(`/list-content/content/${contentId}`);
        if (listContentResponse.data && listContentResponse.data.data) {
          const listContents = listContentResponse.data.data;

          const listPromises = listContents.map(listContent => this.$http.get(`/playlist/id/${listContent.listId}`));
          const listResponses = await Promise.all(listPromises);
          this.lists = listResponses
              .filter(response => response.data && response.data.data)
              .map(response => response.data.data);
        }
      } catch (error) {
        console.error("Error fetching lists:", error);
      }
    },
    async saveUpdatedRecord(updatedData) {
      // TODO: edit Record method
    }
  },
  created() {
    this.recordId = this.$route.params.id;
    if (this.recordId) {
      this.fetchRecordDetails();
    } else {
      this.error = "Invalid record ID";
      this.loading = false;
    }
  }
}
</script>

<style scoped>
.record-detail-page {
  font-family: var(--font-fontFamily-primary);
  color: var(--text-primary);
  padding: var(--spacing-xl) var(--spacing-xl);
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
}

.page-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xxl);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
}

.page-title {
  font-family: var(--font-fontFamily-secondary);
  font-weight: var(--font-fontWeight-bold);
  font-size: var(--font-fontSize-xxl);
  color: var(--text-primary);
  margin: 0;
}

/* Loading and error states */
.loading-state, .error-state {
  text-align: center;
  padding: var(--spacing-xxl);
  font-size: var(--font-fontSize-lg);
  color: var(--text-secondary);
  background-color: var(--background-subtle);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level1-default);
  margin-top: var(--spacing-xl);
}

.error-state {
  color: var(--accent-error);
}

/* Main content container */
.record-detail-content {
  padding: 0;
}

.content-detail-container {
  display: flex;
  gap: var(--spacing-xl);
  align-items: stretch;
}

/* Responsive adjustments */
@media (max-width: 1024px) {
  .content-detail-container {
    flex-direction: column;
  }
}

@media (max-width: 600px) {
  .record-detail-page {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
}
</style>