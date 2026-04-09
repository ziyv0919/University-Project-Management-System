<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header">
			<div class="header-left-toolbar">
				<el-button round :icon="Plus" @click="handleAdd">新增通知</el-button>
			</div>
			<div class="header-right-search">
				<el-popover :width="400" :visible="visible" :teleported="false" placement="bottom-start">
					<template #reference>
						<el-button :icon="Search" @click="visible = !visible">检索</el-button>
					</template>
					<el-form label-position="top" label-width="100px" :model="pagination" @keyup.enter="searchData">
						<el-form-item label="通知ID">
							<el-input v-model="pagination.nId" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="用户ID">
							<el-input v-model="pagination.uId" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="通知标题">
							<el-input v-model="pagination.nTitle" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="通知类型">
							<el-select v-model="pagination.nType">
								<el-option v-for="item in notiTypeArr" :key="item.value" :label="item.label" :value="item.value"></el-option>
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
			v-loading="loading"
			:data="notificationData"
			style="width: 100%"
			border
			stripe
			:pagination="pagination"
			@row-dblclick="handleRowDblclick"
		>
			<el-table-column prop="nId" label="通知编号" min-width="120" />
			<el-table-column prop="nTitle" label="通知标题" min-width="150" />
			<el-table-column prop="nType" label="通知类型" min-width="120">
				<template #default="{ row }">
					{{ notiTypeArr.find((i) => i.value === row.nType)?.label }}
				</template>
			</el-table-column>
			<el-table-column prop="uId" label="用户ID" min-width="120" />
			<el-table-column prop="uName" label="用户" min-width="120" />
			<el-table-column prop="nIsRead" label="阅读状态" min-width="100">
				<template #default="{ row }">
					<el-tag :type="row.nIsRead ? 'success' : 'info'">
						{{ row.nIsRead ? '已读' : '未读' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="createdAt" label="发送时间" min-width="150" />
			<el-table-column label="操作" min-width="260">
				<template #default="{ row }">
					<el-button type="primary" class="mr-2" @click="handleAction('read', row)"> 标为已读 </el-button>
					<el-button type="info" class="mr-2" @click="handleAction('detail', row)"> 详情 </el-button>
					<el-button type="danger" @click="handleAction('delete', row)"> 删除 </el-button>
				</template>
			</el-table-column>
		</el-table>

		<div class="mt-4 text-center">
			<el-pagination
				:current-page="pagination.pageIndex"
				:page-size="pagination.pageSize"
				:total="total"
				layout="total, prev, pager, next, jumper"
				@current-change="handlePageChange"
			/>
		</div>
	</div>

	<!-- 详情对话框 -->
	<el-dialog v-model="detailDialogVisible" title="通知详情" width="50%">
		<div v-if="detailData">
			<el-descriptions :column="1" border>
				<el-descriptions-item label="通知编号">{{ detailData.nId }}</el-descriptions-item>
				<el-descriptions-item label="通知标题">{{ detailData.nTitle }}</el-descriptions-item>
				<el-descriptions-item label="通知类型">{{ notiTypeArr.find((i) => i.value === detailData.nType)?.label }}</el-descriptions-item>
				<el-descriptions-item label="用户ID">{{ detailData.uId }}</el-descriptions-item>
				<el-descriptions-item label="用户">{{ detailData.uName }}</el-descriptions-item>
				<el-descriptions-item label="阅读状态">{{ detailData.nIsRead ? '已读' : '未读' }}</el-descriptions-item>
				<el-descriptions-item label="发送时间">{{ detailData.createdAt }}</el-descriptions-item>
				<el-descriptions-item label="通知内容">{{ detailData.nContent }}</el-descriptions-item>
			</el-descriptions>
		</div>
	</el-dialog>

	<!-- 新增通知对话框 -->
	<el-dialog v-model="addDialogVisible" title="新增通知" width="40%">
		<el-form :model="addForm" label-width="80px" :rules="rules" ref="addFormRef">
			<el-form-item label="用户" prop="uId">
				<el-select
					v-model="addForm.uId"
					filterable
					remote
					reserve-keyword
					placeholder="请输入关键词搜索用户"
					:remote-method="remoteSearchUsers"
					:loading="userLoading"
					clearable
				>
					<el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value" />
				</el-select>
			</el-form-item>
			<el-form-item label="标题" prop="nTitle">
				<el-input v-model="addForm.nTitle" placeholder="请输入通知标题" />
			</el-form-item>
			<el-form-item label="类型" prop="nType">
				<el-select v-model="addForm.nType">
					<el-option v-for="item in notiTypeArr" :key="item.value" :label="item.label" :value="item.value"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="内容" prop="nContent">
				<el-input v-model="addForm.nContent" type="textarea" :rows="4" placeholder="请输入通知内容" />
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
import {
	postNotificationsInsert,
	postNotificationsFindAll,
	postNotificationsFind,
	postNotificationsDelete,
	postNotificationMarkAsRead,
} from '@/apis/notifications';
import { postUsersRemoteSelect } from '@/apis/users';

defineOptions({
	name: 'SystemNotification',
});

const pagination = ref<{
	pageIndex: number;
	pageSize: number;
	nId?: number;
	uId?: number;
	nType?: number;
	nTitle?: string;
}>({
	pageIndex: 1,
	pageSize: 10,
});

const notificationData = ref([]);
const total = ref(0);
const loading = ref(false);
const visible = ref(false);

const notiTypeArr = [
	{
		value: 1,
		label: '课题上新',
	},
	{
		value: 2,
		label: '学生申请课题',
	},
	{
		value: 3,
		label: '学生新增阶段性进展',
	},
	{
		value: 4,
		label: '学生删除阶段性进展',
	},
	{
		value: 5,
		label: '申请被审核',
	},
	{
		value: 6,
		label: '阶段性进展被评价',
	},
	{
		value: 7,
		label: '课题资料上新',
	},
];

// 详情对话框
const detailDialogVisible = ref(false);
const detailData = ref<any>(null);

// 新增对话框
const addDialogVisible = ref(false);
const addFormRef = ref();
const addForm = ref({
	uId: '',
	nTitle: '',
	nType: '',
	nContent: '',
});

// 用户远程搜索
const userOptions = ref([]);
const userLoading = ref(false);

// 远程搜索用户
async function remoteSearchUsers(query: string) {
	if (query) {
		userLoading.value = true;
		try {
			const { data } = await postUsersRemoteSelect({
				searchText: query,
			});
			if (data && Array.isArray(data)) {
				userOptions.value = data.map((item) => ({
					value: item.UId,
					label: `${item.UName} (${item.UUsername})`,
				}));
			} else {
				userOptions.value = [];
			}
		} catch (error) {
			console.error('获取用户列表失败', error);
			userOptions.value = [];
		} finally {
			userLoading.value = false;
		}
	} else {
		userOptions.value = [];
	}
}

// 表单验证规则
const rules = {
	uId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
	nTitle: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
	nType: [{ required: true, message: '请输入通知类型', trigger: 'blur' }],
	nContent: [{ required: true, message: '请输入通知内容', trigger: 'blur' }],
};

// 加载通知数据
function searchData() {
	loading.value = true;
	const params = {
		pageIndex: pagination.value.pageIndex,
		pageSize: pagination.value.pageSize,
		nId: pagination.value.nId,
		uId: pagination.value.uId,
		nType: pagination.value.nType,
		nTitle: pagination.value.nTitle,
	};
	postNotificationsFindAll({ data: params })
		.then(({ data }) => {
			notificationData.value = data.list || [];
			total.value = data.total || 0;
		})
		.catch(() => {
			ElMessage.error('获取通知列表失败');
		})
		.finally(() => {
			loading.value = false;
		});
}

// 处理分页变化
function handlePageChange(page: number) {
	pagination.value.pageIndex = page;
	searchData();
}

// 清空搜索表单
function clearSearchForm() {
	pagination.value = {
		pageIndex: 1,
		pageSize: 10,
	};
	searchData();
}

// 处理操作按钮
function handleAction(action: string, row: any) {
	if (action === 'detail') {
		postNotificationsFind({ data: { nId: row.nId } })
			.then(({ data }) => {
				detailData.value = data;
				detailDialogVisible.value = true;
			})
			.catch(() => {
				ElMessage.error('获取通知详情失败');
			});
	} else if (action === 'read') {
		postNotificationMarkAsRead({
			data: {
				nId: row.nId,
			},
		})
			.then(() => {
				ElMessage.success('已标记为已读');
				searchData();
			})
			.catch(() => {
				ElMessage.error('操作失败');
			});
	} else if (action === 'delete') {
		ElMessageBox.confirm('确认删除该通知？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		}).then(() => {
			postNotificationsDelete({ data: { nId: row.nId } })
				.then(() => {
					ElMessage.success('删除成功');
					searchData();
				})
				.catch(() => {
					ElMessage.error('删除失败');
				});
		});
	}
}

// 新增通知
function handleAdd() {
	addForm.value = {
		uId: '',
		nTitle: '',
		nType: '',
		nContent: '',
	};
	userOptions.value = [];
	addDialogVisible.value = true;
}

// 提交新增表单
function submitAddForm() {
	addFormRef.value?.validate((valid: boolean) => {
		if (valid) {
			postNotificationsInsert({
				data: {
					uId: addForm.value.uId,
					nTitle: addForm.value.nTitle,
					nType: addForm.value.nType,
					nContent: addForm.value.nContent,
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
		} else {
			return false;
		}
	});
}

// 双击行查看详情
function handleRowDblclick(row: any) {
	handleAction('detail', row);
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
