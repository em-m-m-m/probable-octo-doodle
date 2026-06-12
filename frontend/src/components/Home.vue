<template>
  <div class="dashboard-container">
    <header class="dashboard-header">
      <div class="header-left">
        <div class="logo">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
        </div>
        <div class="title-section">
          <h1 class="main-title">数据仪表盘</h1>
          <p class="sub-title">实时监控用户行为数据</p>
        </div>
      </div>
      <div class="header-right">
        <button class="action-btn refresh-btn" @click="refreshData">
          <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 4 23 10 17 10"/>
            <polyline points="12 20 12 14 8 14"/>
            <path d="M20.49 9H14a6 6 0 0 0-5.95 5.05L7 19l4 1 1-4-1.05-5.95A6 6 0 0 0 14 5h6.49"/>
          </svg>
          刷新
        </button>
        <button class="action-btn logout-btn" @click="handleLogout">
          <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          退出登录
        </button>
      </div>
    </header>

    <main class="dashboard-content">
      <section class="stats-section">
        <div class="stats-grid">
          <div class="stat-card users-card">
            <div class="stat-icon">👥</div>
            <div class="stat-info">
              <div class="stat-value">{{ overviewStats.totalUsers || 0 }}</div>
              <div class="stat-label">总用户数</div>
            </div>
            <div class="stat-trend positive">+12%</div>
          </div>
          <div class="stat-card items-card">
            <div class="stat-icon">📦</div>
            <div class="stat-info">
              <div class="stat-value">{{ overviewStats.totalItems || 0 }}</div>
              <div class="stat-label">总商品数</div>
            </div>
            <div class="stat-trend positive">+8%</div>
          </div>
          <div class="stat-card records-card">
            <div class="stat-icon">📊</div>
            <div class="stat-info">
              <div class="stat-value">{{ overviewStats.totalRecords || 0 }}</div>
              <div class="stat-label">总行为记录</div>
            </div>
            <div class="stat-trend positive">+15%</div>
          </div>
          <div class="stat-card purchases-card">
            <div class="stat-icon">🛒</div>
            <div class="stat-info">
              <div class="stat-value">{{ overviewStats.totalPurchases || 0 }}</div>
              <div class="stat-label">总购买数</div>
            </div>
            <div class="stat-trend negative">-2%</div>
          </div>
        </div>
      </section>

      <section class="charts-section">
        <div class="chart-row">
          <div class="chart-card pie-chart">
            <div class="chart-header">
              <h3>行为类型分布</h3>
            </div>
            <div ref="pieChart" class="chart-content"></div>
          </div>
          <div class="chart-card bar-chart">
            <div class="chart-header">
              <h3>热门商品TOP10</h3>
            </div>
            <div ref="barChart" class="chart-content"></div>
          </div>
        </div>
        <div class="chart-row">
          <div class="chart-card line-chart full-width">
            <div class="chart-header">
              <h3>时段活跃趋势</h3>
            </div>
            <div ref="lineChart" class="chart-content"></div>
          </div>
        </div>
      </section>

      <div class="bottom-section">
        <section class="recent-section">
          <div class="section-header">
            <h3>最新行为记录</h3>
            <button class="view-all-btn" @click="goToDataManagement">查看全部</button>
          </div>
          <div class="recent-table">
            <table>
              <thead>
                <tr>
                  <th>用户ID</th>
                  <th>商品ID</th>
                  <th>行为类型</th>
                  <th>时间</th>
                  <th>位置</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="record in recentRecords" :key="record.id">
                  <td>{{ record.userId }}</td>
                  <td>{{ record.itemId }}</td>
                  <td>
                    <el-tag :type="getBehaviorTypeTag(record.behaviorType)">
                      {{ record.behaviorType }}
                    </el-tag>
                  </td>
                  <td>{{ formatTimestamp(record.timestamp) }}</td>
                  <td>{{ record.location }}</td>
                </tr>
                <tr v-if="recentRecords.length === 0">
                  <td colspan="5" class="empty-row">暂无数据</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section class="actions-section">
          <div class="section-header">
            <h3>快捷操作</h3>
          </div>
          <div class="action-grid">
            <button class="action-card import-card" @click="showUpload = true">
              <div class="action-icon">📁</div>
              <div class="action-text">导入数据</div>
              <div class="action-desc">从CSV导入用户行为数据</div>
            </button>
            <button class="action-card manage-card" @click="goToDataManagement">
              <div class="action-icon">📋</div>
              <div class="action-text">管理数据</div>
              <div class="action-desc">查看和管理所有记录</div>
            </button>

          </div>
        </section>
      </div>
    </main>

    <div class="modal-overlay" v-if="showUpload" @click="showUpload = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>CSV数据导入</h3>
          <button class="close-btn" @click="showUpload = false">✕</button>
        </div>
        <div class="upload-area" @click="triggerFileInput" @dragover.prevent @drop.prevent="handleDrop">
          <input 
            type="file" 
            ref="fileInput" 
            class="file-input" 
            accept=".csv" 
            @change="handleFileSelect"
          />
          <div class="upload-icon">📁</div>
          <p class="upload-text">{{ selectedFile || '点击或拖拽CSV文件到此处' }}</p>
          <p class="upload-hint">支持 .csv 格式文件</p>
        </div>
        <button class="upload-btn" :disabled="!selectedFile || uploading" @click="uploadFile">
          <span v-if="uploading" class="spinner"></span>
          <span>{{ uploading ? '导入中...' : '开始导入' }}</span>
        </button>
        <div v-if="uploadResult" :class="['result-message', uploadResult.success ? 'success' : 'error']">
          <span class="result-icon">{{ uploadResult.success ? '✓' : '✗' }}</span>
          <span class="result-text">{{ uploadResult.message }}</span>
          <span v-if="uploadResult.count" class="result-count">共导入 {{ uploadResult.count }} 条记录</span>
        </div>
      </div>
    </div>

    <div class="toast" :class="{ show: showToast }">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import * as echarts from 'echarts'

const router = useRouter()

const overviewStats = reactive({
  totalUsers: 0,
  totalItems: 0,
  totalRecords: 0,
  totalPurchases: 0
})

const recentRecords = ref([])

const pieChart = ref(null)
const barChart = ref(null)
const lineChart = ref(null)

let pieChartInstance = null
let barChartInstance = null
let lineChartInstance = null

const showUpload = ref(false)
const fileInput = ref(null)
const selectedFile = ref('')
const uploading = ref(false)
const uploadResult = ref(null)

const showToast = ref(false)
const toastMessage = ref('')

const getBehaviorTypeTag = (type) => {
  const tags = {
    '浏览': 'info',
    '收藏': 'warning',
    '加购': 'primary',
    '购买': 'success'
  }
  return tags[type] || 'default'
}

const formatTimestamp = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN')
}

const goToDataManagement = () => {
  router.push('/data-management')
}

const handleLogout = () => {
  localStorage.removeItem('login_username')
  localStorage.removeItem('login_password')
  router.push('/login')
}

const refreshData = () => {
  loadOverviewStats()
  loadRecentRecords()
  loadCharts()
  showToastMessage('数据已刷新')
}

const showToastMessage = (message) => {
  toastMessage.value = message
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const loadOverviewStats = async () => {
  try {
    const res = await axios.get('/api/stats/overview')
    Object.assign(overviewStats, res.data)
  } catch (error) {
    console.error('加载概览数据失败:', error)
  }
}

const loadRecentRecords = async () => {
  try {
    const res = await axios.get('/api/stats/recent')
    recentRecords.value = res.data.data.slice(0, 5) || []
  } catch (error) {
    console.error('加载最新记录失败:', error)
  }
}

const loadCharts = async () => {
  await loadPieChart()
  await loadBarChart()
  await loadLineChart()
}

const loadPieChart = async () => {
  try {
    const res = await axios.get('/api/stats/behavior-type')
    const data = res.data.data || []
    
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'horizontal',
        bottom: '5%'
      },
      series: [{
        name: '行为类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{d}%'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: true
        },
        data: data.map((item, index) => ({
          ...item,
          itemStyle: {
            color: ['#667eea', '#f093fb', '#4facfe', '#43e97b'][index % 4]
          }
        }))
      }]
    }
    
    pieChartInstance.setOption(option)
  } catch (error) {
    console.error('加载饼图失败:', error)
  }
}

const loadBarChart = async () => {
  try {
    const res = await axios.get('/api/stats/top-items')
    const itemIds = res.data.itemIds || []
    const counts = res.data.counts || []
    
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
        data: itemIds,
        axisLabel: {
          rotate: 45,
          fontSize: 11
        }
      },
      yAxis: {
        type: 'value',
        name: '操作次数'
      },
      series: [{
        name: '操作次数',
        type: 'bar',
        barWidth: '50%',
        data: counts,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }]
    }
    
    barChartInstance.setOption(option)
  } catch (error) {
    console.error('加载柱状图失败:', error)
  }
}

const loadLineChart = async () => {
  try {
    const res = await axios.get('/api/stats/hourly')
    const hours = res.data.hours || []
    const counts = res.data.counts || []
    
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: hours
      },
      yAxis: {
        type: 'value',
        name: '行为数量'
      },
      series: [{
        name: '活跃数',
        type: 'line',
        smooth: true,
        data: counts,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
            { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
          ])
        },
        lineStyle: {
          color: '#667eea',
          width: 3
        },
        itemStyle: {
          color: '#667eea'
        }
      }]
    }
    
    lineChartInstance.setOption(option)
  } catch (error) {
    console.error('加载折线图失败:', error)
  }
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event) => {
  const file = event.target.files?.[0]
  if (file) {
    selectedFile.value = file.name
    uploadResult.value = null
  }
}

const handleDrop = (event) => {
  const file = event.dataTransfer.files?.[0]
  if (file && file.name.toLowerCase().endsWith('.csv')) {
    selectedFile.value = file.name
    uploadResult.value = null
    fileInput.value.files = event.dataTransfer.files
  }
}

const uploadFile = async () => {
  const file = fileInput.value?.files?.[0]
  if (!file) return

  uploading.value = true
  uploadResult.value = null

  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await fetch('/api/import/csv', {
      method: 'POST',
      body: formData
    })
    
    const data = await response.json()
    uploadResult.value = data
    if (data.success) {
      refreshData()
    }
  } catch (error) {
    uploadResult.value = {
      success: false,
      message: '导入失败: ' + error.message
    }
  } finally {
    uploading.value = false
  }
}

const handleResize = () => {
  pieChartInstance?.resize()
  barChartInstance?.resize()
  lineChartInstance?.resize()
}

onMounted(() => {
  pieChartInstance = echarts.init(pieChart.value)
  barChartInstance = echarts.init(barChart.value)
  lineChartInstance = echarts.init(lineChart.value)
  
  loadOverviewStats()
  loadRecentRecords()
  loadCharts()
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChartInstance?.dispose()
  barChartInstance?.dispose()
  lineChartInstance?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
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

.logo svg {
  width: 24px;
  height: 24px;
}

.title-section {
  display: flex;
  flex-direction: column;
}

.main-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

.sub-title {
  font-size: 13px;
  color: #999;
  margin: 4px 0 0;
}

.header-right {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-icon {
  width: 18px;
  height: 18px;
}

.refresh-btn {
  background: #f0f0f0;
  color: #333;
}

.refresh-btn:hover {
  background: #e0e0e0;
}

.logout-btn {
  background: linear-gradient(135deg, #ff5252 0%, #ff1744 100%);
  color: white;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 82, 82, 0.4);
}

.dashboard-content {
  padding: 24px 32px;
}

.stats-section {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.users-card::before {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.items-card::before {
  background: linear-gradient(180deg, #f093fb 0%, #f5576c 100%);
}

.records-card::before {
  background: linear-gradient(180deg, #4facfe 0%, #00f2fe 100%);
}

.purchases-card::before {
  background: linear-gradient(180deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: #f8f9fa;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.stat-trend {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 12px;
}

.stat-trend.positive {
  background: #e8f5e9;
  color: #388e3c;
}

.stat-trend.negative {
  background: #ffebee;
  color: #c62828;
}

.charts-section {
  margin-bottom: 24px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart-row:last-child {
  margin-bottom: 0;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.chart-card.full-width {
  grid-column: span 2;
}

.chart-header {
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.chart-content {
  height: 280px;
}

.bottom-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.view-all-btn {
  padding: 6px 16px;
  background: #f0f0f0;
  color: #667eea;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.view-all-btn:hover {
  background: #e8eaf6;
}

.recent-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.recent-table {
  overflow-x: auto;
}

.recent-table table {
  width: 100%;
  border-collapse: collapse;
}

.recent-table th,
.recent-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.recent-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
  font-size: 13px;
}

.recent-table td {
  font-size: 14px;
  color: #666;
}

.recent-table .empty-row {
  text-align: center;
  color: #999;
}

.actions-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.action-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
}

.action-card:hover {
  transform: translateX(8px);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.import-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.import-card .action-icon {
  background: rgba(255, 255, 255, 0.2);
}

.manage-card {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.manage-card .action-icon {
  background: rgba(255, 255, 255, 0.2);
}

.action-text {
  font-size: 16px;
  font-weight: 600;
  flex: 1;
}

.action-desc {
  font-size: 12px;
  opacity: 0.8;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 32px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  color: #1a1a2e;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
  padding: 4px;
}

.close-btn:hover {
  color: #333;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 12px;
  padding: 40px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
  text-align: center;
}

.upload-area:hover {
  border-color: #667eea;
  background: #f5f7ff;
}

.file-input {
  display: none;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.upload-text {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 14px;
  color: #999;
}

.upload-btn {
  margin-top: 20px;
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.upload-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.upload-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.result-message {
  margin-top: 20px;
  padding: 16px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.result-message.success {
  background: #d1fae5;
  color: #065f46;
}

.result-message.error {
  background: #fee2e2;
  color: #991b1b;
}

.result-icon {
  font-size: 20px;
  font-weight: bold;
}

.result-text {
  font-size: 14px;
}

.result-count {
  font-size: 14px;
  font-weight: 600;
}

.toast {
  position: fixed;
  top: 80px;
  right: 32px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 14px 24px;
  border-radius: 8px;
  font-size: 14px;
  opacity: 0;
  transform: translateX(100%);
  transition: all 0.3s ease;
  z-index: 2000;
}

.toast.show {
  opacity: 1;
  transform: translateX(0);
}
</style>