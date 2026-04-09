<template>
	<div class="p-6">
		<div class="flex items-center mb-4">
			<el-input v-model="searchQuery" placeholder="搜索通知" class="w-60 mr-4" :prefix-icon="Search" clearable @input="fetchNotificationsDebounced" />
			<el-select v-model="filter" placeholder="筛选" class="w-40 mr-4" @change="fetchNotifications">
				<el-option label="未读" value="unread" />
				<el-option label="已读" value="read" />
			</el-select>
			<el-button type="primary" @click="markAllAsRead" :icon="Check">全部标为已读</el-button>
		</div>

		<el-table :data="notificationList" style="width: 100%">
			<el-table-column prop="title" label="标题" min-width="180">
				<template #default="{ row }">
					<span :class="{ 'font-bold': !row.nIsRead }">{{ row.nTitle }}</span>
				</template>
			</el-table-column>
			<el-table-column prop="nContent" label="内容" min-width="240" show-overflow-tooltip />
			<el-table-column prop="createdAt" label="时间" width="180" />
			<el-table-column label="操作" width="240">
				<template #default="{ row }">
					<el-button type="primary" link @click="viewNotification(row)">查看</el-button>
					<el-button v-if="!row.nIsRead" type="success" link @click="markAsRead(row)"> 标为已读 </el-button>
					<el-button type="danger" link @click="deleteNotification(row.nId)">删除</el-button>
				</template>
			</el-table-column>
		</el-table>

		<el-pagination
			v-model:current-page="currentPage"
			:page-size="pageSize"
			:total="total"
			layout="total, prev, pager, next"
			class="mt-4"
			@current-change="handlePageChange"
		/>

		<el-drawer v-model="drawerVisible" title="通知详情" size="40%">
			<template #default>
				<div v-if="selectedNotification">
					<h2 class="text-lg font-bold mb-2">{{ selectedNotification.nTitle }}</h2>
					<p class="text-gray-600 mb-4">{{ selectedNotification.nContent }}</p>
					<p class="text-sm text-gray-400 mb-4">发布时间：{{ selectedNotification.createdAt }}</p>
				</div>
			</template>
		</el-drawer>
	</div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Check } from '@element-plus/icons-vue';
import { debounce } from 'lodash-es';
import { postNotificationFindByUserId, postNotificationsDelete, postNotificationMarkAllAsRead, postNotificationMarkAsRead } from '@/apis';
import useSysStore from '@/store/sys';

const userStore = useSysStore();
const userId = userStore.userInfo?.UId;

const notificationList = ref<any[]>([]);
const total = ref(0);
const searchQuery = ref('');
const filter = ref('unread');
const currentPage = ref(1);
const pageSize = ref(10);
const drawerVisible = ref(false);
const selectedNotification = ref<any | null>(null);

const fetchNotifications = async () => {
	try {
		const { data } = await postNotificationFindByUserId({
			data: {
				uId: userId,
				pageIndex: currentPage.value,
				pageSize: pageSize.value,
				nTitle: searchQuery.value,
				nIsRead: filter.value === 'read' ? 1 : 0,
			},
		});
		notificationList.value = data.list;
		total.value = data.total;
	} catch (err) {
		ElMessage.error('获取通知失败');
	}
};

const fetchNotificationsDebounced = debounce(fetchNotifications, 300);

const handlePageChange = (page: number) => {
	currentPage.value = page;
	fetchNotifications();
};

const viewNotification = (notification: any) => {
	selectedNotification.value = notification;
	drawerVisible.value = true;
	if (!notification.nIsRead) {
		markAsRead(notification, false);
	}
};

const markAsRead = async (notification: any, showMessage = true) => {
	try {
		postNotificationMarkAsRead({
			data: {
				nId: notification.nId,
			},
		})
			.then(() => {
				notification.nIsRead = 1;
				if (showMessage) {
					ElMessage.success('已标记为已读');
				}
			})
			.catch(() => {
				ElMessage.error('操作失败');
			});
	} catch (err) {
		ElMessage.error('标记为已读失败');
	}
};

const markAllAsRead = async () => {
	try {
		await postNotificationMarkAllAsRead({
			data: {
				uId: userId,
			},
		});
		fetchNotifications();
		ElMessage.success('全部标记为已读');
	} catch (err) {
		ElMessage.error('全部标记失败');
	}
};

const deleteNotification = async (nId: number) => {
	try {
		await postNotificationsDelete({ data: { nId } });
		ElMessage.success('删除成功');
		fetchNotifications();
	} catch (err) {
		ElMessage.error('删除失败');
	}
};

onMounted(() => {
	fetchNotifications();
});
</script>

<style scoped>
.notification-badge ::v-deep(.el-badge__content.is-dot) {
	right: 5px;
	top: 5px;
}

.notification-content {
	line-height: 1.6;
}
</style>
