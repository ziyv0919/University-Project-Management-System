import { AxiosRequestConfig } from 'axios';
import request from '@/request';

// 新增课题请求接口
interface InsertProjectParams {
	pTtitle: string;
	pDifficulty: number;
	uId: number;
	pMaxStudents: number;
}

export function postProjectsInsert(data: InsertProjectParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/projects/insert',
		method: 'post',
		data,
		...config,
	});
}

// 分页查询课题请求接口
interface FindAllProjectsParams {
	pageIndex: number;
	pageSize: number;
	pId?: number;
	pTitle?: string;
	pDirection?: string;
	pDifficulty?: number;
	uId?: number;
}

export function postProjectsFindAll(data: FindAllProjectsParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/projects/findAll',
		method: 'post',
		data,
		...config,
	});
}

// 查看课题详情请求接口
interface FindProjectParams {
	pId: number;
}

export function postProjectsFind(data: FindProjectParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/projects/find',
		method: 'post',
		data,
		...config,
	});
}

// 编辑课题请求接口
interface UpdateProjectParams {
	pId: number;
	pTtitle: string;
	pDescription: string;
	pDirection: string;
	pDifficulty: number;
	uId: number;
	pMaxStudents: number;
}

export function postProjectsUpdate(data: UpdateProjectParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/projects/update',
		method: 'post',
		data,
		...config,
	});
}

// 删除课题请求接口
interface DeleteProjectParams {
	pId: number;
}

export function postProjectsDelete(data: DeleteProjectParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/projects/delete',
		method: 'post',
		data,
		...config,
	});
}

interface ProjectRemoteSelectParams {
	searchText: string;
}

export function postProjectsRemoteSelect(data: ProjectRemoteSelectParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/projects/remoteSelect',
		method: 'post',
		data,
		...config,
	});
}

interface CoverProjectParams {
	pId: number;
	pCover: string;
}

export function postProjectsCover(data: CoverProjectParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/projects/cover',
		method: 'post',
		data,
		...config,
	});
}
