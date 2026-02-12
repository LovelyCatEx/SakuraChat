import {doGet, doPost, type PageQuery, type PaginatedResponseData} from "./sakurachat-request.ts";
import type {Agent, CreateAgentDTO, UpdateAgentDTO} from "../types/agent.types.ts";

export function getAgentList(query: PageQuery) {
    return doGet<PaginatedResponseData<Agent>>('/api/manager/agent/list', {...query});
}

export function searchAgents(keyword: string, page: number = 1, pageSize: number = 5) {
    return doGet<PaginatedResponseData<Agent>>('/api/manager/agent/search', { keyword, page, pageSize });
}

export function deleteAgent(id: string) {
    return doPost<unknown>('/api/manager/agent/delete', { id: id });
}

export function createAgent(dto: CreateAgentDTO) {
    return doPost<unknown>('/api/manager/agent/create', {...dto});
}

export function updateAgent(dto: UpdateAgentDTO) {
    return doPost<unknown>('/api/manager/agent/update', {...dto});
}
