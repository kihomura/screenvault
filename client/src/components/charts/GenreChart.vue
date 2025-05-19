<template>
  <div class="chart-wrapper">
    <h2 class="chart-title">What genres do you watch the most?</h2>
    <div class="chart-container" ref="chartContainer"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'GenreChart',
  props: {
    chartData: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      chart: null,
      chartInitialized: false
    };
  },
  methods: {
    getCssVarColor(varName) {
      return getComputedStyle(document.documentElement).getPropertyValue(varName).trim() || undefined;
    },

    initChart() {
      try {
        const chartContainer = this.$refs.chartContainer;
        if (!chartContainer) return;

        if (chartContainer.clientWidth === 0 || chartContainer.clientHeight === 0) {
          setTimeout(() => {
            this.initChart();
          }, 100);
          return;
        }

        if (this.chart) {
          this.chart.dispose();
        }

        this.chart = echarts.init(chartContainer);
        this.chartInitialized = true;

        const textPrimaryColor = this.getCssVarColor('--text-primary') || '#000000'; // Fallback
        const textSecondaryColor = this.getCssVarColor('--text-secondary') || '#888888'; // Fallback
        const borderLightColor = this.getCssVarColor('--border-light') || '#DDDDDD'; // Fallback

        if (!Array.isArray(this.chartData) || this.chartData.length === 0) {
          const emptyOption = {
            title: {
              text: 'No data available',
              left: 'center',
              top: 'center',
              textStyle: {
                color: textSecondaryColor
              }
            }
          };
          this.chart.setOption(emptyOption);
          return;
        }

        // Sort data if needed, or assume it's pre-sorted
        const sortedData = [...this.chartData].sort((a, b) => b.count - a.count);

        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'value',
            axisLabel: {
              color: textPrimaryColor
            },
            axisLine: {
              lineStyle: {
                color: borderLightColor
              }
            },
            splitLine: {
              lineStyle: {
                color: borderLightColor,
                type: 'dashed'
              }
            }
          },
          yAxis: {
            type: 'category',
            data: sortedData.map(item => this.formatGenreName(item.name || '')),
            axisLabel: {
              interval: 0,
              rotate: 0,
              color: textPrimaryColor
            },
            axisLine: {
              lineStyle: {
                color: borderLightColor
              }
            }
          },
          series: [
            {
              name: 'Count',
              type: 'bar',
              data: sortedData.map(item => ({
                value: item.count || 0,
                itemStyle: {
                  color: this.getGenreColor(item.name) // Use existing getGenreColor
                }
              })),
              label: {
                show: true,
                position: 'right',
                formatter: '{c}',
                color: textPrimaryColor
              }
            }
          ]
        };

        this.chart.setOption(option);
      } catch (error) {
        console.error('Failed to initialize chart:', error);
      }
    },

    resizeChart() {
      if (this.chart) {
        this.chart.resize();
      }
    },

    formatGenreName(name) {
      if (!name) return 'Unknown';
      return name.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
    },

    getGenreColor(genreName) {
      const genreColorMap = {
        'ACTION': '#a0b2c6',
        'ADVENTURE': '#b5aec8',
        'ANIMATION': '#a3c9a8',
        'COMEDY': '#f0dca3',
        'CRIME': '#d37e7e',
        'DOCUMENTARY': '#95a7b5',
        'DRAMA': '#d8a48f',
        'FAMILY': '#c5d4af',
        'FANTASY': '#b3b5d9',
        'HISTORY': '#c8b9a8',
        'HORROR': '#8c9f8d',
        'MUSIC': '#e3c4c3',
        'MYSTERY': '#7d92a8',
        'ROMANCE': '#e8b5bf',
        'SCI_FI_FANTASY': '#9dacb2',
        'TV_MOVIE': '#cab5a2',
        'THRILLER': '#a18d98',
        'WAR': '#8e8e8e',
        'WESTERN': '#ac9a7c',
        'ACTION_ADVENTURE': '#b3aab3',
        'KIDS': '#d2c0a0',
        'NEWS': '#b1c3c8',
        'REALITY': '#d0bdb3',
        'SOAP': '#e9d5c9',
        'TALK': '#c9cdcf',
        'WAR_POLITICS': '#9a9b94',
        'SCIENCE_FICTION': '#8aa3b8'
      };

      return genreColorMap[genreName] || '#c9c9c9';
    },

    getColorByIndex(index) {
      const colorList = [
        '#a0b2c6', '#b5aec8', '#a3c9a8', '#f0dca3', '#d37e7e',
        '#95a7b5', '#d8a48f', '#c5d4af', '#b3b5d9', '#c8b9a8',
        '#8c9f8d', '#e3c4c3', '#7d92a8', '#e8b5bf', '#9dacb2',
        '#cab5a2', '#a18d98', '#8e8e8e', '#ac9a7c', '#b3aab3',
        '#d2c0a0', '#b1c3c8', '#d0bdb3', '#e9d5c9', '#c9cdcf',
        '#9a9b94', '#8aa3b8'
      ];
      return colorList[index % colorList.length];
    }
  },
  mounted() {
    setTimeout(() => {
      this.initChart();
    }, 300);

    window.addEventListener('resize', this.resizeChart);
  },
  watch: {
    chartData: {
      handler(newData) {
        if (!Array.isArray(newData)) {
          console.error('Invalid chart data:', newData);
          return;
        }

        this.$nextTick(() => {
          if (this.chartInitialized) {
            this.initChart();
          }
        });
      },
      deep: true,
      immediate: false
    }
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.resizeChart);
    if (this.chart) {
      this.chart.dispose();
      this.chart = null;
    }
  }
};
</script>

<style scoped>
.chart-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chart-title {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-semibold);
  color: var(--text-primary);
  margin-top: 0;
  margin-bottom: var(--spacing-md);
}

.chart-container {
  flex-grow: 1;
  min-height: 350px;
  width: 100%;
  height: 600px;
}
</style>