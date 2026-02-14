import {doGet, doPost, type PageQuery, type PaginatedResponseData, type ApiResponse} from "./sakurachat-request";
import type {CreatePointsCdKeyRequest, PointsCdKey, UpdatePointsCdKeyRequest} from "../types/points-cdkey.types";

export async function getPointsCdKeyList(query: PageQuery): Promise<ApiResponse<PaginatedResponseData<PointsCdKey>>> {
    return doGet<PaginatedResponseData<PointsCdKey>>('/api/manager/points-cdkey/list', {...query});
}

export async function searchPointsCdKeys(
    keyword: string,
    page: number = 1,
    pageSize: number = 5
): Promise<ApiResponse<PaginatedResponseData<PointsCdKey>>> {
    return doGet<PaginatedResponseData<PointsCdKey>>('/api/manager/points-cdkey/search', { keyword, page, pageSize });
}

export async function getPointsCdKeyById(id: string): Promise<ApiResponse<PointsCdKey>> {
    return doGet<PointsCdKey>('/api/manager/points-cdkey/getById', { id });
}

export async function deletePointsCdKey(id: string) {
    return doPost('/api/manager/points-cdkey/delete', { id });
}

export async function createPointsCdKey(data: CreatePointsCdKeyRequest) {
    return doPost('/api/manager/points-cdkey/create', data);
}

export async function updatePointsCdKey(data: UpdatePointsCdKeyRequest) {
    return doPost('/api/manager/points-cdkey/update', data);
}
