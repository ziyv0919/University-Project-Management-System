import request from '@/request';
import { AxiosRequestConfig } from 'axios';

// 日志分页查询参数类型
type LogFindAllParams = {
	pageIndex: number;
	pageSize: number;
	logId?: number;
	requestUrl?: string;
	uId?: number;
	uName?: string;
};

// 日志详情查询参数类型
type LogFindParams = {
	logId: number;
};

// 分页获取日志列表
export function postLogsFindAll(data: LogFindAllParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/logs/findAll',
		method: 'post',
		data,
		...config,
	});
}

// 获取日志详情
export function postLogsFind(data: LogFindParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/logs/find',
		method: 'post',
		data,
		...config,
	});
}
