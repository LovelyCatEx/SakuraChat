import { Button, Layout, Typography } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import {useEffect} from "react";

const { Content } = Layout;
const { Title, Paragraph } = Typography;

const PrivacyPolicyPage = () => {
    useEffect(() => {
        document.title = '隐私政策 - SakuraChat';
    }, []);

    const handleBack = () => {
        window.history.back();
    };

    return (
        <Layout className="min-h-screen bg-white">
            <Content className="max-w-4xl mx-auto px-6 py-16">
                <div className="mb-12">
                    <Button 
                        icon={<ArrowLeftOutlined />} 
                        onClick={handleBack}
                        className="mb-8"
                    >
                        返回首页
                    </Button>
                    
                    <Title level={1} className="mb-8 text-center">隐私协议</Title>
                    
                    <div className="prose max-w-none">
                        <Paragraph className="text-gray-500 mb-6">
                            SakuraChat 非常重视用户隐私保护。本隐私协议（以下简称“本协议”）旨在向您说明我们如何收集、使用、存储和保护您的个人信息，以及您享有的相关权利。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">1. 我们收集的信息</Title>
                        <Paragraph className="text-gray-600">
                            1.1 当您注册账户或使用我们的服务时，我们可能收集以下信息：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            1.1.1 您提供的个人信息，如姓名、邮箱地址、电话号码等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            1.1.2 您使用我们服务的行为信息，如对话内容、使用时间、使用频率等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            1.1.3 设备信息，如设备型号、操作系统版本、IP 地址等。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">2. 我们如何使用您的信息</Title>
                        <Paragraph className="text-gray-600">
                            2.1 我们使用您的信息主要用于以下目的：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.1.1 提供、维护和改进我们的服务；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.1.2 为您提供个性化的服务体验；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.1.3 与您沟通，回复您的咨询和请求；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.1.4 分析和统计，以了解用户需求和服务使用情况；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.1.5 遵守法律法规，保护我们和用户的合法权益。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">3. 信息的存储与保护</Title>
                        <Paragraph className="text-gray-600">
                            3.1 我们采取各种安全措施来保护您的个人信息，包括但不限于：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            3.1.1 数据加密存储；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            3.1.2 访问权限控制；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            3.1.3 定期安全审计；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            3.1.4 防止未授权访问的技术措施。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            3.2 我们会在法律法规要求的期限内存储您的个人信息，或为实现本协议所述目的所必需的时间内存储。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">4. 信息的共享</Title>
                        <Paragraph className="text-gray-600">
                            4.1 我们不会向第三方出售、出租或分享您的个人信息，除非：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            4.1.1 获得您的明确同意；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            4.1.2 遵守法律法规的要求；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            4.1.3 保护我们和用户的合法权益；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            4.1.4 与我们的关联公司、服务提供商共享，但这些方必须按照我们的要求处理信息并保持保密。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">5. 您的权利</Title>
                        <Paragraph className="text-gray-600">
                            5.1 您对您的个人信息享有以下权利：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            5.1.1 访问权：您有权访问我们收集的关于您的个人信息；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            5.1.2 修改权：您有权要求我们修改不准确的个人信息；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            5.1.3 删除权：在特定情况下，您有权要求我们删除您的个人信息；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            5.1.4 限制处理权：在特定情况下，您有权要求我们限制对您个人信息的处理；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            5.1.5 数据携带权：您有权要求我们以结构化、通用格式提供您的个人信息；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            5.1.6 反对权：您有权反对我们对您个人信息的处理。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">6. 隐私协议的修改</Title>
                        <Paragraph className="text-gray-600">
                            6.1 我们有权随时修改本隐私协议，修改后的协议将在我们的网站或应用程序上公布。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            6.2 您继续使用我们的服务，即表示您接受修改后的隐私协议。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">7. 联系我们</Title>
                        <Paragraph className="text-gray-600">
                            7.1 如果您对本隐私协议有任何疑问或建议，或需要行使您的权利，请通过以下方式联系我们：
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            邮箱：privacy@sakurachat.ai
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">8. 法律适用与争议解决</Title>
                        <Paragraph className="text-gray-600">
                            8.1 本隐私协议的订立、执行、解释及争议的解决均适用中华人民共和国法律。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            8.2 如就本隐私协议发生任何争议，双方应友好协商解决；协商不成的，任何一方均有权将争议提交至有管辖权的人民法院诉讼解决。
                        </Paragraph>
                    </div>
                </div>
            </Content>
        </Layout>
    );
};

export default PrivacyPolicyPage;