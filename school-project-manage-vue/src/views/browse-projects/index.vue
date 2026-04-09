<template>
	<div class="min-h-full mx-auto p-6">
		<!-- 页面标题和搜索区域 -->
		<div class="mb-8">
			<h1 class="text-2xl font-bold text-gray-800 mb-4">浏览课题</h1>
			<div class="flex flex-wrap gap-4 items-center">
				<!-- 搜索框 -->
				<div class="flex-grow max-w-md">
					<el-input v-model="searchQuery" placeholder="搜索课题名称" :prefix-icon="Search" clearable @keyup.enter="handleSearch" />
				</div>
				<el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
			</div>
		</div>

		<!-- 课题卡片列表 -->
		<div v-loading="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
			<el-card
				v-for="project in projectList"
				:key="project.PId"
				class="project-card hover:shadow-lg transition-shadow duration-300"
				@click="viewProjectDetail(project)"
			>
				<template #header>
					<div class="flex justify-between items-center">
						<h3 class="text-lg font-medium truncate" :title="project.PTitle">{{ project.PTitle }}</h3>
						<el-tag size="small" :type="project.PDifficulty === 1 ? 'success' : project.PDifficulty === 2 ? 'warning' : 'danger'">
							{{ project.PDifficulty === 1 ? '简单' : project.PDifficulty === 2 ? '中等' : '困难' }}
						</el-tag>
					</div>
				</template>
				<div class="project-info">
					<p class="mb-2 text-gray-700">
						<el-icon><User /></el-icon>
						<span class="ml-1">课题编号: {{ project.PId }}</span>
					</p>
					<p class="mb-2 text-gray-700">
						<el-icon><User /></el-icon>
						<span class="ml-1">指导教师: {{ project.uName || '未知' }}</span>
					</p>
					<p class="mb-2 text-gray-700">
						<el-icon><School /></el-icon>
						<span class="ml-1">研究方向: {{ project.PDirection || '未设置' }}</span>
					</p>
					<p class="mb-4 text-gray-700">
						<el-icon><Calendar /></el-icon>
						<span class="ml-1">发布时间: {{ formatDate(project.createdAt) }}</span>
					</p>
					<div class="project-description mb-4">
						<p class="text-gray-600 line-clamp-3">{{ project.PDescription || '暂无描述' }}</p>
					</div>
				</div>
				<div class="flex justify-end mt-4">
					<el-button type="primary" text @click.stop="viewProjectDetail(project)">查看详情</el-button>
				</div>
			</el-card>
		</div>

		<!-- 分页 -->
		<div class="mt-8 flex justify-center">
			<el-pagination
				:current-page="pagination.pageIndex"
				:page-size="pagination.pageSize"
				:total="total"
				layout="total, prev, pager, next, jumper"
				@current-change="handlePageChange"
			/>
		</div>

		<!-- 课题详情对话框 -->
		<el-dialog v-model="dialogVisible" title="课题详情" width="60%">
			<template v-if="selectedProject">
				<div class="project-detail">
					<div class="mb-4 p-4 bg-gray-50 rounded-lg">
						<h3 class="text-lg font-medium text-gray-900 mb-2">项目封面</h3>
						<div class="flex justify-center mb-4">
							<img
								v-if="selectedProject.pCover"
								:src="fileShowUrl + selectedProject.pCover"
								class="w-32 h-32 object-cover rounded border"
								alt="课题封面"
							/>
							<div v-else class="w-32 h-32 flex items-center justify-center bg-gray-100 rounded">暂无封面</div>
						</div>
					</div>
					<div class="mb-4 p-4 bg-gray-50 rounded-lg">
						<h3 class="text-lg font-medium text-gray-900 mb-2">基本信息</h3>
						<div class="grid grid-cols-2 gap-4">
							<div class="text-sm">
								<div class="text-gray-500">课题编号</div>
								<div class="font-medium">{{ selectedProject.PId }}</div>
							</div>
							<div class="text-sm">
								<div class="text-gray-500">课题名称</div>
								<div class="font-medium">{{ selectedProject.PTitle }}</div>
							</div>
							<div class="text-sm">
								<div class="text-gray-500">研究方向</div>
								<div class="font-medium">{{ selectedProject.PDirection || '未设置' }}</div>
							</div>
							<div class="text-sm">
								<div class="text-gray-500">难度等级</div>
								<div class="font-medium">
									<el-tag
										size="small"
										:type="selectedProject.PDifficulty === 1 ? 'success' : selectedProject.PDifficulty === 2 ? 'warning' : 'danger'"
									>
										{{ selectedProject.PDifficulty === 1 ? '简单' : selectedProject.PDifficulty === 2 ? '中等' : '困难' }}
									</el-tag>
								</div>
							</div>
						</div>
					</div>
					<div class="mb-4 p-4 bg-gray-50 rounded-lg">
						<h3 class="text-lg font-medium text-gray-900 mb-2">详细信息</h3>
						<div class="grid grid-cols-1 gap-4">
							<div class="text-sm">
								<div class="text-gray-500">课题描述</div>
								<div class="font-medium whitespace-pre-line">{{ selectedProject.PDescription || '未设置' }}</div>
							</div>
							<div class="text-sm">
								<div class="text-gray-500">指导教师</div>
								<div class="font-medium">{{ selectedProject.uName || '未知' }} (ID: {{ selectedProject.UId }})</div>
							</div>
							<div class="text-sm">
								<div class="text-gray-500">最大学生数</div>
								<div class="font-medium">{{ selectedProject.PMaxStudents }}</div>
							</div>
						</div>
					</div>
					<div class="p-4 bg-gray-50 rounded-lg">
						<h3 class="text-lg font-medium text-gray-900 mb-2">时间信息</h3>
						<div class="grid grid-cols-1 gap-4">
							<div class="text-sm">
								<div class="text-gray-500">创建时间</div>
								<div class="font-medium">{{ formatDate(selectedProject.createdAt) }}</div>
							</div>
							<div class="text-sm">
								<div class="text-gray-500">更新时间</div>
								<div class="font-medium">{{ formatDate(selectedProject.updatedAt) }}</div>
							</div>
						</div>
					</div>
				</div>
			</template>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="dialogVisible = false">关闭</el-button>
					<el-button type="primary" @click="applyForProject">申请选题</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Search, User, School, Calendar } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { postProjectsFindAll, postProjectsFind } from '@/apis/projects';
import { postProjectApplicationsInsert } from '@/apis/projectApplications';
import useSysStore from '@/store/sys';

defineOptions({
	name: 'BrowseProjects',
});

const fileShowUrl = import.meta.env.VITE_APP_FILE_SHOW_URL;

// 搜索
const searchQuery = ref('');

// 分页
const pagination = reactive({
	pageIndex: 1,
	pageSize: 9,
	pTitle: '',
});
const total = ref(0);

// 课题列表
const projectList = ref<any[]>([]);
const loading = ref(false);

// 详情对话框
const dialogVisible = ref(false);
const selectedProject = ref<any>(null);

// 加载课题数据
async function loadProjects() {
	loading.value = true;
	try {
		const params = {
			pageIndex: pagination.pageIndex,
			pageSize: pagination.pageSize,
			pTitle: pagination.pTitle,
		};

		const { data } = await postProjectsFindAll(params);

		if (data) {
			projectList.value = data.list || [];
			total.value = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取课题数据失败');
	} finally {
		loading.value = false;
	}
}

// 初始化数据
onMounted(() => {
	loadProjects();
});

// 搜索处理
function handleSearch() {
	pagination.pTitle = searchQuery.value;
	pagination.pageIndex = 1;
	loadProjects();
}

// 分页处理
function handlePageChange(page: number) {
	pagination.pageIndex = page;
	loadProjects();
}

// 查看课题详情
async function viewProjectDetail(project: any) {
	try {
		const { data } = await postProjectsFind({
			pId: project.PId,
		});
		if (data) {
			selectedProject.value = data;
			dialogVisible.value = true;
		}
	} catch (error) {
		ElMessage.error('获取课题详情失败');
	}
}

// 申请选题
async function applyForProject() {
	if (!selectedProject.value) return;

	// 获取当前用户信息
	const sysStore = useSysStore();
	const userInfo = sysStore.userInfo;

	// 检查用户是否登录
	if (!userInfo || !userInfo.UId) {
		ElMessage.warning('请先登录后再申请选题');
		return;
	}

	// 确认申请

	await ElMessageBox.confirm(`确定要申请课题「${selectedProject.value.PTitle}」吗？`, '申请确认', {
		confirmButtonText: '确定申请',
		cancelButtonText: '取消',
		type: 'info',
	});

	// 调用API提交申请
	await postProjectApplicationsInsert({
		pId: selectedProject.value.PId,
		uId: userInfo.UId,
	})
		.then(() => {
			ElMessage.success(`已成功申请课题: ${selectedProject.value.PTitle}`);
		})
		.finally(() => {
			dialogVisible.value = false;
		});
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
</script>

<style scoped>
.project-card {
	cursor: pointer;
	height: 100%;
	display: flex;
	flex-direction: column;
}

.project-info {
	flex: 1;
}

.line-clamp-3 {
	display: -webkit-box;
	-webkit-line-clamp: 3;
	-webkit-box-orient: vertical;
	overflow: hidden;
}
</style>
