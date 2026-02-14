import {getUserProfile, getUserRoles} from "../api/user.api.ts";
import {useEffect, useMemo, useState} from "react";
import type {UserProfileVO} from "../types/user.types.ts";
import {message} from "antd";
import {mapToUserRole, UserRole} from "../types/enums/user-roles.enum.ts";

export const useLoggedUser = () => {
    const [userProfile, setUserProfile] = useState<UserProfileVO | null>(null);
    const [userRoles, setUserRoles] = useState<UserRole[]>([]);

    const hasRootRole = useMemo(() => {
        return userRoles.includes(UserRole.ROOT);
    }, [userRoles])

    const hasAdminRole = useMemo(() => {
        return userRoles.includes(UserRole.ROOT) || userRoles.includes(UserRole.ADMIN);
    }, [userRoles])

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
                const roles = res.data ?? []
                setUserRoles(roles.map((e) => mapToUserRole(e)));
            })
            .catch(() => {
                void message.warning("无法获取用户角色信息")
            })
    }, []);

    return { userProfile, userRoles, hasRootRole, hasAdminRole };
}