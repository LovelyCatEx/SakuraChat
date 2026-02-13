import {useEffect, useState} from 'react';
import {Button, Card, ConfigProvider, Form, Input, message, Steps} from 'antd';
import {LockOutlined, MailOutlined, UserOutlined} from '@ant-design/icons';
import {useNavigate} from 'react-router-dom';
import {
  completeInitialization,
  createRootUser,
  type CreateRootUserDTO,
  getInitializationStatus,
  getRootUserStatus
} from '../api/initializer.api.ts';

const { Password } = Input;

export function InitializerPage() {
  const [currentStep, setCurrentStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [checkingStatus, setCheckingStatus] = useState(true);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  useEffect(() => {
    checkInitializationStatus();
  }, []);

  const checkInitializationStatus = async () => {
    setCheckingStatus(true);
    try {
      const statusResponse = await getInitializationStatus();
      if (statusResponse.data) {
        navigate('/auth/login');
      } else {
        const rootUserResponse = await getRootUserStatus();
        if (rootUserResponse.data) {
          setCurrentStep(1);
        }
      }
    } catch (error) {
      console.error('检查初始化状态失败:', error);
    } finally {
      setCheckingStatus(false);
    }
  };

  const steps = [
    {
      title: '创建系统管理员',
      description: '创建具有最高权限的系统管理员账号',
    },
    {
      title: '完成初始化',
      description: '确认系统初始化完成',
    },
  ];

  const handleCreateRootUser = async (values: CreateRootUserDTO) => {
    setLoading(true);
    try {
      await createRootUser({
        username: values.username,
        password: values.password,
        nickname: values.nickname,
        email: values.email,
        points: 0,
      });
      message.success('系统管理员创建成功');
      setCurrentStep(currentStep + 1);
    } catch (error) {
      message.error('系统管理员创建失败');
      console.error('创建root用户失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCompleteInitialization = async () => {
    setLoading(true);
    try {
      await completeInitialization();
      message.success('系统初始化完成');
      navigate('/auth/login');
    } catch (error) {
      message.error('系统初始化失败');
      console.error('完成初始化失败:', error);
    } finally {
      setLoading(false);
    }
  };

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
        </nav>

        {/* Background Decorations */}
        <div className="fixed top-[-10%] left-[-5%] w-72 h-72 bg-blue-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob"></div>
        <div className="fixed bottom-[-10%] right-[-5%] w-96 h-96 bg-cyan-200 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-2000"></div>

        {/* Main Content */}
        <div className="w-full max-w-3xl p-8 z-10">
          <Card className="border-none shadow-lg rounded-2xl overflow-hidden">
            <div className="text-center mb-8">
              <h1 className="text-2xl font-bold text-gray-800 mb-2">系统初始化</h1>
              <p className="text-gray-500">完成以下步骤初始化您的SakuraChat系统</p>
            </div>

            <Steps current={currentStep} items={steps} titlePlacement="vertical" className="mb-8" />

            <Form
              form={form}
              layout="vertical"
              disabled={checkingStatus}
              onFinish={currentStep === 0 ? handleCreateRootUser : handleCompleteInitialization}
            >
              {currentStep === 0 ? (
                <>
                  <Form.Item
                    name="username"
                    label="用户名"
                    rules={[{ required: true, message: '请输入用户名' }]}
                  >
                    <Input prefix={<UserOutlined className="text-gray-400 mr-2" />} placeholder="输入用户名" className="rounded-lg h-10" />
                  </Form.Item>

                  <Form.Item
                    name="password"
                    label="密码"
                    rules={[{ required: true, message: '请输入密码' }]}
                  >
                    <Password prefix={<LockOutlined className="text-gray-400 mr-2" />} placeholder="输入密码" className="rounded-lg h-10" />
                  </Form.Item>

                  <Form.Item
                    name="nickname"
                    label="昵称"
                    rules={[{ required: true, message: '请输入昵称' }]}
                  >
                    <Input placeholder="输入昵称" className="rounded-lg h-10" />
                  </Form.Item>

                  <Form.Item
                    name="email"
                    label="邮箱"
                    rules={[{ required: true, message: '请输入邮箱' }, { type: 'email', message: '邮箱格式不正确' }]}
                  >
                    <Input prefix={<MailOutlined className="text-gray-400 mr-2" />} placeholder="输入邮箱" className="rounded-lg h-10" />
                  </Form.Item>
                </>
              ) : (
                <div className="text-center py-8">
                  <p className="text-gray-600 mb-4">系统管理员已创建成功</p>
                  <p className="text-gray-500">点击"完成初始化"按钮完成系统初始化过程</p>
                </div>
              )}

              <Form.Item className="mt-8">
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  className="w-full h-12 rounded-xl font-bold"
                >
                  {currentStep === 0 ? '创建系统管理员' : '完成初始化'}
                </Button>
              </Form.Item>
            </Form>
          </Card>
        </div>

        <style>{`
          @keyframes blob {
            0% {
              transform: translate(0px, 0px) scale(1);
            }
            33% {
              transform: translate(30px, -50px) scale(1.1);
            }
            66% {
              transform: translate(-20px, 20px) scale(0.9);
            }
            100% {
              transform: translate(0px, 0px) scale(1);
            }
          }
          .animate-blob {
            animation: blob 7s infinite;
          }
          .animation-delay-2000 {
            animation-delay: 2s;
          }
        `}</style>
      </div>
    </ConfigProvider>
  );
}
