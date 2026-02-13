import {doGet, type ApiResponse} from "./sakurachat-request.ts";

export interface DashboardStats {
  totalUsers: number;
  totalProviders: number;
  totalModels: number;
  totalThirdPartyAccounts: number;
  totalPointsConsumed: number;
}

export function getDashboardStats(): Promise<ApiResponse<DashboardStats>> {
  return doGet<DashboardStats>('/api/manager/dashboard/stats');
}
