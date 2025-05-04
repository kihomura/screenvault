<template>
  <div class="chart-wrapper">
    <h2 class="chart-title">Your rating history looks like...</h2>
    <div class="chart-container" ref="chartContainer"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'RatingChart',
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

        const textPrimaryColor = this.getCssVarColor('--text-primary');
        const borderLightColor = this.getCssVarColor('--border-light');
        const textSecondaryColor = this.getCssVarColor('--text-secondary');

        if (!Array.isArray(this.chartData) || this.chartData.length === 0) {
          const emptyOption = {
            title: {
              text: 'No data available',
              left: 'center',
              top: 'center',
              textStyle: {
                color: textSecondaryColor,
              }
            }
          };
          this.chart.setOption(emptyOption);
          return;
        }

        // sort rating data by rating range
        const sortedRatings = [...this.chartData]
            .filter(item => item && item.name)
            .sort((a, b) => {
              try {
                const aValue = parseFloat(a.name.split('-')[0]);
                const bValue = parseFloat(b.name.split('-')[0]);
                return isNaN(aValue) || isNaN(bValue) ? 0 : aValue - bValue;
              } catch (error) {
                console.warn('Error sorting rating data:', error);
                return 0;
              }
            });

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
            type: 'category',
            data: sortedRatings.map(item => item.name || ''),
            axisLabel: {
              interval: 0,
              rotate: 30,
              color: textPrimaryColor
            },
            axisLine: {
              lineStyle: {
                color: borderLightColor
              }
            }
          },
          yAxis: {
            type: 'value',
            name: 'Count',
            nameTextStyle: {
              color: textSecondaryColor,
              padding: [0, 0, 0, 30]
            },
            axisLabel: {
              color: textSecondaryColor
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
          series: [
            {
              name: 'Rating',
              type: 'bar',
              data: sortedRatings.map(item => {
                return {
                  value: item.count || 0,
                  itemStyle: {
                    color: this.getRatingColor(item.name || '')
                  }
                };
              }),
              label: {
                show: true,
                position: 'top',
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

    getRatingColor(rating) {
      if (!rating) return '#d1d1d1';

      const parsed = parseFloat(rating);
      if (isNaN(parsed) || parsed < 0 || parsed > 10) return '#d1d1d1';

      const colorMap = [
        '#d1d1d1',
        '#c7c7c7',
        '#b6b8a6',
        '#a6ad94',
        '#a2b086',
        '#b0ba92',
        '#c5caa0',
        '#d2cba0',
        '#e0d6a3',
        '#e6dca6',
        '#e8d9a9',
        '#e8d0a6',
        '#e6c9a1',
        '#e6c1a0',
        '#e5bca0',
        '#e3b6a1',
        '#e1aea4',
        '#dda5a8',
        '#d79da7',
        '#d296a9',
        '#cc8fa7'
      ];

      const index = Math.round(parsed * 2);
      return colorMap[index] || '#d1d1d1';
    },
    getCssVarColor(varName) {
      return getComputedStyle(document.documentElement).getPropertyValue(varName).trim() || undefined;
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