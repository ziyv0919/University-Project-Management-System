import { AxiosRequestConfig } from 'axios';
import request from '@/request';

export function postProjectStudentsFindAll(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-students/findAll',
		method: 'post',
		...config,
	});
}

export function postProjectStudentsFind(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-students/find',
		method: 'post',
		...config,
	});
}

export function postProjectStudentsDelete(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-students/delete',
		method: 'post',
		...config,
	});
}

export function postProjectStudentsInsert(config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-students/insert',
		method: 'post',
		...config,
	});
}
