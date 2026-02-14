export interface PointsCdKey {
    id: string;
    cdKey: string;
    points: number;
    generatedBy: number;
    usedBy: number | null;
    createdTime: number;
    modifiedTime: number;
    deletedTime: number | null;
}

export interface CreatePointsCdKeyRequest {
    cdKey: string;
    points: number;
}

export interface UpdatePointsCdKeyRequest {
    id: string;
    cdKey?: string;
    points?: number;
    generatedBy?: number;
    usedBy?: number | null;
}
