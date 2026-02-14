export enum UserRole {
    ROOT = 'root',
    ADMIN = 'admin',
    USER = 'user',
}

export function mapToUserRole(role: string): UserRole {
    const roleMap: Record<string, UserRole> = {
        'root': UserRole.ROOT,
        'admin': UserRole.ADMIN,
        'user': UserRole.USER
    };
    return roleMap[role.toLowerCase()];
}