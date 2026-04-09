<template>
	<div class="menu-container">
		<div class="menu-expand-collapse" @click="isCollapse = !isCollapse">
			<el-icon v-show="!isCollapse"><ArrowLeftBold /></el-icon>
			<el-icon v-show="isCollapse"><ArrowRightBold /></el-icon>
		</div>
		<el-menu :default-active="$route.path" :collapse="isCollapse">
			<el-menu-item index="/entrance/home" @click="router.push('/entrance/home')">
				<el-icon><home-filled /></el-icon>
				<template #title>首页</template>
			</el-menu-item>

			<!-- 管理员专属菜单 -->
			<template v-if="userRole === 1">
				<el-menu-item index="/entrance/user-manage" @click="router.push('/entrance/user-manage')">
					<el-icon><user /></el-icon>
					<template #title>用户管理</template>
				</el-menu-item>
				<el-menu-item index="/entrance/tag-manage" @click="router.push('/entrance/tag-manage')">
					<el-icon><price-tag /></el-icon>
					<template #title>标签管理</template>
				</el-menu-item>
				<el-menu-item index="/entrance/project-manage" @click="router.push('/entrance/project-manage')">
					<el-icon><folder /></el-icon>
					<template #title>课题管理</template>
				</el-menu-item>
				<el-menu-item index="/entrance/project-application" @click="router.push('/entrance/project-application')">
					<el-icon><document /></el-icon>
					<template #title>课题申请与审批</template>
				</el-menu-item>
				<el-menu-item index="/entrance/selection-result" @click="router.push('/entrance/selection-result')">
					<el-icon><list /></el-icon>
					<template #title>选题结果展示</template>
				</el-menu-item>
				<el-menu-item index="/entrance/system-notification" @click="router.push('/entrance/system-notification')">
					<el-icon><message-box /></el-icon>
					<template #title>系统通知</template>
				</el-menu-item>
				<el-menu-item index="/entrance/progress-manage" @click="router.push('/entrance/progress-manage')">
					<el-icon><timer /></el-icon>
					<template #title>阶段进度管理</template>
				</el-menu-item>
				<el-menu-item index="/entrance/announcement" @click="router.push('/entrance/announcement')">
					<el-icon><message /></el-icon>
					<template #title>公告发布</template>
				</el-menu-item>
				<el-menu-item index="/entrance/project-materials" @click="router.push('/entrance/project-materials')">
					<el-icon><files /></el-icon>
					<template #title>课题资料</template>
				</el-menu-item>
				<el-menu-item index="/entrance/log-manage" @click="router.push('/entrance/log-manage')">
					<el-icon><Notification /></el-icon>
					<template #title>日志管理</template>
				</el-menu-item>
			</template>

			<!-- 教师专属菜单 -->
			<template v-if="userRole === 2">
				<el-menu-item index="/entrance/my-projects" @click="router.push('/entrance/my-projects')">
					<el-icon><folder /></el-icon>
					<template #title>我的课题</template>
				</el-menu-item>
			</template>

			<!-- 学生专属菜单 -->
			<template v-if="userRole === 3">
				<el-menu-item index="/entrance/browse-projects" @click="router.push('/entrance/browse-projects')">
					<el-icon><search /></el-icon>
					<template #title>浏览课题</template>
				</el-menu-item>
				<el-menu-item index="/entrance/my-select" @click="router.push('/entrance/my-select')">
					<el-icon><folder /></el-icon>
					<template #title>我的选题</template>
				</el-menu-item>
				<el-menu-item index="/entrance/my-application" @click="router.push('/entrance/my-application')">
					<el-icon><document /></el-icon>
					<template #title>我的申请</template>
				</el-menu-item>
			</template>

			<!-- 所有角色共有菜单 -->
			<el-menu-item index="/entrance/my-notifications" @click="router.push('/entrance/my-notifications')">
				<el-icon><bell /></el-icon>
				<template #title>我的通知</template>
			</el-menu-item>
			<el-menu-item index="/entrance/my-announcement" @click="router.push('/entrance/my-announcement')">
				<el-icon><message /></el-icon>
				<template #title>我的公告</template>
			</el-menu-item>
		</el-menu>
	</div>
</template>

<script setup lang="ts">
import {
	ArrowLeftBold,
	ArrowRightBold,
	HomeFilled,
	User,
	Folder,
	Document,
	List,
	Bell,
	Timer,
	Message,
	Search,
	MessageBox,
	PriceTag,
	Files,
	Notification,
} from '@element-plus/icons-vue';
import { isCollapse } from './hooks/useCollapse';
import { useRouter } from 'vue-router';
import useSysStore from '@/store/sys';
import { computed } from 'vue';

defineOptions({
	name: 'MenuIndex',
});

const router = useRouter();
const sysStore = useSysStore();

// 获取用户角色
const userRole = computed(() => {
	return sysStore.userInfo?.URole || '';
});
</script>

<style scoped lang="scss">
@import url('./index.scss');
</style>
