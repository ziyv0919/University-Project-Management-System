import { ElMessage } from 'element-plus';
import { AppContext, DefineComponent, h } from 'vue';
import Message from '@/components/message/index.vue';

// message默认配置
const defaultOptions = {
	// 是否显示关闭图标
	showClose: false,
	// 是否可渲染html结构
	dangerouslyUseHTMLString: true,
	// 延时关闭，如果是0，不自动关闭
	duration: 0,
	// 手动延时关闭的时间
	time: 3000,
};

const message = (options?: object, appContext?: null | AppContext) => ElMessage({ ...options, ...defaultOptions }, appContext);

export class MessageInfo {
	message: string;

	close: () => void;

	constructor(message: string, close: () => void) {
		this.message = message;
		this.close = close;
	}
}

export enum E_Type {
	'warning' = 'warning',
	'info' = 'info',
	'error' = 'error',
	'success' = 'success',
}

const messageLogic = (type: E_Type) => {
	const messageInfo = new MessageInfo('', () => {});

	return (message: string, isShowTime: boolean = true) => {
		let timer: number;
		if (!messageInfo.message || messageInfo.message !== message) {
			const close = () => {
				messageIns?.close();
			};
			const pointerClick = () => {
				clearTimeout(timer);
			};

			const changeMessage = (message: string) => {
				messageInfo.message = message;
			};
			const messageIns = ElMessage[E_Type[type]]?.({
				...{
					message: h(
						Message as DefineComponent<{
							message: string;
							type: E_Type;
							close: () => void;
							isShowTime: boolean;
							time: number;
							pointerClick: () => void;
							messageInfo: MessageInfo;
							changeMessage: (message: string) => void;
						}>,
						{
							message,
							type,
							close,
							isShowTime,
							time: defaultOptions.time,
							pointerClick,
							messageInfo,
							changeMessage,
						}
					),
				},
				...defaultOptions,
			});
			messageInfo.message = message;
			messageInfo.close = close;
			timer = setTimeout(() => {
				messageInfo.message = '';
			}, defaultOptions.time) as unknown as number;
		}
	};
};

message.warning = messageLogic(E_Type.warning);

message.success = messageLogic(E_Type.success);

message.info = messageLogic(E_Type.info);

message.error = messageLogic(E_Type.error);

message.closeAll = () => ElMessage.closeAll();

export default message;
