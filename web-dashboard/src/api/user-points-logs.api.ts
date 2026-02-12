import {doGet, type PaginatedResponseData} from "./sakurachat-request.ts";
import type {UserPointsLog} from "../types/user-points-log.types.ts";

export function listUserPointsLogs(page: number, pageSize: number) {
    return doGet<PaginatedResponseData<UserPointsLog>>('/api/point/logs', {page, pageSize});
}