import {Button, Card, Col, Form, Input, InputNumber, message, Row, Space, Switch} from "antd";
import {GiftOutlined, MailOutlined, WalletOutlined} from "@ant-design/icons";
import {useEffect, useState} from "react";
import type {SakuraChatSystemSettings} from "../../../../types/system-settings.types.ts";
import {
    getAllSystemSettings,
    sendSystemTestEmail,
    updateAllSystemSettings
} from "../../../../api/system-settings.api.ts";

export function SettingsPage() {
    const [settingsForm] = Form.useForm();
    const [requesting, setRequesting] = useState(false);

    function refreshData() {
        if (requesting) {
            return;
        }

        setRequesting(true);

        getAllSystemSettings()
            .then((res) => {
                if (res.data) {
                    settingsForm.setFieldsValue(res.data);
                } else {
                    void message.warning("无法获取系统设置")
                }
            })
            .catch(() => {
                void message.error("无法获取系统设置")
            })
            .finally(() => {
                setRequesting(false);
            });
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refreshData();
    }, []);

    const handleSaveSettings = (values: SakuraChatSystemSettings) => {
        if (requesting) {
            return;
        }

        setRequesting(true);

        updateAllSystemSettings(values)
            .then(() => {
                void message.success("系统设置保存成功")
            })
            .catch(() => {
                void message.error("系统设置保存失败");
            })
            .finally(() => {
                setRequesting(false);
            });
    };

    const [sendingTestEmail, setSendingTestEmail] = useState(false);
    const [testEmailReceiver, setTestEmailReceiver] = useState<string>("");
    const handleSendTestEmail = () => {
        if (requesting) {
            return;
        }

        if (!testEmailReceiver) {
            void message.warning("测试收件地址不能为空")
            return;
        }

        setSendingTestEmail(true);

        sendSystemTestEmail(testEmailReceiver)
            .then(() => {
                void message.success("测试邮件发送成功")
            })
            .catch(() => {
                void message.error("测试邮件发送失败")
            })
            .finally(() => {
                setSendingTestEmail(false);
            })
    };

    return (
        <div className="max-w-4xl mx-auto">
            <div className="mb-8">
                <h2 className="text-2xl font-bold text-gray-800">系统设置</h2>
                <p className="text-gray-500 mt-1">管理系统各项设置</p>
            </div>

            <Form
                form={settingsForm}
                layout="vertical"
                onFinish={handleSaveSettings}
                initialValues={{
                    mail: {
                        smtp: {
                            host: '',
                            port: 465,
                            username: '',
                            password: '',
                            fromEmail: ''
                        }
                    }
                }}
            >
                <Space orientation="vertical" size={24} className="w-full">
                    <Card
                        loading={requesting}
                        title={<Space><MailOutlined className="text-primary" /><span>SMTP 邮件服务配置</span></Space>}
                        className="border-none shadow-sm rounded-[2rem] overflow-hidden"
                    >
                        <Row gutter={24}>
                            <Col span={18}>
                                <Form.Item
                                    name={['mail', 'smtp', 'host']}
                                    label="SMTP 服务器地址"
                                    rules={[{ required: true, message: '请输入SMTP服务器地址' }]}
                                >
                                    <Input placeholder="smtp.example.com" className="rounded-xl h-10" />
                                </Form.Item>
                            </Col>
                            <Col span={6}>
                                <Form.Item
                                    name={['mail', 'smtp', 'port']}
                                    label="端口"
                                    rules={[{ required: true, message: '请输入端口' }]}
                                >
                                    <InputNumber className="w-full rounded-xl h-10 flex items-center" placeholder="465" />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={24}>
                            <Col span={12}>
                                <Form.Item
                                    name={['mail', 'smtp', 'username']}
                                    label="SMTP 账号"
                                    rules={[{ required: true, message: '请输入SMTP账号' }]}
                                >
                                    <Input placeholder="user@example.com" className="rounded-xl h-10" />
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item
                                    name={['mail', 'smtp', 'password']}
                                    label="SMTP 密码/授权码"
                                    rules={[{ required: true, message: '请输入SMTP密码/授权码' }]}
                                >
                                    <Input.Password placeholder="••••••••" className="rounded-xl h-10" />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={24}>
                            <Col span={12}>
                                <Form.Item
                                    name={['mail', 'smtp', 'fromEmail']}
                                    label="发件人地址"
                                    rules={[{ required: true, message: '请输入发件人地址' }]}
                                >
                                    <Input placeholder="user@example.com" className="rounded-xl h-10" />
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item
                                    name={['mail', 'smtp', 'ssl']}
                                    label="安全连接 (SSL/TLS)"
                                    valuePropName="checked"
                                >
                                    <Switch checkedChildren="开启" unCheckedChildren="关闭" />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={24}>
                            <Col span={24}>
                                <Form.Item
                                    label="测试邮件接收地址"
                                    rules={[{ required: true, message: '请输入收件人地址' }]}
                                >
                                    <div className="flex flex-row items-center space-x-4">
                                        <Input
                                            placeholder="user@example.com"
                                            className="rounded-xl h-10"
                                            onChange={(e) => {
                                                setTestEmailReceiver(e.target.value);
                                            }}
                                        />

                                        <Button onClick={handleSendTestEmail} loading={sendingTestEmail}>
                                            发送测试邮件
                                        </Button>
                                    </div>
                                </Form.Item>
                            </Col>
                        </Row>
                    </Card>

                    <Card
                        loading={requesting}
                        title={<Space><GiftOutlined className="text-orange-500" /><span>新用户注册设置</span></Space>}
                        className="border-none shadow-sm rounded-[2rem] overflow-hidden"
                    >
                        <Row gutter={24} align="middle">
                            <Col span={16}>
                                <div className="pr-4">
                                    <h4 className="text-sm font-semibold text-gray-700 mb-1">新用户注册赠送积分</h4>
                                    <p className="text-xs text-gray-400">当新用户完成注册后，系统将自动发放到该账户的积分。</p>
                                </div>
                            </Col>
                            <Col span={8}>
                                <Form.Item
                                    name={['userRegistration', 'initialPoints']}
                                    className="mb-0"
                                    rules={[{ required: true, message: '请输入赠送积分' }]}
                                >
                                    <InputNumber
                                        min={0}
                                        className="w-full rounded-xl h-12 text-lg font-bold flex items-center"
                                        addonAfter={<WalletOutlined />}
                                    />
                                </Form.Item>
                            </Col>
                        </Row>
                    </Card>

                    <div className="flex justify-end pt-4">
                        <Button
                            type="primary"
                            size="large"
                            loading={requesting}
                            onClick={() => settingsForm.submit()}
                            className="h-12 px-12 rounded-xl shadow-lg font-bold"
                        >
                            保存全局配置
                        </Button>
                    </div>
                </Space>
            </Form>
        </div>
    );
}
