<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header mb-4">
			<el-page-header :title="`课题详情: ${projectDetail?.PTitle || ''}`" @back="goBack"> </el-page-header>
		</div>

		<el-card v-loading="loading" class="mb-4">
			<template #header>
				<div class="card-header">
					<span class="font-bold">课题基本信息</span>
				</div>
			</template>
			<div v-if="projectDetail" class="project-detail">
				<div class="mb-4 p-4 bg-gray-50 rounded-lg">
					<h3 class="text-lg font-medium text-gray-900 mb-2">项目封面</h3>
					<div class="flex justify-center mb-4">
						<img v-if="projectDetail.pCover" :src="fileShowUrl + projectDetail.pCover" class="w-32 h-32 object-cover rounded border" alt="课题封面" />
						<div v-else class="w-32 h-32 flex items-center justify-center bg-gray-100 rounded">暂无封面</div>
					</div>
				</div>
				<div class="mb-4 p-4 bg-gray-50 rounded-lg">
					<div class="grid grid-cols-2 gap-4">
						<div class="text-sm">
							<div class="text-gray-500">课题编号</div>
							<div class="font-medium">{{ projectDetail.PId }}</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">课题名称</div>
							<div class="font-medium">{{ projectDetail.PTitle }}</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">研究方向</div>
							<div class="font-medium">{{ projectDetail.PDirection || '未设置' }}</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">难度等级</div>
							<div class="font-medium">
								<el-tag size="small" :type="projectDetail.PDifficulty === 1 ? 'success' : projectDetail.PDifficulty === 2 ? 'warning' : 'danger'">
									{{ projectDetail.PDifficulty === 1 ? '简单' : projectDetail.PDifficulty === 2 ? '中等' : '困难' }}
								</el-tag>
							</div>
						</div>
					</div>
				</div>
				<div class="mb-4 p-4 bg-gray-50 rounded-lg">
					<div class="grid grid-cols-1 gap-4">
						<div class="text-sm">
							<div class="text-gray-500">课题描述</div>
							<div class="font-medium whitespace-pre-wrap">{{ projectDetail.PDescription || '未设置' }}</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">指导教师</div>
							<div class="font-medium">{{ projectDetail.UName || '未知' }} (ID: {{ projectDetail.UId }})</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">最大学生数</div>
							<div class="font-medium">{{ projectDetail.PMaxStudents }}</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">创建时间</div>
							<div class="font-medium">{{ formatDate(projectDetail.createdAt) }}</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">更新时间</div>
							<div class="font-medium">{{ formatDate(projectDetail.updatedAt) }}</div>
						</div>
					</div>
				</div>
			</div>
			<div v-else class="text-center text-gray-500">暂无课题信息</div>
		</el-card>

		<el-tabs type="border-card" class="mb-4">
			<el-tab-pane label="选课学生">
				<div v-loading="studentsLoading">
					<el-table :data="studentsList" style="width: 100%" border stripe>
						<el-table-column prop="UId" label="学生ID" min-width="80" />
						<el-table-column prop="uName" label="学生姓名" min-width="120" />
						<el-table-column prop="createdAt" label="选课时间" min-width="150">
							<template #default="{ row }">
								{{ formatDate(row.createdAt) }}
							</template>
						</el-table-column>
						<el-table-column v-if="canKickStudent" label="操作" min-width="120">
							<template #default="{ row }">
								<el-button type="danger" size="small" @click="handleKickStudent(row)">踢出</el-button>
							</template>
						</el-table-column>
					</el-table>
					<div v-if="studentsList.length === 0" class="text-center text-gray-500 py-4">暂无选课学生</div>
				</div>
			</el-tab-pane>

			<el-tab-pane label="课题资料">
				<div v-loading="materialsLoading">
					<el-table :data="materialsList" style="width: 100%" border stripe>
						<el-table-column prop="FId" label="资料ID" min-width="80" />
						<el-table-column prop="FOriginalName" label="资料名称" min-width="150" />
						<el-table-column prop="FSize" label="资料文件大小" min-width="200" />
						<el-table-column prop="FFileUrl" label="附件" min-width="150">
							<template #default="{ row }">
								<el-button v-if="row.FFileUrl" type="primary" link @click="downloadMaterial(row)"> 查看附件 </el-button>
								<span v-else>无附件</span>
							</template>
						</el-table-column>
						<el-table-column prop="createdAt" label="上传时间" min-width="150">
							<template #default="{ row }">
								{{ formatDate(row.createdAt) }}
							</template>
						</el-table-column>
						<el-table-column v-if="canKickStudent" label="操作" min-width="120">
							<template #default="{ row }">
								<el-button type="danger" size="small" @click="deleteMaterial(row)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
					<div v-if="materialsList.length === 0" class="text-center text-gray-500 py-4">暂无课题资料</div>
					<div v-if="canKickStudent" class="mt-4 flex gap-2">
						<el-button type="primary" :icon="Plus" @click="openAddMaterialDialog">新增资料</el-button>
					</div>
					<el-dialog v-model="addMaterialDialogVisible" title="新增课题资料" width="500px">
						<el-form ref="materialFormRef" :model="materialForm" :rules="materialRules" label-width="100px">
							<el-form-item label="文件上传" prop="fFileUrl">
								<el-upload
									class="upload-demo"
									drag
									:before-upload="beforeUpload"
									:http-request="customUploadRequest"
									:limit="1"
									:show-file-list="true"
								>
									<i class="el-icon-upload"></i>
									<div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
								</el-upload>
							</el-form-item>
						</el-form>
						<template #footer>
							<span class="dialog-footer">
								<el-button @click="addMaterialDialogVisible = false">取消</el-button>
								<el-button type="primary" :loading="submitLoading" @click="submitMaterialForm">确定</el-button>
							</span>
						</template>
					</el-dialog>
				</div>
			</el-tab-pane>

			<el-tab-pane label="阶段性进展">
				<div v-loading="progressLoading">
					<el-table :data="progressList" style="width: 100%" border stripe>
						<el-table-column prop="prId" label="进度编号" min-width="100" />
						<el-table-column prop="psId" label="选课ID" min-width="100" />
						<el-table-column prop="prTitle" label="进度标题" min-width="150" />
						<el-table-column prop="uName" label="学生姓名" min-width="120" />
						<el-table-column prop="createdAt" label="创建时间" min-width="150">
							<template #default="{ row }">
								{{ formatDate(row.createdAt) }}
							</template>
						</el-table-column>
						<el-table-column label="操作" min-width="250" fixed="right">
							<template #default="{ row }">
								<el-button v-if="canEditProgress" type="primary" size="small" class="mr-2" @click="editProgress(row)">编辑</el-button>
								<el-button v-if="canEvaluateProgress" type="success" size="small" class="mr-2" @click="evaluateProgress(row)">评价</el-button>
								<el-button
									v-if="userRole === 1 || userRole === 2 || (userRole === 3 && row.uId === userInfo.UId)"
									type="danger"
									size="small"
									class="mr-2"
									@click="deleteProgress(row)"
									>删除</el-button
								>
								<el-button type="info" size="small" @click="viewProgressDetail(row)">详情</el-button>
							</template>
						</el-table-column>
					</el-table>
					<div v-if="progressList.length === 0" class="text-center text-gray-500 py-4">暂无阶段性进展</div>
					<div v-if="canAddProgress" class="mt-4 text-right">
						<el-button type="primary" :icon="Plus" @click="addProgress">新增进度</el-button>
					</div>

					<!-- 新增/编辑进度对话框 -->
					<el-dialog v-model="progressDialogVisible" :title="isEditProgress ? '编辑进度' : '新增进度'" width="500px">
						<el-form ref="progressFormRef" :model="progressForm" :rules="progressRules" label-width="100px">
							<!-- 选课ID隐藏域，自动带入，不让用户编辑 -->
							<input type="hidden" v-model="progressForm.psId" />
							<el-form-item label="进度标题" prop="prTitle">
								<el-input v-model="progressForm.prTitle" placeholder="请输入进度标题" />
							</el-form-item>
							<el-form-item label="进度内容" prop="prContent">
								<el-input v-model="progressForm.prContent" type="textarea" :rows="4" placeholder="请输入进度内容" />
							</el-form-item>
						</el-form>
						<template #footer>
							<span class="dialog-footer">
								<el-button @click="progressDialogVisible = false">取消</el-button>
								<el-button type="primary" :loading="progressSubmitLoading" @click="submitProgressForm">确定</el-button>
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
										<div class="text-gray-500">选课ID</div>
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
				</div>
			</el-tab-pane>
			<el-tab-pane v-if="userRole === 2 || userRole === 1" label="课题申请记录">
				<div v-loading="applicationLoading">
					<el-table :data="applicationData" style="width: 100%" border stripe @row-dblclick="viewApplicationDetail">
						<el-table-column prop="paId" label="申请编号" min-width="120" />
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
								<el-button v-if="row.paStatus === 1" type="success" size="small" class="mr-2" @click="approveApplication(row, 2)"> 通过 </el-button>
								<el-button v-if="row.paStatus === 1" type="danger" size="small" class="mr-2" @click="approveApplication(row, 3)"> 拒绝 </el-button>
								<el-button type="danger" size="small" class="mr-2" @click="deleteApplication(row)"> 删除 </el-button>
								<el-button type="info" size="small" @click="viewApplicationDetail(row)"> 详情 </el-button>
							</template>
						</el-table-column>
					</el-table>
					<div class="mt-4 text-center">
						<el-pagination
							:current-page="applicationPagination.pageIndex"
							:page-size="applicationPagination.pageSize"
							:total="applicationTotal"
							:page-sizes="[10, 20, 50, 100]"
							layout="total, sizes, prev, pager, next, jumper"
							@current-change="handleApplicationCurrentChange"
							@size-change="handleApplicationSizeChange"
						/>
					</div>
					<el-dialog v-model="applicationDetailDialogVisible" title="申请详情" width="500px">
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
											<el-tag
												size="small"
												:type="currentApplication.paStatus === 2 ? 'success' : currentApplication.paStatus === 3 ? 'danger' : 'info'"
											>
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
				</div>
			</el-tab-pane>
		</el-tabs>
	</div>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue';
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
import { postFilesBatchInsert, postFilesDelete, postFilesFindByProjectId } from '@/apis/files';
import {
	postProgressReportsInsert,
	postProgressReportsFindAll,
	postProgressReportsFind,
	postProgressReportsUpdate,
	postProgressReportsDelete,
	postProgressReportsEvaluate,
} from '@/apis/progressReports';
import { postProjectsFind } from '@/apis/projects';
import { postProjectStudentsFindAll, postProjectStudentsDelete } from '@/apis/projectStudents';
import useSysStore from '@/store/sys';

defineOptions({
	name: 'ProjectDetail',
});

const progressDialogVisible = ref(false);
const isEditProgress = ref(false);
const progressFormRef = ref<FormInstance>();
const progressForm = reactive({
	prId: undefined as number | undefined,
	psId: undefined as number | undefined,
	prTitle: '',
	prContent: '',
});
const progressRules = reactive<FormRules>({
	prTitle: [{ required: true, message: '请输入进度标题', trigger: 'blur' }],
	prContent: [{ required: true, message: '请输入进度内容', trigger: 'blur' }],
});
const progressSubmitLoading = ref(false);

const evaluateDialogVisible = ref(false);
const evaluateFormRef = ref<FormInstance>();
const evaluateForm = reactive({
	prId: undefined as number | undefined,
	prTeacherComment: '',
	prScore: 100,
});
const evaluateRules = reactive<FormRules>({
	prTeacherComment: [{ required: true, message: '请输入评价内容', trigger: 'blur' }],
	prScore: [{ required: true, message: '请输入评分', trigger: 'blur' }],
});
const evaluateLoading = ref(false);

const detailDialogVisible = ref(false);
const currentProgress = ref<any>(null);

// 用户信息与角色
const sysStore = useSysStore();
const userInfo = sysStore.userInfo;
const userRole = userInfo.URole;
const canAddProgress = computed(() => userRole === 1 || userRole === 3);
const canEditProgress = computed(() => userRole === 1);
const canEvaluateProgress = computed(() => userRole === 1 || userRole === 2);

// 课题申请记录相关
import {
	postProjectApplicationsFindAll,
	postProjectApplicationsApprove,
	postProjectApplicationsDelete,
	postProjectApplicationsFind,
} from '@/apis/projectApplications';
const applicationLoading = ref(false);
const applicationDetailDialogVisible = ref(false);
const applicationPagination = reactive({
	pageIndex: 1,
	pageSize: 10,
	paId: undefined as number | undefined,
	uName: '',
	paStatus: undefined as number | undefined,
});
const applicationData = ref<any[]>([]);
const applicationTotal = ref(0);
const currentApplication = ref<any>(null);

async function fetchApplications() {
	applicationLoading.value = true;
	try {
		const params = {
			pageIndex: applicationPagination.pageIndex,
			pageSize: applicationPagination.pageSize,
			paId: applicationPagination.paId,
			paStatus: applicationPagination.paStatus,
			uName: applicationPagination.uName,
			pId: projectId.value, // 只查当前课题
		};
		const { data } = await postProjectApplicationsFindAll(params);
		if (data) {
			applicationData.value = data.list || [];
			applicationTotal.value = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取申请数据失败');
	} finally {
		applicationLoading.value = false;
	}
}

function handleApplicationCurrentChange(page: number) {
	applicationPagination.pageIndex = page;
	fetchApplications();
}
function handleApplicationSizeChange(size: number) {
	applicationPagination.pageSize = size;
	fetchApplications();
}
async function approveApplication(row: any, status: number) {
	try {
		await postProjectApplicationsApprove({ paId: row.paId, paStatus: status });
		ElMessage.success(status === 2 ? '已通过' : '已拒绝');
		fetchApplications();
	} catch (error) {
		ElMessage.error('操作失败');
	}
}
async function deleteApplication(row: any) {
	ElMessageBox.confirm('确认删除该申请记录？', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	}).then(async () => {
		try {
			await postProjectApplicationsDelete({ paId: row.paId });
			ElMessage.success('删除成功');
			fetchApplications();
		} catch (error) {
			ElMessage.error('删除失败');
		}
	});
}
async function viewApplicationDetail(row: any) {
	try {
		const { data } = await postProjectApplicationsFind({ paId: row.paId });
		if (data) {
			currentApplication.value = data;
			applicationDetailDialogVisible.value = true;
		}
	} catch (error) {
		ElMessage.error('获取详情失败');
	}
}

// 获取阶段性进展
async function fetchProgress() {
	progressLoading.value = true;
	try {
		const { data } = await postProgressReportsFindAll({
			data: {
				pageIndex: 1,
				pageSize: 9999,
				pId: projectId.value,
			},
		});
		progressList.value = data?.list || [];
	} catch (error) {
		ElMessage.error('获取阶段性进展失败');
	} finally {
		progressLoading.value = false;
	}
	progressLoading.value = true;
	try {
		const { data } = await postProgressReportsFindAll({
			data: {
				pageIndex: 1,
				pageSize: 9999,
				pId: projectId.value,
			},
		});
		progressList.value = data?.list || [];
	} catch (error) {
		ElMessage.error('获取阶段性进展失败');
	} finally {
		progressLoading.value = false;
	}
}

// 新增进度
function addProgress() {
	isEditProgress.value = false;
	resetProgressForm();
	// 默认psId为第一个学生的psId
	if (studentsList.value.length > 0) {
		progressForm.psId = studentsList.value[0].psId;
	}
	progressDialogVisible.value = true;
	// 默认psId为第一个学生的psId
	if (studentsList.value.length > 0) {
		progressForm.psId = studentsList.value[0].psId;
	}
	progressDialogVisible.value = true;
}
// 编辑进度
function editProgress(row: any) {
	isEditProgress.value = true;
	resetProgressForm();
	progressForm.prId = row.prId;
	progressForm.psId = row.psId;
	progressForm.prTitle = row.prTitle;
	progressForm.prContent = row.prContent || '';
	progressDialogVisible.value = true;
}
// 删除进度
function deleteProgress(row: any) {
	ElMessageBox.confirm(`确定要删除进度编号为 ${row.prId} 的进度记录吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				await postProgressReportsDelete({ data: { prId: row.prId } });
				ElMessage.success('删除成功');
				fetchProgress();
			} catch (error) {
				ElMessage.error('删除失败');
			}
		})
		.catch(() => {});
}
// 评价进度
function evaluateProgress(row: any) {
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
	await evaluateFormRef.value.validate(async (valid: boolean) => {
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
				fetchProgress();
			} catch (error) {
				ElMessage.error('评价提交失败');
			}
			evaluateLoading.value = false;
		}
	});
}
// 提交进度表单
async function submitProgressForm() {
	if (!progressFormRef.value) return;
	await progressFormRef.value.validate(async (valid: boolean) => {
		if (valid) {
			progressSubmitLoading.value = true;
			try {
				const params = {
					psId: progressForm.psId,
					prTitle: progressForm.prTitle,
					prContent: progressForm.prContent,
				};
				if (isEditProgress.value && progressForm.prId) {
					await postProgressReportsUpdate({ data: { ...params, prId: progressForm.prId } });
					ElMessage.success('更新成功');
				} else {
					await postProgressReportsInsert({ data: params });
					ElMessage.success('添加成功');
				}
				progressDialogVisible.value = false;
				fetchProgress();
			} catch (error) {
				ElMessage.error(isEditProgress.value ? '更新失败' : '添加失败');
			}
			progressSubmitLoading.value = false;
		}
	});
}
function resetProgressForm() {
	progressForm.prId = undefined;
	progressForm.psId = undefined;
	progressForm.prTitle = '';
	progressForm.prContent = '';
	if (progressFormRef.value) {
		progressFormRef.value.resetFields();
	}
}
// 进度详情
async function viewProgressDetail(row: any) {
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

const route = useRoute();
const router = useRouter();
const projectId = ref<number>(Number(route.params.id) || 0);

// 课题详情
const loading = ref<boolean>(false);
const projectDetail = ref<any>(null);

// 选课学生
const studentsLoading = ref<boolean>(false);
const studentsList = ref<any[]>([]);

// 权限判断：管理员(1) 或课题教师(2 且是本课题教师)

const canKickStudent = computed(() => {
	const user = sysStore.userInfo;
	if (!user || !projectDetail.value) return false;
	return user.URole === 1 || (user.URole === 2 && user.UId === projectDetail.value.UId);
});

// 课题资料
const materialsLoading = ref<boolean>(false);
const materialsList = ref<any[]>([]);
const addMaterialDialogVisible = ref(false);
const materialFormRef = ref();
const materialForm = ref({
	fOriginalName: '',
	fFileUrl: '',
	fSize: 0,
});
const materialRules = {
	fFileUrl: [{ required: true, message: '请上传文件', trigger: 'blur' }],
};
const submitLoading = ref(false);

// 阶段性进展
const progressLoading = ref<boolean>(false);
const progressList = ref<any[]>([]);

// 格式化日期
const formatDate = (dateStr: string) => {
	if (!dateStr) return '-';
	const date = new Date(dateStr);
	return date.toLocaleString('zh-CN', {
		year: 'numeric',
		month: '2-digit',
		day: '2-digit',
		hour: '2-digit',
		minute: '2-digit',
		second: '2-digit',
	});
};

// 获取课题详情
const fetchProjectDetail = async () => {
	if (!projectId.value) {
		ElMessage.error('课题ID不能为空');
		return;
	}

	try {
		loading.value = true;
		const { data } = await postProjectsFind({
			pId: projectId.value,
		});
		projectDetail.value = data;
	} catch (error) {
		console.error('获取课题详情失败:', error);
		ElMessage.error('获取课题详情失败');
	} finally {
		loading.value = false;
	}
};

// 获取选课学生列表
const fetchStudents = async () => {
	if (!projectId.value) return;

	try {
		studentsLoading.value = true;
		const { data } = await postProjectStudentsFindAll({
			data: {
				pageIndex: 1,
				pageSize: 9999,
				pId: projectId.value,
			},
		});
		studentsList.value = data?.list || [];
	} finally {
		studentsLoading.value = false;
	}
};

// 获取课题资料列表
const fetchMaterials = async () => {
	if (!projectId.value) return;

	try {
		materialsLoading.value = true;
		const { data } = await postFilesFindByProjectId({
			pId: projectId.value,
		});
		materialsList.value = data || [];
	} catch (error) {
		console.error('获取课题资料失败:', error);
		ElMessage.error('获取课题资料失败');
	} finally {
		materialsLoading.value = false;
	}
};

const fileShowUrl = import.meta.env.VITE_APP_FILE_SHOW_URL;

// 下载课题资料
const downloadMaterial = (material: any) => {
	if (!material.FFileUrl) {
		ElMessage.warning('该资料没有附件');
		return;
	}

	window.open(fileShowUrl + material.FFileUrl, '_blank');
};

// 新增资料弹窗打开
function openAddMaterialDialog() {
	materialForm.value = { fOriginalName: '', fFileUrl: '', fSize: 0 };
	addMaterialDialogVisible.value = true;
}

// 文件上传前校验
function beforeUpload(file: File) {
	if (file.size > 50 * 1024 * 1024) {
		ElMessage.error('文件大小不能超过50MB');
		return false;
	}
	materialForm.value.file = file;
	materialForm.value.fOriginalName = file.name;
	materialForm.value.fSize = file.size;
	return true;
}

const uploadUrl = import.meta.env.VITE_APP_FILE_UPLOAD_URL;

// 自定义上传逻辑（假设已有上传接口，需适配实际接口）
async function customUploadRequest(option: any) {
	const formData = new FormData();
	formData.append('file', option.file);
	try {
		const response = await fetch(uploadUrl, {
			method: 'POST',
			body: formData,
		});
		const result = await response.json();
		option.onSuccess(result, option.file[0]);
		handleUploadSuccess(result);
	} catch (err) {
		option.onError(err);
		ElMessage.error('上传异常');
	}
}

function handleUploadSuccess(response: any) {
	const returnFile = response[0];
	if (returnFile.success) {
		ElMessage.success('文件上传成功');
		materialForm.value.fOriginalName = returnFile.originalName;
		materialForm.value.fSize = returnFile.size;
		materialForm.value.fFileUrl = returnFile.fileUrl;
		console.log(materialForm, 'materialForm1');
	} else {
		ElMessage.error('文件上传失败');
	}
}

// 提交资料表单
async function submitMaterialForm() {
	if (!materialFormRef.value) return;
	await materialFormRef.value.validate(async (valid: boolean) => {
		if (valid) {
			submitLoading.value = true;
			try {
				await postFilesBatchInsert({
					files: [
						{
							fOriginalName: materialForm.value.fOriginalName,
							fFileUrl: materialForm.value.fFileUrl,
							fSize: materialForm.value.fSize,
							pId: projectId.value,
						},
					],
				});
				ElMessage.success('添加成功');
				addMaterialDialogVisible.value = false;
				fetchMaterials();
			} catch (error) {
				ElMessage.error('添加失败');
			} finally {
				submitLoading.value = false;
			}
		}
	});
}

// 删除资料
async function deleteMaterial(row: any) {
	ElMessageBox.confirm(`确定要删除资料 ${row.FOriginalName} 吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				await postFilesDelete({ fId: row.FId });
				ElMessage.success('删除成功');
				fetchMaterials();
			} catch (error) {
				ElMessage.error('删除失败');
			}
		})
		.catch(() => {});
}

// 返回上一页
const goBack = () => {
	router.back();
};

// 踢出学生逻辑（复用 selection-result 页的删除逻辑）
function handleKickStudent(row: any) {
	ElMessageBox.confirm('确认将该学生移出本课题？', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	}).then(() => {
		postProjectStudentsDelete({ data: { psId: row.psId } })
			.then(() => {
				ElMessage.success('操作成功');
				fetchStudents();
			})
			.catch(() => {
				ElMessage.error('操作失败');
			});
	});
}

onMounted(async () => {
	await fetchProjectDetail();
	await Promise.all([fetchStudents(), fetchMaterials(), fetchProgress(), fetchApplications()]);
});
</script>

<style scoped>
.header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}
</style>
