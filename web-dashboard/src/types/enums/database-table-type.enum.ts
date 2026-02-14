export enum DatabaseTableType {
    USERS = 0,
    AGENTS = 1,
    CHAT_MODELS = 2,
    POINTS_CD_KEYS = 3
}

export function mapToDatabaseTableType(type: number | null): DatabaseTableType | null {
    if (type === null) {
        return null;
    }
    const typeMap: Record<number, DatabaseTableType> = {
        0: DatabaseTableType.USERS,
        1: DatabaseTableType.AGENTS,
        2: DatabaseTableType.CHAT_MODELS,
        3: DatabaseTableType.POINTS_CD_KEYS
    };
    return typeMap[type] || null;
}
