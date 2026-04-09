import { NavigationGuardWithThis, NavigationHookAfter } from 'vue-router';
import useSysStore from '@/store/sys';

export const beforeNav: NavigationGuardWithThis<undefined> = (to, _from, next) => {
	// 公开路由，不需要登录即可访问
	const publicRoutes = ['/login', '/register', '/404'];

	// 获取 token
	const token = localStorage.getItem('sys-token');
	const sysStore = useSysStore();
	const userInfo = sysStore.userInfo;

	// 已登录用户访问登录或注册页，重定向到首页
	if ((to.path === '/login' || to.path === '/register') && token && userInfo && userInfo.UId) {
		next('/entrance/home');
		return;
	}

	// 如果是公开路由，直接放行
	if (publicRoutes.includes(to.path)) {
		next();
		return;
	}

	// 如果没有 token 或者用户信息为空，跳转到登录页
	if (!token || !userInfo || !userInfo.UId) {
		next('/login');
		return;
	}

	// 基于角色的权限控制
	const userRole = userInfo.URole; // 假设角色信息保存在 userInfo.role 中
	const adminRoutes = [
		'/entrance/change-password',
		'/entrance/profile',
		'/entrance/my-announcement',
		'/entrance/announcement',
		'/entrance/progress-manage',
		'/entrance/system-notification',
		'/entrance/selection-result',
		'/entrance/project-application',
		'/entrance/my-notifications',
		'/entrance/project-detail',
		'/entrance/project-manage',
		'/entrance/tag-manage',
		'/entrance/user-manage',
		'/entrance/home',
		'/entrance/project-materials',
		'/entrance/log-manage',
	];

	const teacherRoutes = [
		'/entrance/home',
		'/entrance/my-notifications',
		'/entrance/my-announcement',
		'/entrance/profile',
		'/entrance/change-password',
		'/entrance/project-detail',
		'/entrance/my-projects',
	];

	const studentRoutes = [
		'/entrance/home',
		'/entrance/browse-projects',
		'/entrance/my-notifications',
		'/entrance/my-announcement',
		'/entrance/profile',
		'/entrance/change-password',
		'/entrance/my-select',
		'/entrance/project-detail',
		'/entrance/my-application',
	];

	// 检查当前路径是否在用户角色允许的路径中
	let hasPermission = false;

	// 处理包含参数的路由，如 /entrance/project-detail/:id
	const currentPath = to.path.includes('/entrance/project-detail/') ? '/entrance/project-detail' : to.path;
	if (userRole === 1 && adminRoutes.some((route) => currentPath.startsWith(route))) {
		hasPermission = true;
	} else if (userRole === 2 && teacherRoutes.some((route) => currentPath.startsWith(route))) {
		hasPermission = true;
	} else if (userRole === 3 && studentRoutes.some((route) => currentPath.startsWith(route))) {
		hasPermission = true;
	}

	if (hasPermission) {
		next();
	} else {
		// 如果没有权限访问，重定向到首页
		next('/entrance/home');
	}
};

export const afterNav: NavigationHookAfter = (to) => {
	if ('title' in to.meta) {
		document.title = to.meta.title + '';
	} else {
		document.title = '高校课题管理系统';
	}
};
