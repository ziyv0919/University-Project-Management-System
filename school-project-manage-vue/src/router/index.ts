import { RouteRecordRaw, createRouter, createWebHashHistory } from 'vue-router';

import { afterNav, beforeNav } from './navigate';

export const routes: RouteRecordRaw[] = [
	{
		path: '/',
		redirect: '/login',
	},
	{
		path: '/login',
		component: () => import('@/views/login/index.vue'),
		name: 'Login',
		meta: {
			keepalive: false,
			title: '登录',
			transition: 'fade',
		},
	},
	{
		path: '/register',
		component: () => import('@/views/register/index.vue'),
		name: 'Register',
		meta: {
			keepalive: false,
			title: '注册',
			transition: 'fade',
		},
	},
	{
		path: '/entrance',
		component: () => import('@/views/entrance/index.vue'),
		meta: {
			keepAlive: true,
		},
		children: [
			{
				path: '/entrance',
				redirect: '/entrance/home',
			},
			{
				path: '/entrance/home',
				component: () => import('@/views/home/index.vue'),
				name: 'Home',
				meta: {
					keepAlive: false,
					title: '首页',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/user-manage',
				component: () => import('@/views/user-manage/index.vue'),
				name: 'UserManage',
				meta: {
					keepAlive: false,
					title: '用户管理',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/tag-manage',
				component: () => import('@/views/tag-manage/index.vue'),
				name: 'TagManage',
				meta: {
					keepAlive: false,
					title: '标签管理',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/project-manage',
				component: () => import('@/views/project-manage/index.vue'),
				name: 'ProjectManage',
				meta: {
					keepAlive: false,
					title: '课题管理',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/browse-projects',
				component: () => import('@/views/browse-projects/index.vue'),
				name: 'BrowseProjects',
				meta: {
					keepAlive: false,
					title: '浏览课题',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/project-detail/:id',
				component: () => import('@/views/project-detail/index.vue'),
				name: 'ProjectDetail',
				meta: {
					keepAlive: false,
					title: '课题详情',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/my-projects',
				component: () => import('@/views/my-projects/index.vue'),
				name: 'MyProjects',
				meta: {
					keepAlive: false,
					title: '我的发布',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/log-manage',
				component: () => import('@/views/log-manage/index.vue'),
				name: 'LogManage',
				meta: {
					keepAlive: false,
					title: '日志管理',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/my-select',
				component: () => import('@/views/my-select/index.vue'),
				name: 'MySelect',
				meta: {
					keepAlive: false,
					title: '我的选题',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/my-notifications',
				component: () => import('@/views/my-notifications/index.vue'),
				name: 'MyNotifications',
				meta: {
					keepAlive: false,
					title: '我的通知',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/project-application',
				component: () => import('@/views/project-application/index.vue'),
				name: 'ProjectApplication',
				meta: {
					keepAlive: false,
					title: '课题申请与审批',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/my-application',
				component: () => import('@/views/my-application/index.vue'),
				name: 'MyApplication',
				meta: {
					keepAlive: false,
					title: '我的申请',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/selection-result',
				component: () => import('@/views/selection-result/index.vue'),
				name: 'SelectionResult',
				meta: {
					keepAlive: false,
					title: '选题结果展示',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/system-notification',
				component: () => import('@/views/system-notification/index.vue'),
				name: 'SystemNotification',
				meta: {
					keepAlive: false,
					title: '系统通知',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/progress-manage',
				component: () => import('@/views/progress-manage/index.vue'),
				name: 'ProgressManage',
				meta: {
					keepAlive: false,
					title: '阶段进度管理',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/announcement',
				component: () => import('@/views/announcement/index.vue'),
				name: 'Announcement',
				meta: {
					keepAlive: false,
					title: '公告发布',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/my-announcement',
				component: () => import('@/views/my-announcement/index.vue'),
				name: 'MyAnnouncement',
				meta: {
					keepAlive: false,
					title: '我的公告',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/project-materials',
				component: () => import('@/views/project-materials/index.vue'),
				name: 'ProjectMaterials',
				meta: {
					keepAlive: false,
					title: '课题资料',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/profile',
				component: () => import('@/views/profile/index.vue'),
				name: 'UserProfile',
				meta: {
					keepAlive: false,
					title: '个人中心',
					transition: 'slide-right',
				},
			},
			{
				path: '/entrance/change-password',
				component: () => import('@/views/change-password/index.vue'),
				name: 'ChangePassword',
				meta: {
					keepAlive: false,
					title: '修改密码',
					transition: 'slide-right',
				},
			},
		],
	},
	{
		path: '/404',
		component: () => import('@/views/not-found/index.vue'),
		meta: {
			keepAlive: false,
			title: '404',
			transition: 'slide-right',
		},
	},
	{
		path: '/:pathMatch(.*)*',
		redirect: '/404',
	},
];

export const getKeepAliveRoutes = (routes: RouteRecordRaw[]) => {
	let keepAliveRoutes: string[] = [];

	for (const route of routes) {
		if (route.meta && route.meta.keepAlive && route.name) {
			keepAliveRoutes.push(String(route.name));
		}

		if (route.children) {
			const childRoutes = getKeepAliveRoutes(route.children);
			keepAliveRoutes = keepAliveRoutes.concat(childRoutes);
		}
	}

	return keepAliveRoutes;
};

export const keepAliveRoutes = getKeepAliveRoutes(routes);

export const router = createRouter({
	history: createWebHashHistory('/'),
	routes,
});

router.beforeEach(beforeNav);
router.afterEach(afterNav);

export default router;
