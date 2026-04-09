import { AxiosRequestConfig } from 'axios';
import request from '@/request';

export function postNotificationsInsert(config: AxiosRequestConfig = {}) {
	return request({
		url: '/notifications/insert',
		method: 'post',
		...config,
	});
}

export function postNotificationsFindAll(config: AxiosRequestConfig = {}) {
	return request({
		url: '/notifications/findAll',
		method: 'post',
		...config,
	});
}

export function postNotificationsFind(config: AxiosRequestConfig = {}) {
	return request({
		url: '/notifications/find',
		method: 'post',
		...config,
	});
}

export function postNotificationsUpdate(config: AxiosRequestConfig = {}) {
	return request({
		url: '/notifications/update',
		method: 'post',
		...config,
	});
}

export function postNotificationsDelete(config: AxiosRequestConfig = {}) {
	return request({
		url: '/notifications/delete',
		method: 'post',
		...config,
	});
}

export function postNotificationFindByUserId(config: AxiosRequestConfig = {}) {
	return request({
		url: '/notifications/findByUserId',
		method: 'post',
		...config,
	});
}

export function postNotificationMarkAllAsRead(config: AxiosRequestConfig = {}) {
	return request({
		url: '/notifications/markAllAsRead',
		method: 'post',
		...config,
	});
}

export function postNotificationMarkAsRead(config: AxiosRequestConfig = {}) {
	return request({
		url: '/notifications/markAsRead',
		method: 'post',
		...config,
	});
}
