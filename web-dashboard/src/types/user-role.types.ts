export interface UserRole {
    id: string;
    name: string;
    description: string | null;
    createdTime: number;
    modifiedTime: number;
    deletedTime: number | null;
}

export interface CreateUserRoleDTO {
    name: string;
    description?: string | null;
}

export interface UpdateUserRoleDTO {
    id: string;
    name?: string;
    description?: string | null;
}
