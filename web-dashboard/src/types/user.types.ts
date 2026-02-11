export interface UserProfileVO {
    userId: number;
    username: string;
    nickname: string;
    email: string | null;
}

export interface User {
    id: string;
    username: string;
    password: string;
    nickname: string;
    email: string;
    points: number;
    createdTime: number;
    modifiedTime: number;
    deletedTime: number | null;
}

export interface CreateUserDTO {
    username: string;
    password: string;
    nickname: string;
    email: string;
}

export interface UpdateUserDTO {
    id: string;
    nickname?: string;
    email?: string;
    points?: number;
}
