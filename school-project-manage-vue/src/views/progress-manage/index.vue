<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header">
			<div class="header-left-toolbar">
				<el-button round :icon="Plus" @click="addProgress">新增进度</el-button>
			</div>
			<div class="header-right-search">
				<el-popover :width="400" :visible="visible" :teleported="false" placement="bottom-start">
					<template #reference>
						<el-button :icon="Search" @click="visible = !visible">检索</el-button>
					</template>
					<el-form label-position="top" label-width="100px" :model="pagination" @keyup.enter="searchData">
						<el-form-item label="进度编号">
							<el-input v-model="pagination.prId" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="阶段编号">
							<el-input v-model="pagination.psId" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="进度标题">
							<el-input v-model="pagination.prTitle" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="学生姓名">
							<el-input v-model="pagination.uName" placeholder="请输入" />
						</el-form-item>
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

		<el-table :data="progressData" style="width: 100%" border stripe v-loading="loading" @row-dblclick="handleRowDblclick">
			<el-table-column prop="prId" label="进度编号" min-width="100" />
			<el-table-column prop="psId" label="阶段编号" min-width="100" />
			<el-table-column prop="prTitle" label="进度标题" min-width="150" />
			<el-table-column prop="pTitle" label="课题名称" min-width="150" />
			<el-table-column prop="uName" label="学生姓名" min-width="120" />
			<el-table-column prop="createdAt" label="创建时间" min-width="150">
				<template #default="{ row }">
					{{ formatDate(row.createdAt) }}
				</template>
			</el-table-column>
			<el-table-column label="操作" min-width="250" fixed="right">
				<template #default="{ row }">
					<el-button type="primary" size="small" class="mr-2" @click="editProgress(row)">编辑</el-button>
					<el-button type="success" size="small" class="mr-2" @click="evaluateProgress(row)">评价</el-button>
					<el-button type="danger" size="small" class="mr-2" @click="deleteProgress(row)">删除</el-button>
					<el-button type="info" size="small" @click="viewProgressDetail(row)">详情</el-button>
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

	<!-- 新增/编辑进度对话框 -->
	<el-dialog v-model="dialogVisible" :title="isEdit ? '编辑进度' : '新增进度'" width="500px">
		<el-form ref="progressFormRef" :model="progressForm" :rules="progressRules" label-width="100px">
			<el-form-item label="选课ID" prop="psId">
				<el-input v-model="progressForm.psId" placeholder="请输入选课ID" />
			</el-form-item>
			<el-form-item label="进度标题" prop="prTitle">
				<el-input v-model="progressForm.prTitle" placeholder="请输入进度标题" />
			</el-form-item>
			<el-form-item label="进度内容" prop="prContent">
				<el-input v-model="progressForm.prContent" type="textarea" :rows="4" placeholder="请输入进度内容" />
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="dialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
			</span>
		</template>
	</el-dialog>

	<!-- 评价对话框 -->
	<el-dialog v-model="evaluateDialogVisible" title="教师评价" width="500px">
		<el-form ref="evaluateFormRef" :model="evaluateForm" :rules="evaluateRules" label-width="100px">
			<el-form-item label="评价内容" prop="prTeacherComment">
				<el-input v-model="evaluateForm.prTeacherComment" type="textarea" :rows="4" placeholder="请输入评价内容" />
			</el-form-item>
			<el-form-item label="评分" prop="prScore">
				<el-input-number v-model="evaluateForm.prScore" :min="0" :max="100" placeholder="请输入评分(0-100)" />
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="evaluateDialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="evaluateLoading" @click="submitEvaluate">提交评价</el-button>
			</span>
		</template>
	</el-dialog>

	<!-- 进度详情对话框 -->
	<el-dialog v-model="detailDialogVisible" title="进度详情" width="500px">
		<div v-if="currentProgress" class="progress-detail">
			<div class="mb-4 p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">基本信息</h3>
				<div class="grid grid-cols-2 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">进度编号</div>
						<div class="font-medium">{{ currentProgress.prId }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">阶段编号</div>
						<div class="font-medium">{{ currentProgress.psId }}</div>
					</div>
				</div>
			</div>
			<div class="mb-4 p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">进度信息</h3>
				<div class="grid grid-cols-1 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">进度标题</div>
						<div class="font-medium">{{ currentProgress.prTitle }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">课题名称</div>
						<div class="font-medium">{{ currentProgress.pTitle }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">学生姓名</div>
						<div class="font-medium">{{ currentProgress.uName }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">创建时间</div>
						<div class="font-medium">{{ formatDate(currentProgress.createdAt) }}</div>
					</div>
				</div>
			</div>
			<div class="p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">进度内容</h3>
				<div class="text-sm whitespace-pre-wrap">{{ currentProgress.prContent }}</div>
			</div>
			<div v-if="currentProgress.prTeacherComment || currentProgress.prScore" class="mt-4 p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">教师评价</h3>
				<div v-if="currentProgress.prTeacherComment" class="text-sm mb-2">
					<div class="text-gray-500">评价内容</div>
					<div class="whitespace-pre-wrap">{{ currentProgress.prTeacherComment }}</div>
				</div>
				<div v-if="currentProgress.prScore != null" class="text-sm">
					<div class="text-gray-500">评分</div>
					<div class="font-medium text-orange-500 text-lg">{{ currentProgress.prScore }} 分</div>
				</div>
			</div>
		</div>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Plus, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
import {
	postProgressReportsInsert,
	postProgressReportsFindAll,
	postProgressReportsFind,
	postProgressReportsUpdate,
	postProgressReportsDelete,
	postProgressReportsEvaluate,
} from '@/apis/progressReports';

// 定义类型
interface ProgressData {
	prId: number;
	psId: number;
	prTitle: string;
	prContent: string;
	pTitle: string;
	uName: string;
	createdAt: string;
	[key: string]: any;
}

// 定义页面名称
defineOptions({
	name: 'ProgressManage',
});

// 分页和查询参数
const pagination = ref<{
	pageIndex: number;
	pageSize: number;
	prId?: number;
	psId?: number;
	prTitle?: string;
	uName?: string;
	pTitle?: string;
}>({
	pageIndex: 1,
	pageSize: 10,
});

// 进度数据
const progressData = ref<ProgressData[]>([]);
const total = ref(0);
const loading = ref(false);
const visible = ref(false);

// 对话框控制
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const evaluateDialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const evaluateLoading = ref(false);
const progressFormRef = ref<FormInstance>();
const evaluateFormRef = ref<FormInstance>();
const currentProgress = ref<ProgressData | null>(null);

// 进度表单
const progressForm = reactive({
	prId: undefined as number | undefined,
	psId: undefined as number | undefined,
	prTitle: '',
	prContent: '',
});

// 评价表单
const evaluateForm = reactive({
	prId: undefined as number | undefined,
	prTeacherComment: '',
	prScore: 100,
});

// 表单验证规则
const progressRules = reactive<FormRules>({
	psId: [{ required: true, message: '请输入阶段编号', trigger: 'blur' }],
	prTitle: [{ required: true, message: '请输入进度标题', trigger: 'blur' }],
	prContent: [{ required: true, message: '请输入进度内容', trigger: 'blur' }],
});

// 评价表单验证规则
const evaluateRules = reactive<FormRules>({
	prTeacherComment: [{ required: true, message: '请输入评价内容', trigger: 'blur' }],
	prScore: [{ required: true, message: '请输入评分', trigger: 'blur' }],
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

// 加载进度数据
async function searchData() {
	loading.value = true;
	try {
		const params = filterEmptyParams({
			pageIndex: pagination.value.pageIndex,
			pageSize: pagination.value.pageSize,
			prId: pagination.value.prId,
			psId: pagination.value.psId,
			prTitle: pagination.value.prTitle,
			uName: pagination.value.uName,
			pTitle: pagination.value.pTitle,
		});

		const { data } = await postProgressReportsFindAll({ data: params });

		if (data) {
			progressData.value = data.list || [];
			total.value = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取进度数据失败');
		console.error('获取进度数据失败:', error);
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

// 新增进度
function addProgress() {
	isEdit.value = false;
	resetForm();
	dialogVisible.value = true;
}

// 编辑进度
function editProgress(row: ProgressData) {
	isEdit.value = true;
	resetForm();
	progressForm.prId = row.prId;
	progressForm.psId = row.psId;
	progressForm.prTitle = row.prTitle;
	progressForm.prContent = row.prContent || '';
	dialogVisible.value = true;
}

// 查看进度详情
async function viewProgressDetail(row: ProgressData) {
	try {
		const { data } = await postProgressReportsFind({ data: { prId: row.prId } });
		if (data) {
			currentProgress.value = data;
			detailDialogVisible.value = true;
		}
	} catch (error) {
		ElMessage.error('获取进度详情失败');
	}
}

// 删除进度
function deleteProgress(row: ProgressData) {
	ElMessageBox.confirm(`确定要删除进度编号为 ${row.prId} 的进度记录吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				await postProgressReportsDelete({ data: { prId: row.prId } });
				ElMessage.success('删除成功');
				searchData();
			} catch (error) {
				ElMessage.error('删除失败');
			}
		})
		.catch(() => {});
}

// 评价进度
function evaluateProgress(row: ProgressData) {
	evaluateForm.prId = row.prId;
	evaluateForm.prTeacherComment = '';
	evaluateForm.prScore = 100;
	if (evaluateFormRef.value) {
		evaluateFormRef.value.resetFields();
	}
	evaluateDialogVisible.value = true;
}

// 提交评价
async function submitEvaluate() {
	if (!evaluateFormRef.value) return;

	await evaluateFormRef.value.validate(async (valid) => {
		if (valid) {
			evaluateLoading.value = true;
			try {
				const params = {
					prId: evaluateForm.prId,
					prTeacherComment: evaluateForm.prTeacherComment,
					prScore: evaluateForm.prScore,
				};
				await postProgressReportsEvaluate({ data: params });
				ElMessage.success('评价提交成功');
				evaluateDialogVisible.value = false;
				searchData();
			} catch (error) {
				ElMessage.error('评价提交失败');
				console.error(error);
			} finally {
				evaluateLoading.value = false;
			}
		}
	});
}

// 提交表单
async function submitForm() {
	if (!progressFormRef.value) return;

	await progressFormRef.value.validate(async (valid) => {
		if (valid) {
			submitLoading.value = true;
			try {
				const params = {
					psId: progressForm.psId,
					prTitle: progressForm.prTitle,
					prContent: progressForm.prContent,
				};

				if (isEdit.value && progressForm.prId) {
					// 编辑进度
					await postProgressReportsUpdate({
						data: {
							...params,
							prId: progressForm.prId,
						},
					});
					ElMessage.success('更新成功');
				} else {
					// 新增进度
					await postProgressReportsInsert({ data: params });
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
	progressForm.prId = undefined;
	progressForm.psId = undefined;
	progressForm.prTitle = '';
	progressForm.prContent = '';
	if (progressFormRef.value) {
		progressFormRef.value.resetFields();
	}
}

// 双击行查看详情
function handleRowDblclick(row: ProgressData) {
	viewProgressDetail(row);
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
