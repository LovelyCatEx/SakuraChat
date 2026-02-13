import {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {Badge, Button, Card, Col, Layout, Row, Table, Tooltip} from 'antd';
import {
    CheckCircleFilled,
    ClockCircleFilled,
    CloseCircleFilled,
    GithubOutlined,
    MenuOutlined,
    MessageOutlined,
    SyncOutlined,
    UserOutlined
} from '@ant-design/icons';
import type {ColumnGroupType, ColumnType} from "antd/es/table";

const { Header, Content, Footer } = Layout;

const SakuraLogo = ({ size = 40 }) => (
    <div style={{ width: size, height: size }}>
        <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
            <circle cx="100" cy="100" r="70" fill="#FFB7C5" opacity="0.2" />
            <circle cx="100" cy="65" r="35" fill="#FFB7C5" />
            <circle cx="135" cy="100" r="35" fill="#FF8DA1" />
            <circle cx="65" cy="100" r="35" fill="#FF8DA1" />
            <circle cx="100" cy="135" r="35" fill="#FFB7C5" />
            <circle cx="100" cy="100" r="12" fill="white" />
        </svg>
    </div>
);

const ChatPreview = () => {
    const [messages] = useState([
        { id: 1, type: 'user', text: '刚看到你分享的那张落日照片' },
        { id: 2, type: 'user', text: '很漂亮' },
        { id: 3, type: 'ai', text: '谢谢' },
        { id: 4, type: 'ai', text: '那一刻的光影' },
        { id: 5, type: 'ai', text: '确实很难得' },
        { id: 6, type: 'user', text: '是在学校拍的吗' },
        { id: 7, type: 'ai', text: '嗯' },
        { id: 8, type: 'ai', text: '在图书馆顶楼' },
        { id: 9, type: 'ai', text: '正好低头看累了' },
        { id: 10, type: 'ai', text: '抬头就看到了' },
        { id: 11, type: 'user', text: '挺有生活情调的' },
        { id: 12, type: 'ai', text: '还行吧' },
        { id: 13, type: 'ai', text: '只是习惯记录一下' }
    ]);

    return (
        <div className="relative p-1">
            <div className="absolute -top-10 -left-10 w-40 h-40 bg-pink-100 rounded-full mix-blend-multiply filter blur-2xl opacity-70 animate-pulse"></div>
            <div className="absolute -bottom-10 -right-10 w-40 h-40 bg-purple-100 rounded-full mix-blend-multiply filter blur-2xl opacity-70 animate-pulse delay-700"></div>

            <Card
                className="relative z-10 rounded-[2rem] shadow-2xl border-gray-100 overflow-hidden"
                styles={{ body: { padding: 0 } }}
            >
                <div className="bg-gray-50 p-4 border-b border-gray-100 flex items-center space-x-2">
                    <div className="w-3 h-3 rounded-full bg-red-400" />
                    <div className="w-3 h-3 rounded-full bg-yellow-400" />
                    <div className="w-3 h-3 rounded-full bg-green-400" />
                    <span className="ml-4 text-xs text-gray-400 font-medium">SakuraChat WebUI v1.0.0</span>
                </div>

                <div className="p-6 space-y-4 h-[380px] bg-white overflow-y-auto">
                    {messages.map((msg) => (
                        <div key={msg.id} className={`flex items-start space-x-3 ${msg.type === 'user' ? 'flex-row-reverse space-x-reverse' : ''}`}>
                            <div className={`w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center text-white text-[10px] ${msg.type === 'ai' ? 'bg-pink-400' : 'bg-gray-200'}`}>
                                {msg.type === 'ai' ? 'AI' : 'U'}
                            </div>
                            <div className={`p-3 rounded-2xl max-w-[80%] text-sm shadow-sm ${
                                msg.type === 'ai' ? 'bg-gray-50 text-gray-600 rounded-tl-none' : 'bg-pink-400 text-white rounded-tr-none'
                            }`}>
                                {msg.text}
                            </div>
                        </div>
                    ))}
                </div>

                {/*<div className="p-4 bg-white border-t border-gray-100">
                    <Input
                        placeholder="输入消息..."
                        suffix={<SendOutlined className="text-pink-400 cursor-pointer" />}
                        className="rounded-xl bg-gray-50 border-none py-2"
                    />
                </div>*/}
            </Card>
        </div>
    );
};

const MainPage = () => {
    const [scrolled, setScrolled] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        document.title = 'SakuraChat';

        const handleScroll = () => setScrolled(window.scrollY > 50);
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
        }, []);

    type DevelopmentStatus = 'supported' | 'developing' | 'planned' | 'not-supported';
    interface PlatformMessageSupportItem {
        key: string;
        type: string;
        qq: DevelopmentStatus;
        qqTooltips?: string;
        lark: DevelopmentStatus;
        larkTooltips?: string;
        web: DevelopmentStatus;
        webTooltips?: string;
    }

    const renderStatus = (status: DevelopmentStatus, tooltips?: string) => {
        const getStatusConfig = (status: DevelopmentStatus) => {
            switch (status) {
                case 'supported':
                    return {
                        className: 'bg-green-100 text-green-800',
                        icon: <CheckCircleFilled className="text-xs" />,
                        text: '支持'
                    };
                case 'developing':
                    return {
                        className: 'bg-orange-100 text-yellow-800',
                        icon: <ClockCircleFilled className="text-xs" />,
                        text: '开发中'
                    };
                case 'planned':
                    return {
                        className: 'bg-blue-50 text-blue-400',
                        icon: <ClockCircleFilled className="text-xs" />,
                        text: '计划中'
                    };
                case 'not-supported':
                    return {
                        className: 'bg-red-50 text-red-600',
                        icon: <CloseCircleFilled className="text-xs" />,
                        text: '不支持'
                    };
                default:
                    return null;
            }
        };

        const config = getStatusConfig(status);

        if (!config) {
            return status;
        }

        const content = (
            <span className={`px-2 py-1 ${config.className} rounded-full inline-flex items-center justify-center gap-1`}>
            {config.icon}
                {config.text}
        </span>
        );

        return tooltips ? (
            <Tooltip title={tooltips}>
                {content}
            </Tooltip>
        ) : content;
    };

    interface FeatureSupportItem {
        key: string;
        feature: string;
        description: string;
        status: DevelopmentStatus;
        tooltips?: string;
    }

    const featureSupportData: FeatureSupportItem[] = [
        {
            key: 'multi-user',
            feature: '多用户管理',
            description: '支持多个用户同时使用系统',
            status: 'supported'
        },
        {
            key: 'model-management',
            feature: '模型管理',
            description: '管理第三方模型',
            status: 'supported'
        },
        {
            key: 'agent-management',
            feature: '智能体管理',
            description: '管理和配置 AI 智能体',
            status: 'supported'
        },
        {
            key: 'points-management',
            feature: '积分管理',
            description: '用户积分系统管理',
            status: 'supported'
        },
        {
            key: 'third-party-account',
            feature: '第三方账号管理',
            description: '支持绑定和管理第三方账号',
            status: 'supported'
        },
        {
            key: 'openai-format',
            feature: 'OpenAI 格式',
            description: '支持 OpenAI API 格式的请求和响应',
            status: 'supported'
        },
        {
            key: 'gemini-format',
            feature: 'Gemini 格式',
            description: '支持 Google Gemini API 格式',
            status: 'not-supported'
        },
        {
            key: 'anthropic-format',
            feature: 'Anthropic 格式',
            description: '支持 Anthropic API 格式',
            status: 'not-supported'
        },
        {
            key: 'cross-platform-context',
            feature: '跨平台上下文',
            description: '在不同平台间保持对话上下文',
            status: 'supported'
        },
        {
            key: 'speech-recognition',
            feature: '语音识别',
            description: '将语音转换为文本',
            status: 'planned'
        },
        {
            key: 'text-to-speech',
            feature: '语音合成',
            description: '将文本转换为语音',
            status: 'planned'
        },
        {
            key: 'image-recognition',
            feature: '图像识别',
            description: '分析和识别图片内容',
            status: 'planned'
        },
        {
            key: 'image-generation',
            feature: '图像生成',
            description: '根据描述生成图片',
            status: 'planned'
        },
        {
            key: 'emoji-sending',
            feature: '发送表情包',
            description: '支持发送和管理表情包',
            status: 'planned'
        },
        {
            key: 'napcat-manager',
            feature: 'NapCat 服务端管理',
            description: '支持管理接入 SakuraChat 的 NapCat 服务端',
            status: 'planned'
        },
        {
            key: 'lark-bot-manager',
            feature: '飞书机器人管理',
            description: '支持管理接入 SakuraChat 的飞书机器人',
            status: 'planned'
        }
    ];

    const featureSupportColumns: (ColumnGroupType<FeatureSupportItem> | ColumnType<FeatureSupportItem>)[] = [
        {
            title: '功能',
            dataIndex: 'feature',
            key: 'feature',
            width: 280,
            align: 'center'
        },
        {
            title: '描述',
            dataIndex: 'description',
            key: 'description',
            align: 'center'
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status: DevelopmentStatus, record: FeatureSupportItem) => renderStatus(status, record.tooltips),
            align: 'center',
            width: 200.
        }
    ];

    const platformMessageSupportData: PlatformMessageSupportItem[] = [
        {
            key: 'text',
            type: '文本',
            qq: 'supported',
            lark: 'supported',
            web: 'planned',
        },
        {
            key: 'at-reply',
            type: '@/回复',
            qq: 'supported',
            lark: 'planned',
            web: 'planned',
        },
        {
            key: 'emoji',
            type: '内置表情',
            qq: 'supported',
            lark: 'planned',
            web: 'planned',
        },
        {
            key: 'asr',
            type: '语音识别',
            qq: 'planned',
            lark: 'not-supported',
            larkTooltips: '飞书暂不支持语音消息',
            web: 'planned',
        },
        {
            key: 'tts',
            type: '语音合成',
            qq: 'planned',
            lark: 'not-supported',
            larkTooltips: '飞书暂不支持语音消息',
            web: 'planned',
        },
        {
            key: 'image-analyze',
            type: '图片分析',
            qq: 'planned',
            lark: 'planned',
            web: 'planned',
        },
        {
            key: 'image-generation',
            type: '图片生成',
            qq: 'planned',
            lark: 'planned',
            web: 'planned',
        }
    ];

    const platformMessageSupportColumns: (ColumnGroupType<PlatformMessageSupportItem> | ColumnType<PlatformMessageSupportItem>)[] = [
        {
            title: '消息类型',
            dataIndex: 'type',
            key: 'type',
            width: 120
        },
        {
            title: 'QQ',
            dataIndex: 'qq',
            key: 'qq',
            render: (status: DevelopmentStatus, record: PlatformMessageSupportItem) => renderStatus(status, record.qqTooltips),
            align: 'center'
        },
        {
            title: '飞书',
            dataIndex: 'lark',
            key: 'lark',
            render: (status: DevelopmentStatus, record: PlatformMessageSupportItem) => renderStatus(status, record.larkTooltips),
            align: 'center'
        },
        {
            title: 'Web',
            dataIndex: 'web',
            key: 'web',
            render: (status: DevelopmentStatus, record: PlatformMessageSupportItem) => renderStatus(status, record.webTooltips),
            align: 'center'
        }
    ];

    return (
        <Layout className="min-h-screen bg-white">
            <Header className={`fixed w-full z-50 flex items-center justify-between px-6 md:px-12 transition-all duration-300 ${
                scrolled ? 'h-16 bg-white/80 backdrop-blur-md shadow-sm' : 'h-20 bg-transparent'
            }`}>
                <div className="flex items-center space-x-3">
                    <SakuraLogo size={36} />
                    <span className="text-2xl font-bold tracking-tight text-gray-900">
                            Sakura<span className="text-pink-400">Chat</span>
                        </span>
                </div>

                <div className="hidden md:flex items-center space-x-8">
                    <Link to="/terms" className="text-sm font-medium text-gray-600 hover:text-pink-400 transition-colors">
                        服务条款
                    </Link>
                    <Link to="/privacy" className="text-sm font-medium text-gray-600 hover:text-pink-400 transition-colors">
                        隐私协议
                    </Link>
                    <a href="https://github.com/LovelyCatEx/SakuraChat/" target="_blank" rel="noopener noreferrer" className="text-sm font-medium text-gray-600 hover:text-pink-400 transition-colors">
                        GitHub
                    </a>
                    <Button type="primary" shape="round" size="large" className="font-bold px-8" onClick={() => navigate('/auth/register')}>
                        立即开始
                    </Button>
                </div>
                <Button type="text" icon={<MenuOutlined />} className="md:hidden" />
            </Header>

            <Content>
                <section className="pt-32 pb-20 px-6 bg-[radial-gradient(circle_at_top_right,_#fff5f7_0%,_#ffffff_100%)]">
                    <div className="max-w-7xl mx-auto">
                        <Row gutter={[48, 48]} align="middle">
                            <Col xs={24} md={12}>
                                <Badge count="SakuraChat v1.0.0 现已发布"
                                       style={{ backgroundColor: '#fff1f3', color: '#ff8da1', fontWeight: 'bold', padding: '0 12px' }}
                                       className="mb-6"
                                />
                                <h1 className="text-5xl md:text-7xl font-bold leading-tight text-gray-900 mb-6">
                                    遇见未来，<br />
                                    与 AI <span className="text-pink-400">温柔</span>对话
                                </h1>
                                <p className="text-lg text-gray-500 mb-10 leading-relaxed max-w-lg">
                                    SakuraChat 致力于将强大的 AI 技术与极简的设计美学融合。
                                    无论是日常对话还是专业咨询，都能根据你的定制，用自然且精准的交互衔接每一次交互。
                                </p>
                                <div className="flex space-x-4">
                                    <Button type="primary" size="large" className="h-14 px-10 text-lg shadow-lg shadow-pink-100" onClick={() => navigate('/auth/register')}>
                                        免费体验
                                    </Button>
                                    <Button size="large" className="h-14 px-10 text-lg" onClick={() => window.open('https://github.com/LovelyCatEx/SakuraChat?tab=readme-ov-file#sakurachat', '_blank', 'noopener noreferrer')}>
                                        了解更多
                                    </Button>
                                </div>
                            </Col>
                            <Col xs={24} md={12}>
                                <ChatPreview />
                            </Col>
                        </Row>
                    </div>
                </section>

                <section className="py-24 bg-white px-6">
                    <div className="max-w-7xl mx-auto">
                        <div className="text-center mb-16">
                            <h2 className="text-3xl font-bold text-gray-900">为什么选择 SakuraChat？</h2>
                            <div className="w-12 h-1 bg-pink-400 mx-auto mt-4 rounded-full" />
                        </div>

                        <Row gutter={[32, 32]}>
                            {
                                [
                                    {
                                        title: '打破次元壁的交互体验',
                                        desc: '融合语音、视觉与图像生成技术，实现从“对话”到“交流”的跨越。它能听、会看、懂创作，以灵动的反馈为您开启多端自然沟通新境界。',
                                        icon: <MessageOutlined />
                                    },
                                    {
                                        title: '多平台无缝集成',
                                        desc: '全面适配 QQ、飞书等主流协作平台。支持跨端上下文深度记忆，消除信息孤岛，让智能服务完美融入您的工作与生活。',
                                        icon: <SyncOutlined />
                                    },
                                    {
                                        title: '灵活的个性化定义',
                                        desc: '支持自定义人格特质与专属知识库。它是能听懂弦外之音的助手，更是持续进化的数字伙伴，让科技陪伴更具温度。',
                                        icon: <UserOutlined />
                                    }
                                ].map((feature, idx) => (
                                    <Col xs={24} md={8} key={idx}>
                                        <Card
                                            hoverable
                                            className="rounded-3xl border-gray-100 h-full text-center"
                                            styles={{ body: { padding: '40px' } }}
                                        >
                                            <div className="w-14 h-14 bg-pink-50 text-pink-500 rounded-2xl flex items-center justify-center mx-auto mb-6 text-2xl">
                                                {feature.icon}
                                            </div>
                                            <h3 className="text-xl font-bold mb-4">{feature.title}</h3>
                                            <p className="text-gray-500 leading-relaxed">{feature.desc}</p>
                                        </Card>
                                    </Col>
                                ))
                            }
                        </Row>
                    </div>
                </section>

                <section className="bg-white px-6">
                    <div className="max-w-7xl mx-auto">
                        <div className="text-center mb-16">
                            <h2 className="text-3xl font-bold text-gray-900">支持的功能</h2>
                            <p className="text-gray-500 mt-4 max-w-2xl mx-auto">我们提供丰富的功能来满足您的需求，以下是当前的支持情况和未来的开发计划</p>
                            <div className="w-12 h-1 bg-pink-400 mx-auto mt-4 rounded-full" />
                        </div>

                        <Card className="rounded-3xl shadow-sm border-gray-100 overflow-hidden">
                            <Table
                                dataSource={featureSupportData}
                                columns={featureSupportColumns}
                                pagination={false}
                                scroll={{ x: 800 }}
                                className="ant-table-custom"
                            />
                        </Card>
                    </div>
                </section>

                <section className="py-24 bg-[#f8fafc] px-6">
                    <div className="max-w-7xl mx-auto">
                        <div className="text-center mb-16">
                            <h2 className="text-3xl font-bold text-gray-900">支持的平台</h2>
                            <p className="text-gray-500 mt-4 max-w-2xl mx-auto">我们正在不断拓展支持的平台和消息类型，以下是当前的支持情况和未来的开发计划</p>
                            <div className="w-12 h-1 bg-pink-400 mx-auto mt-4 rounded-full" />
                        </div>

                        <Card className="rounded-3xl shadow-sm border-gray-100 overflow-hidden">
                            <Table
                                dataSource={platformMessageSupportData}
                                columns={platformMessageSupportColumns}
                                pagination={false}
                                scroll={{ x: 600 }}
                                className="ant-table-custom"
                            />
                        </Card>
                    </div>
                </section>
            </Content>

            <Footer className="bg-gray-50 py-16 px-6">
                <div className="max-w-7xl mx-auto">
                    <div className="flex flex-col md:flex-row justify-between items-center pb-8 border-b border-gray-200">
                        <div className="flex items-center space-x-3 mb-6 md:mb-0">
                            <SakuraLogo size={28} />
                            <span className="text-xl font-bold text-gray-900">SakuraChat</span>
                        </div>
                        <div className="flex space-x-6">
                            <a href="https://github.com/LovelyCatEx/SakuraChat/" target="_blank" rel="noopener noreferrer">
                                <GithubOutlined className="text-2xl text-gray-400 hover:text-gray-600 cursor-pointer" />
                            </a>
                        </div>
                    </div>
                    <div className="mt-8 flex flex-col md:flex-row justify-between items-center text-gray-400 text-sm">
                        <p>© 2026 SakuraChat AI (LovelyCat). 保留所有权利。</p>
                        <div className="flex space-x-8 mt-4 md:mt-0">
                            <Link to="/terms" className="hover:text-pink-400">服务条款</Link>
                            <Link to="/privacy" className="hover:text-pink-400">隐私政策</Link>
                        </div>
                    </div>
                </div>
            </Footer>
        </Layout>
    );
};

export default MainPage;