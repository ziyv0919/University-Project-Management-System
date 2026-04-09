import { AxiosRequestConfig } from 'axios';
import request from '@/request';

export function postProjectTagsInsert(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-tags/insert',
		method: 'post',
		...config,
	});
}

export function postProjectTagsFindAll(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-tags/findAll',
		method: 'post',
		...config,
	});
}

export function postProjectTagsDelete(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-tags/delete',
		method: 'post',
		...config,
	});
}
