import {doGet, doPost, type PageQuery, type PaginatedResponseData, type ApiResponse} from "./sakurachat-request.ts";
import type {CreateProviderRequest, Provider, UpdateProviderRequest} from "../types/provider.types.ts";

export async function getProviderList(query: PageQuery): Promise<ApiResponse<PaginatedResponseData<Provider>>> {
    return doGet<PaginatedResponseData<Provider>>('/api/manager/provider/list', {...query});
}

export async function searchProviders(
    keyword: string,
    page: number = 1,
    pageSize: number = 5
): Promise<ApiResponse<PaginatedResponseData<Provider>>> {
    return doGet<PaginatedResponseData<Provider>>('/api/manager/provider/search', { keyword, page, pageSize });
}

export async function getProviderById(id: string): Promise<ApiResponse<Provider>> {
    return doGet<Provider>('/api/manager/provider/getById', { id });
}

export async function deleteProvider(id: string) {
    return doPost('/api/manager/provider/delete', { id });
}

export async function createProvider(data: CreateProviderRequest) {
    return doPost('/api/manager/provider/create', data);
}

export async function updateProvider(data: UpdateProviderRequest) {
    return doPost('/api/manager/provider/update', data);
}
