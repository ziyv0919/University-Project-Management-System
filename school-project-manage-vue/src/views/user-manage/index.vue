<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header">
			<div class="header-left-toolbar">
				<el-button round :icon="Plus" @click="addUser">新增</el-button>
			</div>
			<div class="header-right-search">
				<el-popover :width="400" :visible="visible" :teleported="false" placement="bottom-start">
					<template #reference>
						<el-button :icon="Search" @click="visible = !visible">检索</el-button>
					</template>
					<el-form label-position="top" label-width="100px" :model="pagination" @keyup.enter="searchData">
						<el-form-item label="用户ID">
							<el-input v-model="pagination.uId" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="用户名">
							<el-input v-model="pagination.uUsername" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="姓名">
							<el-input v-model="pagination.uName" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="角色">
							<el-select v-model="pagination.uRole" placeholder="请选择" clearable>
								<el-option :value="1" label="管理员" />
								<el-option :value="2" label="教师" />
								<el-option :value="3" label="学生" />
							</el-select>
						</el-form-item>
						<el-form-item label="邮箱">
							<el-input v-model="pagination.uEmail" placeholder="请输入" />
						</el-form-item>
						<el-form-item class="search-footer">
							<el-button @click="clearSearchForm">清空</el-button>
							<el-button type="primary" @click="searchData">搜索</el-button>
						</el-form-item>
					</el-form>
				</el-popover>
			</div>
		</div>

		<el-table :data="userData" style="width: 100%" border stripe v-loading="loading" @row-dblclick="handleRowDblclick">
			<el-table-column prop="UId" label="用户ID" min-width="80" />
			<el-table-column prop="UUsername" label="用户名" min-width="120" />
			<el-table-column prop="UName" label="姓名" min-width="120" />
			<el-table-column label="角色" min-width="100">
				<template #default="{ row }">
					<el-tag :type="row.URole === 1 ? 'danger' : row.URole === 2 ? 'warning' : 'success'">
						{{ row.URole === 1 ? '管理员' : row.URole === 2 ? '教师' : '学生' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="UEmail" label="邮箱" min-width="150" />
			<el-table-column prop="UPhone" label="联系电话" min-width="120" />
			<el-table-column prop="createdAt" label="创建时间" min-width="150">
				<template #default="{ row }">
					{{ formatDate(row.createdAt) }}
				</template>
			</el-table-column>
			<el-table-column label="操作" min-width="200" fixed="right">
				<template #default="{ row }">
					<el-button type="primary" size="small" class="mr-2" @click="editUser(row)">编辑</el-button>
					<el-button type="danger" size="small" class="mr-2" @click="deleteUser(row)">删除</el-button>
					<el-button type="info" size="small" @click="viewUserDetail(row)">详情</el-button>
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

	<!-- 新增/编辑用户对话框 -->
	<el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
		<el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px">
			<el-form-item label="用户名" prop="uUsername" v-if="!isEdit">
				<el-input v-model="userForm.uUsername" placeholder="请输入用户名" />
			</el-form-item>
			<el-form-item label="密码" prop="uPassword" v-if="!isEdit">
				<el-input v-model="userForm.uPassword" type="password" placeholder="请输入密码" show-password />
			</el-form-item>
			<el-form-item label="姓名" prop="uName">
				<el-input v-model="userForm.uName" placeholder="请输入姓名" />
			</el-form-item>
			<el-form-item label="角色" prop="uRole">
				<el-select v-model="userForm.uRole" placeholder="请选择角色">
					<el-option :value="1" label="管理员" />
					<el-option :value="2" label="教师" />
					<el-option :value="3" label="学生" />
				</el-select>
			</el-form-item>
			<el-form-item label="邮箱" prop="uEmail">
				<el-input v-model="userForm.uEmail" placeholder="请输入邮箱" />
			</el-form-item>
			<el-form-item label="手机号" prop="uPhone">
				<el-input v-model="userForm.uPhone" placeholder="请输入手机号" />
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="dialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
			</span>
		</template>
	</el-dialog>

	<!-- 用户详情对话框 -->
	<el-dialog v-model="detailDialogVisible" title="用户详情" width="500px">
		<div v-if="currentUser" class="user-detail">
			<div class="mb-4 p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">基本信息</h3>
				<div class="grid grid-cols-2 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">用户ID</div>
						<div class="font-medium">{{ currentUser.UId }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">用户名</div>
						<div class="font-medium">{{ currentUser.UUsername }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">姓名</div>
						<div class="font-medium">{{ currentUser.UName }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">角色</div>
						<div class="font-medium">
							<el-tag size="small" :type="currentUser.URole === 1 ? 'danger' : currentUser.URole === 2 ? 'warning' : 'success'">
								{{ currentUser.URole === 1 ? '管理员' : currentUser.URole === 2 ? '教师' : '学生' }}
							</el-tag>
						</div>
					</div>
				</div>
			</div>
			<div class="mb-4 p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">联系方式</h3>
				<div class="grid grid-cols-1 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">邮箱</div>
						<div class="font-medium">{{ currentUser.UEmail || '未设置' }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">手机号</div>
						<div class="font-medium">{{ currentUser.UPhone || '未设置' }}</div>
					</div>
				</div>
			</div>
			<div class="p-4 bg-gray-50 rounded-lg">
				<h3 class="text-lg font-medium text-gray-900 mb-2">时间信息</h3>
				<div class="grid grid-cols-1 gap-4">
					<div class="text-sm">
						<div class="text-gray-500">创建时间</div>
						<div class="font-medium">{{ formatDate(currentUser.createdAt) }}</div>
					</div>
					<div class="text-sm">
						<div class="text-gray-500">更新时间</div>
						<div class="font-medium">{{ formatDate(currentUser.updatedAt) }}</div>
					</div>
				</div>
			</div>
		</div>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Plus, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
import { postUsersFindAll, postUsersInsert, postUsersUpdate, postUsersDelete, postUsersFind } from '@/apis/users';

defineOptions({
	name: 'UserManage',
});

// 分页和查询参数
const pagination = ref<{
	pageIndex: number;
	pageSize: number;
	uId?: number;
	uUsername?: string;
	uName?: string;
	uRole?: number;
	uEmail?: string;
}>({
	pageIndex: 1,
	pageSize: 10,
});

// 用户数据
const userData = ref([]);
const total = ref(0);
const loading = ref(false);
const visible = ref(false);

// 对话框控制
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const userFormRef = ref<FormInstance>();
const currentUser = ref(null);

// 用户表单
const userForm = reactive({
	uId: undefined as number | undefined,
	uUsername: '',
	uPassword: '',
	uName: '',
	uRole: 3,
	uEmail: '',
	uPhone: '',
});

// 表单验证规则
const userRules = reactive<FormRules>({
	uUsername: [
		{ required: true, message: '请输入用户名', trigger: 'blur' },
		{ min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
	],
	uPassword: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
	],
	uName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
	uRole: [{ required: true, message: '请选择角色', trigger: 'change' }],
	uEmail: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
	uPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }],
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

// 加载用户数据
async function searchData() {
	loading.value = true;
	try {
		const params = filterEmptyParams({
			pageIndex: pagination.value.pageIndex,
			pageSize: pagination.value.pageSize,
			uId: pagination.value.uId,
			uRole: pagination.value.uRole,
			uName: pagination.value.uName,
			uUsername: pagination.value.uUsername,
			uEmail: pagination.value.uEmail,
		});

		const { data } = await postUsersFindAll(params);

		if (data) {
			userData.value = data.list || [];
			total.value = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取用户数据失败');
		console.error('获取用户数据失败:', error);
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

// 新增用户
function addUser() {
	isEdit.value = false;
	resetForm();
	dialogVisible.value = true;
}

// 编辑用户
function editUser(row: any) {
	isEdit.value = true;
	resetForm();
	userForm.uId = row.UId;
	userForm.uName = row.UName;
	userForm.uRole = row.URole;
	userForm.uEmail = row.UEmail || '';
	userForm.uPhone = row.UPhone || '';
	dialogVisible.value = true;
}

// 查看用户详情
async function viewUserDetail(row: any) {
	try {
		const params = filterEmptyParams({
			uId: row.UId,
		});
		const { data } = await postUsersFind(params);
		if (data) {
			currentUser.value = data;
			detailDialogVisible.value = true;
		}
	} catch (error) {
		ElMessage.error('获取用户详情失败');
	}
}

// 删除用户
function deleteUser(row: any) {
	ElMessageBox.confirm(`确定要删除用户 ${row.UName} 吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				const params = filterEmptyParams({
					uId: row.UId,
				});
				await postUsersDelete(params);
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
	if (!userFormRef.value) return;

	await userFormRef.value.validate(async (valid) => {
		if (valid) {
			submitLoading.value = true;
			try {
				if (isEdit.value) {
					// 编辑用户
					const params = filterEmptyParams({
						uId: userForm.uId,
						uName: userForm.uName,
						uRole: userForm.uRole,
						uEmail: userForm.uEmail,
						uPhone: userForm.uPhone,
					});
					await postUsersUpdate(params);
					ElMessage.success('更新成功');
				} else {
					// 新增用户
					const params = filterEmptyParams({
						uUsername: userForm.uUsername,
						uPassword: userForm.uPassword,
						uName: userForm.uName,
						uRole: userForm.uRole,
						uEmail: userForm.uEmail,
						uPhone: userForm.uPhone,
					});
					await postUsersInsert(params);
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
	userForm.uId = undefined;
	userForm.uUsername = '';
	userForm.uPassword = '';
	userForm.uName = '';
	userForm.uRole = 3;
	userForm.uEmail = '';
	userForm.uPhone = '';
	if (userFormRef.value) {
		userFormRef.value.resetFields();
	}
}

// 双击行查看详情
function handleRowDblclick(row: any) {
	viewUserDetail(row);
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
