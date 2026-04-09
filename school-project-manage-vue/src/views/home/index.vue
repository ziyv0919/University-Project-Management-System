<template>
	<div class="home-container p-6">
		<!-- Role-based content -->
		<div v-if="userInfo.URole === 3">
			<!-- Student View -->
			<div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
				<!-- Course Recommendations - 2/3 width on large screens -->
				<div class="lg:col-span-2 bg-white rounded-lg shadow p-6">
					<div class="flex justify-between items-center mb-4">
						<div>
							<h2 class="text-xl font-bold text-gray-800">课题推荐</h2>
							<el-tag type="success" size="small">最新</el-tag>
						</div>
						<el-button @click="goToProjectList" type="primary" size="small" text>查看全部</el-button>
					</div>

					<!-- Course cards -->
					<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
						<!-- Example course cards -->
						<div v-for="(project, index) in recommendedProjects" :key="index" class="border rounded-lg p-4 hover:shadow-md transition-shadow">
							<h3 class="text-lg font-medium text-gray-900 mb-2">{{ project.PTitle }}</h3>
							<p class="text-gray-500 text-sm mb-3">{{ project.PDescription }}</p>
							<p class="text-gray-800 text-sm mb-3">{{ project.uName }}</p>
							<div class="flex justify-between items-center">
								<el-tag size="small" v-for="item in project.tags" :key="item.TId">{{ item.TName }}</el-tag>
								<el-button type="primary" link size="small" @click="openProjectDetailDialog(project)">查看详情</el-button>
							</div>
						</div>
					</div>
				</div>

				<!-- Announcements - 1/3 width on large screens -->
				<div class="bg-white rounded-lg shadow p-6">
					<div class="flex justify-between items-center mb-4">
						<h2 class="text-xl font-bold text-gray-800">公告概览</h2>
						<el-button type="primary" size="small" text @click="goToAnnouncements">更多公告</el-button>
					</div>

					<div class="space-y-4">
						<div v-for="(announcement, index) in announcements" :key="index" class="border-b pb-3 last:border-b-0">
							<h3 class="font-medium text-gray-800">{{ announcement.ATitle }}</h3>
							<p class="text-gray-500 text-sm mt-1">{{ announcement.AContent }}</p>
							<div class="text-xs text-gray-400 mt-2">{{ announcement.createdAt }}</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div v-else-if="userInfo.URole === 2">
			<!-- Teacher View -->
			<div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
				<!-- Teacher Controls - Full width -->
				<div class="lg:col-span-3 bg-white rounded-lg shadow p-6 mb-6">
					<div class="flex justify-between items-center">
						<h2 class="text-xl font-bold text-gray-800">快捷操作</h2>
					</div>
					<div class="flex flex-wrap gap-4 mt-4">
						<el-button type="primary" @click="openAddProjectDialog">
							<el-icon class="mr-1"><Plus /></el-icon>发布课题
						</el-button>

						<el-button @click="$router.push('/entrance/my-projects')">
							<el-icon class="mr-1"><Document /></el-icon>管理我的课题
						</el-button>
						<el-button @click="$router.push('/entrance/my-notifications')">
							<el-icon class="mr-1"><Bell /></el-icon>查看通知
						</el-button>
					</div>
				</div>

				<!-- Data Overview - 2/3 width -->
				<div class="lg:col-span-2 bg-white rounded-lg shadow p-6">
					<h2 class="text-xl font-bold text-gray-800 mb-4">数据概览</h2>
					<div class="grid grid-cols-1 md:grid-cols-3 gap-4">
						<div class="text-center p-4 border rounded-lg bg-blue-50">
							<div class="text-3xl font-bold text-blue-600">{{ teacherStats.totalProjects }}</div>
							<div class="text-sm text-gray-600">已发布课题</div>
						</div>
					</div>
				</div>

				<!-- Notifications - 1/3 width -->
				<div class="bg-white rounded-lg shadow p-6">
					<div class="flex justify-between items-center mb-4">
						<h2 class="text-xl font-bold text-gray-800">公告概览</h2>
						<el-button type="primary" size="small" text @click="goToAnnouncements">更多公告</el-button>
					</div>

					<div class="space-y-4">
						<div v-for="(announcement, index) in announcements" :key="index" class="border-b pb-3 last:border-b-0">
							<h3 class="font-medium text-gray-800">{{ announcement.ATitle }}</h3>
							<p class="text-gray-500 text-sm mt-1">{{ announcement.AContent }}</p>
							<div class="text-xs text-gray-400 mt-2">{{ announcement.createdAt }}</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div v-else-if="userInfo.URole === 1">
			<!-- Admin View -->
			<div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
				<!-- System Overview - Full width -->
				<div class="lg:col-span-4 bg-white rounded-lg shadow p-6 mb-4">
					<h2 class="text-xl font-bold text-gray-800 mb-4">系统概况</h2>
					<div class="grid grid-cols-2 md:grid-cols-4 gap-4">
						<div class="text-center p-4 border rounded-lg bg-blue-50">
							<div class="text-3xl font-bold text-blue-600">{{ adminStats.totalUsers }}</div>
							<div class="text-sm text-gray-600">总用户数</div>
						</div>
						<div class="text-center p-4 border rounded-lg bg-green-50">
							<div class="text-3xl font-bold text-green-600">{{ adminStats.totalProjects }}</div>
							<div class="text-sm text-gray-600">总课题数</div>
						</div>
						<div class="text-center p-4 border rounded-lg bg-red-50">
							<div class="text-3xl font-bold text-red-600">{{ adminStats.pendingApprovals }}</div>
							<div class="text-sm text-gray-600">待审批课题申请</div>
						</div>
					</div>
				</div>

				<!-- Quick Actions - 1/4 width -->
				<div class="bg-white rounded-lg shadow p-6">
					<h2 class="text-xl font-bold text-gray-800 mb-4">快捷操作</h2>
					<div class="space-y-3">
						<el-button type="success" class="w-full" @click="$router.push('/entrance/user-manage')">
							<el-icon class="mr-1"><User /></el-icon>用户管理
						</el-button>
						<el-button type="warning" class="w-full" @click="openAnnouncementDialog">
							<el-icon class="mr-1"><Bell /></el-icon>发布公告
						</el-button>
						<el-button type="info" class="w-full" @click="$router.push('/entrance/project-application')">
							<el-icon class="mr-1"><Document /></el-icon>课题审核
						</el-button>
					</div>
				</div>
			</div>
		</div>

		<!-- 发布公告弹窗（仅管理员可见） -->
		<el-dialog v-model="announcementDialogVisible" title="发布公告" width="50%">
			<el-form :model="announcementForm" label-width="100px">
				<el-form-item label="公告标题" required>
					<el-input v-model="announcementForm.aTitle" placeholder="请输入公告标题" />
				</el-form-item>
				<el-form-item label="公告内容" required>
					<el-input v-model="announcementForm.aContent" type="textarea" :rows="6" placeholder="请输入公告内容" />
				</el-form-item>
				<el-form-item label="发布对象">
					<el-select v-model="announcementForm.aTarget" placeholder="请选择发布对象">
						<el-option :value="1" label="管理员" />
						<el-option :value="2" label="教师" />
						<el-option :value="3" label="学生" />
						<el-option :value="4" label="所有用户" />
					</el-select>
				</el-form-item>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="announcementDialogVisible = false">取消</el-button>
					<el-button type="primary" :loading="announcementSubmitLoading" @click="submitAnnouncementForm">确定</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
	<!-- 课题详情弹窗 -->
	<el-dialog v-model="detailDialogVisible" title="课题详情" width="60%">
		<template v-if="selectedProject">
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
							<el-tag size="small" :type="selectedProject.PDifficulty === 1 ? 'success' : selectedProject.PDifficulty === 2 ? 'warning' : 'danger'">
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
						<div class="font-medium">{{ selectedProject.UName || '未知' }} (ID: {{ selectedProject.UId }})</div>
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
		</template>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="detailDialogVisible = false">关闭</el-button>
				<el-button type="primary" @click="applyForProject">申请选题</el-button>
			</div>
		</template>
	</el-dialog>

	<!-- 新增/编辑课题对话框（仅教师可见） -->
	<el-dialog v-model="dialogVisible" title="新增课题" width="500px">
		<el-form ref="projectFormRef" :model="projectForm" :rules="projectRules" label-width="100px">
			<el-form-item label="课题名称" prop="pTitle">
				<el-input v-model="projectForm.pTitle" placeholder="请输入课题名称" />
			</el-form-item>
			<el-form-item label="研究方向" prop="pDirection">
				<el-input v-model="projectForm.pDirection" placeholder="请输入研究方向" />
			</el-form-item>
			<el-form-item label="课题描述" prop="pDescription">
				<el-input v-model="projectForm.pDescription" type="textarea" :rows="3" placeholder="请输入课题描述" />
			</el-form-item>
			<el-form-item label="难度等级" prop="pDifficulty">
				<el-select v-model="projectForm.pDifficulty" placeholder="请选择难度等级">
					<el-option :value="1" label="简单" />
					<el-option :value="2" label="中等" />
					<el-option :value="3" label="困难" />
				</el-select>
			</el-form-item>
			<el-form-item label="最大学生数" prop="pMaxStudents">
				<el-input-number v-model="projectForm.pMaxStudents" :min="1" :max="200" />
			</el-form-item>
			<el-form-item label="课题标签" prop="tagIds">
				<el-select v-model="projectForm.tagIds" multiple filterable placeholder="请选择课题标签" :loading="tagLoading">
					<el-option v-for="tag in tagOptions" :key="tag.TId" :label="tag.TName" :value="tag.TId" />
				</el-select>
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="dialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { postAnnouncementsInsert } from '@/apis/announcements';
import { ref } from 'vue';
import { Plus, Document, Bell, User } from '@element-plus/icons-vue';
import { ElForm, ElFormItem, ElInput, ElInputNumber, ElSelect, ElOption } from 'element-plus';
import { postProjectsInsert } from '@/apis/projects';
import { postTagsSelect } from '@/apis/tags';
import useSysStore from '@/store/sys';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';
import { postAnnouncementsFindAll, postProjectsFindAll, postUsersFindAll } from '@/apis';
import { ElMessage, ElMessageBox } from 'element-plus';
import { postProjectsFind } from '@/apis';
import { postProjectApplicationsFindAll, postProjectApplicationsInsert } from '@/apis/projectApplications';
defineOptions({
	name: 'HomePage',
});

const router = useRouter();

const sysStore = useSysStore();
const { userInfo } = storeToRefs(sysStore);

// 公告弹窗相关
const announcementDialogVisible = ref(false);
const announcementSubmitLoading = ref(false);
const announcementForm = reactive({
	aTitle: '',
	aContent: '',
	aTarget: 1,
});

// 新增课题弹窗相关
const dialogVisible = ref(false);
const projectFormRef = ref();
const submitLoading = ref(false);
const tagLoading = ref(false);

interface TagOption {
	TId: number;
	TName: string;
}

const tagOptions = ref<TagOption[]>([]);

const projectForm = reactive({
	pTitle: '',
	pDescription: '',
	pDirection: '',
	pDifficulty: 1,
	pMaxStudents: 30,
	tagIds: [] as number[],
});

const projectRules = reactive({
	pTitle: [{ required: true, message: '请输入课题名称', trigger: 'blur' }],
	pDifficulty: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
	pMaxStudents: [{ required: true, message: '请输入最大学生数', trigger: 'blur' }],
	tagIds: [{ required: true, message: '请选择课题标签', trigger: 'change' }],
});

function openAddProjectDialog() {
	resetProjectForm();
	fetchTagOptions();
	dialogVisible.value = true;
}

function resetProjectForm() {
	projectForm.pTitle = '';
	projectForm.pDescription = '';
	projectForm.pDirection = '';
	projectForm.pDifficulty = 1;
	projectForm.pMaxStudents = 30;
	projectForm.tagIds = [];
	if (projectFormRef.value) {
		projectFormRef.value.resetFields();
	}
}

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

async function submitForm() {
	if (!projectFormRef.value) return;
	await projectFormRef.value.validate(async (valid: boolean) => {
		if (valid) {
			submitLoading.value = true;
			try {
				const params = filterEmptyParams({
					pTitle: projectForm.pTitle,
					pDescription: projectForm.pDescription,
					pDirection: projectForm.pDirection,
					pDifficulty: projectForm.pDifficulty,
					uId: sysStore.userInfo.UId,
					pMaxStudents: projectForm.pMaxStudents,
					tagIds: projectForm.tagIds,
				});
				await postProjectsInsert(params);
				ElMessage.success('添加成功');
				dialogVisible.value = false;
				// 可选：刷新课题数据
				searchMyProjectData && searchMyProjectData();
			} catch (error) {
				ElMessage.error('添加失败');
				console.error(error);
			} finally {
				submitLoading.value = false;
			}
		}
	});
}

function goToProjectList() {
	router.push('/entrance/browse-projects');
}

function goToAnnouncements() {
	router.push('/entrance/my-announcement');
}

const detailDialogVisible = ref(false);
const selectedProject = ref<any>(null);

// 申请选题逻辑
async function applyForProject() {
	if (!selectedProject.value) return;
	// 获取当前用户信息
	const sysStore = useSysStore();
	const userInfo = sysStore.userInfo;
	if (!userInfo || !userInfo.UId) {
		ElMessage.warning('请先登录后再申请选题');
		return;
	}
	try {
		await ElMessageBox.confirm(`确定要申请课题「${selectedProject.value.PTitle}」吗？`, '申请确认', {
			confirmButtonText: '确定申请',
			cancelButtonText: '取消',
			type: 'info',
		});
		await postProjectApplicationsInsert({
			pId: selectedProject.value.PId,
			uId: userInfo.UId,
		});
		ElMessage.success(`已成功申请课题: ${selectedProject.value.PTitle}`);
		detailDialogVisible.value = false;
	} catch (error) {
		// 用户取消不提示
		if (error !== 'cancel') {
			ElMessage.error('申请失败或已申请过该课题');
		}
	}
}

async function openProjectDetailDialog(project: any) {
	try {
		const { data } = await postProjectsFind({ pId: project.PId });
		if (data) {
			selectedProject.value = data;
			detailDialogVisible.value = true;
		}
	} catch (error) {
		ElMessage.error('获取课题详情失败');
	}
}

function filterEmptyParams(params: Record<string, any>) {
	const filteredParams: Record<string, any> = {};
	for (const key in params) {
		if (params[key] !== '' && params[key] !== undefined) {
			filteredParams[key] = params[key];
		}
	}
	return filteredParams;
}

async function searchProjectData() {
	try {
		const params = filterEmptyParams({
			pageIndex: 1,
			pageSize: 4,
		});

		const { data } = await postProjectsFindAll(params);

		if (data) {
			recommendedProjects.value = data.list || [];
			adminStats.value.totalProjects = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取课题数据失败');
	}
}

async function searchAppvaData() {
	try {
		const params = filterEmptyParams({
			pageIndex: 1,
			pageSize: 1,
		});

		const { data } = await postProjectApplicationsFindAll(params);

		if (data) {
			adminStats.value.pendingApprovals = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取申请数据失败');
	}
}

async function searchAnnouncementData() {
	try {
		const params = filterEmptyParams({
			pageIndex: 1,
			pageSize: 3,
		});

		// 获取针对当前用户角色的公告
		const { data } = await postAnnouncementsFindAll({
			data: params,
		});

		if (data) {
			announcements.value = data.list || [];
		}
	} catch (error) {
		ElMessage.error('获取公告数据失败');
		console.error('获取公告数据失败:', error);
	}
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

// Student data
const recommendedProjects = ref([]);

const announcements = ref([]);

async function searchMyProjectData() {
	try {
		const params = filterEmptyParams({
			pageIndex: 1,
			pageSize: 1,
			uId: useSysStore().userInfo.UId,
		});

		const { data } = await postProjectsFindAll(params);

		if (data) {
			teacherStats.value.totalProjects = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取我的课题数据失败');
	}
}

// Teacher data
const teacherStats = ref({
	totalProjects: 0,
});

// Admin data
const adminStats = ref({
	totalUsers: 1258,
	totalProjects: 186,
	pendingApprovals: 17,
});

async function searchUserData() {
	try {
		const params = filterEmptyParams({
			pageIndex: 1,
			pageSize: 1,
		});

		const { data } = await postUsersFindAll(params);

		if (data) {
			adminStats.value.totalUsers = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取用户数据失败');
	}
}

// 打开公告弹窗
function openAnnouncementDialog() {
	resetAnnouncementForm();
	announcementDialogVisible.value = true;
}
// 重置公告表单
function resetAnnouncementForm() {
	announcementForm.aTitle = '';
	announcementForm.aContent = '';
	announcementForm.aTarget = 1;
}
// 提交公告表单
async function submitAnnouncementForm() {
	if (!announcementForm.aTitle || !announcementForm.aContent) {
		ElMessage.warning('请填写完整的公告信息');
		return;
	}
	announcementSubmitLoading.value = true;
	try {
		const params = filterEmptyParams({
			aTitle: announcementForm.aTitle,
			aContent: announcementForm.aContent,
			aTarget: announcementForm.aTarget,
			uId: sysStore.userInfo.UId,
		});
		await postAnnouncementsInsert({ data: params });
		ElMessage.success('公告发布成功');
		announcementDialogVisible.value = false;
		searchAnnouncementData && searchAnnouncementData();
	} catch (error) {
		ElMessage.error('公告发布失败');
		console.error(error);
	} finally {
		announcementSubmitLoading.value = false;
	}
}

onMounted(() => {
	if (useSysStore().userInfo.URole === 3) {
		searchProjectData();
		searchAnnouncementData();
	}
	if (useSysStore().userInfo.URole === 2) {
		searchAnnouncementData();
		searchMyProjectData();
	}
	if (useSysStore().userInfo.URole === 1) {
		searchUserData();
		searchProjectData();
		searchAppvaData();
	}
});
</script>

<style scoped>
.home-container {
	background-color: #f5f7fa;
	min-height: calc(100vh - 60px);
}
</style>
