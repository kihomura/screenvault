<template>
  <div class="statistics-page">
    <div class="page-header">
      <div class="header-content">
        <h2>Statistics</h2>
      </div>
      <div class="header-actions">
        <YearFilter
            :availableYears="availableYears"
            :selectedYear="selectedYear"
            @year-change="handleYearChange"
        />
      </div>
    </div>

    <div class="loading-container" v-if="loading">
      <div class="loading-spinner"></div>
      <p>Loading statistics...</p>
    </div>

    <div v-else class="statistics-content">
      <div class="chart-tabs">
        <button
            v-for="tab in tabs"
            :key="tab.id"
            :class="['tab-button', { active: activeTab === tab.id }]"
            @click="activeTab = tab.id">
          {{ tab.name }}
        </button>
      </div>

      <div class="chart-container">
        <div v-show="activeTab === 'country'" class="chart-panel">
          <country-chart
              v-if="statisticsData"
              :chartData="statisticsData.countryData"
          />
        </div>

        <div v-show="activeTab === 'genre'" class="chart-panel">
          <genre-chart
              v-if="statisticsData"
              :chartData="statisticsData.genreData"
          />
        </div>

        <div v-show="activeTab === 'category'" class="chart-panel">
          <category-chart
              v-if="statisticsData"
              :chartData="statisticsData.categoryData"
          />
        </div>

        <div v-show="activeTab === 'rating'" class="chart-panel">
          <rating-chart
              v-if="statisticsData"
              :chartData="statisticsData.ratingData"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import YearFilter from '../components/business/controls/YearFilter.vue';
import CountryChart from '../components/business/charts/CountryChart.vue';
import GenreChart from '../components/business/charts/GenreChart.vue';
import CategoryChart from '../components/business/charts/CategoryChart.vue';
import RatingChart from '../components/business/charts/RatingChart.vue';

export default {
  name: 'StatisticsPage',
  components: {
    YearFilter,
    CountryChart,
    GenreChart,
    CategoryChart,
    RatingChart
  },
  data() {
    return {
      loading: true,
      statisticsData: null,
      selectedYear: '',
      availableYears: [],
      activeTab: 'country',
      tabs: [
        { id: 'country', name: 'Country' },
        { id: 'genre', name: 'Genre' },
        { id: 'category', name: 'Category' },
        { id: 'rating', name: 'Rating' }
      ]
    };
  },
  methods: {
    async fetchAvailableYears() {
      try {
        const response = await this.$http.get('/record');
        if (response.data.code === 200) {
          const years = response.data.data
              .map(record => new Date(record.watchDate).getFullYear())
              .filter(year => !isNaN(year));

          this.availableYears = [...new Set(years)].sort((a, b) => b - a);
        } else {
          console.error('Failed to fetch records:', response.data.message);
        }
      } catch (error) {
        console.error('Error fetching records:', error);
      }
    },

    async fetchStatisticsData() {
      try {
        this.loading = true;
        const url = this.selectedYear
            ? `/statistics?year=${this.selectedYear}`
            : '/statistics';

        const response = await this.$http.get(url);

        if (response.data.code === 200) {
          this.statisticsData = response.data.data;
        } else {
          console.error('Failed to fetch statistics data:', response.data.message);
        }
      } catch (error) {
        console.error('Error fetching statistics data:', error);
      } finally {
        this.loading = false;
      }
    },

    handleYearChange(year) {
      this.selectedYear = year;
      this.fetchStatisticsData();
    }
  },
  async mounted() {
    await this.fetchAvailableYears();
    await this.fetchStatisticsData();
  }
};
</script>

<style scoped>
.statistics-page {
  font-family: var(--font-fontFamily-primary);
  color: var(--text-primary);
  padding: var(--spacing-lg);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--background-muted);
  border-top: 4px solid var(--accent-info);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--spacing-md);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.statistics-content {
  background-color: var(--background-base);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-level2-default);
  overflow: hidden;
}

.chart-tabs {
  display: flex;
  border-bottom: 1px solid var(--border-light);
  background-color: var(--background-subtle);
}

.tab-button {
  padding: var(--spacing-lg) var(--spacing-xl);
  background: none;
  border: none;
  font-family: var(--font-fontFamily-primary);
  font-size: var(--font-fontSize-base);
  font-weight: var(--font-fontWeight-medium);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 3px solid transparent;
}

.tab-button:hover {
  background-color: var(--interactive-hover);
  color: var(--text-primary);
}

.tab-button.active {
  color: var(--accent-info);
  border-bottom: 3px solid var(--accent-info);
  font-weight: var(--font-fontWeight-semibold);
}

.chart-container {
  padding: var(--spacing-xl);
  min-height: 400px;
}

.chart-panel {
  height: 100%;
  width: 100%;
}
</style>