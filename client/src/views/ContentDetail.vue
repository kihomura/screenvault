<template>
  <div class="content-detail-page">
    <div class="page-header">
      <back-button aria-label="Back to previous page"/>
      <h1 class="page-title">{{ this.content?.title }}</h1>
    </div>

    <div v-if="loading" class="loading-state">
      Loading content details...
    </div>

    <div v-else-if="error" class="error-state">
      {{ error }}
    </div>

    <div v-else class="content-detail-content">
      <div class="content-detail-container">
        <!-- Content Detail -->
        <content-info-card :content="content" />

        <!-- User's Record Detail -->
        <recording-info-card
            :user-content="record"
            :tags="tags"
            :lists="lists"
            @edit="openEditModal"
            @add-record="openAddRecordModal"
        />
      </div>
    </div>

    <!-- Add Record Modal -->
    <add-record-modal
        :is-open="showAddRecordModal || showEditModal"
        :selected-content="content"
        :existing-record="showEditModal? record : null"
        :existing-tags="showEditModal? tags : []"
        @close="closeModal"
        @save="saveOrUpdateNewRecord"
    />
  </div>
</template>

<script>
import BackButton from "../components/buttons/BackButton.vue";
import ContentInfoCard from "../components/cards/ContentInfoCard.vue";
import RecordingInfoCard from "../components/cards/RecordInfoCard.vue";
import AddRecordModal from "../components/modals/AddRecordModal.vue";

export default {
  name: 'ContentDetailPage',
  components: {
    BackButton,
    ContentInfoCard,
    RecordingInfoCard,
    AddRecordModal
  },
  data() {
    return {
      contentId: null,
      content: null,
      record: null,
      tags: [],
      lists: [],
      loading: true,
      error: null,
      showEditModal: false,
      showAddRecordModal: false,
      isAdding: false,
    }
  },
  methods: {
    openEditModal() {
      this.showEditModal = true;
    },
    openAddRecordModal() {
      this.showAddRecordModal = true;
    },
    closeModal() {
      this.showAddRecordModal = false;
      this.showEditModal = false;
    },
    async fetchContentDetails() {
      this.loading = true;
      this.error = null;
      try {
        const response = await this.$http.get(`/content/id/${this.contentId}`);
        if (response.data && response.data.data) {
          this.content = response.data.data;
          await this.fetchTags(this.contentId);
          await this.fetchLists(this.contentId)
        } else {
          this.error = "Failed to load content details";
        }
      } catch (error) {
        console.error("Error fetching content details:", error);
        this.error = "Error loading content details. Please try again.";
      } finally {
        this.loading = false;
      }
    },
    async fetchRecordDetails() {
      try {
        const response = await this.$http.get(`/record/content/${this.contentId}`);
        if (response.data && response.data.data) {
          this.record = response.data.data;
        } else {
          // set record to null when no record fetched
          this.record = null;
          console.log("No record found for this content");
        }
      } catch (error) {
        console.error("Error fetching record details:", error);
        this.record = null;
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
    async saveOrUpdateNewRecord(recordData) {
      if(this.isAdding) return;
      try {
        this.isAdding = true;
        const recordResponse = await this.$http.post('/record', recordData.recordingData);

        if (recordResponse.data && recordResponse.data.data) {
          const originalTagIds = recordData.originalTags.map(tag => tag.tagId);
          const newTagIds = recordData.newTags.map(tag => tag.tagId);

          // find tags to add (in new but not in original)
          const tagsToAdd = recordData.newTags.filter(tag =>
              !originalTagIds.includes(tag.tagId)
          );

          // find tags to remove (in original but not in new)
          const tagsToRemove = recordData.originalTags.filter(tag =>
              !newTagIds.includes(tag.tagId)
          );

          const apiCalls = [
            ...tagsToAdd.map(tag => this.$http.post('/tag-content', tag)),
            ...tagsToRemove.map(tag => this.$http.delete('/tag-content', { data: tag }))
          ];

          const results = await Promise.all(apiCalls);
          const allSuccessful = results.every(response => response.data.code === 200);

          if (allSuccessful) {
            console.log(`Successfully updated tags for ${this.content.title}`);
            await Promise.all([
              this.fetchRecordDetails(),
              this.fetchTags(this.contentId),
              this.fetchLists(this.contentId)
            ]);
          } else {
            console.error(`Error updating tags`, results);
          }

          this.closeModal();
        }
      } catch (error) {
        console.error("Error saving new record:", error);
      } finally {
        this.isAdding = false;
      }
    }
  },
  created() {
    this.contentId = this.$route.params.id;
    if (this.contentId) {
      this.fetchContentDetails();
      this.fetchRecordDetails();
      this.fetchTags();
      this.fetchLists();
    } else {
      this.error = "Invalid content ID";
      this.loading = false;
    }
  }
}
</script>

<style scoped>
.content-detail-page {
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
.content-detail-content {
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
  .content-detail-page {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
}
</style>