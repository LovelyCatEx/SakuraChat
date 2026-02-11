import {doGet, doPost, type PageQuery, type PaginatedResponseData} from "./sakurachat-request.ts";
import type {ThirdPartyAccount, CreateThirdPartyAccountDTO, UpdateThirdPartyAccountDTO} from "../types/third-party-account.types.ts";

export function getThirdPartyAccountList(query: PageQuery) {
    return doGet<PaginatedResponseData<ThirdPartyAccount>>('/api/manager/third-party-account/list', {...query});
}

export function deleteThirdPartyAccount(id: string) {
    return doPost<unknown>('/api/manager/third-party-account/delete', { id: id });
}

export function createThirdPartyAccount(dto: CreateThirdPartyAccountDTO) {
    return doPost<unknown>('/api/manager/third-party-account/create', {...dto});
}

export function updateThirdPartyAccount(dto: UpdateThirdPartyAccountDTO) {
    return doPost<unknown>('/api/manager/third-party-account/update', {...dto});
}
