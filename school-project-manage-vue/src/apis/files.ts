import { AxiosRequestConfig } from 'axios';
import request from '@/request';

// 文件相关接口类型定义
interface FileInfo {
	fId?: number;
	pId: number;
	fFileUrl: string;
	fOriginalName: string;
	fSize: number;
}

interface BatchInsertFilesParams {
	files: FileInfo[];
}

interface DeleteFileParams {
	fId: number;
}

interface BatchDeleteFilesParams {
	fIds: number[];
}

interface FindByProjectIdParams {
	pId: number;
}

interface FindFileParams {
	fId: number;
}

interface FindAllFilesParams {
	pageIndex: number;
	pageSize: number;
	fId?: number;
	pId?: number;
	fOriginalName?: string;
}

// 文件相关API接口
export const postFilesBatchInsert = (data: BatchInsertFilesParams, config: AxiosRequestConfig = {}) => {
	return request({
		url: '/files/batchInsert',
		method: 'post',
		data,
		...config,
	});
};

export const postFilesDelete = (data: DeleteFileParams, config: AxiosRequestConfig = {}) => {
	return request({
		url: '/files/delete',
		method: 'post',
		data,
		...config,
	});
};

export const postFilesBatchDeleteFiles = (data: BatchDeleteFilesParams, config: AxiosRequestConfig = {}) => {
	return request({
		url: '/files/batchDelete',
		method: 'post',
		data,
		...config,
	});
};

export const postFilesFindByProjectId = (data: FindByProjectIdParams, config: AxiosRequestConfig = {}) => {
	return request({
		url: '/files/findByProjectId',
		method: 'post',
		data,
		...config,
	});
};

export const postFilesFind = (data: FindFileParams, config: AxiosRequestConfig = {}) => {
	return request({
		url: '/files/find',
		method: 'post',
		data,
		...config,
	});
};

export const postFilesFindAll = (data: FindAllFilesParams, config: AxiosRequestConfig = {}) => {
	return request({
		url: '/files/findAll',
		method: 'post',
		data,
		...config,
	});
};
