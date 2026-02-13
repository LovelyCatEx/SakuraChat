import {CloudServerOutlined, DatabaseOutlined, TeamOutlined, WalletOutlined} from "@ant-design/icons";
import {Card, Col, Row, Spin} from "antd";
import React, {type JSX, useEffect, useState} from "react";
import {getDashboardStats} from "../../../../api/dashboard.api.ts";
import {useLoggedUser} from "../../../../compositions/use-logged-user.ts";
import {getGreeting} from "../../../../utils/datetime.utils.ts";

interface StatsItem {
    title: string;
    value: string | number;
    icon: JSX.Element | React.ReactNode;
    color: string;
}

export function DashboardPage() {
    const { userProfile } = useLoggedUser();
    const [stats, setStats] = useState<StatsItem[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                setLoading(true);
                const response = await getDashboardStats();
                if (response.code === 200 && response.data) {
                    const data = response.data;
                    setStats([
                        {
                            title: '系统用户数',
                            value: data.totalUsers,
                            icon: <TeamOutlined />,
                            color: 'bg-blue-500',
                        },
                        {
                            title: '模型供应商',
                            value: data.totalProviders,
                            icon: <CloudServerOutlined />,
                            color: 'bg-blue-600',
                        },
                        {
                            title: '活跃模型数',
                            value: data.totalModels,
                            icon: <DatabaseOutlined />,
                            color: 'bg-sky-500',
                        },
                        {
                            title: '第三方账号数',
                            value: data.totalThirdPartyAccounts,
                            icon: <TeamOutlined />,
                            color: 'bg-sky-500',
                        },
                        /*{
                            title: '总 Token 消耗',
                            value: '0',
                            icon: <ThunderboltOutlined />,
                            color: 'bg-blue-400',
                        },*/
                        {
                            title: '总积分消耗',
                            value: data.totalPointsConsumed,
                            icon: <WalletOutlined />,
                            color: 'bg-indigo-400',
                        },
                    ]);
                }
            } catch (error) {
                console.error('Failed to fetch dashboard stats:', error);
            } finally {
                setLoading(false);
            }
        };

        void fetchStats();
    }, []);

    return (
        <div className="max-w-7xl mx-auto">
            <div className="mb-8">
                <h2 className="text-2xl font-bold text-gray-800">
                    {getGreeting()}，{userProfile?.nickname}
                </h2>
                <p className="text-gray-500 mt-1">这是系统当前的运行概览。</p>
            </div>

            {/* Statistics */}
            <Spin spinning={loading}>
                <Row gutter={[24, 24]} className="mb-8">
                    {stats.map((stat, index) => (
                        <Col
                            key={index}
                            xs={24}
                            sm={12}
                            lg={6}
                        >
                            <Card className="border-none shadow-sm hover:shadow-md transition-shadow rounded-[2rem] overflow-hidden group">
                                <div className="flex items-center gap-4">
                                    <div
                                        className={`w-14 h-14 rounded-2xl ${stat.color} flex items-center justify-center text-white text-2xl shadow-inner`}
                                    >
                                        {stat.icon}
                                    </div>
                                    <div>
                                        <p className="text-gray-400 text-sm mb-0">
                                            {stat.title}
                                        </p>
                                        <h3 className="text-2xl font-bold text-gray-800">
                                            {stat.value}
                                        </h3>
                                    </div>
                                </div>
                            </Card>
                        </Col>
                    ))}
                </Row>
            </Spin>
        </div>
    )
}