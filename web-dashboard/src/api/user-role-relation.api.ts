import {doGet, doPost, type PageQuery, type PaginatedResponseData} from "./sakurachat-request.ts";
import type {UserRoleRelation} from "../types/user-role.types.ts";

export function getUserRoleRelationList(query: PageQuery) {
    return doGet<PaginatedResponseData<UserRoleRelation>>('/api/manager/user-role-relation/list', {...query});
}

export function searchUserRoleRelations(keyword: string, page: number = 1, pageSize: number = 5) {
    return doGet<PaginatedResponseData<UserRoleRelation>>('/api/manager/user-role-relation/search', { keyword, page, pageSize });
}

export function getUserRoles(userId: number) {
    return doGet<string[]>('/api/manager/user-role-relation/get-user-roles', { userId });
}

export function updateUserRoleRelations(userId: number, roleIds: string[]) {
    return doPost<unknown>('/api/manager/user-role-relation/update', { userId, roleIds }, { 'Content-Type': 'application/json' });
}
