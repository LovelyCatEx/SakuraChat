import {
    CloudServerOutlined,
    DatabaseOutlined,
    TeamOutlined,
    ThunderboltOutlined,
    WalletOutlined
} from "@ant-design/icons";
import {Card, Col, Row} from "antd";

export function DashboardPage() {
    const stats = [
        {
            title: '系统用户数',
            value: 12580,
            icon: <TeamOutlined />,
            color: 'bg-blue-500',
        },
        {
            title: '模型供应商',
            value: 24,
            icon: <CloudServerOutlined />,
            color: 'bg-blue-600',
        },
        {
            title: '活跃模型数',
            value: 156,
            icon: <DatabaseOutlined />,
            color: 'bg-sky-500',
        },
        {
            title: '第三方账号数',
            value: 9847,
            icon: <TeamOutlined />,
            color: 'bg-sky-500',
        },
        {
            title: '总 Token 消耗',
            value: '45.8M',
            icon: <ThunderboltOutlined />,
            color: 'bg-blue-400',
        },
        {
            title: '总积分消耗',
            value: 892300,
            icon: <WalletOutlined />,
            color: 'bg-indigo-400',
        },
    ];

    return (
        <div className="max-w-7xl mx-auto">
            <div className="mb-8">
                <h2 className="text-2xl font-bold text-gray-800">
                    下午好，管理员
                </h2>
                <p className="text-gray-500 mt-1">这是系统当前的运行概览。</p>
            </div>

            {/* Statistics */}
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
        </div>
    )
}