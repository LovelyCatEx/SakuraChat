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