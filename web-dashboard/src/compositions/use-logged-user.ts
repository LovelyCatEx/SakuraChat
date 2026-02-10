import {getUserProfile} from "../api/user.api.ts";
import {useEffect, useState} from "react";
import type {UserProfileVO} from "../types/user.types.ts";
import {message} from "antd";

export const useLoggedUser = () => {
    const [userProfile, setUserProfile] = useState<UserProfileVO | null>(null);

    useEffect(() => {
        getUserProfile()
            .then((res) => {
                setUserProfile(res.data);
            })
            .catch(() => {
                void message.warning("无法获取用户信息")
            })
    }, []);

    return { userProfile };
}