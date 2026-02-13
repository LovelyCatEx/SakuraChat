import {doPost} from "./sakurachat-request.ts";
import type {LoginResponse} from "../types/auth.types.ts";

export async function login(username: string, password: string) {
    return doPost<LoginResponse>('/api/user/login', { username: username, password: password });
}

export async function register(
    username: string,
    password: string,
    email: string,
    emailCode: string
) {
    return doPost(
        '/api/user/register',
        { username: username, password: password, email: email, emailCode: emailCode }
    );
}

export async function requestRegisterEmailCode(email: string) {
    return doPost('/api/user/requestRegisterEmailCode', { email: email });
}

export async function requestPasswordResetEmailCode(email: string) {
    return doPost('/api/user/requestPasswordResetEmailCode', { email: email });
}

export async function resetPassword(email: string, emailCode: string, newPassword: string) {
    return doPost(
        '/api/user/resetPassword',
        { email: email, emailCode: emailCode, newPassword: newPassword }
    );
}