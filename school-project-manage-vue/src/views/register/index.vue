<template>
	<div class="flex min-h-screen bg-gradient-to-b from-indigo-50 to-white">
		<!-- Left Section: Background Image -->
		<div class="hidden lg:block lg:w-1/2 relative">
			<div class="absolute inset-0 bg-blue-600 opacity-80"></div>
			<div
				class="w-full h-full bg-[url('https://img.freepik.com/free-vector/group-students-school-character_187782-368.jpg')] bg-cover bg-center"
			></div>
			<div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-white text-center w-3/4">
				<h2 class="text-4xl font-bold mb-4">课题管理系统</h2>
				<p class="text-xl">创建账号，开始您的学术研究之旅</p>
			</div>
		</div>

		<!-- Right Section: Register Form -->
		<div class="w-full lg:w-1/2 flex flex-col justify-center items-center px-8 md:px-16 py-12">
			<div class="w-full max-w-md">
				<!-- Header -->
				<div class="text-center mb-8">
					<h1 class="text-3xl font-bold text-gray-800 mb-2">注册账号</h1>
					<p class="text-gray-500">填写以下信息创建您的账号</p>
				</div>

				<!-- Register Form -->
				<el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" status-icon class="w-full" label-position="top">
					<!-- User Type Selection -->
					<el-form-item prop="uRole" label="用户类型">
						<el-radio-group v-model="registerForm.uRole" class="flex flex-wrap gap-4">
							<el-radio :value="3">学生</el-radio>
							<el-radio :value="2">教师</el-radio>
						</el-radio-group>
					</el-form-item>

					<!-- Name -->
					<el-form-item prop="uName" label="姓名">
						<el-input v-model="registerForm.uName" placeholder="请输入真实姓名" size="large" class="rounded-md">
							<template #prefix>
								<el-icon><User /></el-icon>
							</template>
						</el-input>
					</el-form-item>

					<!-- Username/Student ID -->
					<el-form-item prop="uUsername" label="账户">
						<el-input v-model="registerForm.uUsername" placeholder="请输入账户" size="large" class="rounded-md">
							<template #prefix>
								<el-icon><UserFilled /></el-icon>
							</template>
						</el-input>
					</el-form-item>

					<!-- Password -->
					<el-form-item prop="uPassword" label="密码">
						<el-input v-model="registerForm.uPassword" placeholder="请设置密码" type="password" size="large" show-password class="rounded-md">
							<template #prefix>
								<el-icon><Lock /></el-icon>
							</template>
						</el-input>
					</el-form-item>

					<!-- Confirm Password -->
					<el-form-item prop="confirmPassword" label="确认密码">
						<el-input
							v-model="registerForm.confirmPassword"
							placeholder="请再次输入密码"
							type="password"
							size="large"
							show-password
							class="rounded-md"
						>
							<template #prefix>
								<el-icon><Lock /></el-icon>
							</template>
						</el-input>
					</el-form-item>

					<!-- Register Button -->
					<el-button type="primary" class="w-full !h-12 text-base mt-2" :loading="isLoading" @click="handleRegister"> 注册 </el-button>

					<!-- Login Link -->
					<div class="text-center mt-6">
						<span class="text-gray-600">已有账号？</span>
						<router-link to="/login" class="text-blue-600 hover:text-blue-800 ml-1">立即登录</router-link>
					</div>
				</el-form>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { User, UserFilled, Lock } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { postUsersRegister } from '@/apis/users';

defineOptions({
	name: 'Register',
});

const router = useRouter();
const registerFormRef = ref<FormInstance>();
const isLoading = ref(false);

const registerForm = reactive({
	uRole: 3,
	uName: '',
	uUsername: '',
	uPassword: '',
	confirmPassword: '',
});

const validatePass = (_: any, value: string, callback: any) => {
	if (value === '') {
		callback(new Error('请输入密码'));
	} else {
		if (registerForm.confirmPassword !== '') {
			if (registerFormRef.value) {
				registerFormRef.value.validateField('confirmPassword', () => {});
			}
		}
		callback();
	}
};

const validatePass2 = (_: any, value: string, callback: any) => {
	if (value === '') {
		callback(new Error('请再次输入密码'));
	} else if (value !== registerForm.uPassword) {
		callback(new Error('两次输入密码不一致!'));
	} else {
		callback();
	}
};

const registerRules = reactive<FormRules>({
	uRole: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
	uName: [
		{ required: true, message: '请输入姓名', trigger: 'blur' },
		{ min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
	],
	uUsername: [
		{ required: true, message: '请输入账户', trigger: 'blur' },
		{ min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
	],
	uPassword: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
		{ validator: validatePass, trigger: 'blur' },
	],
	confirmPassword: [
		{ required: true, message: '请再次输入密码', trigger: 'blur' },
		{ validator: validatePass2, trigger: 'blur' },
	],
	agreement: [
		{
			required: true,
			validator: (_: any, value: boolean, callback: any) => {
				if (value) {
					callback();
				} else {
					callback(new Error('请阅读并同意服务条款和隐私政策'));
				}
			},
			trigger: 'change',
		},
	],
});

const handleRegister = async () => {
	if (!registerFormRef.value) return;

	await registerFormRef.value.validate(async (valid: boolean): Promise<void> => {
		if (valid) {
			isLoading.value = true;

			const registerData = {
				uUsername: registerForm.uUsername,
				uPassword: registerForm.uPassword,
				uName: registerForm.uName,
				uRole: registerForm.uRole,
			};

			try {
				await postUsersRegister(registerData);

				ElMessage.success('注册成功，即将跳转到登录页面');
				setTimeout(() => {
					router.push('/login');
				}, 1500);
			} finally {
				isLoading.value = false;
			}
		}
	});
};
</script>
