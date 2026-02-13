import {doGet, doPost, type PageQuery, type PaginatedResponseData, type ApiResponse} from "./sakurachat-request.ts";
import type {User, CreateUserDTO, UpdateUserDTO} from "../types/user.types.ts";
import type {UserProfileVO} from "../types/user.types.ts";

export function getUserProfile(userId?: number): Promise<ApiResponse<UserProfileVO>> {
    return doGet<UserProfileVO>('/api/user/profile', { userId });
}

export function getMyPoints(): Promise<ApiResponse<number>> {
    return doGet<number>('/api/user/points');
}

export function getUserList(query: PageQuery): Promise<ApiResponse<PaginatedResponseData<User>>> {
    return doGet<PaginatedResponseData<User>>('/api/manager/user/list', {...query});
}

export function searchUsers(keyword: string, page: number = 1, pageSize: number = 5): Promise<ApiResponse<PaginatedResponseData<User>>> {
    return doGet<PaginatedResponseData<User>>('/api/manager/user/search', { keyword, page, pageSize });
}

export function getUserById(id: string): Promise<ApiResponse<User>> {
    return doGet<User>('/api/manager/user/getById', { id });
}

export function deleteUser(id: string): Promise<ApiResponse<unknown>> {
    return doPost<unknown>('/api/manager/user/delete', { id: id });
}

export function createUser(dto: CreateUserDTO): Promise<ApiResponse<unknown>> {
    return doPost<unknown>('/api/manager/user/create', {...dto});
}

export function updateUser(dto: UpdateUserDTO): Promise<ApiResponse<unknown>> {
    return doPost<unknown>('/api/manager/user/update', {...dto});
}