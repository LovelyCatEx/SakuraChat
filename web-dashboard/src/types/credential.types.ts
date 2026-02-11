export interface Credential {
    id: string;
    type: number;
    data: string;
    active: boolean;
    createdTime: number;
    modifiedTime: number;
    deletedTime: number | null;
}

export interface CreateCredentialDTO {
    type: number;
    data: string;
}

export interface UpdateCredentialDTO {
    id: string;
    type?: number;
    data?: string;
    active?: boolean;
}
