export interface ChatModel {
    id: string;
    name: string;
    description: string | null;
    providerId: string;
    credentialId: string;
    qualifiedName: string;
    temperature: number;
    maxContextTokens: number;
    maxTokens: number;
    inputTokenPointRate: number;
    outputTokenPointRate: number;
    cachedInputTokenPointRate: number;
    active: boolean;
    createdTime: number;
    modifiedTime: number;
    deletedTime: number | null;
}