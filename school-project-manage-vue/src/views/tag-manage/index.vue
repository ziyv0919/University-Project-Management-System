<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header">
			<div class="header-left-toolbar">
				<el-button round :icon="Plus" @click="addTag">新增标签</el-button>
			</div>
			<div class="header-right-search">
				<el-popover :width="400" :visible="visible" :teleported="false" placement="bottom-start">
					<template #reference>
						<el-button :icon="Search" @click="visible = !visible">检索</el-button>
					</template>
					<el-form label-position="top" label-width="100px" :model="pagination" @keyup.enter="searchData">
						<el-form-item label="标签ID">
							<el-input v-model="pagination.tId" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="标签名称">
							<el-input v-model="pagination.tName" placeholder="请输入" />
						</el-form-item>
						<el-form-item class="search-footer">
							<el-button @click="clearSearchForm">清空</el-button>
							<el-button type="primary" @click="searchData">搜索</el-button>
						</el-form-item>
					</el-form>
				</el-popover>
			</div>
		</div>

		<el-table :data="tagData" style="width: 100%" border stripe v-loading="loading" @row-dblclick="handleRowDblclick">
			<el-table-column prop="TId" label="标签ID" min-width="100" />
			<el-table-column prop="TName" label="标签名称" min-width="120" />
			<el-table-column prop="createdAt" label="创建时间" min-width="150">
				<template #default="{ row }">
					{{ formatDate(row.createdAt) }}
				</template>
			</el-table-column>
			<el-table-column prop="updatedAt" label="更新时间" min-width="150">
				<template #default="{ row }">
					{{ formatDate(row.updatedAt) }}
				</template>
			</el-table-column>
			<el-table-column label="操作" min-width="200" fixed="right">
				<template #default="{ row }">
					<el-button type="primary" size="small" class="mr-2" @click="editTag(row)">编辑</el-button>
					<el-button type="danger" size="small" class="mr-2" @click="deleteTag(row)">删除</el-button>
					<el-button type="info" size="small" @click="viewTagDetail(row)">详情</el-button>
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

		<!-- 新增/编辑标签对话框 -->
		<el-dialog v-model="dialogVisible" :title="isEdit ? '编辑标签' : '新增标签'" width="500px">
			<el-form ref="tagFormRef" :model="tagForm" :rules="tagRules" label-width="100px">
				<el-form-item label="标签名称" prop="tName">
					<el-input v-model="tagForm.tName" placeholder="请输入标签名称" />
				</el-form-item>
			</el-form>
			<template #footer>
				<div class="dialog-footer">
					<el-button @click="dialogVisible = false">取消</el-button>
					<el-button type="primary" @click="submitForm">确定</el-button>
				</div>
			</template>
		</el-dialog>

		<!-- 标签详情对话框 -->
		<el-dialog v-model="detailDialogVisible" title="标签详情" width="500px">
			<div v-if="currentTag" class="tag-detail">
				<div class="mb-4 p-4 bg-gray-50 rounded-lg">
					<h3 class="text-lg font-medium text-gray-900 mb-2">基本信息</h3>
					<div class="grid grid-cols-2 gap-4">
						<div class="text-sm">
							<div class="text-gray-500">标签ID</div>
							<div class="font-medium">{{ currentTag.TId }}</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">标签名称</div>
							<div class="font-medium">{{ currentTag.TName }}</div>
						</div>
					</div>
				</div>
				<div class="p-4 bg-gray-50 rounded-lg">
					<h3 class="text-lg font-medium text-gray-900 mb-2">时间信息</h3>
					<div class="grid grid-cols-1 gap-4">
						<div class="text-sm">
							<div class="text-gray-500">创建时间</div>
							<div class="font-medium">{{ formatDate(currentTag.createdAt) }}</div>
						</div>
						<div class="text-sm">
							<div class="text-gray-500">更新时间</div>
							<div class="font-medium">{{ formatDate(currentTag.updatedAt) }}</div>
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
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { postTagsInsert, postTagsFindAll, postTagsFind, postTagsUpdate, postTagsDelete } from '@/apis/tags';

defineOptions({
	name: 'TagManage',
});

// 分页和筛选参数
const pagination = ref<{
	pageIndex: number;
	pageSize: number;
	tId?: number;
	tName?: string;
}>({
	pageIndex: 1,
	pageSize: 10,
});

// 标签数据
const tagData = ref([]);
const total = ref(0);
const visible = ref(false);
const loading = ref(false);

// 对话框状态
const dialogVisible = ref(false);
const isEdit = ref(false);
const tagFormRef = ref<FormInstance>();

// 当前操作的标签
const currentTag = ref({
	TId: undefined as number | undefined,
	TName: '',
	createdAt: '',
	updatedAt: '',
});

// 表单数据
const tagForm = reactive({
	tId: undefined as number | undefined,
	tName: '',
});

// 表单验证规则
const tagRules = reactive<FormRules>({
	tName: [
		{ required: true, message: '请输入标签名称', trigger: 'blur' },
		{ min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' },
	],
});

// 过滤空字符串参数
function filterEmptyParams(params: Record<string, any>) {
	const filteredParams: Record<string, any> = {};
	for (const key in params) {
		if (params[key] !== '') {
			filteredParams[key] = params[key];
		}
	}
	return filteredParams;
}

// 分页切换
const handleCurrentChange = (page: number) => {
	pagination.value.pageIndex = page;
	searchData();
};

const handleSizeChange = (size: number) => {
	pagination.value.pageSize = size;
	pagination.value.pageIndex = 1;
	searchData();
};

// 清空搜索表单
function clearSearchForm() {
	pagination.value = {
		pageIndex: 1,
		pageSize: 10,
	};
	searchData();
}

// 加载标签数据
async function searchData() {
	loading.value = true;
	try {
		const params = filterEmptyParams({
			pageIndex: pagination.value.pageIndex,
			pageSize: pagination.value.pageSize,
			tId: pagination.value.tId,
			tName: pagination.value.tName,
		});

		const { data } = await postTagsFindAll(params);

		if (data) {
			tagData.value = data.list || [];
			total.value = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取标签数据失败');
		console.error('获取标签数据失败:', error);
	} finally {
		loading.value = false;
	}
}

// 添加标签
function addTag() {
	isEdit.value = false;
	resetTagForm();
	dialogVisible.value = true;
}

// 编辑标签
function editTag(row: any) {
	isEdit.value = true;
	resetTagForm();
	tagForm.tId = row.TId;
	tagForm.tName = row.TName;
	dialogVisible.value = true;
}

// 查看标签详情
async function viewTagDetail(row: any) {
	try {
		const params = filterEmptyParams({
			tId: row.TId,
		});
		const { data } = await postTagsFind(params);
		if (data) {
			currentTag.value = data;
			detailDialogVisible.value = true;
		}
	} catch (error) {
		ElMessage.error('获取标签详情失败');
	}
}

// 删除标签
function deleteTag(row: any) {
	ElMessageBox.confirm(`确定要删除标签 ${row.TName} 吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				const params = filterEmptyParams({
					tId: row.TId,
				});
				await postTagsDelete(params);
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
	if (!tagFormRef.value) return;

	await tagFormRef.value.validate(async (valid) => {
		if (valid) {
			try {
				if (isEdit.value) {
					// 编辑标签
					const params = filterEmptyParams({
						tId: tagForm.tId,
						tName: tagForm.tName,
					});
					await postTagsUpdate(params);
					ElMessage.success('更新成功');
				} else {
					// 新增标签
					const params = filterEmptyParams({
						tName: tagForm.tName,
					});
					await postTagsInsert(params);
					ElMessage.success('添加成功');
				}
				dialogVisible.value = false;
				searchData();
			} catch (error) {
				ElMessage.error(isEdit.value ? '更新失败' : '添加失败');
				console.error(error);
			}
		}
	});
}

// 重置表单
function resetTagForm() {
	tagForm.tId = undefined;
	tagForm.tName = '';
	if (tagFormRef.value) {
		tagFormRef.value.resetFields();
	}
}

// 双击行查看详情
function handleRowDblclick(row: any) {
	viewTagDetail(row);
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

// 对话框控制
const detailDialogVisible = ref(false);

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
	margin-bottom: 20px;

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

.delete-confirm {
	text-align: center;
	padding: 20px 0;
}
</style>
