import {doGet, doPost, type PaginatedResponseData} from "./sakurachat-request.ts";
import type {ThirdPartyAccount} from "../types/third-party-account.types.ts";

export function getAgentThirdPartyAccounts(agentId: string, page: number = 1, pageSize: number = 20) {
    return doGet<PaginatedResponseData<ThirdPartyAccount>>('/api/manager/agent/third-party-account/list', { agentId, page, pageSize });
}

export function getUnboundAgentThirdPartyAccounts(agentId: string, page: number = 1, pageSize: number = 20) {
    return doGet<PaginatedResponseData<ThirdPartyAccount>>('/api/manager/agent/third-party-account/unbound-list', { agentId, page, pageSize });
}

export function bindAgentThirdPartyAccount(agentId: string, id: string) {
    return doPost<unknown>('/api/manager/agent/third-party-account/bind', { agentId, id });
}

export function unbindAgentThirdPartyAccount(agentId: string, id: string) {
    return doPost<unknown>('/api/manager/agent/third-party-account/unbind', { agentId, id });
}
