<template>
  <div class="statistics-container">
    <div class="header">
      <div class="logo">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
          <polyline points="22 4 12 14.01 9 11.01"></polyline>
        </svg>
      </div>
      <span class="title">数据分析看板</span>
      <button class="back-btn" @click="goBack">返回首页</button>
    </div>

    <div class="overview-cards">
      <div class="overview-card">
        <div class="card-icon users-icon">👥</div>
        <div class="card-content">
          <div class="card-value">{{ overviewStats.totalUsers || 0 }}</div>
          <div class="card-label">总用户数</div>
        </div>
      </div>
      <div class="overview-card">
        <div class="card-icon items-icon">📦</div>
        <div class="card-content">
          <div class="card-value">{{ overviewStats.totalItems || 0 }}</div>
          <div class="card-label">总商品数</div>
        </div>
      </div>
      <div class="overview-card">
        <div class="card-icon records-icon">📊</div>
        <div class="card-content">
          <div class="card-value">{{ overviewStats.totalRecords || 0 }}</div>
          <div class="card-label">总行为数</div>
        </div>
      </div>
      <div class="overview-card">
        <div class="card-icon purchases-icon">🛒</div>
        <div class="card-content">
          <div class="card-value">{{ overviewStats.totalPurchases || 0 }}</div>
          <div class="card-label">总购买数</div>
        </div>
      </div>
    </div>

    <div class="charts-wrapper">
      <div class="chart-card">
        <div class="chart-title">行为类型分布</div>
        <div ref="pieChart" class="chart"></div>
      </div>

      <div class="chart-card">
        <div class="chart-title">热门商品TOP10</div>
        <div ref="barChart" class="chart"></div>
      </div>

      <div class="chart-card full-width">
        <div class="chart-title">时段活跃度分布</div>
        <div ref="lineChart" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import axios from 'axios';

const router = useRouter();
const pieChart = ref(null);
const barChart = ref(null);
const lineChart = ref(null);
const overviewStats = ref({
  totalUsers: 0,
  totalItems: 0,
  totalRecords: 0,
  totalPurchases: 0
});

let pieChartInstance = null;
let barChartInstance = null;
let lineChartInstance = null;

const goBack = () => {
  router.push('/home');
};

const loadOverviewStats = async () => {
  try {
    const res = await axios.get('/api/stats/overview');
    overviewStats.value = res.data;
  } catch (error) {
    console.error('加载概览数据失败:', error);
  }
};

const loadBehaviorTypeChart = async () => {
  try {
    const res = await axios.get('/api/stats/behavior-type');
    const data = res.data.data || [];
    
    if (data.length === 0) {
      data.push({ name: '暂无数据', value: 1 });
    }

    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '行为类型',
          type: 'pie',
          radius: '50%',
          data: data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };

    pieChartInstance.setOption(option);
  } catch (error) {
    console.error('加载行为类型统计失败:', error);
  }
};

const loadTopItemsChart = async () => {
  try {
    const res = await axios.get('/api/stats/top-items');
    const itemIds = res.data.itemIds || ['商品1','商品2','商品3','商品4','商品5','商品6','商品7','商品8','商品9','商品10'];
    const counts = res.data.counts || [0,0,0,0,0,0,0,0,0,0];

    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        data: itemIds,
        axisLabel: {
          rotate: 45
        }
      },
      yAxis: {
        type: 'value',
        name: '操作次数',
        min: 0,
        axisLabel: {
          formatter: '{value} 次'
        }
      },
      series: [
        {
          name: '操作次数',
          type: 'bar',
          data: counts,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#667eea' },
              { offset: 1, color: '#764ba2' }
            ])
          }
        }
      ]
    };

    barChartInstance.setOption(option);
  } catch (error) {
    console.error('加载热门商品统计失败:', error);
  }
};

const loadHourlyChart = async () => {
  try {
    const res = await axios.get('/api/stats/hourly');
    const hours = res.data.hours || ['0时','1时','2时','3时','4时','5时','6时','7时','8时','9时','10时','11时','12时','13时','14时','15时','16时','17时','18时','19时','20时','21时','22时','23时'];
    const counts = res.data.counts || [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];

    const option = {
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: hours
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '行为数量',
          type: 'line',
          data: counts,
          smooth: true,
          lineStyle: {
            width: 3,
            color: '#667eea'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(102,126,234,0.5)' },
              { offset: 1, color: 'rgba(102,126,234,0.1)' }
            ])
          },
          itemStyle: {
            color: '#764ba2'
          }
        }
      ]
    };

    lineChartInstance.setOption(option);
  } catch (error) {
    console.error('加载时段统计失败:', error);
  }
};

onMounted(() => {
  pieChartInstance = echarts.init(pieChart.value);
  barChartInstance = echarts.init(barChart.value);
  lineChartInstance = echarts.init(lineChart.value);

  loadOverviewStats();
  loadBehaviorTypeChart();
  loadTopItemsChart();
  loadHourlyChart();

  window.addEventListener('resize', () => {
    pieChartInstance.resize();
    barChartInstance.resize();
    lineChartInstance.resize();
  });
});

onUnmounted(() => {
  pieChartInstance?.dispose();
  barChartInstance?.dispose();
  lineChartInstance?.dispose();
});
</script>

<style scoped>
.statistics-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 32px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.logo {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
}

.back-btn {
  padding: 8px 20px;
  background: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #e0e0e0;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 24px 32px;
}

.overview-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.overview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.users-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.items-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.records-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.purchases-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-content {
  flex: 1;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.card-label {
  font-size: 14px;
  color: #666;
}

.charts-wrapper {
  padding: 32px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 16px;
  padding-left: 8px;
  border-left: 4px solid #667eea;
}

.chart {
  width: 100%;
  height: 350px;
}
</style>