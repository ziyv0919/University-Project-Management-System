<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header">
			<div class="header-left-toolbar"></div>
			<div class="header-right-search">
				<el-popover :width="400" :visible="visible" :teleported="false" placement="bottom-start">
					<template #reference>
						<el-button :icon="Search" @click="visible = !visible">检索</el-button>
					</template>
					<el-form label-position="top" label-width="100px" :model="pagination" @keyup.enter="searchData">
						<el-form-item label="申请编号">
							<el-input v-model="pagination.paId" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="课题名称">
							<el-select
								v-model="pagination.pId"
								filterable
								remote
								reserve-keyword
								placeholder="请输入课题名称搜索"
								:remote-method="remoteSearchProjects"
								:loading="projectLoading"
							>
								<el-option v-for="item in projectOptions" :key="item.value" :label="item.label" :value="item.value" />
							</el-select>
						</el-form-item>
						<el-form-item label="申请状态">
							<el-select v-model="pagination.paStatus" placeholder="请选择" clearable>
								<el-option :label="'审核中'" :value="1" />
								<el-option :label="'已通过'" :value="2" />
								<el-option :label="'已拒绝'" :value="3" />
							</el-select>
						</el-form-item>
						<el-form-item class="search-footer">
							<el-button @click="clearSearchForm">清空</el-button>
							<el-button type="primary" @click="searchData">搜索</el-button>
						</el-form-item>
					</el-form>
				</el-popover>
			</div>
		</div>

		<el-table v-loading="loading" :data="applicationData" style="width: 100%" border stripe @row-dblclick="handleRowDblclick">
			<el-table-column prop="paId" label="申请编号" min-width="120" />
			<el-table-column prop="pTitle" label="课题名称" min-width="150" />
			<el-table-column prop="uName" label="申请学生" min-width="120" />
			<el-table-column label="申请状态" min-width="100">
				<template #default="{ row }">
					<el-tag :type="row.paStatus === 2 ? 'success' : row.paStatus === 3 ? 'danger' : 'info'">
						{{ row.paStatus === 2 ? '已通过' : row.paStatus === 3 ? '已拒绝' : '审核中' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="createdAt" label="申请时间" min-width="150">
				<template #default="{ row }">
					{{ formatDate(row.createdAt) }}
				</template>
			</el-table-column>
			<el-table-column label="操作" min-width="250" fixed="right">
				<template #default="{ row }">
					<el-button type="danger" size="small" class="mr-2" @click="deleteApplication(row)"> 删除 </el-button>
					<el-button type="info" size="small" @click="viewApplicationDetail(row)"> 详情 </el-button>
				</template>
			</el-table-column>
		</el-table>

		<div class="mt-4 text-center">
			<el-pagination
				:current-page="pagination.pageIndex"
				:page-size="pagination.pageSize"
				:total="total"
				:page-sizes="[10, 20, 50, 100]"
				layout="total, sizes, prev, pager, next, jumper"
				@current-change="handleCurrentChange"
				@size-change="handleSizeChange"
			/>
		</div>
	</div>

	<!-- 申请详情对话框 -->
	<el-dialog v-model="detailDialogVisible" title="申请详情" width="500px">
		<div v-if="currentApplication" class="application-detail">
			<div class="mb-4 p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">基本信息</h3>
				<div class="grid grid-cols-2 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">申请编号</div>
						<div class="font-medium">{{ currentApplication.paId }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">申请状态</div>
						<div class="font-medium">
							<el-tag size="small" :type="currentApplication.paStatus === 2 ? 'success' : currentApplication.paStatus === 3 ? 'danger' : 'info'">
								{{ currentApplication.paStatus === 2 ? '已通过' : currentApplication.paStatus === 3 ? '已拒绝' : '审核中' }}
							</el-tag>
						</div>
					</div>
				</div>
			</div>
			<div class="mb-4 p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">课题信息</h3>
				<div class="grid grid-cols-1 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">课题编号</div>
						<div class="font-medium">{{ currentApplication.PId }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">课题名称</div>
						<div class="font-medium">{{ currentApplication.pTitle }}</div>
					</div>
				</div>
			</div>
			<div class="p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">申请学生</h3>
				<div class="grid grid-cols-1 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">学生ID</div>
						<div class="font-medium">{{ currentApplication.UId }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">学生姓名</div>
						<div class="font-medium">{{ currentApplication.uName }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">申请时间</div>
						<div class="font-medium">{{ formatDate(currentApplication.createdAt) }}</div>
					</div>
				</div>
			</div>
		</div>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { postProjectApplicationsDelete, postProjectApplicationsFind, postProjectApplicationsFindAll } from '@/apis/projectApplications';
import { postProjectsRemoteSelect } from '@/apis/projects';
import useSysStore from '@/store/sys';

defineOptions({
	name: 'ProjectApplication',
});

// 分页和查询参数
const pagination = ref<{
	pageIndex: number;
	pageSize: number;
	paId?: number;
	pTitle?: string;
	paStatus?: number;
	uId?: number;
	pId?: number;
}>({
	pageIndex: 1,
	pageSize: 10,
});

// 申请数据
const applicationData = ref([]);
const total = ref(0);
const loading = ref(false);
const visible = ref(false);

// 对话框控制
const detailDialogVisible = ref(false);
const currentApplication = ref(null);

// 课题远程搜索
const projectLoading = ref(false);
const projectOptions = ref<Array<{ value: number; label: string }>>([]);

// 远程搜索课题
async function remoteSearchProjects(query: string) {
	if (query) {
		projectLoading.value = true;
		try {
			const { data } = await postProjectsRemoteSelect({ searchText: query });
			if (data && Array.isArray(data)) {
				projectOptions.value = data.map((item) => ({
					value: item.PId,
					label: `${item.PTitle} (ID: ${item.PId})`,
				}));
			} else {
				projectOptions.value = [];
			}
		} catch (error) {
			console.error('搜索课题失败:', error);
			projectOptions.value = [];
		} finally {
			projectLoading.value = false;
		}
	} else {
		projectOptions.value = [];
	}
}

// 过滤空字符串参数
function filterEmptyParams(params: Record<string, any>) {
	const filteredParams: Record<string, any> = {};
	for (const key in params) {
		if (params[key] !== '' && params[key] !== undefined) {
			filteredParams[key] = params[key];
		}
	}
	return filteredParams;
}

// 加载申请数据
async function searchData() {
	loading.value = true;
	try {
		const params = filterEmptyParams({
			pageIndex: pagination.value.pageIndex,
			pageSize: pagination.value.pageSize,
			paId: pagination.value.paId,
			pId: pagination.value.pId,
			uId: useSysStore().userInfo.UId,
			paStatus: pagination.value.paStatus,
		});

		const { data } = await postProjectApplicationsFindAll(params);

		if (data) {
			applicationData.value = data.list || [];
			total.value = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取申请数据失败');
		console.error('获取申请数据失败:', error);
	} finally {
		loading.value = false;
	}
}

// 清空搜索表单
function clearSearchForm() {
	pagination.value = {
		pageIndex: 1,
		pageSize: 10,
	};
	searchData();
}

// 分页处理
const handleCurrentChange = (page: number) => {
	pagination.value.pageIndex = page;
	searchData();
};

const handleSizeChange = (size: number) => {
	pagination.value.pageSize = size;
	pagination.value.pageIndex = 1;
	searchData();
};

// 查看申请详情
async function viewApplicationDetail(row: any) {
	try {
		const params = filterEmptyParams({
			paId: row.paId,
		});
		const { data } = await postProjectApplicationsFind(params);
		if (data) {
			currentApplication.value = data;
			detailDialogVisible.value = true;
		}
	} catch (error) {
		ElMessage.error('获取申请详情失败');
	}
}

// 删除申请
function deleteApplication(row: any) {
	ElMessageBox.confirm(`确定要删除申请编号为 ${row.paId} 的申请吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				const params = filterEmptyParams({
					paId: row.paId,
				});
				await postProjectApplicationsDelete(params);
				ElMessage.success('删除成功');
				searchData();
			} catch (error) {
				ElMessage.error('删除失败');
			}
		})
		.catch(() => {});
}

// 双击行查看详情
function handleRowDblclick(row: any) {
	viewApplicationDetail(row);
}

// 格式化日期
function formatDate(dateString: string) {
	if (!dateString) return '-';
	try {
		const date = new Date(dateString);
		return date.toLocaleString('zh-CN', {
			year: 'numeric',
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit',
			second: '2-digit',
		});
	} catch (e) {
		return dateString;
	}
}

onMounted(() => {
	searchData();
});
</script>

<style lang="scss" scoped>
.header {
	height: 60px;
	width: 100%;
	display: flex;
	justify-content: space-between;
	align-items: center;

	.search-footer {
		:deep(.el-form-item__content) {
			display: flex;
			justify-content: flex-end;
		}
	}
}
.el-table {
	margin-bottom: 20px;
}

.el-pagination {
	text-align: center;
}
</style>
