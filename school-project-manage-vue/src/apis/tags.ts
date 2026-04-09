import { AxiosRequestConfig } from 'axios';
import request from '@/request';

// 新增标签请求参数接口
interface InsertTagParams {
	tName: string;
}

// 分页查询标签请求参数接口
interface FindAllTagsParams {
	pageIndex: number;
	pageSize: number;
	tId?: number;
	tName?: string;
}

// 查看标签详情请求参数接口
interface FindTagParams {
	tId: number;
}

// 编辑标签请求参数接口
interface UpdateTagParams {
	tId: number;
	tName: string;
}

// 删除标签请求参数接口
interface DeleteTagParams {
	tId: number;
}

export function postTagsInsert(data: InsertTagParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/tags/insert',
		method: 'post',
		data,
		...config,
	});
}

export function postTagsFindAll(data: FindAllTagsParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/tags/findAll',
		method: 'post',
		data,
		...config,
	});
}

export function postTagsFind(data: FindTagParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/tags/find',
		method: 'post',
		data,
		...config,
	});
}

export function postTagsUpdate(data: UpdateTagParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/tags/update',
		method: 'post',
		data,
		...config,
	});
}

export function postTagsDelete(data: DeleteTagParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/tags/delete',
		method: 'post',
		data,
		...config,
	});
}

export function postTagsSelect(config: AxiosRequestConfig = {}) {
	return request({
		url: '/tags/select',
		method: 'post',
		...config,
	});
}
