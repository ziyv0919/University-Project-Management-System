import { defineStore } from 'pinia';

const useSysStore = defineStore<string, { userInfo: any }, any, { changeUserInfo: (userInfo: any) => void }>('sysStore', {
	state: () => ({
		userInfo: localStorage.getItem('sys-userInfo') === 'undefined' ? '' : JSON.parse(localStorage.getItem('sys-userInfo') ?? '{}'),
	}),
	actions: {
		changeUserInfo(userInfo: any) {
			this.userInfo = userInfo;
			localStorage.setItem('sys-userInfo', JSON.stringify(userInfo));
			localStorage.setItem('sys-token', userInfo?.token);
		},
	},
});

export default useSysStore;
