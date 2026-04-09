<template>
	<div class="flex min-h-screen bg-gradient-to-b from-indigo-50 to-white">
		<!-- Left Section: Background Image -->
		<div class="hidden lg:block lg:w-1/2 relative">
			<div class="absolute inset-0 bg-blue-600 opacity-80"></div>
			<div
				class="w-full h-full bg-[url('https://img.freepik.com/free-vector/college-university-students-group-young-happy-people-standing-isolated-white-background_575670-66.jpg')] bg-cover bg-center"
			></div>
			<div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-white text-center w-3/4">
				<h2 class="text-4xl font-bold mb-4">课题管理系统</h2>
				<p class="text-xl">高效管理学校课题，方便师生交流协作</p>
			</div>
		</div>

		<!-- Right Section: Login Form -->
		<div class="w-full lg:w-1/2 flex flex-col justify-center items-center px-8 md:px-16 py-12">
			<div class="w-full max-w-md">
				<!-- Logo & Header -->
				<div class="text-center mb-10">
					<h1 class="text-3xl font-bold text-gray-800 mb-2">欢迎登录</h1>
					<p class="text-gray-500">请输入您的账号和密码进行登录</p>
				</div>

				<!-- Login Form -->
				<el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" status-icon class="w-full">
					<!-- Username -->
					<el-form-item prop="username">
						<el-input v-model="loginForm.username" placeholder="账号" size="large" class="rounded-md" @keyup.enter="handleLogin">
							<template #prefix>
								<el-icon><User /></el-icon>
							</template>
						</el-input>
					</el-form-item>

					<!-- Password -->
					<el-form-item prop="password">
						<el-input
							v-model="loginForm.password"
							placeholder="密码"
							type="password"
							size="large"
							show-password
							class="rounded-md"
							@keyup.enter="handleLogin"
						>
							<template #prefix>
								<el-icon><Lock /></el-icon>
							</template>
						</el-input>
					</el-form-item>

					<!-- Space before button -->
					<div class="mb-6"></div>

					<!-- Login Button -->
					<el-button type="primary" class="w-full !h-12 text-base mt-2" :loading="isLoading" @click="handleLogin"> 登录 </el-button>

					<!-- Register Link -->
					<div class="text-center mt-6">
						<span class="text-gray-600">还没有账号？</span>
						<router-link to="/register" class="text-blue-600 hover:text-blue-800 ml-1">立即注册</router-link>
					</div>
				</el-form>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { User, Lock } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { postUsersLogin } from '@/apis/users';
import useSysStore from '@/store/sys';

defineOptions({
	name: 'Login',
});

const router = useRouter();
const loginFormRef = ref<FormInstance>();
const isLoading = ref(false);
const sysStore = useSysStore();

const loginForm = reactive({
	username: '',
	password: '',
});

const loginRules = reactive<FormRules>({
	username: [
		{ required: true, message: '请输入账号', trigger: 'blur' },
		{ min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
	],
	password: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
	],
});

const handleLogin = async () => {
	if (!loginFormRef.value) return;

	await loginFormRef.value.validate(async (valid: boolean) => {
		if (valid) {
			isLoading.value = true;
			try {
				// 调用登录API
				const response = await postUsersLogin({
					uUsername: loginForm.username,
					uPassword: loginForm.password,
				});

				// 保存用户信息到store
				sysStore.changeUserInfo(response.data);

				// 登录成功提示
				ElMessage.success('登录成功');

				// 跳转到首页
				router.push('/entrance');
			} catch {
				isLoading.value = false;
			}
		}
	});
};
</script>
