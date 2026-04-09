import { AxiosRequestConfig } from 'axios';
import request from '@/request';

// Login request interface
interface LoginRequestParams {
	uUsername: string;
	uPassword: string;
}

// Register request interface
interface RegisterRequestParams {
	uUsername: string;
	uPassword: string;
	uName: string;
	uRole: number;
}

interface ChangePasswordRequestParams {
	uPassword: string;
	uNewPassword: string;
	uId: number;
}

interface FindUserParams {
	uId: number;
}

export function postUsersRegister(data: RegisterRequestParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/users/register',
		method: 'post',
		data,
		...config,
	});
}

export function postUsersLogin(data: LoginRequestParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/users/login',
		method: 'post',
		data,
		...config,
	});
}

interface InsertUserParams {
	uUsername: string;
	uPassword: string;
	uName: string;
	uRole: number;
}

export function postUsersInsert(data: InsertUserParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/users/insert',
		method: 'post',
		data,
		...config,
	});
}

interface FindAllUsersParams {
	pageIndex: number;
	pageSize: number;
	uId?: number;
	uRole?: number;
	uName?: string;
	uUsername?: string;
	uEmail?: string;
}

export function postUsersFindAll(data: FindAllUsersParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/users/findAll',
		method: 'post',
		data,
		...config,
	});
}

export function postUsersFind(data: FindUserParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/users/find',
		method: 'post',
		data,
		...config,
	});
}

export function postUsersUpdatePassword(data: ChangePasswordRequestParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/users/updatePassword',
		method: 'post',
		data,
		...config,
	});
}

interface UpdateUserParams {
	uId: number;
	uName: string;
	uRole: number;
	uEmail: string;
	uPhone: string;
}

export function postUsersUpdate(data: UpdateUserParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/users/update',
		method: 'post',
		data,
		...config,
	});
}

interface DeleteUserParams {
	uId: number;
}

export function postUsersDelete(data: DeleteUserParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/users/delete',
		method: 'post',
		data,
		...config,
	});
}

interface RemoteSelectParams {
	searchText: string;
}

export function postUsersRemoteSelect(data: RemoteSelectParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/users/remoteSelect',
		method: 'post',
		data,
		...config,
	});
}

interface AvatarDataParams {
	uId: number;
	uAvatar: string;
}

export function postUsersAvatar(data: AvatarDataParams, config: AxiosRequestConfig = {}) {
	return request({
		url: '/users/avatar',
		method: 'post',
		data,
		...config,
	});
}
