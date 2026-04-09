<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header">
			<div class="header-left-toolbar">
				<el-button round :icon="Plus" @click="addProject">新增</el-button>
			</div>
			<div class="header-right-search">
				<el-popover :width="400" :visible="visible" :teleported="false" placement="bottom-start">
					<template #reference>
						<el-button :icon="Search" @click="visible = !visible">检索</el-button>
					</template>
					<el-form label-position="top" label-width="100px" :model="pagination" @keyup.enter="searchData">
						<el-form-item label="课题编号">
							<el-input v-model="pagination.pId" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="课题名称">
							<el-input v-model="pagination.pTitle" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="研究方向">
							<el-input v-model="pagination.pDirection" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="难度等级">
							<el-select v-model="pagination.pDifficulty" placeholder="请选择" clearable>
								<el-option :value="1" label="简单" />
								<el-option :value="2" label="中等" />
								<el-option :value="3" label="困难" />
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

		<el-table v-loading="loading" :data="projectData" style="width: 100%" border stripe @row-dblclick="handleRowDblclick">
			<el-table-column prop="PId" label="课题编号" min-width="80" />
			<el-table-column prop="PTitle" label="课题名称" min-width="150" />
			<el-table-column prop="PDirection" label="研究方向" min-width="120" />
			<el-table-column label="难度等级" min-width="100">
				<template #default="{ row }">
					<el-tag :type="row.PDifficulty === 1 ? 'success' : row.PDifficulty === 2 ? 'warning' : 'danger'">
						{{ row.PDifficulty === 1 ? '简单' : row.PDifficulty === 2 ? '中等' : '困难' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="PMaxStudents" label="最大学生数" min-width="100" />
			<el-table-column label="指导教师" min-width="120">
				<template #default="{ row }">
					{{ row.UName || '未知' }}
				</template>
			</el-table-column>
			<el-table-column prop="createdAt" label="创建时间" min-width="150">
				<template #default="{ row }">
					{{ formatDate(row.createdAt) }}
				</template>
			</el-table-column>

			<!-- 标签列 -->
			<el-table-column label="标签" min-width="180">
				<template #default="{ row }">
					<div class="flex flex-wrap gap-1">
						<el-tag v-for="tag in row.tags" :key="tag.TId" type="info" size="small">
							{{ tag.TName }}
						</el-tag>
					</div>
				</template>
			</el-table-column>
			<el-table-column label="操作" min-width="260" fixed="right">
				<template #default="{ row }">
					<el-button type="primary" size="small" class="mr-2" @click="editProject(row)">编辑</el-button>
					<el-button type="danger" size="small" class="mr-2" @click="deleteProject(row)">删除</el-button>
					<el-button type="info" size="small" @click="viewProjectDetail(row)">详情</el-button>
					<el-button type="success" size="small" @click="goToProjectDetail(row)">详细详情</el-button>
					<el-button type="warning" size="small" @click="openCoverDialog(row)">修改封面</el-button>
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

	<!-- 新增/编辑课题对话框 -->
	<el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课题' : '新增课题'" width="500px">
		<el-form ref="projectFormRef" :model="projectForm" :rules="projectRules" label-width="100px">
			<el-form-item label="课题名称" prop="pTitle">
				<el-input v-model="projectForm.pTitle" placeholder="请输入课题名称" />
			</el-form-item>
			<el-form-item v-if="isEdit" label="研究方向" prop="pDirection">
				<el-input v-model="projectForm.pDirection" placeholder="请输入研究方向" />
			</el-form-item>
			<el-form-item v-if="isEdit" label="课题描述" prop="pDescription">
				<el-input v-model="projectForm.pDescription" type="textarea" :rows="3" placeholder="请输入课题描述" />
			</el-form-item>
			<el-form-item label="难度等级" prop="pDifficulty">
				<el-select v-model="projectForm.pDifficulty" placeholder="请选择难度等级">
					<el-option :value="1" label="简单" />
					<el-option :value="2" label="中等" />
					<el-option :value="3" label="困难" />
				</el-select>
			</el-form-item>
			<el-form-item label="指导教师" prop="uId">
				<el-select
					v-model="projectForm.uId"
					filterable
					remote
					reserve-keyword
					placeholder="请输入教师姓名搜索"
					:remote-method="remoteSearchTeachers"
					:loading="teacherLoading"
				>
					<el-option v-for="item in teacherOptions" :key="item.value" :label="item.label" :value="item.value" />
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
						<div class="font-medium">{{ currentProject.PTitle }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">研究方向</div>
						<div class="font-medium">{{ currentProject.PDirection || '未设置' }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">难度等级</div>
						<div class="font-medium">
							<el-tag size="small" :type="currentProject.PDifficulty === 1 ? 'success' : currentProject.PDifficulty === 2 ? 'warning' : 'danger'">
								{{ currentProject.PDifficulty === 1 ? '简单' : currentProject.PDifficulty === 2 ? '中等' : '困难' }}
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
						<div class="font-medium">{{ currentProject.PDescription || '未设置' }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">指导教师</div>
						<div class="font-medium">{{ currentProject.uName || '未知' }} (ID: {{ currentProject.UId }})</div>
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

	<!-- 修改封面对话框 -->
	<el-dialog v-model="coverDialogVisible" title="修改课题封面" width="400px">
		<div v-if="coverProject" class="flex flex-col items-center">
			<div class="mb-4">
				<img v-if="coverProject.PCover" :src="fileShowUrl + coverProject.PCover" class="w-32 h-32 object-cover rounded border" alt="当前封面" />
				<div v-else class="w-32 h-32 flex items-center justify-center bg-gray-100 rounded">暂无封面</div>
			</div>
			<el-upload
				class="upload-demo"
				:action="uploadUrl"
				:show-file-list="false"
				:before-upload="beforeCoverUpload"
				:http-request="customCoverUploadRequest"
				:disabled="isUploadingCover"
			>
				<el-button type="primary" :loading="isUploadingCover">选择图片</el-button>
			</el-upload>
		</div>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="coverDialogVisible = false">取消</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Plus, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
import {
	postProjectsInsert,
	postProjectsUpdate,
	postProjectsDelete,
	postProjectsFind,
	postProjectsFindAll,
	postProjectsCover,
} from '@/apis/projects';
import { postUsersRemoteSelect } from '@/apis/users';
import { postTagsSelect } from '@/apis/tags';
defineOptions({
	name: 'ProjectManage',
});

const router = useRouter();

const uploadUrl = import.meta.env.VITE_APP_FILE_UPLOAD_URL;
const fileShowUrl = import.meta.env.VITE_APP_FILE_SHOW_URL;

// 封面相关
const coverDialogVisible = ref(false);
const coverProject = ref<any>(null);
const isUploadingCover = ref(false);

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

// 对话框控制
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const projectFormRef = ref<FormInstance>();
const currentProject = ref(null);

// 课题表单
const projectForm = reactive({
	pId: undefined as number | undefined,
	pTitle: '',
	pDescription: '',
	pDirection: '',
	pDifficulty: 1,
	uId: undefined as number | undefined,
	pMaxStudents: 30,
	tagIds: [] as number[],
});

// 教师远程搜索
const teacherLoading = ref(false);
const teacherOptions = ref<Array<{ value: number; label: string }>>([]);

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

// 远程搜索教师
async function remoteSearchTeachers(query: string) {
	if (query) {
		teacherLoading.value = true;
		try {
			const { data } = await postUsersRemoteSelect({
				searchText: query,
			});
			if (data && Array.isArray(data)) {
				teacherOptions.value = data.map((item) => ({
					value: item.UId,
					label: `${item.UName} (${item.UUsername})`,
				}));
			} else {
				teacherOptions.value = [];
			}
		} catch (error) {
			console.error('搜索教师失败:', error);
			teacherOptions.value = [];
		} finally {
			teacherLoading.value = false;
		}
	} else {
		teacherOptions.value = [];
	}
}

// 表单验证规则
const projectRules = reactive<FormRules>({
	pTitle: [{ required: true, message: '请输入课题名称', trigger: 'blur' }],
	pDifficulty: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
	uId: [{ required: true, message: '请输入指导教师ID', trigger: 'blur' }],
	pMaxStudents: [{ required: true, message: '请输入最大学生数', trigger: 'blur' }],
	tagIds: [{ required: true, message: '请选择课题标签', trigger: 'change' }],
});

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
			pDirection: pagination.value.pDirection,
			pDifficulty: pagination.value.pDifficulty,
			uId: pagination.value.uId,
		});

		const { data } = await postProjectsFindAll(params);

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

// 新增课题
function addProject() {
	isEdit.value = false;
	resetForm();
	dialogVisible.value = true;
}

// 编辑课题
function editProject(row: any) {
	isEdit.value = true;
	resetForm();
	projectForm.pId = row.PId;
	projectForm.pTitle = row.PTitle;
	projectForm.pDescription = row.PDescription || '';
	projectForm.pDirection = row.PDirection || '';
	projectForm.pDifficulty = row.PDifficulty;
	projectForm.uId = row.UId;
	projectForm.pMaxStudents = row.PMaxStudents;
	projectForm.tagIds = row.tags.map((i: any) => i.TId);
	dialogVisible.value = true;
}

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

// 删除课题
function deleteProject(row: any) {
	ElMessageBox.confirm(`确定要删除课题 ${row.PTitle} 吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				const params = filterEmptyParams({
					pId: row.PId,
				});
				await postProjectsDelete(params);
				ElMessage.success('删除成功');
				searchData();
			} catch (error) {
				ElMessage.error('删除失败');
			}
		})
		.catch(() => {});
}

// 提交表单
async function submitForm() {
	if (!projectFormRef.value) return;

	await projectFormRef.value.validate(async (valid) => {
		if (valid) {
			submitLoading.value = true;
			try {
				const params = filterEmptyParams({
					pId: projectForm.pId,
					pTitle: projectForm.pTitle,
					pDescription: projectForm.pDescription,
					pDirection: projectForm.pDirection,
					pDifficulty: projectForm.pDifficulty,
					uId: projectForm.uId,
					pMaxStudents: projectForm.pMaxStudents,
					tagIds: projectForm.tagIds,
				});
				if (isEdit.value) {
					await postProjectsUpdate(params);
					ElMessage.success('更新成功');
				} else {
					await postProjectsInsert(params);
					ElMessage.success('添加成功');
				}
				dialogVisible.value = false;
				searchData();
			} catch (error) {
				ElMessage.error(isEdit.value ? '更新失败' : '添加失败');
				console.error(error);
			} finally {
				submitLoading.value = false;
			}
		}
	});
}

// 重置表单
function resetForm() {
	projectForm.pId = undefined;
	projectForm.pTitle = '';
	projectForm.pDescription = '';
	projectForm.pDirection = '';
	projectForm.pDifficulty = 1;
	projectForm.tagIds = [];
	projectForm.uId = undefined;
	projectForm.pMaxStudents = 30;
	if (projectFormRef.value) {
		projectFormRef.value.resetFields();
	}
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

function openCoverDialog(row: any) {
	coverProject.value = { ...row };
	coverDialogVisible.value = true;
}

function beforeCoverUpload(file: File) {
	if (!file.type.startsWith('image/')) {
		ElMessage.error('请选择图片文件');
		return false;
	}
	if (file.size > 2 * 1024 * 1024) {
		ElMessage.error('图片大小不能超过2MB');
		return false;
	}
	return true;
}

async function customCoverUploadRequest(option: any) {
	if (!coverProject.value) return;
	isUploadingCover.value = true;
	try {
		const formData = new FormData();
		formData.append('file', option.file);
		const response = await fetch(uploadUrl, {
			method: 'POST',
			body: formData,
		});
		const result = await response.json();
		const fileObj = Array.isArray(result) ? result[0] : result;
		if (!fileObj || !fileObj.success || !fileObj.fileUrl) {
			ElMessage.error('封面上传失败');
			return;
		}
		await postProjectsCover({
			pId: coverProject.value.PId,
			pCover: fileObj.fileUrl,
		});
		ElMessage.success('封面修改成功');
		coverDialogVisible.value = false;
		searchData();
	} catch (err) {
		ElMessage.error('封面上传失败');
	} finally {
		isUploadingCover.value = false;
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
