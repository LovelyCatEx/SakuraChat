import {doGet, doPost, type PageQuery, type PaginatedResponseData} from "./sakurachat-request.ts";
import type {User, CreateUserDTO, UpdateUserDTO} from "../types/user.types.ts";
import type {UserProfileVO} from "../types/user.types.ts";

export function getUserProfile(userId?: number) {
    return doGet<UserProfileVO>('/api/user/profile', { userId });
}

export function getUserList(query: PageQuery) {
    return doGet<PaginatedResponseData<User>>('/api/manager/user/list', {...query});
}

export function deleteUser(id: string) {
    return doPost<unknown>('/api/manager/user/delete', { id: id });
}

export function createUser(dto: CreateUserDTO) {
    return doPost<unknown>('/api/manager/user/create', {...dto});
}

export function updateUser(dto: UpdateUserDTO) {
    return doPost<unknown>('/api/manager/user/update', {...dto});
}
