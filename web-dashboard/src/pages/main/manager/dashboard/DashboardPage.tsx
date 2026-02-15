import {useNavigate} from "react-router-dom";
import {useState} from "react";
import {useLoggedUser} from "../../../../compositions/use-logged-user.ts";
import {getGreeting} from "../../../../utils/datetime.utils.ts";
import {DashboardStatistics} from "../../../../components/common/DashboardStatistics.tsx";
import {Card, Col, Modal, Row} from "antd";
import {
    UserOutlined,
    PayCircleOutlined,
    TeamOutlined,
    LogoutOutlined,
} from '@ant-design/icons';
import {clearUserAuthentication} from "../../../../utils/token.utils.ts";

interface RouteCardItem {
    key: string;
    title: string;
    icon: React.ReactNode;
    color: string;
    path: string;
}

export function DashboardPage() {
    const { userProfile } = useLoggedUser();
    const navigate = useNavigate();
    const [logoutModalVisible, setLogoutModalVisible] = useState(false);

    const routeCards: RouteCardItem[] = [
        {
            key: 'profile',
            title: '个人中心',
            icon: <UserOutlined />,
            color: 'bg-pink-500',
            path: '/profile',
        },
        {
            key: 'points-logs',
            title: '积分变更记录',
            icon: <PayCircleOutlined />,
            color: 'bg-purple-500',
            path: '/points-logs',
        },
        {
            key: 'third-party-account',
            title: '第三方账号绑定',
            icon: <TeamOutlined />,
            color: 'bg-indigo-500',
            path: '/third-party-account/bind',
        },
        {
            key: 'logout',
            title: '退出登录',
            icon: <LogoutOutlined />,
            color: 'bg-red-500',
            path: '',
        },
    ];

    interface UpdateLogItem {
        version: string;
        date: string;
        color: string;
        changes: string[];
    }

    const updateLogs: UpdateLogItem[] = [
        {
            version: 'v1.0.2',
            date: '2026-02-16',
            color: 'border-pink-400',
            changes: [
                '新增仪表盘更新日志',
                '将上下文窗口从最近 128 条消息调整到 256 条消息',
                '提示词嵌入当前地区的日期时间',
                '提示词嵌入当天的节假日/节气'
            ]
        },
        {
            version: 'v1.0.1',
            date: '2026-02-15',
            color: 'border-purple-400',
            changes: [
                '修复手机端导航菜单被隐藏',
                '调整管理页面样式'
            ]
        },
        {
            version: 'v1.0.0',
            date: '2026-02-14',
            color: 'border-indigo-400',
            changes: [
                'v1.0.0 公测'
            ]
        }
    ];

    const handleLogout = () => {
        clearUserAuthentication();
        navigate('/auth/login');
    };

    return (
        <div className="max-w-7xl mx-auto">
            <div className="mb-8">
                <h2 className="text-2xl font-bold text-gray-800">
                    {getGreeting()}，{userProfile?.nickname}
                </h2>
            </div>

            {/* Statistics */}
            <DashboardStatistics />

            {/* Basic Route Cards */}
            <div className="mb-8">
                <h3 className="text-xl font-semibold text-gray-800 mb-4">快速访问</h3>
                <Row gutter={[24, 24]}>
                    {routeCards.map((card) => (
                        <Col
                            key={card.key}
                            xs={24}
                            sm={12}
                            lg={6}
                        >
                            <Card 
                                className="border-none shadow-sm hover:shadow-md transition-shadow rounded-[2rem] overflow-hidden group cursor-pointer"
                                onClick={() => {
                                    if (card.key === 'logout') {
                                        setLogoutModalVisible(true);
                                    } else {
                                        navigate(card.path);
                                    }
                                }}
                            >
                                <div className="flex items-center gap-4">
                                    <div
                                        className={`w-14 h-14 rounded-2xl ${card.color} flex items-center justify-center text-white text-2xl shadow-inner`}
                                    >
                                        {card.icon}
                                    </div>
                                    <div>
                                        <p className="text-gray-400 text-sm mb-0">
                                            {card.title}
                                        </p>
                                    </div>
                                </div>
                            </Card>
                        </Col>
                    ))}
                </Row>
            </div>

            {/* Update Logs */}
            <div className="mb-8">
                <h3 className="text-xl font-semibold text-gray-800 mb-4">更新日志</h3>
                <Card className="border-none shadow-sm rounded-[2rem] overflow-hidden">
                    <div className="p-4 space-y-4">
                        {updateLogs.map((log, index) => (
                            <div key={index} className={`border-l-4 ${log.color} pl-4 py-2`}>
                                <div className="flex justify-between items-center">
                                    <h4 className="font-semibold text-gray-800">{log.version}</h4>
                                    <span className="text-sm text-gray-500">{log.date}</span>
                                </div>
                                <ul className="mt-2 space-y-2 text-sm text-gray-600">
                                    {log.changes.map((change, changeIndex) => (
                                        <li key={changeIndex} className="flex items-start gap-2">
                                            <span className={`${log.color.replace('border-', 'text-')} mt-1`}>•</span>
                                            <span>{change}</span>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        ))}
                    </div>
                </Card>
            </div>

            {/* Logout Modal */}
            <Modal
                title="退出登录"
                open={logoutModalVisible}
                onCancel={() => setLogoutModalVisible(false)}
                onOk={handleLogout}
                okText="确认退出"
                cancelText="取消"
                okType="danger"
            >
                <p>确定要退出登录吗？</p>
            </Modal>

        </div>
    )
}