import type {UserProfileVO} from "../types/user.types.ts";
import {doGet} from "./sakurachat-request.ts";

export function getUserProfile(userId?: number) {
    return doGet<UserProfileVO>('/api/user/profile', { userId });
}