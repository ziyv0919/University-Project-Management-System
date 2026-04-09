<template>
	<el-config-provider :locale="zhCn" :size="GlobalComponentSize">
		<router-view v-slot="{ Component }">
			<keep-alive :include="keepAliveRoutes">
				<component :is="Component" />
			</keep-alive>
		</router-view>
	</el-config-provider>
</template>

<script setup lang="ts">
import { keepAliveRoutes } from '@/router';
import { GlobalComponentSize } from '@/config';

import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
</script>

<style lang="scss">
@use '@/assets/styles/scroll.scss' as *;

body {
	overflow: hidden;
}

* {
	font-size: 14px;
}
</style>
