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
						<el-form-item label="课题名称">
							<el-input v-model="pagination.pTitle" placeholder="请输入" />
						</el-form-item>
						<el-form-item class="search-footer">
							<el-button @click="clearSearchForm">清空</el-button>
							<el-button type="primary" @click="searchData">搜索</el-button>
						</el-form-item>
					</el-form>
				</el-popover>
			</div>
		</div>

		<el-table v-loading="loading" :data="projectData" style="width: 100%" border stripe @row-dblclick="handleRowDblclick">
			<el-table-column prop="PId" label="课题编号" min-width="80" />
			<el-table-column prop="pTitle" label="课题名称" min-width="150" />
			<el-table-column prop="createdAt" label="选上时间" min-width="150">
				<template #default="{ row }">
					{{ formatDate(row.createdAt) }}
				</template>
			</el-table-column>

			<el-table-column label="操作" min-width="260" fixed="right">
				<template #default="{ row }">
					<el-button type="info" size="small" @click="viewProjectDetail(row)">详情</el-button>
					<el-button type="success" size="small" @click="goToProjectDetail(row)">详细详情</el-button>
					<el-button type="danger" size="small" @click="quitProject(row)">退出课题</el-button>
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

	<!-- 课题详情对话框 -->
	<el-dialog v-model="detailDialogVisible" title="课题详情" width="500px">
		<div v-if="currentProject" class="project-detail">
			<div class="mb-4 p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">项目封面</h3>
				<div class="flex justify-center mb-4">
					<img v-if="currentProject.pCover" :src="fileShowUrl + currentProject.pCover" class="w-32 h-32 object-cover rounded border" alt="课题封面" />
					<div v-else class="w-32 h-32 flex items-center justify-center bg-gray-100 rounded">暂无封面</div>
				</div>
			</div>
			<div class="mb-4 p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">基本信息</h3>
				<div class="grid grid-cols-2 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">课题编号</div>
						<div class="font-medium">{{ currentProject.PId }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">课题名称</div>
						<div class="font-medium">{{ currentProject.pTitle }}</div>
					</div>
				</div>
			</div>
			<div class="mb-4 p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">详细信息</h3>
				<div class="grid grid-cols-1 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">课题描述</div>
						<div class="font-medium">{{ currentProject.PDescription || '未设置' }}</div>
					</div>

					<div class="text-sm">
						<div class="text-gray-500">最大学生数</div>
						<div class="font-medium">{{ currentProject.PMaxStudents }}</div>
					</div>
				</div>
			</div>
			<div class="p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">时间信息</h3>
				<div class="grid grid-cols-1 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">创建时间</div>
						<div class="font-medium">{{ formatDate(currentProject.createdAt) }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">更新时间</div>
						<div class="font-medium">{{ formatDate(currentProject.updatedAt) }}</div>
					</div>
				</div>
			</div>
		</div>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { postProjectsFind } from '@/apis/projects';
import { postTagsSelect } from '@/apis/tags';
import useSysStore from '@/store/sys';
import { postProjectStudentsFindAll, postProjectStudentsDelete } from '@/apis';
defineOptions({
	name: 'ProjectManage',
});

// 退出课题
async function quitProject(row: any) {
	ElMessageBox.confirm(`确定要退出课题 ${row.pTitle} 吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				await postProjectStudentsDelete({
					data: { psId: row.psId },
				});
				ElMessage.success('已退出课题');
				searchData();
			} catch (error) {
				ElMessage.error('退出课题失败');
			}
		})
		.catch(() => {});
}

const router = useRouter();

const fileShowUrl = import.meta.env.VITE_APP_FILE_SHOW_URL;

// 分页和查询参数
const pagination = ref<{
	pageIndex: number;
	pageSize: number;
	pId?: number;
	pTitle?: string;
	pDirection?: string;
	pDifficulty?: number;
	uId?: number;
}>({
	pageIndex: 1,
	pageSize: 10,
});

// 课题数据
const projectData = ref([]);
const total = ref(0);
const loading = ref(false);
const visible = ref(false);

const detailDialogVisible = ref(false);

const currentProject = ref(null);

const tagLoading = ref(false);
const tagOptions = ref<Array<{ TId: number; TName: string }>>([]);

async function fetchTagOptions() {
	tagLoading.value = true;
	try {
		const { data } = await postTagsSelect();
		if (data && Array.isArray(data)) {
			tagOptions.value = data;
		} else {
			tagOptions.value = [];
		}
	} catch (error) {
		console.error('获取标签失败:', error);
		tagOptions.value = [];
	} finally {
		tagLoading.value = false;
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

// 加载课题数据
async function searchData() {
	loading.value = true;
	try {
		const params = filterEmptyParams({
			pageIndex: pagination.value.pageIndex,
			pageSize: pagination.value.pageSize,
			pId: pagination.value.pId,
			pTitle: pagination.value.pTitle,
			uId: useSysStore().userInfo.UId,
		});

		const { data } = await postProjectStudentsFindAll({
			data: params,
		});

		if (data) {
			projectData.value = data.list || [];
			total.value = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取课题数据失败');
		console.error('获取课题数据失败:', error);
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

// 查看课题详情
async function viewProjectDetail(row: any) {
	try {
		const params = filterEmptyParams({
			pId: row.PId,
		});
		const { data } = await postProjectsFind(params);
		if (data) {
			currentProject.value = data;
			detailDialogVisible.value = true;
		}
	} catch (error) {
		ElMessage.error('获取课题详情失败');
	}
}

// 跳转到课题详情页面
function goToProjectDetail(row: any) {
	router.push(`/entrance/project-detail/${row.PId}`);
}

// 双击行查看详情
function handleRowDblclick(row: any) {
	viewProjectDetail(row);
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
	fetchTagOptions();
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
