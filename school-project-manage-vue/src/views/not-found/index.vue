<template>
	<div class="flex flex-col items-center justify-center min-h-screen bg-gray-50 px-4">
		<div class="text-center">
			<!-- 404 Icon/Image -->
			<el-image class="mx-auto h-48 w-48" src="https://img.icons8.com/clouds/500/000000/error.png" fit="contain" :preview-src-list="[]">
				<template #error>
					<el-icon class="text-gray-400" :size="100">
						<WarningFilled />
					</el-icon>
				</template>
			</el-image>

			<!-- Error Code -->
			<h1 class="mt-6 text-8xl font-bold text-indigo-600">404</h1>

			<!-- Error Message -->
			<h2 class="mt-4 text-3xl font-bold tracking-tight text-gray-900 sm:text-4xl">页面未找到</h2>
			<p class="mt-3 text-lg text-gray-500">很抱歉，您访问的页面不存在或已被删除。</p>

			<!-- Action Buttons -->
			<div class="mt-8 flex gap-4 justify-center">
				<el-button type="primary" size="large" @click="router.push('/')">
					<el-icon class="mr-1"><HomeFilled /></el-icon>
					返回首页
				</el-button>
				<el-button size="large" @click="router.go(-1)">
					<el-icon class="mr-1"><Back /></el-icon>
					返回上一页
				</el-button>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { HomeFilled, WarningFilled, Back } from '@element-plus/icons-vue';

defineOptions({
	name: 'NotFound',
});

const router = useRouter();
</script>
