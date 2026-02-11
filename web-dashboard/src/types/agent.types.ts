export interface Agent {
    id: string;
    name: string;
    description: string | null;
    prompt: string;
    delimiter: string | null;
    userId: string;
    chatModelId: string;
    createdTime: number;
    modifiedTime: number;
    deletedTime: number | null;
}

export interface CreateAgentDTO {
    name: string;
    description?: string | null;
    prompt: string;
    delimiter?: string | null;
    userId: string;
    chatModelId: string;
}

export interface UpdateAgentDTO {
    id: string;
    name?: string | null;
    description?: string | null;
    prompt?: string | null;
    delimiter?: string | null;
    userId?: string | null;
    chatModelId?: string | null;
}
