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
