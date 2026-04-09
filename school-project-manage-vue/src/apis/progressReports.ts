import { AxiosRequestConfig } from 'axios';
import request from '@/request';

export function postProgressReportsInsert(config: AxiosRequestConfig = {}) {
	return request({
		url: '/progress-reports/insert',
		method: 'post',
		...config,
	});
}

export function postProgressReportsFindAll(config: AxiosRequestConfig = {}) {
	return request({
		url: '/progress-reports/findAll',
		method: 'post',
		...config,
	});
}

export function postProgressReportsFind(config: AxiosRequestConfig = {}) {
	return request({
		url: '/progress-reports/find',
		method: 'post',
		...config,
	});
}

export function postProgressReportsUpdate(config: AxiosRequestConfig = {}) {
	return request({
		url: '/progress-reports/update',
		method: 'post',
		...config,
	});
}

export function postProgressReportsDelete(config: AxiosRequestConfig = {}) {
	return request({
		url: '/progress-reports/delete',
		method: 'post',
		...config,
	});
}

export function postProgressReportsEvaluate(config: AxiosRequestConfig = {}) {
	return request({
		url: '/progress-reports/evaluate',
		method: 'post',
		...config,
	});
}

export function postProgressReportsBatchInsert(config: AxiosRequestConfig = {}) {
	return request({
		url: '/progress-reports/batchInsert',
		method: 'post',
		...config,
	});
}
