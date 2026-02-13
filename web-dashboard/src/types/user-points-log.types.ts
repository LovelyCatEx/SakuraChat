
export interface UserPointsLog {
    id: string;
    userId: number;
    deltaPoints: number;
    afterBalance: number;
    reasonType: number;
    relatedTableType1: number | null;
    relatedTableId1: number | null;
    relatedTableType2: number | null;
    relatedTableId2: number | null;
    relatedTableType3: number | null;
    relatedTableId3: number | null;
    relatedTableType4: number | null;
    relatedTableId4: number | null;
    createdTime: number;
    modifiedTime: number;
    deletedTime: number | null;
    realReasonType: string;
}