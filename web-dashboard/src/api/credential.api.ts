import {doGet, doPost, type PageQuery, type PaginatedResponseData, type ApiResponse} from "./sakurachat-request.ts";
import type {Credential, CreateCredentialDTO, UpdateCredentialDTO} from "../types/credential.types.ts";

export function getCredentialList(query: PageQuery): Promise<ApiResponse<PaginatedResponseData<Credential>>> {
    return doGet<PaginatedResponseData<Credential>>('/api/manager/credential/list', {...query});
}

export function searchCredentials(
    keyword: string,
    page: number = 1,
    pageSize: number = 5
): Promise<ApiResponse<PaginatedResponseData<Credential>>> {
    return doGet<PaginatedResponseData<Credential>>('/api/manager/credential/search', { keyword, page, pageSize });
}

export function getCredentialById(id: string): Promise<ApiResponse<Credential>> {
    return doGet<Credential>('/api/manager/credential/getById', { id });
}

export function deleteCredential(id: string): Promise<ApiResponse<unknown>> {
    return doPost<unknown>('/api/manager/credential/delete', { id: id });
}

export function createCredential(dto: CreateCredentialDTO): Promise<ApiResponse<unknown>> {
    return doPost<unknown>('/api/manager/credential/create', {...dto});
}

export function updateCredential(dto: UpdateCredentialDTO): Promise<ApiResponse<unknown>> {
    return doPost<unknown>('/api/manager/credential/update', {...dto});
}
