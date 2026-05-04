<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header">
			<div class="header-left-toolbar">
				<el-button round :icon="Plus" @click="addAnnouncement">发布公告</el-button>
			</div>
			<div class="header-right-search">
				<el-popover :width="400" :visible="visible" :teleported="false" placement="bottom-start">
					<template #reference>
						<el-button :icon="Search" @click="visible = !visible">检索</el-button>
					</template>
					<el-form label-position="top" label-width="100px" :model="pagination" @keyup.enter="searchData">
						<el-form-item label="公告标题">
							<el-input v-model="pagination.aTitle" placeholder="请输入" />
						</el-form-item>
						<el-form-item label="发布人">
							<el-input v-model="pagination.uName" placeholder="请输入" />
						</el-form-item>
						<el-form-item class="search-footer">
							<el-button @click="clearSearchForm">清空</el-button>
							<el-button type="primary" @click="searchData">搜索</el-button>
						</el-form-item>
					</el-form>
				</el-popover>
			</div>
		</div>

		<el-table :data="announcementData" style="width: 100%" border stripe :pagination="pagination">
			<el-table-column prop="AId" label="公告编号" min-width="120" />
			<el-table-column prop="ATitle" label="公告标题" min-width="150" />
			<el-table-column prop="UName" label="发布人" min-width="120" />
			<el-table-column prop="createdAt" label="发布时间" min-width="150" />
			<el-table-column prop="ATarget" label="公告内容" min-width="200">
				<template #default="{ row }">
					<span>{{ ['', '管理员', '教师', '学生', '所有用户'][row.ATarget] }}</span>
				</template>
			</el-table-column>

			<el-table-column label="操作" min-width="250">
				<template #default="{ row }">
					<el-button type="primary" class="mr-2" @click="handleAction('edit', row)"> 编辑 </el-button>
					<el-button type="danger" class="mr-2" @click="handleAction('delete', row)"> 删除 </el-button>
					<el-button type="info" @click="handleAction('detail', row)"> 详情 </el-button>
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

	<!-- 添加公告详情对话框 -->
	<el-dialog v-model="detailDialogVisible" title="公告详情" width="50%">
		<div v-if="currentAnnouncement" class="announcement-detail">
			<div class="detail-item">
				<span class="label">公告标题：</span>
				<span class="value">{{ currentAnnouncement.ATitle }}</span>
			</div>
			<div class="detail-item">
				<span class="label">发布人：</span>
				<span class="value">{{ currentAnnouncement.UName }}</span>
			</div>
			<div class="detail-item">
				<span class="label">发布时间：</span>
				<span class="value">{{ currentAnnouncement.createdAt }}</span>
			</div>
			<div class="detail-item">
				<span class="label">更新时间：</span>
				<span class="value">{{ currentAnnouncement.updatedAt }}</span>
			</div>
			<div class="detail-item content">
				<span class="label">公告内容：</span>
				<div class="value content-text">{{ currentAnnouncement.AContent }}</div>
			</div>
		</div>
	</el-dialog>

	<!-- 添加/编辑公告对话框 -->
	<el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '添加公告'" width="50%">
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
				<el-button @click="dialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Plus, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
	postAnnouncementsInsert,
	postAnnouncementsUpdate,
	postAnnouncementsDelete,
	postAnnouncementsFind,
	postAnnouncementsFindAll,
} from '@/apis/announcements';

defineOptions({
	name: 'Announcement',
});

const pagination = ref<{
	pageIndex: number;
	pageSize: number;
	aTitle?: string;
	uId?: number;
	aTarget?: number;
	uName?: string;
}>({
	pageIndex: 1,
	pageSize: 10,
});

const announcementData = ref([]);
const total = ref(0);
const loading = ref(false);
const visible = ref(false);

const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const currentAnnouncement = ref<any>(null);
const announcementForm = reactive({
	aId: undefined as number | undefined,
	aTitle: '',
	aContent: '',
	aTarget: 1,
});

async function searchData() {
	loading.value = true;
	try {
		const params = filterEmptyParams({
			pageIndex: pagination.value.pageIndex,
			pageSize: pagination.value.pageSize,
			aTitle: pagination.value.aTitle,
			uId: pagination.value.uId,
			aTarget: pagination.value.aTarget,
			uName: pagination.value.uName,
		});

		const { data } = await postAnnouncementsFindAll({ data: params });

		if (data) {
			announcementData.value = data.list || [];
			total.value = data.totalRecords || 0;
		}
	} catch (error) {
		ElMessage.error('获取公告数据失败');
		console.error('获取公告数据失败:', error);
	} finally {
		loading.value = false;
	}
}

function clearSearchForm() {
	pagination.value = {
		pageIndex: 1,
		pageSize: 10,
	};
	searchData();
}

function addAnnouncement() {
	isEdit.value = false;
	resetForm();
	dialogVisible.value = true;
}

function editAnnouncement(row: any) {
	isEdit.value = true;
	resetForm();
	announcementForm.aId = row.AId;
	announcementForm.aTitle = row.ATitle;
	announcementForm.aContent = row.AContent;
	announcementForm.aTarget = row.ATarget;
	dialogVisible.value = true;
}

async function viewAnnouncementDetail(row: any) {
	try {
		const params = filterEmptyParams({
			aId: row.AId,
		});
		const { data } = await postAnnouncementsFind({ data: params });
		if (data) {
			currentAnnouncement.value = data;
			detailDialogVisible.value = true;
		}
	} catch (error) {
		ElMessage.error('获取公告详情失败');
		console.error('获取公告详情失败:', error);
	}
}

function deleteAnnouncement(row: any) {
	ElMessageBox.confirm(`确定要删除公告 ${row.ATitle} 吗？`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	})
		.then(async () => {
			try {
				const params = filterEmptyParams({
					aId: row.AId,
				});
				await postAnnouncementsDelete({ data: params });
				ElMessage.success('删除成功');
				searchData();
			} catch (error) {
				ElMessage.error('删除失败');
			}
		})
		.catch(() => {});
}

async function submitForm() {
	if (!announcementForm.aTitle || !announcementForm.aContent) return;

	submitLoading.value = true;
	try {
		if (isEdit.value) {
			const params = filterEmptyParams({
				aId: announcementForm.aId,
				aTitle: announcementForm.aTitle,
				aContent: announcementForm.aContent,
				aTarget: announcementForm.aTarget,
			});
			await postAnnouncementsUpdate({ data: params });
			ElMessage.success('更新成功');
		} else {
			const params = filterEmptyParams({
				aTitle: announcementForm.aTitle,
				aContent: announcementForm.aContent,
				aTarget: announcementForm.aTarget,
			});
			await postAnnouncementsInsert({ data: params });
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

function resetForm() {
	announcementForm.aId = undefined;
	announcementForm.aTitle = '';
	announcementForm.aContent = '';
	announcementForm.aTarget = 1;
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
// 格式化日期显示函数，将日期字符串转换为 YYYY-MM-DD HH:mm:ss 格式




function handleAction(action: string, row: any) {
	switch (action) {
		case 'edit':
			editAnnouncement(row);
			break;
		case 'delete':
			deleteAnnouncement(row);
			break;
		case 'detail':
			viewAnnouncementDetail(row);
			break;
		default:
			console.warn('Unknown action:', action);
	}
}

function handlePageChange(newPage: number) {
	pagination.value.pageIndex = newPage;
	searchData();
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

.announcement-detail {
	padding: 20px;

	.detail-item {
		margin-bottom: 15px;
		display: flex;

		.label {
			font-weight: bold;
			min-width: 100px;
		}

		.value {
			flex: 1;
		}

		&.content {
			flex-direction: column;

			.content-text {
				margin-top: 10px;
				padding: 15px;
				background-color: #f5f7fa;
				border-radius: 4px;
				min-height: 100px;
				white-space: pre-wrap;
			}
		}
	}
}
</style>
