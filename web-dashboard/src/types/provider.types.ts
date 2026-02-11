export interface Provider {
    id: string;
    name: string;
    description: string | null;
    icon: string | null;
    apiType: number;
    chatCompletionsUrl: string;
    createdTime: number;
    modifiedTime: number;
    deletedTime: number | null;
}

export interface CreateProviderRequest {
    name: string;
    description: string | null;
    chatCompletionsUrl: string;
    apiType: number;
}

export interface UpdateProviderRequest {
    id: string;
    name?: string;
    description?: string | null;
    chatCompletionsUrl?: string;
    apiType?: number;
}
