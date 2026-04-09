import { AxiosInterceptorManager, AxiosResponse } from 'axios';
import ElMessage from '@/lib/message';

type FirstParameter<T> = T extends (first: infer P, ...args: any[]) => any ? P : never;

export const interceptorsReq: FirstParameter<AxiosInterceptorManager<any>['use']> = (config) => {
	config.headers.Authorization = 'Bearer ' + localStorage.getItem('sys-token');
	return config;
};

export const interceptorsRes: FirstParameter<AxiosInterceptorManager<AxiosResponse>['use']> = (response) => {
	if (response.data.code === 200) {
		return new Promise((resolve) => {
			resolve(response.data);
		});
	} else {
		if (response.data.code === 401) {
			setTimeout(() => {
				window.location.href = '/login';
				localStorage.removeItem('sys-token');
				localStorage.removeItem('sys-userInfo');
			}, 1000);
		}
		return new Promise((_resolve, reject) => {
			ElMessage.error(response.data.message || '请求错误');
			reject(response);
		});
	}
};
