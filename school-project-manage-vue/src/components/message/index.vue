<template>
	<div :class="['message-container', `message-${$attrs.type}`]">
		<span>
			{{ $attrs.message }}
		</span>
		<span v-if="props.isShowTime" class="time-container"> {{ time }}s </span>
		<el-tooltip placement="top" effect="light">
			<template #content>{{ isShow ? '固定' : '取消固定' }}</template>
			<el-icon class="pointer-icon" @click="pointerClick"><Pointer /></el-icon>
		</el-tooltip>
		<el-tooltip placement="top" effect="light">
			<template #content>{{ '关闭' }}</template>
			<el-icon class="circle-close-filled-icon" @click="closeClick">
				<CircleCloseFilled />
			</el-icon>
		</el-tooltip>
	</div>
</template>

<script setup lang="ts">
import { Pointer, CircleCloseFilled } from '@element-plus/icons-vue';
import { MessageInfo } from '@/lib/message';

defineOptions({
	name: 'JlgFksMessage',
});

const props = withDefaults(
	defineProps<{
		time?: number;
		close: () => void;
		isShowTime?: boolean;
		pointerClick: () => void;
		messageInfo: MessageInfo;
		changeMessage: (message: string) => void;
	}>(),
	{ time: 3000, close() {}, isShowTime: true, pointerClick() {} }
);

const time = ref(props.time / 1000);

const isShow = ref(true);

const pointerClick = () => {
	props.changeMessage('');
	if (isShow.value) {
		isShow.value = false;
		clearInterval(interval.value);
		props.pointerClick();
	} else {
		isShow.value = true;
		setTimeInterval();
	}
};

const closeClick = () => {
	props.changeMessage('');
	props.close();
};

const interval = ref();

const setTimeInterval = () => {
	interval.value = setInterval(() => {
		time.value--;
		if (time.value <= 0) {
			if (isShow.value) {
				props.close();
			}
		}
	}, 1000);
};

onMounted(() => {
	setTimeInterval();
});
</script>

<style lang="scss" scoped>
.message-container {
	padding: 0 20px;
	font-weight: bolder;
	font-size: 14px;

	.time-container {
		margin: 0 10px;
	}

	.pointer-icon,
	.circle-close-filled-icon {
		cursor: pointer;
		margin-right: 10px;
		font-size: 19px;
		vertical-align: bottom;
	}
}
.message-warning {
	color: var(--el-color-warning);
}
.message-error {
	color: var(--el-color-danger);
}
.message-success {
	color: var(--el-color-success);
}
.message-info {
	color: var(--el-color-info);
}
</style>

