<template>
	<div class="common-layout">
		<el-container>
			<el-aside class="aside"><menu-component></menu-component></el-aside>
			<el-container>
				<el-header class="header">
					<div class="header-left"></div>
					<div class="header-right">
						<el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
							<el-popover placement="bottom" :width="300" trigger="click">
								<template #reference>
									<el-button circle>
										<el-icon><Bell /></el-icon>
									</el-button>
								</template>
								<div class="notification-popover">
									<div class="notification-header">
										<div class="flex justify-between items-center w-full">
											<span class="text-gray-900 font-medium">通知</span>
											<el-button type="primary" link size="small" @click="handleViewAllNotifications">查看全部</el-button>
										</div>
									</div>
									<el-divider class="my-2" />
									<div class="notification-list">
										<div
											v-for="(item, index) in notificationList"
											:key="index"
											class="notification-item"
											:class="{ unread: !item.nIsRead }"
											@click="handleNotificationClick(item)"
										>
											<div class="notification-title">{{ item.nTitle }}</div>
											<div class="notification-time">{{ item.createdAt }}</div>
										</div>
										<el-empty v-if="notificationList.length === 0" description="暂无通知" :image-size="60" />
									</div>
								</div>
							</el-popover>
						</el-badge>
						<el-dropdown trigger="click" @command="handleCommand">
							<div class="user-info">
								<el-avatar :src="fileShowUrl + userInfo.uAvatar" :size="32" :icon="UserFilled" />
								<span class="username">{{ userInfo.UName || '未知用户' }}</span>
								<el-icon><ArrowDown /></el-icon>
							</div>
							<template #dropdown>
								<el-dropdown-menu>
									<el-dropdown-item command="profile">
										<el-icon><User /></el-icon>个人中心
									</el-dropdown-item>
									<el-dropdown-item command="change-password">
										<el-icon><Lock /></el-icon>修改密码
									</el-dropdown-item>
									<el-dropdown-item command="logout">
										<el-icon><SwitchButton /></el-icon>退出登录
									</el-dropdown-item>
								</el-dropdown-menu>
							</template>
						</el-dropdown>
					</div>
				</el-header>
				<el-main v-loading="pageLoading" class="main">
					<transition :name="String($route.meta.transition ?? '')">
						<router-view v-slot="{ Component }">
							<keep-alive :include="keepAliveRoutes">
								<component :is="Component" />
							</keep-alive>
						</router-view>
					</transition>
				</el-main>
			</el-container>
		</el-container>
	</div>
</template>

<script setup lang="ts">
import menuComponent from '@/components/menu/index.vue';
import { pageLoading } from './hooks/useLoading';
import { keepAliveRoutes } from '@/router';
import { Bell, User, UserFilled, ArrowDown, SwitchButton, Lock } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import useSysStore from '@/store/sys';
import { ElMessageBox } from 'element-plus';
import { storeToRefs } from 'pinia';
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { postNotificationFindByUserId, postNotificationMarkAsRead } from '@/apis';

defineOptions({
	name: 'EntrancePage',
});

const router = useRouter();
const sysStore = useSysStore();
const { userInfo } = storeToRefs(sysStore);

const fileShowUrl = import.meta.env.VITE_APP_FILE_SHOW_URL;

const handleCommand = (command: string) => {
	if (command === 'profile') {
		// 跳转到个人中心页面
		router.push('/entrance/profile');
	} else if (command === 'change-password') {
		// 跳转到修改密码页面
		router.push('/entrance/change-password');
	} else if (command === 'logout') {
		// 退出登录
		ElMessageBox.confirm('确定要退出登录吗?', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
		})
			.then(() => {
				// 清除用户信息
				sysStore.changeUserInfo({});
				// 跳转到登录页
				router.push('/login');
			})
			.catch(() => {});
	}
};

// 通知列表数据
const notificationList = ref<any[]>([]);

// 未读通知数量
const unreadCount = computed(() => {
	return notificationList.value.length;
});

// 获取通知列表
const fetchNotifications = async () => {
	try {
		const res = await postNotificationFindByUserId({
			data: {
				uId: userInfo.value.UId,
				pageIndex: 1,
				pageSize: 10,
				nIsRead: 0, // 只获取未读通知
			},
		});
		if (res.data?.list) {
			notificationList.value = res.data.list;
		}
	} catch (error) {
		console.error('获取通知列表失败', error);
	}
};

// 查看全部通知
const handleViewAllNotifications = () => {
	router.push('/entrance/my-notifications');
};

// 处理单条通知的点击事件
const handleNotificationClick = (item: any) => {
	// 标记该通知为已读
	markNotificationAsRead(item);

	router.push('/entrance/my-notifications');
};

// 标记通知为已读
const markNotificationAsRead = async (notification: any) => {
	if (notification.nIsRead) return; // 已读则不处理

	try {
		postNotificationMarkAsRead({
			data: {
				nId: notification.nId,
			},
		})
			.then(() => {
				notification.nIsRead = 1;
				ElMessage.success('已标记为已读');
			})
			.catch(() => {
				ElMessage.error('操作失败');
			});

		notificationList.value = [...notificationList.value];
	} catch (error) {
		console.error('标记通知已读失败', error);
	}
};

// 通知自动刷新间隔
const NOTIFICATION_REFRESH_INTERVAL = 5000; // 5秒刷新一次
let notificationTimer: number | null = null;

// 开始自动刷新通知
const startAutoRefresh = () => {
	// 清除已有的定时器
	if (notificationTimer) {
		clearInterval(notificationTimer);
	}

	// 设置新的定时器
	notificationTimer = window.setInterval(() => {
		fetchNotifications();
	}, NOTIFICATION_REFRESH_INTERVAL);
};

// 停止自动刷新通知
const stopAutoRefresh = () => {
	if (notificationTimer) {
		clearInterval(notificationTimer);
		notificationTimer = null;
	}
};

onMounted(() => {
	fetchNotifications();
	startAutoRefresh();
});

onBeforeUnmount(() => {
	stopAutoRefresh();
});
</script>

<style scoped lang="scss">
.common-layout {
	height: 100vh;
	width: 100vw;
	overflow: hidden;
}

.el-container {
	height: 100%;
}

.aside {
	height: 100vh;
	width: auto;
	border-right: 1px solid #eee;
	background-color: #fff;
	overflow-y: auto; /* 侧边栏内容过多时可滚动 */
}

.header {
	height: 60px;
	border-bottom: 1px solid #eee;
	background-color: #fff;
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 0 20px;

	.header-right {
		display: flex;
		align-items: center;
		gap: 20px;
	}

	.user-info {
		display: flex;
		align-items: center;
		cursor: pointer;
		gap: 8px;

		.username {
			margin: 0 5px;
			font-size: 14px;
		}
	}

	.notification-badge {
		margin-right: 10px;
	}

	.notification-popover {
		.notification-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 0 5px;
			font-weight: bold;
		}

		.notification-list {
			max-height: 300px;
			overflow-y: auto;

			.notification-item {
				padding: 10px;
				border-bottom: 1px solid #f0f0f0;
				cursor: pointer;

				&:hover {
					background-color: #f5f7fa;
				}

				&.unread {
					background-color: #f0f9ff;
					.notification-title {
						font-weight: bold;
					}
				}

				.notification-title {
					font-size: 14px;
					margin-bottom: 5px;
				}

				.notification-time {
					font-size: 12px;
					color: #999;
				}
			}
		}
	}
}

.main {
	background-color: #fff;
	padding: 20px;
	overflow-y: auto;
	height: calc(100vh - 60px); /* 减去header的高度 */
}

.slide-right-enter-active {
	transition: all 0.9s ease-out;
}

.slide-right-leave-active {
	transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-right-enter-from,
.slide-right-leave-to {
	transform: translateX(60px);
	opacity: 0;
}
</style>
