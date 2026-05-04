<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header flex items-center justify-between mb-4">
			<h1 class="text-2xl font-bold text-gray-800">日志管理</h1>
			<el-popover :width="420" :visible="visible" :teleported="false" placement="bottom-start">
				<template #reference>
					<el-button :icon="Search" @click="visible = !visible">检索</el-button>
				</template>
				<el-form label-position="top" :model="searchForm" @keyup.enter="searchData">
					<el-form-item label="日志ID">
						<el-input v-model="searchForm.logId" placeholder="请输入日志ID" clearable />
					</el-form-item>
					<el-form-item label="请求URL">
						<el-input v-model="searchForm.requestUrl" placeholder="请输入请求URL" clearable />
					</el-form-item>
					<el-form-item label="用户ID">
						<el-input v-model="searchForm.uId" placeholder="请输入用户ID" clearable />
					</el-form-item>
					<el-form-item label="用户名">
						<el-input v-model="searchForm.uName" placeholder="请输入用户名" clearable />
					</el-form-item>
					<el-form-item class="search-footer">
						<el-button @click="clearSearchForm">清空</el-button>
						<el-button type="primary" @click="searchData">搜索</el-button>
					</el-form-item>
				</el-form>
			</el-popover>
		</div>

		<el-table :data="logData" style="width: 100%" border stripe v-loading="loading">
			<el-table-column prop="logId" label="日志ID" min-width="100" />
			<el-table-column prop="uName" label="用户名" min-width="120" />
			<el-table-column prop="UId" label="用户ID" min-width="120" />
			<el-table-column prop="requestUrl" label="请求URL" min-width="220" />
			<el-table-column prop="createdAt" label="操作时间" min-width="160" />
			<el-table-column label="操作" min-width="120" fixed="right">
				<template #default="{ row }">
					<el-button type="primary" @click="viewLogDetail(row)">查看</el-button>
				</template>
			</el-table-column>
		</el-table>

		<div class="mt-4 text-center">
			<el-pagination
				:current-page="pagination.pageIndex"
				:page-size="pagination.pageSize"
				:total="total"
				layout="total, prev, pager, next, jumper"
				@current-change="handlePageChange"
			/>
		</div>

		<!-- 日志详情对话框 -->
		<el-dialog v-model="detailDialogVisible" title="日志详情" width="50%">
			<div v-if="currentLog" class="log-detail">
				<div class="detail-item">
					<span class="label">日志ID：</span>
					<span class="value">{{ currentLog.logId }}</span>
				</div>
				<div class="detail-item">
					<span class="label">用户名：</span>
					<span class="value">{{ currentLog.uName }}</span>
				</div>
				<div class="detail-item">
					<span class="label">用户ID：</span>
					<span class="value">{{ currentLog.uId }}</span>
				</div>
				<div class="detail-item">
					<span class="label">请求URL：</span>
					<span class="value">{{ currentLog.requestUrl }}</span>
				</div>
				<div class="detail-item">
					<span class="label">操作时间：</span>
					<span class="value">{{ currentLog.createdAt }}</span>
				</div>
				<div class="detail-item content">
					<span class="label">日志内容：</span>
					<div class="value content-text break-all whitespace-pre-line" style="padding: 0; background: none">
						<vue-json-pretty :data="currentLog" :deep="2" :showLine="false" />
					</div>
				</div>
			</div>
		</el-dialog>
	</div>
</template>
<template>
  <div class="log-filter">
    <!-- 筛选表单 -->
    <el-form :inline="true" :model="filterForm" class="filter-form">
      <el-form-item label="操作人">
        <el-input v-model="filterForm.operator" placeholder="请输入操作人" clearable />
      </el-form-item>
      <el-form-item label="操作类型">
        <el-select v-model="filterForm.operationType" placeholder="请选择" clearable>
          <el-option label="登录" value="login" />
          <el-option label="查询" value="query" />
          <el-option label="新增" value="insert" />
          <el-option label="修改" value="update" />
          <el-option label="删除" value="delete" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="filterForm.keyword" placeholder="搜索日志内容" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="searchLogs">查询</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 日志表格 -->
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="logId" label="日志ID" width="80" />
      <el-table-column prop="uName" label="操作人" width="120" />
      <el-table-column prop="requestUrl" label="操作类型" width="150" />
      <el-table-column prop="content" label="日志内容" min-width="300" show-overflow-tooltip />
      <el-table-column prop="createTime" label="操作时间" width="180" />
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pageIndex"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

// 筛选表单
const filterForm = reactive({
  operator: '',
  operationType: '',
  keyword: ''
})

const dateRange = ref<string[]>([])

// 表格数据
const tableData = ref<any[]>([])
const total = ref(0)
const pageIndex = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 搜索日志
const searchLogs = async () => {
  loading.value = true
  try {
    const params = {
      operator: filterForm.operator,
      operationType: filterForm.operationType,
      startTime: dateRange.value?.[0] || '',
      endTime: dateRange.value?.[1] || '',
      keyword: filterForm.keyword,
      pageIndex: pageIndex.value.toString(),
      pageSize: pageSize.value.toString()
    }
    const res = await axios.post('/api/logs/filter', params)
    if (res.data.code === 200) {
      tableData.value = res.data.data?.list || []
      total.value = res.data.data?.total || 0
    } else {
      ElMessage.error(res.data.msg || '查询失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('请求失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  filterForm.operator = ''
  filterForm.operationType = ''
  filterForm.keyword = ''
  dateRange.value = []
  pageIndex.value = 1
  pageSize.value = 10
  searchLogs()
}

// 分页变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  searchLogs()
}

const handleCurrentChange = (val: number) => {
  pageIndex.value = val
  searchLogs()
}

// 初始化加载
searchLogs()
</script>

<style scoped>
.log-filter {
  padding: 20px;
}
.filter-form {
  margin-bottom: 20px;
}
</style>
<script setup lang="ts">
import VueJsonPretty from 'vue-json-pretty';
import 'vue-json-pretty/lib/styles.css';
import { ref, reactive, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { postLogsFindAll, postLogsFind } from '@/apis/logs';

defineOptions({ name: 'LogManage' });

const loading = ref(false);
const visible = ref(false);
const detailDialogVisible = ref(false);
const logData = ref<any[]>([]);
const total = ref(0);
const currentLog = ref<any>(null);
const pagination = reactive({
	pageIndex: 1,
	pageSize: 10,
});
const searchForm = reactive({
	logId: '',
	requestUrl: '',
	uId: '',
	uName: '',
});

const searchData = async () => {
	loading.value = true;
	try {
		const params = {
			pageIndex: pagination.pageIndex,
			pageSize: pagination.pageSize,
			logId: searchForm.logId || undefined,
			requestUrl: searchForm.requestUrl || undefined,
			uId: searchForm.uId || undefined,
			uName: searchForm.uName || undefined,
		};
		const res = await postLogsFindAll(params);
		if (res?.data) {
			logData.value = res.data.list || [];
			total.value = res.data.totalRecords || 0;
		}
	} catch (e) {
		ElMessage.error('获取日志失败');
	} finally {
		loading.value = false;
		visible.value = false;
	}
};

const clearSearchForm = () => {
	searchForm.logId = '';
	searchForm.requestUrl = '';
	searchForm.uId = '';
	searchForm.uName = '';
	pagination.pageIndex = 1;
	searchData();
};

const handlePageChange = (page: number) => {
	pagination.pageIndex = page;
	searchData();
};

const viewLogDetail = async (row: any) => {
	loading.value = true;
	try {
		const res = await postLogsFind({ logId: row.logId });
		if (res?.data) {
			currentLog.value = res.data;
			detailDialogVisible.value = true;
		} else {
			ElMessage.error('未找到日志详情');
		}
	} catch (e) {
		ElMessage.error('获取日志详情失败');
	} finally {
		loading.value = false;
	}
};

onMounted(() => {
	searchData();
});
</script>

<style scoped>
.header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.detail-item {
	margin-bottom: 12px;
	display: flex;
}
.label {
	font-weight: 600;
	color: #444;
	min-width: 90px;
}
.value {
	color: #333;
}
.content-text {
	background: #f8fafc;
	padding: 12px;
	border-radius: 6px;
	margin-top: 4px;
	word-break: break-all;
}
</style>
