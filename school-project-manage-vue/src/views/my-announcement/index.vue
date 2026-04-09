<template>
	<div class="min-h-full mx-auto p-6">
		<div class="header">
			<h1 class="text-2xl font-bold text-gray-800 mb-4">我的公告</h1>
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

		<el-table :data="announcementData" style="width: 100%" border stripe :pagination="pagination" v-loading="loading">
			<el-table-column prop="ATitle" label="公告标题" min-width="150" />
			<el-table-column prop="UName" label="发布人" min-width="120" />
			<el-table-column prop="createdAt" label="发布时间" min-width="150" />
			<el-table-column label="操作" min-width="120" fixed="right">
				<template #default="{ row }">
					<el-button type="primary" @click="viewAnnouncementDetail(row)">查看详情</el-button>
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

	<!-- 公告详情对话框 -->
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
			<div class="detail-item content">
				<span class="label">公告内容：</span>
				<div class="value content-text">{{ currentAnnouncement.AContent }}</div>
			</div>
		</div>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { postAnnouncementsFind, postAnnouncementsFindAll } from '@/apis/announcements';
import useSysStore from '@/store/sys';
import { storeToRefs } from 'pinia';

defineOptions({
	name: 'MyAnnouncement',
});

const sysStore = useSysStore();
const { userInfo } = storeToRefs(sysStore);

const pagination = ref<{
	pageIndex: number;
	pageSize: number;
	aTitle?: string;
	aTarget?: number;
	uName?: string;
}>({
	pageIndex: 1,
	pageSize: 10,
	// 根据当前用户角色设置对应的筛选条件，只看对应角色和所有用户的公告
	aTarget: getUserTargetValue(),
});

const announcementData = ref([]);
const total = ref(0);
const loading = ref(false);
const visible = ref(false);
const detailDialogVisible = ref(false);
const currentAnnouncement = ref<any>(null);

// 根据用户角色获取对应的筛选值
function getUserTargetValue() {
	// 1:管理员, 2:教师, 3:学生, 4:所有用户
	const roleMap: Record<string, number> = {
		admin: 1,
		teacher: 2,
		student: 3,
	};

	const userRole = userInfo.value?.role || '';
	return roleMap[userRole] || 4; // 默认返回所有用户
}

async function searchData() {
	loading.value = true;
	try {
		const params = filterEmptyParams({
			pageIndex: pagination.value.pageIndex,
			pageSize: pagination.value.pageSize,
			aTitle: pagination.value.aTitle,
			aTarget: pagination.value.aTarget,
			uName: pagination.value.uName,
		});

		// 获取针对当前用户角色的公告
		const { data } = await postAnnouncementsFindAll({
			data: params,
		});

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
		aTarget: getUserTargetValue(),
	};
	searchData();
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

function filterEmptyParams(params: Record<string, any>) {
	const filteredParams: Record<string, any> = {};
	for (const key in params) {
		if (params[key] !== '' && params[key] !== undefined) {
			filteredParams[key] = params[key];
		}
	}
	return filteredParams;
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
