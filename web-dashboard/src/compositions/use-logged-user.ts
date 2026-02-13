import {getUserProfile, getUserRoles} from "../api/user.api.ts";
import {useEffect, useState} from "react";
import type {UserProfileVO} from "../types/user.types.ts";
import {message} from "antd";

export const useLoggedUser = () => {
    const [userProfile, setUserProfile] = useState<UserProfileVO | null>(null);
    const [userRoles, setUserRoles] = useState<string[]>([]);

    useEffect(() => {
        getUserProfile()
            .then((res) => {
                setUserProfile(res.data);
            })
            .catch(() => {
                void message.warning("无法获取用户信息")
            })
    }, []);

    useEffect(() => {
        getUserRoles()
            .then((res) => {
                setUserRoles(res.data ?? []);
            })
            .catch(() => {
                void message.warning("无法获取用户角色信息")
            })
    }, []);

    return { userProfile, userRoles };
}