<template>
	<div class="profile-container bg-gray-50 min-h-screen pt-6 pb-12 px-4 sm:px-6 lg:px-8">
		<div class="max-w-7xl mx-auto">
			<!-- Profile Header -->
			<div class="bg-white rounded-lg shadow overflow-hidden">
				<div class="bg-gradient-to-r from-blue-500 to-indigo-600 h-32 flex items-end p-4">
					<div class="flex items-center relative pb-12">
						<div class="absolute -bottom-12 bg-white p-1 rounded-full shadow-md">
							<div class="avatar-uploader" @click="triggerAvatarUpload">
								<el-avatar :size="80" :src="fileShowUrl + userInfo.uAvatar" :icon="UserFilled" class="border-4 border-white" />
								<input ref="avatarInputRef" type="file" accept="image/*" style="display: none" @change="handleAvatarChange" />
								<div class="avatar-upload-text text-center text-xs text-gray-400 mt-1">点击头像更换</div>
							</div>
						</div>
					</div>
				</div>
				<div class="bg-white p-4 pt-16">
					<div class="flex justify-between items-center">
						<div>
							<h1 class="text-2xl font-bold text-gray-800">{{ userInfo.name || '未设置姓名' }}</h1>
							<p class="text-gray-500 flex items-center gap-2 mt-1">
								<el-icon><Message /></el-icon>
								{{ userInfo.email || '未设置邮箱' }}
							</p>
						</div>
						<el-button type="primary" @click="isEditingProfile = true">
							<el-icon class="mr-1"><Edit /></el-icon>
							编辑资料
						</el-button>
					</div>
					<div class="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
						<div class="text-center p-3 border border-gray-100 rounded-md bg-gray-50">
							<div class="text-lg font-semibold text-gray-800">
								{{ userInfo.userType === 'student' ? '学生' : userInfo.userType === 'teacher' ? '教师' : '管理员' }}
							</div>
							<div class="text-gray-500 text-sm">用户类型</div>
						</div>
						<div class="text-center p-3 border border-gray-100 rounded-md bg-gray-50">
							<div class="text-lg font-semibold text-gray-800">{{ userInfo.username || '未设置' }}</div>
							<div class="text-gray-500 text-sm">账户</div>
						</div>
					</div>
				</div>
			</div>

			<!-- Profile Tabs and Content -->
			<div class="mt-6">
				<el-tabs v-model="activeTab" class="profile-tabs bg-white rounded-lg shadow">
					<el-tab-pane label="基本信息" name="info">
						<div class="p-6">
							<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
								<div class="space-y-4">
									<h3 class="text-lg font-medium leading-6 text-gray-900 mb-3">用户信息</h3>
									<div class="grid grid-cols-3 text-sm">
										<div class="text-gray-500">姓名</div>
										<div class="col-span-2 text-gray-900">{{ userInfo.name || '未设置' }}</div>
									</div>
									<div class="grid grid-cols-3 text-sm">
										<div class="text-gray-500">用户名/学号</div>
										<div class="col-span-2 text-gray-900">{{ userInfo.username || '未设置' }}</div>
									</div>
									<div class="grid grid-cols-3 text-sm">
										<div class="text-gray-500">手机号</div>
										<div class="col-span-2 text-gray-900">{{ userInfo.phone || '未设置' }}</div>
									</div>
									<div class="grid grid-cols-3 text-sm">
										<div class="text-gray-500">用户类型</div>
										<div class="col-span-2 text-gray-900">
											{{ userInfo.userType === 'student' ? '学生' : userInfo.userType === 'teacher' ? '教师' : '管理员' }}
										</div>
									</div>
								</div>
								<div class="space-y-4">
									<h3 class="text-lg font-medium leading-6 text-gray-900 mb-3">操作信息</h3>
									<div class="grid grid-cols-3 text-sm">
										<div class="text-gray-500">注册时间</div>
										<div class="col-span-2 text-gray-900">{{ formatDate(userInfo.createdAt) }}</div>
									</div>
									<div class="grid grid-cols-3 text-sm">
										<div class="text-gray-500">最后修改</div>
										<div class="col-span-2 text-gray-900">{{ formatDate(userInfo.updatedAt) }}</div>
									</div>
								</div>
							</div>
						</div>
					</el-tab-pane>
					<el-tab-pane v-if="userInfo.URole === 2" label="我的课题" name="projects">
						<div class="p-6">
							<div v-if="userProjects.length > 0">
								<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
									<div v-for="project in userProjects" :key="project.PId" class="border rounded-md p-4 hover:shadow-md transition-shadow">
										<h3 class="text-lg font-medium text-gray-900 mb-2">{{ project.PTitle }}</h3>
										<p class="text-gray-500 text-sm mb-3">{{ project.PDescription || '暂无描述' }}</p>
										<div class="flex justify-between items-center">
											<div class="flex items-center gap-2">
												<el-tag size="small" :type="getDifficultyType(project.PDifficulty)">{{ getDifficultyText(project.PDifficulty) }}</el-tag>
												<span class="text-gray-500 text-xs">{{ formatDate(project.createdAt) }}</span>
											</div>
											<el-button type="primary" link size="small" @click="viewProjectDetail(project.PId)">查看详情</el-button>
										</div>
									</div>
								</div>
								<div class="mt-4 text-center">
									<el-pagination
										v-model:current-page="currentPage"
										v-model:page-size="pageSize"
										:total="totalRecords"
										:page-sizes="[5, 10, 20, 50]"
										layout="total, sizes, prev, pager, next, jumper"
										@size-change="handleSizeChange"
										@current-change="handleCurrentChange"
									/>
								</div>
							</div>
							<el-empty v-else-if="!loading" description="暂无课题" />
							<div v-else class="text-center py-10">
								<el-skeleton :rows="3" animated />
							</div>
						</div>
					</el-tab-pane>
				</el-tabs>
			</div>
		</div>

		<!-- Edit Profile Dialog -->
		<el-dialog v-model="isEditingProfile" title="编辑个人资料" width="500px">
			<el-form ref="editFormRef" :model="editForm" :rules="editRules" label-position="top">
				<el-form-item label="姓名" prop="uName">
					<el-input v-model="editForm.uName" placeholder="请输入真实姓名" />
				</el-form-item>
				<el-form-item label="电子邮箱" prop="uEmail">
					<el-input v-model="editForm.uEmail" placeholder="请输入电子邮箱">
						<template #prefix>
							<el-icon><Message /></el-icon>
						</template>
					</el-input>
				</el-form-item>
				<el-form-item label="手机号" prop="uPhone">
					<el-input v-model="editForm.uPhone" placeholder="请输入手机号">
						<template #prefix>
							<el-icon><Phone /></el-icon>
						</template>
					</el-input>
				</el-form-item>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="isEditingProfile = false">取消</el-button>
					<el-button type="primary" :loading="isUpdatingProfile" @click="handleUpdateProfile">保存</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { UserFilled, Message, Edit, Phone } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import useSysStore from '@/store/sys';
import { storeToRefs } from 'pinia';
import { postUsersFind, postUsersUpdate, postUsersAvatar } from '@/apis/users';
import { postProjectsFindAll } from '@/apis/projects';
import { useRouter } from 'vue-router';

const router = useRouter();

// 定义项目接口
interface Project {
	PId: number;
	PTitle: string;
	PDescription: string;
	PDifficulty: number;
	PDirection: string;
	PMaxStudents: number;
	UId: number;
	uName: string;
	createdAt: string;
	updatedAt: string;
	valid: number;
}

defineOptions({
	name: 'UserProfile',
});

const sysStore = useSysStore();
const { userInfo } = storeToRefs(sysStore);

// 头像上传相关
const avatarInputRef = ref<HTMLInputElement | null>(null);
const isUploadingAvatar = ref(false);

function triggerAvatarUpload() {
	if (!isUploadingAvatar.value) {
		avatarInputRef.value?.click();
	}
}

const uploadUrl = import.meta.env.VITE_APP_FILE_UPLOAD_URL;

const fileShowUrl = import.meta.env.VITE_APP_FILE_SHOW_URL;

async function handleAvatarChange(e: Event) {
	const file = (e.target as HTMLInputElement).files?.[0];
	if (!file) return;

	// 校验图片类型和大小
	if (!file.type.startsWith('image/')) {
		ElMessage.error('请选择图片文件');
		return;
	}
	if (file.size > 2 * 1024 * 1024) {
		ElMessage.error('图片大小不能超过2MB');
		return;
	}

	isUploadingAvatar.value = true;
	try {
		// 1. 上传文件到服务器，获取图片路径
		const formData = new FormData();
		formData.append('file', file);
		const response = await fetch(uploadUrl, {
			method: 'POST',
			body: formData,
		});
		const result = await response.json();
		// 兼容返回数组或对象
		const fileObj = Array.isArray(result) ? result[0] : result;
		if (!fileObj || !fileObj.success || !fileObj.fileUrl) {
			ElMessage.error('头像上传失败');
			return;
		}
		// 2. 拿到服务器返回的图片路径，调用头像保存接口
		await postUsersAvatar({
			uId: userInfo.value.UId,
			uAvatar: fileObj.fileUrl,
		});
		// 3. 更新本地用户信息
		sysStore.changeUserInfo({
			...userInfo.value,
			uAvatar: fileObj.fileUrl,
		});
		ElMessage.success('头像更新成功');
	} catch (err) {
		ElMessage.error('头像上传失败');
	} finally {
		isUploadingAvatar.value = false;
		// 清空input值，避免同一文件无法再次触发change
		if (avatarInputRef.value) avatarInputRef.value.value = '';
	}
}
const fetchUserDetail = async () => {
	loading.value = true;
	try {
		const { data } = await postUsersFind({
			uId: userInfo.value.UId,
		});

		if (data) {
			sysStore.changeUserInfo({
				...userInfo.value,
				name: data.UName,
				email: data.UEmail,
				username: data.UUsername,
				userType: data.URole === 1 ? 'admin' : data.URole === 2 ? 'teacher' : 'student',
				phone: data.UPhone,
				department: '',
				major: '',
				enrollmentYear: '',
				lastLoginTime: data.updatedAt,
			});

			// 更新编辑表单数据
			Object.assign(editForm, {
				avatar: userInfo.value.avatar || '',
				uName: data.UName || '',
				uEmail: data.UEmail || '',
				uPhone: data.UPhone || '',
			});
		}
	} catch (error) {
		ElMessage.error('获取用户信息失败');
	} finally {
		loading.value = false;
	}
};
const loading = ref(false);

const activeTab = ref('info');
const isEditingProfile = ref(false);
const isUpdatingProfile = ref(false);
const editFormRef = ref<FormInstance>();

const userProjects = ref<Project[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalRecords = ref(0);

// 加载课题数据
const loadProjects = async () => {
	// 只有教师角色需要加载课题数据
	if (userInfo.value.URole !== 2) return;

	loading.value = true;
	try {
		const { data } = await postProjectsFindAll({
			pageIndex: currentPage.value,
			pageSize: pageSize.value,
			uId: userInfo.value.UId, // 修正属性名为uId而不是UId
		});

		if (data) {
			userProjects.value = data.list || [];
			totalRecords.value = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('加载课题失败');
		console.error('加载课题失败:', error);
	} finally {
		loading.value = false;
	}
};

onMounted(() => {
	fetchUserDetail();
	loadProjects();
});

// 编辑表单
const editForm = reactive({
	avatar: '',
	uName: '',
	uEmail: '',
	uPhone: '',
	uId: userInfo.value.UId,
	uRole: userInfo.value.URole,
});

// 编辑表单验证规则
const editRules = reactive<FormRules>({
	uName: [
		{ required: true, message: '请输入姓名', trigger: 'blur' },
		{ min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
	],
	uEmail: [
		{ required: true, message: '请输入电子邮箱', trigger: 'blur' },
		{ type: 'email', message: '请输入正确的电子邮箱格式', trigger: 'blur' },
	],
	uPhone: [
		{ required: true, message: '请输入手机号', trigger: 'blur' },
		{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' },
	],
});

// 格式化日期
function formatDate(dateString: string | undefined) {
	if (!dateString) return '未知时间';
	try {
		const date = new Date(dateString);
		return date.toLocaleDateString('zh-CN', {
			year: 'numeric',
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit',
		});
	} catch (e) {
		return '格式错误';
	}
}

// 课题难度标签类型
function getDifficultyType(difficulty: number) {
	const difficultyMap: Record<number, string> = {
		1: 'info',
		2: 'success',
		3: 'warning',
		4: 'danger',
	};
	return difficultyMap[difficulty] || 'info';
}

// 课题难度文本描述
function getDifficultyText(difficulty: number) {
	const difficultyMap: Record<number, string> = {
		1: '简单',
		2: '中等',
		3: '困难',
	};
	return difficultyMap[difficulty] || '未知';
}

// 查看项目详情
function viewProjectDetail(id: number) {
	router.push(`/entrance/project-detail/${id}`);
}

// 更新个人资料
async function handleUpdateProfile() {
	if (!editFormRef.value) return;

	await editFormRef.value.validate(async (valid) => {
		if (valid) {
			isUpdatingProfile.value = true;

			try {
				// 调用API
				await postUsersUpdate({
					uId: editForm.uId,
					uName: editForm.uName,
					uRole: editForm.uRole,
					uEmail: editForm.uEmail,
					uPhone: editForm.uPhone,
				});

				// 更新本地存储的用户信息
				sysStore.changeUserInfo({
					...userInfo.value,
					name: editForm.uName,
					email: editForm.uEmail,
					phone: editForm.uPhone,
				});

				ElMessage.success('个人资料更新成功');
				isEditingProfile.value = false;
			} catch (error) {
				ElMessage.error('更新失败，请稍后重试');
			} finally {
				isUpdatingProfile.value = false;
			}
		}
	});
}

// 处理分页大小变化
const handleSizeChange = (newSize: number) => {
	pageSize.value = newSize;
	currentPage.value = 1;
	loadProjects();
};

// 处理当前页变化
const handleCurrentChange = (newPage: number) => {
	currentPage.value = newPage;
	loadProjects();
};
</script>

<style scoped>
.profile-tabs :deep(.el-tabs__header) {
	margin-bottom: 0;
	padding: 0 1rem;
	border-bottom: 1px solid #ebeef5;
}

.avatar-uploader {
	text-align: center;
	position: relative;
	display: inline-block;
	cursor: pointer;
}

.avatar-upload-text {
	font-size: 12px;
	color: #909399;
	margin-top: 8px;
}
</style>
