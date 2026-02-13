import {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Form, Input, message, Row, Col } from 'antd';
import { LockOutlined, MailOutlined } from '@ant-design/icons';
import { AuthCardLayout } from './AuthorizationPage.tsx';
import { requestPasswordResetEmailCode, resetPassword } from '../../api/auth.api.ts';

const { Password } = Input;

export function ForgotPasswordPage() {
  const [loading, setLoading] = useState(false);
  const [sendingCode, setSendingCode] = useState(false);
  const [countdown, setCountdown] = useState(0);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  useEffect(() => {
    document.title = '忘记密码 - SakuraChat'
  }, []);

  useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => {
        setCountdown(countdown - 1);
      }, 1000);
      return () => clearTimeout(timer);
    }
  }, [countdown]);

  const handleSendCode = async () => {
    const email = form.getFieldValue('email');
    if (!email) {
      void message.warning('请先输入邮箱');
      return;
    }

    setSendingCode(true);
    try {
      await requestPasswordResetEmailCode(email);
      void message.success('验证码发送成功，请注意查收');
      setCountdown(60); // 60秒倒计时
    } catch (error) {
      void message.error('验证码发送失败，请稍后重试');
      console.error('发送验证码失败:', error);
    } finally {
      setSendingCode(false);
    }
  };

  const onFinish = async (values: any) => {
    setLoading(true);
    try {
      await resetPassword(values.email, values.emailCode, values.newPassword);
      void message.success('密码重置成功！');
      navigate('/auth/login');
    } catch (error) {
      void message.error('密码重置失败，请稍后重试');
      console.error('重置密码失败:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthCardLayout
      title="忘记密码"
      subtitle="通过邮箱重置您的密码"
      footerText="想起密码了?"
      footerLink="返回登录"
      footerAction={() => navigate('/auth/login')}
    >
      <Form
        form={form}
        name="forgot_password_form"
        layout="vertical"
        onFinish={onFinish}
        size="large"
        requiredMark={false}
        autoComplete="off"
      >
        <Form.Item
          name="email"
          rules={[
            { required: true, message: '请输入邮箱' },
            { type: 'email', message: '邮箱格式不正确' },
            { max: 256, message: '邮箱长度不能超过256个字符' }
          ]}
        >
          <Input
            prefix={<MailOutlined className="text-gray-400 mr-2" />}
            placeholder="电子邮箱"
            className="hover:border-blue-400 focus:border-blue-500 rounded-xl"
          />
        </Form.Item>

        <Form.Item
          name="emailCode"
          rules={[{ required: true, message: '请输入验证码' }]}
        >
          <Row gutter={12}>
            <Col span={16}>
              <Input
                placeholder="验证码"
                className="hover:border-blue-400 focus:border-blue-500 rounded-xl"
              />
            </Col>
            <Col span={8}>
              <Button
                type="primary"
                htmlType="button"
                loading={sendingCode}
                disabled={countdown > 0}
                onClick={handleSendCode}
                className="w-full h-10 text-base font-semibold rounded-xl border-none bg-blue-600 hover:bg-blue-500 active:scale-[0.98] transition-all"
              >
                {countdown > 0 ? `${countdown}s后重试` : '发送验证码'}
              </Button>
            </Col>
          </Row>
        </Form.Item>

        <Form.Item
          name="newPassword"
          rules={[
            { required: true, message: '请输入新密码' },
            { pattern: /^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$/, message: '密码至少8位，且包含数字和字母' },
            { max: 128, message: '密码长度不能超过128个字符' }
          ]}
        >
          <Password
            prefix={<LockOutlined className="text-gray-400 mr-2" />}
            placeholder="新密码"
            className="hover:border-blue-400 focus:border-blue-500 rounded-xl"
          />
        </Form.Item>

        <Form.Item
          name="confirmPassword"
          dependencies={['newPassword']}
          rules={[
            { required: true, message: '请确认新密码' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('newPassword') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error('两次输入的密码不一致'));
              },
            }),
          ]}
        >
          <Password
            prefix={<LockOutlined className="text-gray-400 mr-2" />}
            placeholder="确认新密码"
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
            重置密码
          </Button>
        </Form.Item>
      </Form>
    </AuthCardLayout>
  );
}
