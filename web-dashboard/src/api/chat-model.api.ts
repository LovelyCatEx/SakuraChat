import {doGet, doPost, type PageQuery, type PaginatedResponseData, type ApiResponse} from "./sakurachat-request.ts";
import type {ChatModel} from "../types/chat-model.types.ts";

export function getChatModelList(query: PageQuery): Promise<ApiResponse<PaginatedResponseData<ChatModel>>> {
    return doGet<PaginatedResponseData<ChatModel>>('/api/manager/chat-model/list', {...query});
}

export function searchChatModels(
    keyword: string,
    page: number = 1,
    pageSize: number = 5
): Promise<ApiResponse<PaginatedResponseData<ChatModel>>> {
    return doGet<PaginatedResponseData<ChatModel>>('/api/manager/chat-model/search', { keyword, page, pageSize });
}

export function getChatModelById(id: string): Promise<ApiResponse<ChatModel>> {
    return doGet<ChatModel>('/api/manager/chat-model/getById', { id });
}

export function deleteChatModel(id: string): Promise<ApiResponse<unknown>> {
    return doPost<unknown>('/api/manager/chat-model/delete', { id: id });
}

export function createChatModel(dto: CreateChatModelDTO): Promise<ApiResponse<unknown>> {
    return doPost<unknown>('/api/manager/chat-model/create', {...dto});
}

export function updateChatModel(dto: UpdateChatModelDTO): Promise<ApiResponse<unknown>> {
    return doPost<unknown>('/api/manager/chat-model/update', {...dto});
}

export interface CreateChatModelDTO {
    name: string;
    description?: string | null;
    providerId: string;
    qualifiedName: string;
    maxContextTokens: number;
    temperature: number;
    maxTokens: number;
    inputTokenPointRate: number;
    outputTokenPointRate: number;
    cachedInputTokenPointRate: number;
    credentialId: string;
}

export interface UpdateChatModelDTO {
    id: string;
    name?: string | null;
    description?: string | null;
    providerId?: string | null;
    qualifiedName?: string | null;
    maxContextTokens?: number | null;
    temperature?: number | null;
    maxTokens?: number | null;
    inputTokenPointRate?: number | null;
    outputTokenPointRate?: number | null;
    cachedInputTokenPointRate?: number | null;
    credentialId?: string | null;
    active?: boolean | null;
}