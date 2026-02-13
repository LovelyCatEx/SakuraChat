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
                    
                    <Title level={1} className="mb-8 text-center">隐私政策</Title>
                    
                    <div className="prose max-w-none">
                        <Paragraph className="text-gray-500 mb-6">
                            SakuraChat（以下简称“我们”）非常重视用户隐私保护。本隐私政策（以下简称“本政策”）旨在向您说明我们如何收集、使用、存储、共享和保护您的个人信息，以及您享有的相关权利。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">1. 定义</Title>
                        <Paragraph className="text-gray-600">
                            1.1 个人信息：指以电子或者其他方式记录的能够单独或者与其他信息结合识别特定自然人身份或者反映特定自然人活动情况的各种信息。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            1.2 敏感个人信息：指一旦泄露或者非法使用，容易导致自然人的人格尊严受到侵害或者人身、财产安全受到危害的个人信息，包括生物识别、宗教信仰、特定身份、医疗健康、金融账户、行踪轨迹等信息，以及不满十四周岁未成年人的个人信息。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            1.3 处理：指对个人信息进行收集、存储、使用、加工、传输、提供、公开等活动。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">2. 我们收集的信息</Title>
                        <Paragraph className="text-gray-600">
                            2.1 当您注册账户或使用我们的服务时，我们可能收集以下信息：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.1.1 您提供的个人信息：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 账户信息：如用户名、密码、邮箱地址、电话号码等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 个人资料：如姓名、头像、性别、生日等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 其他信息：如您在使用服务过程中主动提供的信息。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.1.2 您使用我们服务的行为信息：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 对话内容：您与AI助手的对话历史；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 使用记录：使用时间、使用频率、功能使用情况等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 浏览信息：浏览的内容、搜索记录等。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.1.3 设备信息：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 设备基本信息：设备型号、操作系统版本、设备标识符（如IMEI、MAC地址）等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 网络信息：IP地址、网络类型、网络状态等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 位置信息：如果您授权，我们可能收集您的地理位置信息。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            2.2 我们通过以下方式收集信息：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.2.1 您主动提供：如注册账户、填写个人资料、提交反馈等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.2.2 自动收集：我们的系统会自动记录您使用服务的相关信息；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            2.2.3 第三方来源：如从我们的关联公司、合作伙伴处获得的信息（如有）。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">3. 我们如何使用您的信息</Title>
                        <Paragraph className="text-gray-600">
                            3.1 我们使用您的信息主要用于以下目的：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            3.1.1 提供、维护和改进我们的服务：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 为您提供AI对话服务；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 维护服务的正常运行；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 改进服务质量和用户体验。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            3.1.2 为您提供个性化的服务体验：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 根据您的偏好和使用习惯，提供个性化的推荐和服务；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 记住您的设置和偏好。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            3.1.3 与您沟通，回复您的咨询和请求：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 回复您的问题和反馈；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 向您发送服务相关的通知。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            3.1.4 分析和统计，以了解用户需求和服务使用情况：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 分析用户行为和偏好；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 统计服务使用数据，优化服务功能。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            3.1.5 遵守法律法规，保护我们和用户的合法权益：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 遵守法律法规的要求；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 防止、发现和处理欺诈、滥用或其他违法违规行为；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 保护我们和用户的财产安全。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">4. 信息的存储与保护</Title>
                        <Paragraph className="text-gray-600">
                            4.1 存储期限：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            4.1.1 我们会在实现本政策所述目的所必需的期限内存储您的个人信息，除非法律另有规定或您另有要求。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            4.1.2 一般情况下，我们会在您账户注销后继续存储您的个人信息，直至法律法规要求的期限届满或实现本政策所述目的所必需的时间。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            4.2 存储地点：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            4.2.1 我们会将您的个人信息存储在中华人民共和国境内。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            4.2.2 如因业务需要，我们需要将您的个人信息传输至境外，我们会按照法律法规的要求，获得您的明确同意，并采取相应的安全保障措施。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            4.3 安全措施：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            4.3.1 我们采取各种安全措施来保护您的个人信息，包括但不限于：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 技术措施：数据加密存储、传输加密、访问控制、定期安全审计等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 管理措施：建立健全的数据安全管理制度、明确数据安全责任、对员工进行数据安全培训等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 物理措施：限制数据中心的物理访问、监控设备等。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            4.4 尽管我们采取了上述安全措施，但请注意，任何互联网传输或电子存储方式都不是完全安全的。我们无法保证您的个人信息绝对安全。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">5. 信息的共享</Title>
                        <Paragraph className="text-gray-600">
                            5.1 我们不会向第三方出售、出租或分享您的个人信息，除非：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            5.1.1 获得您的明确同意；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            5.1.2 遵守法律法规的要求，如响应法律法规的要求、法院命令或政府机关的指令；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            5.1.3 保护我们和用户的合法权益，如防止、发现和处理欺诈、滥用或其他违法违规行为；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            5.1.4 与我们的关联公司、服务提供商共享：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 我们可能会与关联公司共享您的个人信息，以便提供更广泛的服务；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 我们可能会与服务提供商共享您的个人信息，这些服务提供商包括但不限于云服务提供商、支付服务提供商、数据分析服务提供商等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 这些方必须按照我们的要求处理信息并保持保密，不得将信息用于其他目的。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            5.2 我们会对共享的个人信息进行去标识化处理，确保第三方无法识别您的身份，除非获得您的明确同意或法律法规另有要求。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">6. 您的权利</Title>
                        <Paragraph className="text-gray-600">
                            6.1 您对您的个人信息享有以下权利：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            6.1.1 访问权：您有权访问我们收集的关于您的个人信息，包括但不限于您的账户信息、个人资料、使用记录等；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            6.1.2 修改权：您有权要求我们修改不准确的个人信息，或补充不完整的个人信息；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            6.1.3 删除权：在特定情况下，您有权要求我们删除您的个人信息，如：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 我们不再需要使用您的个人信息；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 您撤回了同意；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 我们违反法律法规或本政策处理您的个人信息；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 法律法规要求我们删除。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            6.1.4 限制处理权：在特定情况下，您有权要求我们限制对您个人信息的处理，如：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 您对个人信息的准确性提出异议；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 我们的处理行为违法但您不希望删除；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 我们不再需要使用但您需要用于法律程序。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            6.1.5 数据携带权：您有权要求我们以结构化、通用格式提供您的个人信息，以便您可以将其转移给其他服务提供商；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            6.1.6 反对权：您有权反对我们对您个人信息的处理，如：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 基于我们的合法利益进行的处理；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 用于营销目的的处理。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            6.1.7 撤回同意权：您有权撤回对我们处理您个人信息的同意，撤回同意不影响撤回前基于同意的处理的合法性。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            6.2 如何行使您的权利：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            6.2.1 您可以通过以下方式行使您的权利：
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 登录您的账户，在"设置"或"个人中心"中管理您的个人信息；
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-12">
                            - 通过本政策第10条提供的联系方式与我们联系。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            6.2.2 我们会在收到您的请求后，根据法律法规的要求，在合理的时间内处理您的请求。
                        </Paragraph>
                        <Paragraph className="text-gray-600 ml-6">
                            6.2.3 如果您认为我们的处理行为违反法律法规或侵犯了您的合法权益，您可以向有关部门投诉或举报，或向人民法院提起诉讼。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">7. 儿童隐私保护</Title>
                        <Paragraph className="text-gray-600">
                            7.1 我们非常重视未成年人的隐私保护。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            7.2 我们不会主动收集不满十四周岁未成年人的个人信息。如果您是不满十四周岁的未成年人，请在监护人的陪同下使用我们的服务，并由监护人代为提供个人信息。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            7.3 如果我们发现我们收集了不满十四周岁未成年人的个人信息，我们会立即停止处理，并删除相关信息，除非获得监护人的明确同意。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            7.4 如果您是未成年人的监护人，发现我们收集了未成年人的个人信息，请通过本政策第10条提供的联系方式与我们联系，我们会立即处理。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">8. 第三方服务</Title>
                        <Paragraph className="text-gray-600">
                            8.1 我们的服务可能包含第三方服务的链接或集成，如社交媒体分享按钮、地图服务、支付服务等。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            8.2 这些第三方服务可能会收集您的个人信息，我们对此不承担责任。请您仔细阅读第三方服务的隐私政策，了解他们如何处理您的个人信息。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">9. 隐私政策的修改</Title>
                        <Paragraph className="text-gray-600">
                            9.1 我们有权随时修改本隐私政策，修改后的政策将在我们的网站或应用程序上公布。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            9.2 我们会在修改本隐私政策后，通过适当的方式通知您，如在网站或应用程序上发布通知、向您发送邮件或消息等。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            9.3 您继续使用我们的服务，即表示您接受修改后的隐私政策。如果您不接受修改后的隐私政策，您应停止使用我们的服务。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">10. 联系我们</Title>
                        <Paragraph className="text-gray-600">
                            10.1 如果您对本隐私政策有任何疑问或建议，或需要行使您的权利，请通过以下方式联系我们：
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            邮箱：pjrbuyvi@gmail.com
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">11. 法律适用与争议解决</Title>
                        <Paragraph className="text-gray-600">
                            11.1 本隐私政策的订立、执行、解释及争议的解决均适用中华人民共和国法律。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            11.2 如就本隐私政策发生任何争议，双方应友好协商解决；协商不成的，任何一方均有权将争议提交至有管辖权的人民法院诉讼解决。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            11.3 如本隐私政策的任何条款被视为无效或不可执行，不影响其他条款的效力。
                        </Paragraph>
                    </div>
                </div>
            </Content>
        </Layout>
    );
};

export default PrivacyPolicyPage;