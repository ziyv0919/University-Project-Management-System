<template>
	<div class="change-password-container min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
		<div class="max-w-3xl w-full bg-white rounded-lg shadow-lg overflow-hidden">
			<!-- Header with gradient background -->
			<div class="bg-gradient-to-r from-blue-500 to-indigo-600 px-6 py-8 text-center">
				<el-icon class="text-white" :size="48"><Lock /></el-icon>
				<h2 class="mt-4 text-2xl font-bold text-white">修改密码</h2>
				<p class="mt-2 text-sm text-blue-100">设置一个强密码来保护您的账户安全</p>
			</div>

			<!-- Password form -->
			<div class="px-6 py-8">
				<el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top" status-icon class="space-y-6">
					<!-- Current Password -->
					<el-form-item prop="uPassword" label="当前密码">
						<el-input v-model="passwordForm.uPassword" type="password" placeholder="请输入当前密码" show-password size="large" class="rounded-md">
							<template #prefix>
								<el-icon><Key /></el-icon>
							</template>
						</el-input>
					</el-form-item>

					<!-- New Password -->
					<el-form-item prop="uNewPassword" label="新密码">
						<el-input v-model="passwordForm.uNewPassword" type="password" placeholder="请输入新密码" show-password size="large" class="rounded-md">
							<template #prefix>
								<el-icon><Lock /></el-icon>
							</template>
						</el-input>
						<div class="mt-2">
							<div class="text-xs text-gray-500 mb-1">密码强度</div>
							<el-progress :percentage="passwordStrength.score * 25" :color="passwordStrengthColor" :format="formatPasswordStrength" />
						</div>
						<div class="mt-2 text-xs text-gray-500">
							<p>密码必须：</p>
							<ul class="list-disc ml-4 mt-1 space-y-1">
								<li :class="passwordStrength.hasMinLength ? 'text-green-600' : ''">至少8个字符</li>
								<li :class="passwordStrength.hasLowerCase ? 'text-green-600' : ''">包含小写字母</li>
								<li :class="passwordStrength.hasUpperCase ? 'text-green-600' : ''">包含大写字母</li>
								<li :class="passwordStrength.hasNumber ? 'text-green-600' : ''">包含数字</li>
								<li :class="passwordStrength.hasSpecialChar ? 'text-green-600' : ''">包含特殊字符</li>
							</ul>
						</div>
					</el-form-item>

					<!-- Confirm Password -->
					<el-form-item prop="confirmPassword" label="确认新密码">
						<el-input
							v-model="passwordForm.confirmPassword"
							type="password"
							placeholder="请再次输入新密码"
							show-password
							size="large"
							class="rounded-md"
						>
							<template #prefix>
								<el-icon><Lock /></el-icon>
							</template>
						</el-input>
					</el-form-item>

					<!-- Action Buttons -->
					<div class="flex justify-end gap-4 mt-8">
						<el-button @click="goBack">取消</el-button>
						<el-button type="primary" :loading="isSubmitting" :disabled="!canSubmit" @click="handleSubmit"> 确认修改 </el-button>
					</div>
				</el-form>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { Lock, Key } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { postUsersUpdatePassword } from '@/apis';
import useSysStore from '@/store/sys';

defineOptions({
	name: 'ChangePassword',
});

const router = useRouter();
const passwordFormRef = ref<FormInstance>();
const isSubmitting = ref(false);

// Password form data
const passwordForm = reactive({
	uPassword: '',
	uNewPassword: '',
	confirmPassword: '',
});

// Password strength checker
const passwordStrength = reactive({
	score: 0,
	hasMinLength: false,
	hasLowerCase: false,
	hasUpperCase: false,
	hasNumber: false,
	hasSpecialChar: false,
});

// Check password strength
watch(
	() => passwordForm.uNewPassword,
	(uNewPassword) => {
		// Reset all checks
		passwordStrength.hasMinLength = false;
		passwordStrength.hasLowerCase = false;
		passwordStrength.hasUpperCase = false;
		passwordStrength.hasNumber = false;
		passwordStrength.hasSpecialChar = false;

		if (!uNewPassword) {
			passwordStrength.score = 0;
			return;
		}

		// Check password against requirements
		passwordStrength.hasMinLength = uNewPassword.length >= 8;
		passwordStrength.hasLowerCase = /[a-z]/.test(uNewPassword);
		passwordStrength.hasUpperCase = /[A-Z]/.test(uNewPassword);
		passwordStrength.hasNumber = /[0-9]/.test(uNewPassword);
		passwordStrength.hasSpecialChar = /[^A-Za-z0-9]/.test(uNewPassword);

		// Calculate score (0-4)
		const criteriaCount = [
			passwordStrength.hasMinLength,
			passwordStrength.hasLowerCase,
			passwordStrength.hasUpperCase,
			passwordStrength.hasNumber,
			passwordStrength.hasSpecialChar,
		].filter(Boolean).length;

		passwordStrength.score = Math.min(4, criteriaCount);
	}
);

// Get color for password strength indicator
const passwordStrengthColor = computed(() => {
	const colors = {
		0: '#909399', // gray
		1: '#F56C6C', // red
		2: '#E6A23C', // orange
		3: '#409EFF', // blue
		4: '#67C23A', // green
	};
	return colors[passwordStrength.score as keyof typeof colors];
});

// Format password strength text
const formatPasswordStrength = () => {
	const strengthLabels = ['弱', '弱', '中', '强', '非常强'];
	return strengthLabels[passwordStrength.score];
};

// Check if form can be submitted
const canSubmit = computed(() => {
	return passwordStrength.score >= 3 && passwordForm.uNewPassword === passwordForm.confirmPassword && !!passwordForm.uPassword;
});

// Validate password confirmation
const validatePasswordConfirm = (rule: any, value: string, callback: any) => {
	if (value === '') {
		callback(new Error('请再次输入新密码'));
	} else if (value !== passwordForm.uNewPassword) {
		callback(new Error('两次输入密码不一致'));
	} else {
		callback();
	}
};

// Form validation rules
const passwordRules = reactive<FormRules>({
	uPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
	uNewPassword: [
		{ required: true, message: '请输入新密码', trigger: 'blur' },
		{ min: 8, message: '密码长度至少为8个字符', trigger: 'blur' },
	],
	confirmPassword: [
		{ required: true, message: '请再次输入新密码', trigger: 'blur' },
		{ validator: validatePasswordConfirm, trigger: 'blur' },
	],
});

// Go back to previous page
const goBack = () => {
	router.back();
};

// Handle form submission
const handleSubmit = async () => {
	if (!passwordFormRef.value) return;

	await passwordFormRef.value.validate(async (valid) => {
		if (valid) {
			isSubmitting.value = true;

			try {
				const requestData = {
					uPassword: passwordForm.uPassword,
					uNewPassword: passwordForm.uNewPassword,
					uId: useSysStore().userInfo.UId,
				};

				await postUsersUpdatePassword(requestData);

				ElMessage({
					type: 'success',
					message: '密码修改成功！',
				});

				// Reset form and go back to previous page
				passwordForm.uPassword = '';
				passwordForm.uNewPassword = '';
				passwordForm.confirmPassword = '';

				setTimeout(() => {
					router.back();
				}, 1000);
			} finally {
				isSubmitting.value = false;
			}
		}
	});
};
</script>

<style scoped>
.change-password-container {
	background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 100 100'%3E%3Cg fill-rule='evenodd'%3E%3Cg fill='%23e0e7ff' fill-opacity='0.4'%3E%3Cpath opacity='.5' d='M96 95h4v1h-4v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4h-9v4h-1v-4H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15v-9H0v-1h15V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h9V0h1v15h4v1h-4v9h4v1h-4v9h4v1h-4v9h4v1h-4v9h4v1h-4v9h4v1h-4v9h4v1h-4v9h4v1h-4v9zm-1 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-9-10h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm9-10v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-9-10h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm9-10v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-9-10h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm9-10v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-10 0v-9h-9v9h9zm-9-10h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9zm10 0h9v-9h-9v9z'/%3E%3Cpath d='M6 5V0H5v5H0v1h5v94h1V6h94V5H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
	background-position: center;
}
</style>
