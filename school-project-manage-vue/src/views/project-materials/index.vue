<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header">
			<div class="header-left-toolbar">
				<el-button round :icon="Plus" @click="addMaterial">新增</el-button>
			</div>
			<div class="header-right-search">
				<el-popover :width="400" :visible="visible" :teleported="false" placement="bottom-start">
					<template #reference>
						<el-button :icon="Search" @click="visible = !visible">检索</el-button>
					</template>
					<el-form label-position="top" label-width="100px" :model="pagination" @keyup.enter="searchData">
						<el-form-item label="文件编号">
							<el-input v-model="pagination.fId" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="文件名称">
							<el-input v-model="pagination.fOriginalName" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="课题">
							<el-select
								v-model="pagination.pId"
								placeholder="请选择课题"
								filterable
								remote
								:remote-method="searchProjects"
								:loading="projectLoading"
								clearable
							>
								<el-option v-for="item in projectOptions" :key="item.value" :label="item.label" :value="item.value" />
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

		<el-table
			ref="materialTableRef"
			v-loading="loading"
			:data="materialData"
			style="width: 100%"
			border
			stripe
			@selection-change="handleSelectionChange"
			@row-dblclick="handleRowDblclick"
		>
			<el-table-column type="selection" width="55" />
			<el-table-column prop="FId" label="文件编号" min-width="80" />
			<el-table-column prop="FOriginalName" label="文件名称" min-width="150" />
			<el-table-column prop="FSize" label="文件大小" min-width="100" />
			<el-table-column prop="PId" label="课题ID" min-width="100" />
			<el-table-column prop="createdAt" label="创建时间" min-width="150">
				<template #default="{ row }">
					{{ formatDate(row.createdAt) }}
				</template>
			</el-table-column>
			<el-table-column label="操作" min-width="320" fixed="right">
				<template #default="{ row }">
					<el-button type="danger" size="small" class="mr-2" @click="deleteMaterial(row)">删除</el-button>
					<el-button type="info" size="small" class="mr-2" @click="viewMaterialDetail(row)">详情</el-button>
					<el-button type="success" size="small" @click="downloadMaterial(row)">下载</el-button>
				</template>
			</el-table-column>
		</el-table>

		<div class="flex items-center justify-between mt-4">
			<div>
				<el-button type="danger" :disabled="selectedMaterials.length === 0" @click="batchDeleteMaterials">批量删除</el-button>
			</div>
			<div class="text-center">
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

		<!-- 新增/编辑资料对话框 -->
		<el-dialog v-model="dialogVisible" title="新增课题资料" width="500px">
			<el-form ref="materialFormRef" :model="materialForm" :rules="materialRules" label-width="100px">
				<el-form-item label="课题" prop="pId">
					<el-select
						v-model="materialForm.pId"
						placeholder="请选择课题"
						filterable
						remote
						:remote-method="searchProjects"
						:loading="projectLoading"
						clearable
					>
						<el-option v-for="item in projectOptions" :key="item.value" :label="item.label" :value="item.value" />
					</el-select>
				</el-form-item>
				<!-- 文件上传 -->
				<el-form-item label="文件上传" prop="file">
					<el-upload class="upload-demo" drag :before-upload="beforeUpload" :http-request="customUploadRequest" :limit="1">
						<i class="el-icon-upload"></i>
						<div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
					</el-upload>
				</el-form-item>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="dialogVisible = false">取消</el-button>
					<el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
				</span>
			</template>
		</el-dialog>

		<!-- 资料详情对话框 -->
		<el-dialog v-model="detailDialogVisible" title="课题资料详情" width="500px">
			<div v-if="currentMaterial" class="material-detail">
				<div class="mb-4 p-4 bg-gray-50 rounded-lg">
					<h3 class="text-lg font-medium text-gray-900 mb-2">基本信息</h3>
					<div class="grid grid-cols-2 gap-4">
						<div class="text-sm">
							<div class="text-gray-500">文件名称</div>
							<div class="font-medium">{{ currentMaterial.FOriginalName }}</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">文件大小</div>
							<div class="font-medium">{{ currentMaterial.FSize }}</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">课题ID</div>
							<div class="font-medium">{{ currentMaterial.PId }}</div>
						</div>
					</div>
				</div>
				<div class="mb-4 p-4 bg-gray-50 rounded-lg">
					<h3 class="text-lg font-medium text-gray-900 mb-2">时间信息</h3>
					<div class="grid grid-cols-1 gap-4">
						<div class="text-sm">
							<div class="text-gray-500">创建时间</div>
							<div class="font-medium">{{ formatDate(currentMaterial.createdAt) }}</div>
						</div>
					</div>
				</div>
			</div>
		</el-dialog>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Plus, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
import { postFilesBatchInsert, postFilesDelete, postFilesBatchDeleteFiles, postFilesFindAll } from '@/apis/files';
import { postProjectsRemoteSelect } from '@/apis';

defineOptions({
	name: 'ProjectMaterials',
});
const pagination = ref({
	pageIndex: 1,
	pageSize: 10,
	fId: '',
	pId: '',
	fOriginalName: '',
});
const materialData = ref([]);
const total = ref(0);
const loading = ref(false);
const visible = ref(false);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const submitLoading = ref(false);
const materialFormRef = ref<FormInstance>();
const materialTableRef = ref();
const currentMaterial = ref<any>(null);
const materialForm = reactive({
	fOriginalName: '',
	fSize: 0,
	pId: '',
	fFileUrl: '',
	file: null,
});
const materialRules = reactive<FormRules>({
	fOriginalName: [{ required: true, message: '请输入文件名称', trigger: 'blur' }],
	fSize: [{ required: true, message: '请输入文件大小', trigger: 'blur' }],
	pId: [{ required: true, message: '请输入课题ID', trigger: 'blur' }],
});
const selectedMaterials = ref<any[]>([]);
// 动态获取上传和访问地址
const uploadUrl = import.meta.env.VITE_APP_FILE_UPLOAD_URL;
const fileShowUrl = import.meta.env.VITE_APP_FILE_SHOW_URL;

function handleSelectionChange(selection: any[]) {
	selectedMaterials.value = selection;
}

const projectLoading = ref(false);

const projectOptions = ref([]);

// 远程搜索课题
async function searchProjects(query: string) {
	if (query) {
		projectLoading.value = true;
		try {
			const { data } = await postProjectsRemoteSelect({
				searchText: query,
			});
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

function addMaterial() {
	resetForm();
	dialogVisible.value = true;
}

function viewMaterialDetail(row: any) {
	currentMaterial.value = row;
	detailDialogVisible.value = true;
}

function handleRowDblclick(row: any) {
	viewMaterialDetail(row);
}

function clearSearchForm() {
	pagination.value = {
		pageIndex: 1,
		pageSize: 10,
		fId: '',
		pId: '',
		fOriginalName: '',
	};
	searchData();
}

function handleCurrentChange(page: number) {
	pagination.value.pageIndex = page;
	searchData();
}

function handleSizeChange(size: number) {
	pagination.value.pageSize = size;
	searchData();
}

async function searchData() {
	loading.value = true;
	try {
		const params = filterEmptyParams({
			pageIndex: pagination.value.pageIndex,
			pageSize: pagination.value.pageSize,
			fId: pagination.value.fId,
			pId: pagination.value.pId,
			fOriginalName: pagination.value.fOriginalName,
		});
		const { data } = await postFilesFindAll(params);
		if (data) {
			materialData.value = data.list || [];
			total.value = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取课题资料数据失败');
		console.error('获取课题资料数据失败:', error);
	} finally {
		loading.value = false;
	}
}

function resetForm() {
	materialForm.fOriginalName = '';
	materialForm.fSize = 0;
	materialForm.pId = '';
	materialForm.file = null;
}

function beforeUpload(file: File) {
	if (file.size > 50 * 1024 * 1024) {
		ElMessage.error('文件大小不能超过50MB');
		return false;
	}
	materialForm.file = file;
	materialForm.fOriginalName = file.name;
	materialForm.fSize = file.size;
	return true;
}

// 自定义上传逻辑
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
	console.log(returnFile);
	if (returnFile.success) {
		ElMessage.success('文件上传成功');
		materialForm.fOriginalName = returnFile.originalName;
		materialForm.fSize = returnFile.size;
		materialForm.fFileUrl = returnFile.fileUrl;
		console.log(materialForm, 'materialForm1');
	} else {
		ElMessage.error('文件上传失败');
	}
}

async function submitForm() {
	if (!materialFormRef.value) return;
	await materialFormRef.value.validate(async (valid) => {
		if (valid) {
			submitLoading.value = true;
			console.log(materialForm, 'materialForm2');
			try {
				if (!materialForm.fFileUrl) {
					ElMessage.error('请先上传文件');
					return;
				}
				const params = filterEmptyParams({
					fOriginalName: materialForm.fOriginalName,
					fSize: materialForm.fSize,
					fFileUrl: materialForm.fFileUrl,
					pId: materialForm.pId,
				});
				await postFilesBatchInsert({ files: [params] });
				ElMessage.success('添加成功');
				dialogVisible.value = false;
				searchData();
			} catch (error) {
				ElMessage.error('添加失败');
				console.error(error);
			} finally {
				submitLoading.value = false;
			}
		}
	});
}

function downloadMaterial(row: any) {
	if (!row.FFileUrl) {
		ElMessage.warning('该资料没有可下载的文件');
		return;
	}
	window.open(fileShowUrl + row.FFileUrl, '_blank');
}

async function deleteMaterial(row: any) {
	ElMessageBox.confirm(`确定要删除文件 ${row.FOriginalName} 吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				await postFilesDelete({ fId: row.FId });
				ElMessage.success('删除成功');
				searchData();
			} catch (error) {
				ElMessage.error('删除失败');
			}
		})
		.catch(() => {});
}

async function batchDeleteMaterials() {
	if (selectedMaterials.value.length === 0) return;
	ElMessageBox.confirm(`确定要批量删除选中的 ${selectedMaterials.value.length} 个文件吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				const ids = selectedMaterials.value.map((item) => item.FId);
				await postFilesBatchDeleteFiles({ fIds: ids });
				ElMessage.success('批量删除成功');
				selectedMaterials.value = [];
				searchData();
			} catch (error) {
				ElMessage.error('批量删除失败');
			}
		})
		.catch(() => {});
}

function filterEmptyParams(params: Record<string, any>) {
	const filtered: Record<string, any> = {};
	Object.keys(params).forEach((key) => {
		if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
			filtered[key] = params[key];
		}
	});
	return filtered;
}

function formatDate(dateString: string) {
	if (!dateString) return '';
	const date = new Date(dateString);
	const y = date.getFullYear();
	const m = String(date.getMonth() + 1).padStart(2, '0');
	const d = String(date.getDate()).padStart(2, '0');
	const h = String(date.getHours()).padStart(2, '0');
	const min = String(date.getMinutes()).padStart(2, '0');
	const s = String(date.getSeconds()).padStart(2, '0');
	return `${y}-${m}-${d} ${h}:${min}:${s}`;
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
