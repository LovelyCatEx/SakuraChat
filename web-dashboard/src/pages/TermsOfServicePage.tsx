import { Button, Layout, Typography } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import {useEffect} from "react";

const { Content } = Layout;
const { Title, Paragraph } = Typography;

const TermsOfServicePage = () => {
    useEffect(() => {
        document.title = '服务条款 - SakuraChat';
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
                    
                    <Title level={1} className="mb-8 text-center">服务条款</Title>
                    
                    <div className="prose max-w-none">
                        <Paragraph className="text-gray-500 mb-6">
                            欢迎使用 SakuraChat！本服务条款（以下简称“本条款”）是您与 SakuraChat 之间关于使用我们服务的法律协议。请您务必审慎阅读、充分理解本条款各条款内容，特别是免除或者限制责任的条款、法律适用和争议解决条款。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">1. 协议的接受</Title>
                        <Paragraph className="text-gray-600">
                            1.1 您通过注册、登录、使用等方式使用我们的服务，即表示您同意并接受本条款的全部内容，包括我们对本条款随时所做的任何修改。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            1.2 如您不同意本条款的任何内容，您应立即停止使用我们的服务。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">2. 服务内容</Title>
                        <Paragraph className="text-gray-600">
                            2.1 SakuraChat 提供人工智能对话服务，包括但不限于文字聊天、语音交互、图像生成等功能。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            2.2 我们有权根据业务发展需要，随时调整、增加或减少服务内容，无需提前通知您。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">3. 用户账户</Title>
                        <Paragraph className="text-gray-600">
                            3.1 您需要注册一个账户才能使用我们的部分服务。您应提供真实、准确、完整的个人信息，并在信息变更时及时更新。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            3.2 您应妥善保管您的账户密码，对使用您账户进行的所有操作负全部责任。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">4. 用户行为规范</Title>
                        <Paragraph className="text-gray-600">
                            4.1 您在使用我们服务时，应遵守法律法规，不得利用我们的服务从事任何违法违规活动。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            4.2 您不得利用我们的服务侵犯他人的合法权益，包括但不限于知识产权、隐私权、名誉权等。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">5. 知识产权</Title>
                        <Paragraph className="text-gray-600">
                            5.1 SakuraChat 及其相关服务中包含的所有内容（包括但不限于文字、图片、音频、视频、软件等）的知识产权归我们或相关权利人所有。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            5.2 未经我们或相关权利人事先书面许可，您不得以任何方式使用、复制、修改、传播、展示或衍生创作上述内容。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">6. 免责声明</Title>
                        <Paragraph className="text-gray-600">
                            6.1 我们不对因网络故障、系统故障、自然灾害等不可抗力因素导致的服务中断或数据丢失承担责任。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            6.2 我们不对用户之间因使用我们的服务而产生的纠纷承担责任。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">7. 协议的修改</Title>
                        <Paragraph className="text-gray-600">
                            7.1 我们有权随时修改本条款，修改后的条款将在我们的网站或应用程序上公布。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            7.2 您继续使用我们的服务，即表示您接受修改后的条款。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">8. 协议的终止</Title>
                        <Paragraph className="text-gray-600">
                            8.1 您可以随时停止使用我们的服务，终止本协议。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            8.2 如您违反本条款，我们有权暂停或终止向您提供服务，且不承担任何责任。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">9. 法律适用与争议解决</Title>
                        <Paragraph className="text-gray-600">
                            9.1 本条款的订立、执行、解释及争议的解决均适用中华人民共和国法律。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            9.2 如就本条款发生任何争议，双方应友好协商解决；协商不成的，任何一方均有权将争议提交至有管辖权的人民法院诉讼解决。
                        </Paragraph>

                        <Title level={3} className="mt-10 mb-4">10. 其他</Title>
                        <Paragraph className="text-gray-600">
                            10.1 本条款构成您与我们之间关于使用我们服务的完整协议，取代之前的任何口头或书面协议。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            10.2 如本条款的任何条款被视为无效或不可执行，该条款将被视为可分离的，不影响其他条款的效力。
                        </Paragraph>
                        <Paragraph className="text-gray-600">
                            10.3 本条款的标题仅为方便阅读而设，不影响条款的解释。
                        </Paragraph>
                    </div>
                </div>
            </Content>
        </Layout>
    );
};

export default TermsOfServicePage;