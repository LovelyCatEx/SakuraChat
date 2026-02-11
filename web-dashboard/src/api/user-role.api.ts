import {doGet, doPost, type PageQuery, type PaginatedResponseData} from "./sakurachat-request.ts";
import type {UserRole, CreateUserRoleDTO, UpdateUserRoleDTO} from "../types/user-role.types.ts";

export function getUserRoleList(query: PageQuery) {
    return doGet<PaginatedResponseData<UserRole>>('/api/manager/user-role/list', {...query});
}

export function deleteUserRole(id: string) {
    return doPost<unknown>('/api/manager/user-role/delete', { id: id });
}

export function createUserRole(dto: CreateUserRoleDTO) {
    return doPost<unknown>('/api/manager/user-role/create', {...dto});
}

export function updateUserRole(dto: UpdateUserRoleDTO) {
    return doPost<unknown>('/api/manager/user-role/update', {...dto});
}
