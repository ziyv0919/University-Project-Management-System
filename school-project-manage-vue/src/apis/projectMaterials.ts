import { AxiosRequestConfig } from 'axios';
import request from '@/request';

export function postProjectMaterialsInsert(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-materials/insert',
		method: 'post',
		...config,
	});
}

export function postProjectMaterialsFindAll(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-materials/findAll',
		method: 'post',
		...config,
	});
}

export function postProjectMaterialsFind(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-materials/find',
		method: 'post',
		...config,
	});
}

export function postProjectMaterialsDelete(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-materials/delete',
		method: 'post',
		...config,
	});
}
