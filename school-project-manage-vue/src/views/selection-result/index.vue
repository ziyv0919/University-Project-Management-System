<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header">
			<div class="header-left-toolbar">
				<el-button round :icon="Plus" @click="handleAdd">新增选题</el-button>
			</div>
			<div class="header-right-search">
				<el-popover :width="400" :visible="visible" :teleported="false" placement="bottom-start">
					<template #reference>
						<el-button :icon="Search" @click="visible = !visible">检索</el-button>
					</template>
					<el-form label-position="top" label-width="100px" :model="pagination" @keyup.enter="searchData">
						<el-form-item label="选题编号">
							<el-input v-model="pagination.psId" placeholder="请输入" />
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
						<el-form-item label="学生">
							<el-select
								v-model="pagination.uId"
								placeholder="请选择学生"
								filterable
								remote
								:remote-method="searchStudents"
								:loading="studentLoading"
								clearable
							>
								<el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" />
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

		<el-table v-loading="loading" :data="selectionData" style="width: 100%" border stripe :pagination="pagination">
			<el-table-column prop="psId" label="选题编号" min-width="120" />
			<el-table-column prop="PId" label="课题编号" min-width="120" />
			<el-table-column prop="pTitle" label="课题名称" min-width="150" />
			<el-table-column prop="uName" label="选题学生" min-width="120" />
			<el-table-column prop="createdAt" label="选题时间" min-width="150" />
			<el-table-column label="操作" min-width="150">
				<template #default="{ row }">
					<el-button type="danger" class="mr-2" @click="handleAction('delete', row)"> 删除 </el-button>
					<el-button type="info" @click="handleAction('detail', row)"> 详情 </el-button>
				</template>
			</el-table-column>
		</el-table>

		<div class="mt-4 text-center">
			<el-pagination
				:current-page="pagination.page"
				:page-size="pagination.size"
				:total="total"
				layout="total, prev, pager, next, jumper"
				@current-change="handlePageChange"
			/>
		</div>
	</div>
	<!-- 详情对话框 -->
	<el-dialog v-model="detailDialogVisible" title="选题详情" width="50%">
		<div v-if="detailData">
			<el-descriptions :column="1" border>
				<el-descriptions-item label="课题编号">{{ detailData.PId }}</el-descriptions-item>
				<el-descriptions-item label="课题名称">{{ detailData.pTitle }}</el-descriptions-item>
				<el-descriptions-item label="学生ID">{{ detailData.UId }}</el-descriptions-item>
				<el-descriptions-item label="学生姓名">{{ detailData.uName }}</el-descriptions-item>
				<el-descriptions-item label="选题时间">{{ detailData.createdAt }}</el-descriptions-item>
				<el-descriptions-item label="更新时间">{{ detailData.updatedAt }}</el-descriptions-item>
			</el-descriptions>
		</div>
	</el-dialog>

	<!-- 新增选题对话框 -->
	<el-dialog v-model="addDialogVisible" title="新增选题" width="40%">
		<el-form :model="addForm" label-width="80px">
			<el-form-item label="学生">
				<el-select v-model="addForm.uId" placeholder="请选择学生" filterable remote :remote-method="searchStudents" :loading="studentLoading">
					<el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" />
				</el-select>
			</el-form-item>
			<el-form-item label="课题">
				<el-select v-model="addForm.pId" placeholder="请选择课题" filterable remote :remote-method="searchProjects" :loading="projectLoading">
					<el-option v-for="item in projectOptions" :key="item.value" :label="item.label" :value="item.value" />
				</el-select>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="addDialogVisible = false">取消</el-button>
			<el-button type="primary" @click="submitAddForm">确定</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { Plus, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref, onMounted } from 'vue';
import { postProjectStudentsFindAll, postProjectStudentsFind, postProjectStudentsDelete, postProjectStudentsInsert } from '@/apis/projectStudents';
import { postProjectsRemoteSelect } from '@/apis/projects';
import { postUsersRemoteSelect } from '@/apis/users';

defineOptions({
	name: 'SelectionResult',
});

const pagination = ref<{
	page: number;
	size: number;
	psId?: number;
	pTitle?: string;
	uName?: string;
	pId?: number;
	uId?: number;
}>({
	page: 1,
	size: 10,
});

const selectionData = ref([]);
const total = ref(0);
const loading = ref(false);

function searchData() {
	loading.value = true;
	const params = {
		pageIndex: pagination.value.page,
		pageSize: pagination.value.size,
		psId: pagination.value.psId,
		pId: pagination.value.pId,
		uId: pagination.value.uId,
		uName: pagination.value.uName,
		pTitle: pagination.value.pTitle,
	};
	postProjectStudentsFindAll({ data: params })
		.then(({ data }) => {
			selectionData.value = data.list || [];
			total.value = data.total || 0;
		})
		.finally(() => {
			loading.value = false;
		});
}

const detailDialogVisible = ref(false);
const detailData = ref<any>(null);
const addDialogVisible = ref(false);
const addForm = ref({
	uId: null,
	pId: null,
});
const projectOptions = ref([]);
const studentOptions = ref([]);
const visible = ref(false);
const studentLoading = ref(false);
const projectLoading = ref(false);

function handleAction(action: string, row: any) {
	if (action === 'detail') {
		postProjectStudentsFind({ data: { psId: row.psId } })
			.then(({ data }) => {
				detailData.value = data;
				detailDialogVisible.value = true;
			})
			.catch(() => {
				ElMessage.error('获取详情失败');
			});
	} else if (action === 'delete') {
		ElMessageBox.confirm('确认删除选题结果？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		}).then(() => {
			postProjectStudentsDelete({ data: { psId: row.psId } })
				.then(() => {
					ElMessage.success('操作成功');
					searchData();
				})
				.catch(() => {
					ElMessage.error('操作失败');
				});
		});
	}
}

function clearSearchForm() {
	pagination.value = {
		page: 1,
		size: 10,
	};
	searchData();
}

function handlePageChange(page: number) {
	pagination.value.page = page;
	searchData();
}

function handleAdd() {
	addForm.value = {
		uId: null,
		pId: null,
	};
	addDialogVisible.value = true;
}

function submitAddForm() {
	if (!addForm.value.uId || !addForm.value.pId) {
		ElMessage.warning('请选择学生和课题');
		return;
	}

	postProjectStudentsInsert({
		data: {
			uId: addForm.value.uId,
			pId: addForm.value.pId,
		},
	})
		.then(() => {
			ElMessage.success('添加成功');
			addDialogVisible.value = false;
			searchData();
		})
		.catch(() => {
			ElMessage.error('添加失败');
		});
}

// 远程搜索学生
async function searchStudents(query: string) {
	if (query) {
		studentLoading.value = true;
		try {
			const { data } = await postUsersRemoteSelect({
				searchText: query,
			});
			if (data && Array.isArray(data)) {
				studentOptions.value = data.map((item) => ({
					value: item.UId,
					label: `${item.UName} (${item.UUsername})`,
				}));
			} else {
				studentOptions.value = [];
			}
		} catch (error) {
			console.error('搜索学生失败:', error);
			studentOptions.value = [];
		} finally {
			studentLoading.value = false;
		}
	} else {
		studentOptions.value = [];
	}
}

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
