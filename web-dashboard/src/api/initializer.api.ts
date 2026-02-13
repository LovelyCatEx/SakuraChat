import {doGet, doPost} from "./sakurachat-request.ts";

export interface CreateRootUserDTO {
  username: string;
  password: string;
  nickname: string;
  email: string;
  points: number;
}

export function getInitializationStatus() {
  return doGet<boolean>('/api/manager/initializer/status');
}

export function getRootUserStatus() {
  return doGet<boolean>('/api/manager/initializer/root-user-status');
}

export function createRootUser(dto: CreateRootUserDTO) {
  return doPost<unknown>('/api/manager/initializer/create-root-user', {...dto});
}

export function completeInitialization() {
  return doPost<unknown>('/api/manager/initializer/complete-initialization', {});
}
