import { AxiosRequestConfig } from 'axios';
import request from '@/request';

// 新增申请请求接口
interface InsertApplicationParams {
	uId: number;
	pId: number;
}

export function postProjectApplicationsInsert(data: InsertApplicationParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-applications/insert',
		method: 'post',
		data,
		...config,
	});
}

// 编辑申请请求接口
interface UpdateApplicationParams {
	paId: number;
	uId: number;
	pId: number;
}

export function postProjectApplicationsUpdate(data: UpdateApplicationParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-applications/update',
		method: 'post',
		data,
		...config,
	});
}

// 删除申请请求接口
interface DeleteApplicationParams {
	paId: number;
}

export function postProjectApplicationsDelete(data: DeleteApplicationParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-applications/delete',
		method: 'post',
		data,
		...config,
	});
}

// 查看申请详情请求接口
interface FindApplicationParams {
	paId: number;
}

export function postProjectApplicationsFind(data: FindApplicationParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-applications/find',
		method: 'post',
		data,
		...config,
	});
}

// 分页查询申请请求接口
interface FindAllApplicationsParams {
	pageIndex: number;
	pageSize: number;
	paId?: number;
	uId?: number;
	pId?: number;
	paStatus?: number;
}

export function postProjectApplicationsFindAll(data: FindAllApplicationsParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-applications/findAll',
		method: 'post',
		data,
		...config,
	});
}

// 审核申请请求接口
interface ApproveApplicationParams {
	paId: number;
	paStatus: number; // 1-审核中 2-审核通过 3-审核不通过
}

export function postProjectApplicationsApprove(data: ApproveApplicationParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/project-applications/approve',
		method: 'post',
		data,
		...config,
	});
}
