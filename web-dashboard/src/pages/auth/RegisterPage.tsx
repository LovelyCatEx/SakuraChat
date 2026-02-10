import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Form, Input, message } from 'antd';
import { LockOutlined, MailOutlined, UserOutlined } from '@ant-design/icons';
import { AuthCardLayout } from './AuthorizationPage.tsx';

export function RegisterPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = (values: any) => {
    setLoading(true);
    console.log('Register Success:', values);
    setTimeout(() => {
      setLoading(false);
      void message.success('注册成功！');
      navigate('/login');
    }, 1500);
  };

  return (
    <AuthCardLayout
      title="创建账号"
      subtitle="开启您的全新旅程"
      footerText="已经有账号了?"
      footerLink="返回登录"
      footerAction={() => navigate('/auth/login')}
    >
      <Form
        name="register_form"
        layout="vertical"
        onFinish={onFinish}
        size="large"
        requiredMark={false}
        autoComplete="off"
      >
        <Form.Item
          name="username"
          rules={[{ required: true, message: '请输入用户名' }]}
        >
          <Input
            prefix={<UserOutlined className="text-gray-400 mr-2" />}
            placeholder="用户名"
            className="hover:border-blue-400 focus:border-blue-500 rounded-xl"
          />
        </Form.Item>

        <Form.Item
          name="email"
          rules={[
            { required: true, message: '请输入邮箱' },
            { type: 'email', message: '邮箱格式不正确' },
          ]}
        >
          <Input
            prefix={<MailOutlined className="text-gray-400 mr-2" />}
            placeholder="电子邮箱"
            className="hover:border-blue-400 focus:border-blue-500 rounded-xl"
          />
        </Form.Item>

        <Form.Item
          name="password"
          rules={[{ required: true, message: '请输入密码' }]}
        >
          <Input.Password
            prefix={<LockOutlined className="text-gray-400 mr-2" />}
            placeholder="密码"
            className="hover:border-blue-400 focus:border-blue-500 rounded-xl"
          />
        </Form.Item>

        <Form.Item
          name="confirmPassword"
          dependencies={['password']}
          rules={[
            { required: true, message: '请确认密码' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('password') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error('两次输入的密码不一致'));
              },
            }),
          ]}
        >
          <Input.Password
            prefix={<LockOutlined className="text-gray-400 mr-2" />}
            placeholder="确认密码"
            className="hover:border-blue-400 focus:border-blue-500 rounded-xl"
          />
        </Form.Item>

        <Form.Item className="mb-4">
          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
            className="w-full h-12 text-base font-semibold shadow-lg shadow-blue-100 rounded-xl border-none bg-blue-600 hover:bg-blue-500 active:scale-[0.98] transition-all"
          >
            注册账号
          </Button>
        </Form.Item>
      </Form>
    </AuthCardLayout>
  );
}
