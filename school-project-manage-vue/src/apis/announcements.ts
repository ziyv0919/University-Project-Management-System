import { AxiosRequestConfig } from 'axios';
import request from '@/request';

export function postAnnouncementsInsert(config: AxiosRequestConfig = {}) {
	return request({
		url: '/announcements/insert',
		method: 'post',
		...config,
	});
}

export function postAnnouncementsFindAll(config: AxiosRequestConfig = {}) {
	return request({
		url: '/announcements/findAll',
		method: 'post',
		...config,
	});
}

export function postAnnouncementsFind(config: AxiosRequestConfig = {}) {
	return request({
		url: '/announcements/find',
		method: 'post',
		...config,
	});
}

export function postAnnouncementsUpdate(config: AxiosRequestConfig = {}) {
	return request({
		url: '/announcements/update',
		method: 'post',
		...config,
	});
}

export function postAnnouncementsDelete(config: AxiosRequestConfig = {}) {
	return request({
		url: '/announcements/delete',
		method: 'post',
		...config,
	});
}
