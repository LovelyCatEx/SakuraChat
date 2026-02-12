import {doGet, doPost} from "./sakurachat-request.ts";
import type {SakuraChatSystemSettings} from "../types/system-settings.types.ts";

export function getAllSystemSettings() {
    return doGet<SakuraChatSystemSettings>('/api/manager/settings/list');
}

export function updateAllSystemSettings(settings: SakuraChatSystemSettings) {
    return doPost<SakuraChatSystemSettings>(
        '/api/manager/settings/update',
        {...settings},
        {'Content-Type': 'application/json'}
    );
}

export function sendSystemTestEmail(email: string) {
    return doPost<{ email: string }>('/api/manager/settings/sendTestEmail', {email})
}