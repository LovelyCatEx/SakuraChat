import {doGet, doPost, type PageQuery, type PaginatedResponseData} from "./sakurachat-request.ts";
import type {Credential, CreateCredentialDTO, UpdateCredentialDTO} from "../types/credential.types.ts";

export function getCredentialList(query: PageQuery) {
    return doGet<PaginatedResponseData<Credential>>('/api/manager/credential/list', {...query});
}

export function deleteCredential(id: string) {
    return doPost<unknown>('/api/manager/credential/delete', { id: id });
}

export function createCredential(dto: CreateCredentialDTO) {
    return doPost<unknown>('/api/manager/credential/create', {...dto});
}

export function updateCredential(dto: UpdateCredentialDTO) {
    return doPost<unknown>('/api/manager/credential/update', {...dto});
}
