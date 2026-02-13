import {doGet, doPost, type PaginatedResponseData} from "./sakurachat-request.ts";
import type {ThirdPartyAccount} from "../types/third-party-account.types.ts";

export function getUserThirdPartyAccounts(page: number = 1, pageSize: number = 20) {
    return doGet<PaginatedResponseData<ThirdPartyAccount>>('/api/third-party-account/list', { page, pageSize });
}

export function bindUserThirdPartyAccount(code: string) {
    return doPost<unknown>('/api/third-party-account/bind', { code });
}

export function unbindUserThirdPartyAccount(id: string) {
    return doPost<unknown>('/api/third-party-account/unbind', { id });
}