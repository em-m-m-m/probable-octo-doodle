<template>
  <div class="data-management">
    <div class="header">
      <div class="logo">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
          <polyline points="14 2 14 8 20 8"></polyline>
          <line x1="16" y1="13" x2="8" y2="13"></line>
          <line x1="16" y1="17" x2="8" y2="17"></line>
          <polyline points="10 9 9 9 8 9"></polyline>
        </svg>
      </div>
      <span class="title">数据管理</span>
      <button class="back-btn" @click="goBack">返回首页</button>
    </div>

    <div class="content">
      <div class="toolbar">
        <div class="search-section">
          <input 
            v-model="searchForm.userId" 
            type="number" 
            placeholder="按用户ID搜索" 
            class="search-input"
          />
          <select v-model="searchForm.behaviorType" class="search-select">
            <option value="">所有行为类型</option>
            <option value="浏览">浏览</option>
            <option value="收藏">收藏</option>
            <option value="加购">加购</option>
            <option value="购买">购买</option>
          </select>
          <button class="search-btn" @click="loadData">搜索</button>
          <button class="reset-btn" @click="resetSearch">重置</button>
        </div>
        <button class="add-btn" @click="openAddModal">+ 新增记录</button>
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户ID</th>
              <th>商品ID</th>
              <th>行为类型</th>
              <th>时间戳</th>
              <th>商品类别</th>
              <th>用户位置</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in tableData" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.userId }}</td>
              <td>{{ item.itemId }}</td>
              <td>
                <span :class="['behavior-badge', item.behaviorType]">
                  {{ item.behaviorType }}
                </span>
              </td>
              <td>{{ formatTimestamp(item.timestamp) }}</td>
              <td>{{ item.category }}</td>
              <td>{{ item.location }}</td>
              <td>
                <button class="edit-btn" @click="openEditModal(item)">编辑</button>
                <button class="delete-btn" @click="openDeleteModal(item.id)">删除</button>
              </td>
            </tr>
            <tr v-if="tableData.length === 0">
              <td colspan="8" class="empty-message">暂无数据</td>
            </tr>
          </tbody>
        </table>

        <div class="pagination" v-if="totalElements > 0">
          <button 
            class="page-btn" 
            :disabled="currentPage === 0" 
            @click="prevPage"
          >
            上一页
          </button>
          <span class="page-info">
            第 {{ currentPage + 1 }} / {{ totalPages }} 页
          </span>
          <button 
            class="page-btn" 
            :disabled="currentPage >= totalPages - 1" 
            @click="nextPage"
          >
            下一页
          </button>
          <div class="page-size-select">
            <span>每页:</span>
            <select v-model="pageSize" @change="loadData">
              <option :value="10">10条</option>
              <option :value="20">20条</option>
              <option :value="50">50条</option>
              <option :value="100">100条</option>
            </select>
          </div>
          <span class="total-info">共 {{ totalElements }} 条记录</span>
        </div>
      </div>
    </div>

    <div class="modal-overlay" v-if="showAddModal" @click="closeAddModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>新增记录</h3>
          <button class="close-btn" @click="closeAddModal">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>用户ID *</label>
            <input v-model="addForm.userId" type="number" class="form-input" />
          </div>
          <div class="form-group">
            <label>商品ID *</label>
            <input v-model="addForm.itemId" type="number" class="form-input" />
          </div>
          <div class="form-group">
            <label>行为类型 *</label>
            <select v-model="addForm.behaviorType" class="form-select">
              <option value="浏览">浏览</option>
              <option value="收藏">收藏</option>
              <option value="加购">加购</option>
              <option value="购买">购买</option>
            </select>
          </div>
          <div class="form-group">
            <label>时间戳 *</label>
            <input v-model="addForm.timestamp" type="number" class="form-input" placeholder="Unix时间戳" />
          </div>
          <div class="form-group">
            <label>商品类别 *</label>
            <input v-model="addForm.category" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label>用户位置 *</label>
            <input v-model="addForm.location" type="text" class="form-input" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeAddModal">取消</button>
          <button class="submit-btn" @click="submitAdd">提交</button>
        </div>
      </div>
    </div>

    <div class="modal-overlay" v-if="showEditModal" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>编辑记录</h3>
          <button class="close-btn" @click="closeEditModal">✕</button>
        </div>
        <div class="modal-body">
          <input type="hidden" v-model="editForm.id" />
          <div class="form-group">
            <label>用户ID *</label>
            <input v-model="editForm.userId" type="number" class="form-input" />
          </div>
          <div class="form-group">
            <label>商品ID *</label>
            <input v-model="editForm.itemId" type="number" class="form-input" />
          </div>
          <div class="form-group">
            <label>行为类型 *</label>
            <select v-model="editForm.behaviorType" class="form-select">
              <option value="浏览">浏览</option>
              <option value="收藏">收藏</option>
              <option value="加购">加购</option>
              <option value="购买">购买</option>
            </select>
          </div>
          <div class="form-group">
            <label>时间戳 *</label>
            <input v-model="editForm.timestamp" type="number" class="form-input" />
          </div>
          <div class="form-group">
            <label>商品类别 *</label>
            <input v-model="editForm.category" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label>用户位置 *</label>
            <input v-model="editForm.location" type="text" class="form-input" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeEditModal">取消</button>
          <button class="submit-btn" @click="submitEdit">保存</button>
        </div>
      </div>
    </div>

    <div class="modal-overlay" v-if="showDeleteModal" @click="closeDeleteModal">
      <div class="modal-content delete-modal" @click.stop>
        <div class="modal-header">
          <h3>确认删除</h3>
          <button class="close-btn" @click="closeDeleteModal">✕</button>
        </div>
        <div class="modal-body">
          <p>确定要删除ID为 <span class="highlight">{{ deleteId }}</span> 的记录吗？</p>
          <p class="warning">此操作不可撤销！</p>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeDeleteModal">取消</button>
          <button class="delete-confirm-btn" @click="submitDelete">确认删除</button>
        </div>
      </div>
    </div>

    <div class="toast" :class="{ show: showToast }">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

const tableData = ref([]);
const currentPage = ref(0);
const pageSize = ref(10);
const totalElements = ref(0);
const totalPages = ref(0);

const searchForm = reactive({
  userId: '',
  behaviorType: ''
});

const addForm = reactive({
  userId: '',
  itemId: '',
  behaviorType: '浏览',
  timestamp: '',
  category: '',
  location: ''
});

const editForm = reactive({
  id: '',
  userId: '',
  itemId: '',
  behaviorType: '浏览',
  timestamp: '',
  category: '',
  location: ''
});

const showAddModal = ref(false);
const showEditModal = ref(false);
const showDeleteModal = ref(false);
const deleteId = ref(null);

const showToast = ref(false);
const toastMessage = ref('');

const goBack = () => {
  router.push('/home');
};

const loadData = async () => {
  try {
    const params = new URLSearchParams();
    params.set('page', currentPage.value);
    params.set('size', pageSize.value);
    if (searchForm.userId) params.set('userId', searchForm.userId);
    if (searchForm.behaviorType) params.set('behaviorType', searchForm.behaviorType);

    const res = await axios.get('/api/behavior/list?' + params.toString());
    tableData.value = res.data.content || [];
    totalElements.value = res.data.totalElements || 0;
    totalPages.value = res.data.totalPages || 0;
  } catch (error) {
    console.error('加载数据失败:', error);
    showToastMessage('加载数据失败');
  }
};

const resetSearch = () => {
  searchForm.userId = '';
  searchForm.behaviorType = '';
  currentPage.value = 0;
  loadData();
};

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--;
    loadData();
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++;
    loadData();
  }
};

const openAddModal = () => {
  addForm.userId = '';
  addForm.itemId = '';
  addForm.behaviorType = '浏览';
  addForm.timestamp = '';
  addForm.category = '';
  addForm.location = '';
  showAddModal.value = true;
};

const closeAddModal = () => {
  showAddModal.value = false;
};

const submitAdd = async () => {
  if (!addForm.userId || !addForm.itemId || !addForm.timestamp || !addForm.category || !addForm.location) {
    showToastMessage('请填写所有必填字段');
    return;
  }

  try {
    const res = await axios.post('/api/behavior/add', {
      userId: parseInt(addForm.userId),
      itemId: parseInt(addForm.itemId),
      behaviorType: addForm.behaviorType,
      timestamp: parseInt(addForm.timestamp),
      category: addForm.category,
      location: addForm.location
    });

    if (res.data.success) {
      showToastMessage('新增成功');
      closeAddModal();
      loadData();
    } else {
      showToastMessage(res.data.message);
    }
  } catch (error) {
    console.error('新增失败:', error);
    showToastMessage('新增失败');
  }
};

const openEditModal = (item) => {
  editForm.id = item.id;
  editForm.userId = item.userId;
  editForm.itemId = item.itemId;
  editForm.behaviorType = item.behaviorType;
  editForm.timestamp = item.timestamp;
  editForm.category = item.category;
  editForm.location = item.location;
  showEditModal.value = true;
};

const closeEditModal = () => {
  showEditModal.value = false;
};

const submitEdit = async () => {
  if (!editForm.userId || !editForm.itemId || !editForm.timestamp || !editForm.category || !editForm.location) {
    showToastMessage('请填写所有必填字段');
    return;
  }

  try {
    const res = await axios.put('/api/behavior/' + editForm.id, {
      userId: parseInt(editForm.userId),
      itemId: parseInt(editForm.itemId),
      behaviorType: editForm.behaviorType,
      timestamp: parseInt(editForm.timestamp),
      category: editForm.category,
      location: editForm.location
    });

    if (res.data.success) {
      showToastMessage('更新成功');
      closeEditModal();
      loadData();
    } else {
      showToastMessage(res.data.message);
    }
  } catch (error) {
    console.error('更新失败:', error);
    showToastMessage('更新失败');
  }
};

const openDeleteModal = (id) => {
  deleteId.value = id;
  showDeleteModal.value = true;
};

const closeDeleteModal = () => {
  showDeleteModal.value = false;
  deleteId.value = null;
};

const submitDelete = async () => {
  try {
    const res = await axios.delete('/api/behavior/' + deleteId.value);

    if (res.data.success) {
      showToastMessage('删除成功');
      closeDeleteModal();
      loadData();
    } else {
      showToastMessage(res.data.message);
    }
  } catch (error) {
    console.error('删除失败:', error);
    showToastMessage('删除失败');
  }
};

const showToastMessage = (message) => {
  toastMessage.value = message;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 2000);
};

const formatTimestamp = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleString('zh-CN');
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.data-management {
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

.content {
  padding: 24px 32px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  padding: 10px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  width: 150px;
}

.search-select {
  padding: 10px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
}

.search-btn, .reset-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.reset-btn {
  background: #f0f0f0;
  color: #666;
}

.reset-btn:hover {
  background: #e0e0e0;
}

.add-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(67, 233, 123, 0.4);
}

.table-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th, .data-table td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.data-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.data-table tbody tr:hover {
  background: #f8f9fa;
}

.empty-message {
  text-align: center;
  color: #999;
  padding: 40px;
}

.behavior-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.behavior-badge.浏览 {
  background: #e3f2fd;
  color: #1976d2;
}

.behavior-badge.收藏 {
  background: #fce4ec;
  color: #c2185b;
}

.behavior-badge.加购 {
  background: #fff3e0;
  color: #f57c00;
}

.behavior-badge.购买 {
  background: #e8f5e9;
  color: #388e3c;
}

.edit-btn, .delete-btn {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-right: 8px;
}

.edit-btn {
  background: #667eea;
  color: white;
}

.edit-btn:hover {
  background: #5a6fd6;
}

.delete-btn {
  background: #ff5252;
  color: white;
}

.delete-btn:hover {
  background: #e53935;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.page-btn {
  padding: 8px 16px;
  background: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info, .total-info {
  font-size: 14px;
  color: #666;
}

.page-size-select {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.page-size-select select {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
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
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
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

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-input, .form-select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #f0f0f0;
}

.cancel-btn, .submit-btn, .delete-confirm-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #f0f0f0;
  color: #666;
}

.cancel-btn:hover {
  background: #e0e0e0;
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.delete-confirm-btn {
  background: #ff5252;
  color: white;
}

.delete-confirm-btn:hover {
  background: #e53935;
}

.delete-modal .modal-body {
  text-align: center;
}

.delete-modal .highlight {
  color: #ff5252;
  font-weight: 700;
  font-size: 18px;
}

.delete-modal .warning {
  color: #ff5252;
  margin-top: 12px;
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