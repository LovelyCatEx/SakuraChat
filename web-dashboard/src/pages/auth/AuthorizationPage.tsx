import { Route, Routes } from 'react-router-dom';
import { LoginPage } from './LoginPage.tsx';
import { RegisterPage } from './RegisterPage.tsx';
import { Button, ConfigProvider, Divider } from 'antd';
import { GithubOutlined, GoogleOutlined } from '@ant-design/icons';
import * as React from 'react';

export function AuthorizationPage() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#2563eb',
          borderRadius: 12,
          fontFamily: 'Inter, system-ui, sans-serif',
        },
      }}
    >
      <div className="min-h-screen w-full flex flex-col items-center justify-center bg-gradient-to-br from-blue-50 via-white to-cyan-50 relative overflow-hidden">
        {/* Top Navigation Bar */}
        <nav className="fixed top-0 left-0 w-full h-16 px-6 sm:px-12 flex items-center justify-between z-50 bg-white/30 backdrop-blur-md border-b border-white/50">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center shadow-lg shadow-blue-200">
              <span className="text-white font-bold text-lg">S</span>
            </div>
            <span className="text-xl font-bold bg-clip-text bg-gradient-to-r from-gray-800 to-gray-600 tracking-tight">
              SakuraChat
            </span>
          </div>

          <div className="flex items-center">
            <a
              href="https://github.com/LovelyCatEx/SakuraChat"
              target="_blank"
              rel="noopener noreferrer"
              className="p-2 text-gray-600 hover:text-blue-600 hover:bg-white/50 rounded-full transition-all"
            >
              <GithubOutlined className="text-2xl" />
            </a>
          </div>
        </nav>

        {/* Background Decorations */}
        <div className="fixed top-[-10%] left-[-5%] w-72 h-72 bg-blue-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob"></div>
        <div className="fixed bottom-[-10%] right-[-5%] w-96 h-96 bg-cyan-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-2000"></div>

        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Routes>
      </div>
    </ConfigProvider>
  );
}

export function AuthCardLayout({
  children,
  title,
  subtitle,
  footerText,
  footerLink,
  footerAction,
}: {
  children: React.ReactNode;
  title: string;
  subtitle: string;
  footerText: string;
  footerLink: string;
  footerAction: () => void;
}) {
  return (
    <div className="min-h-screen w-full flex flex-col items-center justify-center bg-gradient-to-br from-indigo-50 via-white to-cyan-50 relative overflow-hidden">
      <div className="relative w-full max-w-[440px] bg-white/80 backdrop-blur-xl border border-white shadow-2xl rounded-[2.5rem] overflow-hidden transition-all duration-500 ease-in-out mt-12 z-10">
        <div className="p-8 sm:p-12">
          {/* Title */}
          <div className="text-center mb-10">
            <h1 className="text-3xl font-bold text-gray-800 tracking-tight">
              {title}
            </h1>
            <p className="text-gray-500 mt-2 text-sm">{subtitle}</p>
          </div>

          {children}

          {/* Third Party Login */}
          <div className="mt-8">
            <Divider
              plain
              className="text-gray-400 text-[10px] uppercase tracking-widest"
            >
              或者通过以下方式
            </Divider>
            <div className="flex gap-4 mt-6">
              <Button className="flex-1 h-12 rounded-xl flex items-center justify-center hover:bg-gray-50 border-gray-200">
                <GoogleOutlined className="text-lg" />
              </Button>
              <Button className="flex-1 h-12 rounded-xl flex items-center justify-center hover:bg-gray-50 border-gray-200">
                <GithubOutlined className="text-lg" />
              </Button>
            </div>
          </div>

          <div className="mt-10 text-center">
            <span className="text-gray-500 text-sm">{footerText}</span>
            <span
              onClick={footerAction}
              className="ml-2 text-sm font-bold text-blue-600 hover:text-blue-500 transition-colors cursor-pointer"
            >
              {footerLink}
            </span>
          </div>
        </div>
      </div>

      {/* 底部脚注 */}
      <div className="fixed bottom-6 text-gray-400 text-xs z-0">
        © 2025 SakuraChat. All rights reserved.
      </div>
    </div>
  );
}
