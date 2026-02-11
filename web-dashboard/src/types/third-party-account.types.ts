export interface ThirdPartyAccount {
    id: string;
    accountId: string;
    nickname: string;
    platform: number;
    createdTime: number;
    modifiedTime: number;
    deletedTime: number | null;
}

export interface CreateThirdPartyAccountDTO {
    accountId: string;
    nickname: string;
    platform: number;
}

export interface UpdateThirdPartyAccountDTO {
    id: string;
    accountId?: string | null;
    nickname?: string | null;
    platform?: number | null;
}
