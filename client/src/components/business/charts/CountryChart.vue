<template>
  <div class="chart-wrapper">
    <div class="chart-header">
      <h2 class="chart-title">Which country's films do you watch the most?</h2>
    </div>
    <div class="chart-container" ref="chartContainer"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'CountryChart',
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
  computed: {},
  methods: {
    initChart() {
      try {
        const chartContainer = this.$refs.chartContainer;
        if (!chartContainer) return;

        // check if container has dimensions
        if (chartContainer.clientWidth === 0 || chartContainer.clientHeight === 0) {
          // set a delay and retry if dimensions are not ready
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

        if (!Array.isArray(this.chartData) || this.chartData.length === 0) {
          // empty option when no data
          const secondaryTextColor = getComputedStyle(this.$el).getPropertyValue('--text-secondary').trim();
          const emptyOption = {
            title: {
              text: 'No data available',
              left: 'center',
              top: 'center',
              textStyle: {
                color: secondaryTextColor || '#888888' // Fallback color
              }
            }
          };
          this.chart.setOption(emptyOption);
          return;
        }

        const primaryTextColor = getComputedStyle(this.$el).getPropertyValue('--text-primary').trim();
        const option = {
          tooltip: {
            trigger: 'item',
            formatter: params => {
              return `${params.seriesName} <br/>${params.name}: ${params.value} (${params.percent}%)`
            }
          },
          legend: {
            orient: 'horizontal',
            bottom: 'bottom',
            data: this.chartData.map(item => item.name),
            textStyle: {
              color: primaryTextColor || '#000000', // Fallback color
              fontSize: 12
            }
          },
          series: [
            {
              name: 'Country',
              type: 'pie',
              radius: '70%',
              center: ['50%', '45%'],
              data: this.chartData.map(item => {
                return {
                  value: item.count || 0,
                  name: item.name || ''
                };
              }),
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              label: {
                formatter: params => {
                  return `${params.name}\n${params.value} (${params.percent}%)`
                },
                color: primaryTextColor || '#000000', // Fallback color
                fontSize: 12
              }
            }
          ],
          color: [
            '#cd8b94',
            '#c4cdd0',
            '#c3c8a5',
            '#bea594',
            '#dac890',
            '#87aeb7',
            '#d7ad9b',
            '#7e8093',
            '#e8e2e1',
            '#d0d5c5',
            '#9e89a0',
            '#e7bbb2',
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

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.chart-title {
  font-family: var(--font-fontFamily-secondary);
  font-size: var(--font-fontSize-lg);
  font-weight: var(--font-fontWeight-semibold);
  color: var(--text-primary);
  margin: 0;
}

.chart-container {
  flex-grow: 1;
  min-height: 350px;
  width: 100%;
  height: 600px;
}
</style>