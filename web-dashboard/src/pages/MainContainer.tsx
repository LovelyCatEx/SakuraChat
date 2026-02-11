import React, {type JSX, useMemo, useState} from 'react';
import {Route, Routes, useLocation, useNavigate} from 'react-router-dom';
import {Avatar, Button, ConfigProvider, Divider, Dropdown, Layout, Menu, Space,} from 'antd';
import {
  CloudServerOutlined,
  DashboardOutlined,
  DatabaseOutlined,
  KeyOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  RobotOutlined,
  SettingOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons';
import {DashboardPage} from './manager/dashboard/DashboardPage.tsx';
import {AgentPage} from './manager/agent/AgentPage.tsx';
import {CredentialPage} from './manager/credential/CredentialPage.tsx';
import {ChatModelPage} from './manager/chat-model/ChatModelPage.tsx';
import {ProviderPage} from './manager/provider/ProviderPage.tsx';
import {UserRolePage} from './manager/user/role/UserRolePage.tsx';
import {SettingsPage} from './manager/settings/SettingsPage.tsx';
import {ThirdPartyAccountPage} from './manager/third-party-account/ThirdPartyAccountPage.tsx';
import {UserPage} from './manager/user/UserPage.tsx';
import './MainContainerStyles.css';
import type {ItemType} from "antd/es/menu/interface";
import {clearUserAuthentication} from "../utils/token.utils.ts";
import {useLoggedUser} from "../compositions/use-logged-user.ts";

const { Header, Sider, Content } = Layout;

interface MenuItem {
  key: string;
  icon: JSX.Element | React.ReactNode;
  label: string;
}

export function MainContainer() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const handleMenuClick = (e: unknown) => {
    navigate((e as { key: string }).key);
  };

  const menuItems: (MenuItem & ItemType)[] = useMemo(() => [
    { key: '/dashboard', icon: <DashboardOutlined />, label: '仪表盘' },
    { key: '/agents', icon: <RobotOutlined />, label: '智能体' },
    { key: '/providers', icon: <CloudServerOutlined />, label: '模型提供商' },
    { key: '/models', icon: <DatabaseOutlined />, label: '语言模型' },
    { key: '/credentials', icon: <KeyOutlined />, label: '凭证管理' },
    { key: '/third-party-accounts', icon: <TeamOutlined />, label: '第三方账号' },
    { key: '/users', icon: <UserOutlined />, label: '用户' },
    { key: '/user-roles', icon: <TeamOutlined />, label: '用户角色' },
    { key: '/settings', icon: <SettingOutlined />, label: '系统设置' },
  ], []);

  const selectedKeys = useMemo(() => {
    const currentPath = location.pathname;
    const matchedKey = menuItems.find((item) =>
        currentPath.startsWith(item.key)
    );
    return matchedKey ? [matchedKey.key] : ['/'];
  }, [location.pathname, menuItems]);

  const loggedUser = useLoggedUser();

  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#2563eb',
          borderRadius: 16,
          fontFamily: 'Inter, system-ui, sans-serif',
        },
        components: {
          Layout: {
            headerBg: 'rgba(255, 255, 255, 0.7)',
            siderBg: '#ffffff',
          },
          Menu: {
            itemBorderRadius: 12,
            itemSelectedBg: '#eff6ff',
            itemSelectedColor: '#2563eb',
          },
        },
      }}
    >
      <Layout className="min-h-screen bg-[#f8fafc]">
        <Header className="fixed top-0 left-0 w-full h-16 px-6 flex items-center justify-between z-50 backdrop-blur-md border-b border-gray-100 shadow-sm">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center shadow-lg shadow-blue-200">
              <span className="text-white font-bold text-lg">S</span>
            </div>
            <span className="text-xl font-bold bg-clip-text bg-gradient-to-r from-gray-800 to-blue-600 tracking-tight">
              SakuraChat
            </span>
          </div>

          <div className="flex flex-row items-center gap-4">
            <Dropdown
              menu={{
                items: [
                  { key: 'profile', label: '个人中心', icon: <UserOutlined /> },
                  {
                    key: 'logout',
                    label: '退出登录',
                    icon: <LogoutOutlined />,
                    danger: true,
                  },
                ],
                onClick: (e) => {
                  if (e.key === 'profile') {
                    navigate('/profile');
                  } else if (e.key === 'logout') {
                    clearUserAuthentication();
                    navigate('/auth/login');
                  }
                },
              }}
            >
              <Space className="cursor-pointer">
                <Avatar
                  style={{ backgroundColor: '#2563eb' }}
                  icon={<UserOutlined />}
                />
                <span className="hidden sm:inline font-medium text-gray-700">
                  {loggedUser?.userProfile?.nickname}
                </span>
              </Space>
            </Dropdown>
          </div>
        </Header>

        <Layout className="mt-16">
          {/* Left-Side Menu */}
          <Sider
            width={240}
            trigger={null}
            collapsible
            collapsed={collapsed}
            className="hidden md:block overflow-auto h-[calc(100vh-64px)] fixed left-0 border-r border-gray-100"
          >
            <div className="flex items-center justify-between px-4 py-2">
              {!collapsed && <span className="text-sm text-gray-800"></span>}

              {/* Left-Side Menu Collapse Button */}
              <Button
                type="text"
                size="small"
                onClick={() => setCollapsed(!collapsed)}
                className={`w-8 h-8 flex items-center justify-center rounded-lg hover:bg-gray-100 transition-colors ${collapsed ? 'mx-auto' : ''}`}
              >
                {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              </Button>
            </div>

            <Divider className="mt-0 mb-0" plain />

            <Menu
              mode="inline"
              selectedKeys={selectedKeys}
              items={menuItems}
              onClick={handleMenuClick}
              className="py-4 px-2 border-none"
            />
          </Sider>

          {/* Main Router View */}
          <Content
            className={`p-6 transition-all duration-300 ${collapsed ? 'md:ml-20' : 'md:ml-[240px]'}`}
          >
            <Routes>
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/agents" element={<AgentPage />} />
              <Route path="/providers" element={<ProviderPage />} />
              <Route path="/models" element={<ChatModelPage />} />
              <Route path="/credentials" element={<CredentialPage />} />
              <Route path="/third-party-accounts" element={<ThirdPartyAccountPage />} />
              <Route path="/users" element={<UserPage />} />
              <Route path="/user-roles" element={<UserRolePage />} />
              <Route path="/settings" element={<SettingsPage />} />
            </Routes>
          </Content>
        </Layout>
      </Layout>
    </ConfigProvider>
  );
}
