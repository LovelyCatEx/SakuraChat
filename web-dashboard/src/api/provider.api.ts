import {type ApiResponse, doGet, doPost, type PageQuery, type PaginatedResponseData} from "./sakurachat-request.ts";
import type {CreateProviderRequest, Provider, UpdateProviderRequest} from "../types/provider.types.ts";

export async function getProviderList(query: PageQuery): Promise<ApiResponse<PaginatedResponseData<Provider>>> {
    return doGet<PaginatedResponseData<Provider>>('/api/manager/provider/list', {...query});
}

export async function searchProviders(keyword: string): Promise<ApiResponse<Provider[]>> {
    return doGet<Provider[]>('/api/manager/provider/search', { keyword });
}

export async function getProviderById(id: string): Promise<ApiResponse<Provider>> {
    return doGet<Provider>('/api/manager/provider/getById', { id });
}

export async function deleteProvider(id: string): Promise<ApiResponse<void>> {
    return doPost('/api/manager/provider/delete', { id });
}

export async function createProvider(data: CreateProviderRequest): Promise<ApiResponse<void>> {
    return doPost('/api/manager/provider/create', data);
}

export async function updateProvider(data: UpdateProviderRequest): Promise<ApiResponse<void>> {
    return doPost('/api/manager/provider/update', data);
}
