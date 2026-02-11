import {useEffect, useState} from 'react';
import {Button, Card, Col, Form, Input, message, Modal, Popconfirm, Row, Space, Table, Tag} from 'antd';
import {DeleteOutlined, EditOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons';
import type {Agent} from "../../../types/agent.types.ts";
import {createAgent, deleteAgent, getAgentList, updateAgent} from "../../../api/agent.api.ts";
import {formatTimestamp} from "../../../utils/datetime.utils.ts";
import type {ColumnGroupType, ColumnType} from "antd/es/table";
import {EntitySelector} from "../../../components/common/EntitySelector.tsx";
import {getUserById, searchUsers} from "../../../api/user.api.ts";
import {getChatModelById, searchChatModels} from "../../../api/chat-model.api.ts";

const { TextArea } = Input;

export function AgentPage() {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingItem, setEditingItem] = useState<Agent | null>(null);
    const [form] = Form.useForm();
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);

    const [agents, setAgents] = useState<Agent[]>([]);
    const [users, setUsers] = useState<Record<string, {username: string, nickname: string}>>({});
    const [chatModels, setChatModels] = useState<Record<string, {name: string, qualifiedName: string}>>({});

    const fetchUsers = async (keyword: string) => {
        const res = await searchUsers(keyword);
        if (res.data) {
            const userMap: Record<string, {username: string, nickname: string}> = {};
            res.data.forEach(u => {
                userMap[u.id] = {username: u.username, nickname: u.nickname};
            });
            setUsers(userMap);
            return res.data;
        }
        return [];
    };

    const fetchChatModels = async (keyword: string) => {
        const res = await searchChatModels(keyword);
        if (res.data) {
            const modelMap: Record<string, {name: string, qualifiedName: string}> = {};
            res.data.forEach(m => {
                modelMap[m.id] = {name: m.name, qualifiedName: m.qualifiedName};
            });
            setChatModels(modelMap);
            return res.data;
        }
        return [];
    };

    const refreshData = () => {
        setRefreshing(true);

        Promise.all([
            getAgentList({page: currentPage, pageSize: currentPageSize})
        ]).then(([agentRes]) => {
            if (agentRes.data) {
                setAgents(agentRes.data.records);
                setTotal(agentRes.data.total);
                
                const userIds = new Set<string>();
                const chatModelIds = new Set<string>();
                agentRes.data.records.forEach(a => {
                    userIds.add(a.userId);
                    chatModelIds.add(a.chatModelId);
                });

                const userPromises = Array.from(userIds).map(id => getUserById(id));
                const modelPromises = Array.from(chatModelIds).map(id => getChatModelById(id));

                if (userPromises.length === 0 && modelPromises.length === 0) {
                    setUsers({});
                    setChatModels({});
                    return;
                }

                Promise.all([
                    userPromises.length > 0 ? Promise.all(userPromises) : Promise.resolve([]),
                    modelPromises.length > 0 ? Promise.all(modelPromises) : Promise.resolve([])
                ]).then(([userResults, modelResults]) => {
                    const userMap: Record<string, {username: string, nickname: string}> = {};
                    userResults.forEach(res => {
                        if (res.data) {
                            userMap[res.data.id] = {username: res.data.username, nickname: res.data.nickname};
                        }
                    });
                    setUsers(userMap);

                    const modelMap: Record<string, {name: string, qualifiedName: string}> = {};
                    modelResults.forEach(res => {
                        if (res.data) {
                            modelMap[res.data.id] = {name: res.data.name, qualifiedName: res.data.qualifiedName};
                        }
                    });
                    setChatModels(modelMap);
                });
            }
        }).finally(() => {
            setRefreshing(false);
        })
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refreshData();
    }, []);

    const handleAddOrUpdateEdit = (values: Agent) => {
        if (editingItem) {
            updateAgent({
                id: values.id,
                name: values.name,
                description: values.description,
                prompt: values.prompt,
                delimiter: values.delimiter,
                userId: values.userId,
                chatModelId: values.chatModelId
            }).then(() => {
                refreshData();
                void message.success('更新智能体成功');
            }).catch(() => {
                void message.error('更新智能体失败');
            })
        } else {
            createAgent({
                name: values.name,
                description: values.description,
                prompt: values.prompt,
                delimiter: values.delimiter,
                userId: values.userId,
                chatModelId: values.chatModelId,
            }).then(() => {
                refreshData();
                void message.success('新增智能体成功');
            }).catch(() => {
                void message.error('新增智能体失败');
            })
        }

        setIsModalVisible(false);
        setEditingItem(null);
        form.resetFields();
    };

    const deleteAgentItem = (id: string) => {
        deleteAgent(id)
            .then(() => {
                refreshData();
                void message.success('智能体已刪除');
            })
            .catch(() => {
                void message.error('删除智能体失败');
            })
    };

    const openModal = (item: Agent | null = null) => {
        setEditingItem(item);

        if (item) {
            form.setFieldsValue(item);
        } else {
            form.resetFields();
        }

        setIsModalVisible(true);
    };

    const columns: (ColumnGroupType<Agent> | ColumnType<Agent>)[] = [
        {
            title: '智能体名称',
            dataIndex: 'name',
            key: 'name',
            fixed: 'start',
            width: 150,
            render: (name: string, record: Agent) => (
                <Space orientation='vertical' size={0}>
                    <span className="font-bold text-gray-800">{name}</span>
                    <Tag color="blue" className="m-0 text-[10px] leading-4 h-4 px-1 rounded">ID: {record.id}</Tag>
                </Space>
            ),
        },
        {
            title: '描述',
            dataIndex: 'description',
            key: 'description',
            ellipsis: true,
            width: 200,
            render: (description: string | null) => description || <span className="text-gray-400">无</span>
        },
        {
            title: '用户',
            dataIndex: 'userId',
            key: 'userId',
            width: 120,
            render: (id: string) => (
                <Space direction="vertical" size={0}>
                    <span className="text-gray-600">{users[id]?.nickname ?? `#${id}`}</span>
                    <span className="text-xs text-gray-400">@{users[id]?.username ?? ''}</span>
                </Space>
            )
        },
        {
            title: '模型',
            dataIndex: 'chatModelId',
            key: 'chatModelId',
            width: 120,
            render: (id: string) => (
                <Space direction="vertical" size={0}>
                    <span className="text-gray-600">{chatModels[id]?.name ?? `#${id}`}</span>
                    <span className="text-xs text-gray-400">{chatModels[id]?.qualifiedName ?? ''}</span>
                </Space>
            )
        },
        {
            title: '分隔符',
            dataIndex: 'delimiter',
            key: 'delimiter',
            width: 80,
            render: (delimiter: string | null) => delimiter || <span className="text-gray-400">无</span>
        },
        {
            title: '提示词',
            dataIndex: 'prompt',
            key: 'prompt',
            ellipsis: true,
            width: 300,
            render: (prompt: string) => <span className="text-gray-400 text-sm">{prompt}</span>
        },
        {
            title: '创建时间',
            dataIndex: 'createdTime',
            key: 'createdTime',
            width: 120,
            render: (time: number) => <span>{formatTimestamp(time)}</span>
        },
        {
            title: '修改时间',
            dataIndex: 'modifiedTime',
            key: 'modifiedTime',
            width: 120,
            render: (time: number) => <span>{formatTimestamp(time)}</span>
        },
        {
            title: '操作',
            key: 'action',
            fixed: 'end',
            width: 100,
            render: (_: unknown, record: Agent) => (
                <Space>
                    <Button type="text" size="small" icon={<EditOutlined />} onClick={() => openModal(record)} />
                    <Popconfirm title="确定要删除此智能体？" onConfirm={() => deleteAgentItem(record.id)} okText="确认" cancelText="取消">
                        <Button type="text" size="small" icon={<DeleteOutlined />} danger />
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <>
            <div className="mb-8 flex justify-between items-end">
                <div>
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">智能体管理</h1>
                    <p className="text-gray-500 mt-1">配置系统智能体</p>
                </div>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    size="large"
                    className="rounded-xl h-12 shadow-lg shadow-blue-100"
                    onClick={() => openModal()}
                >
                    新增智能体
                </Button>
            </div>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6 flex gap-4">
                    <Input
                        placeholder="搜索智能体..."
                        prefix={<SearchOutlined className="text-gray-400" />}
                        className="max-w-xs rounded-xl h-10"
                    />
                </div>
                <Table
                    columns={columns}
                    dataSource={agents}
                    rowKey="id"
                    scroll={{ x: 1200 }}
                    className="custom-table"
                    pagination={{
                        showSizeChanger: true,
                        defaultPageSize: 20,
                        className: "pr-6",
                        current: currentPage,
                        total: total,
                        pageSize: currentPageSize,
                        pageSizeOptions: [5, 10, 15, 20],
                        onChange: (page: number, pageSize: number) => {
                            setCurrentPage(page);
                            setCurrentPageSize(pageSize);
                            refreshData();
                        }
                    }}
                    loading={refreshing}
                />
            </Card>

            <Modal
                title={editingItem ? "编辑智能体" : "新建智能体"}
                open={isModalVisible}
                onCancel={() => setIsModalVisible(false)}
                onOk={() => form.submit()}
                width={700}
                centered
                okButtonProps={{ className: "rounded-lg h-10 px-6" }}
                cancelButtonProps={{ className: "rounded-lg h-10 px-6" }}
            >
                <Form form={form} layout="vertical" onFinish={handleAddOrUpdateEdit} className="mt-4">
                    {/* Hidden Id field */}
                    <Form.Item name="id" hidden>
                        <Input />
                    </Form.Item>

                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item name="name" label="智能体名称" rules={[{ required: true }]}>
                                <Input placeholder="例如: 客服助手" className="rounded-lg h-10" />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="delimiter" label="分隔符">
                                <Input placeholder="例如: |||" className="rounded-lg h-10" />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item name="userId" label="用户" rules={[{ required: true }]}>
                                <EntitySelector
                                    placeholder="选择用户"
                                    fetchOptions={fetchUsers}
                                    fetchById={async (id) => {
                                        const res = await getUserById(id);
                                        return res.data || null;
                                    }}
                                    renderLabel={(user) => user.nickname}
                                    renderExtra={(user) => (
                                        <span className="text-xs text-gray-400">@{user.username}</span>
                                    )}
                                />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="chatModelId" label="模型" rules={[{ required: true }]}>
                                <EntitySelector
                                    placeholder="选择模型"
                                    fetchOptions={fetchChatModels}
                                    fetchById={async (id) => {
                                        const res = await getChatModelById(id);
                                        return res.data || null;
                                    }}
                                    renderLabel={(model) => model.name}
                                    renderExtra={(model) => (
                                        <span className="text-xs text-gray-400">{model.qualifiedName}</span>
                                    )}
                                />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Form.Item name="description" label="描述信息">
                        <TextArea rows={2} placeholder="输入智能体描述..." className="rounded-lg" />
                    </Form.Item>

                    <Form.Item name="prompt" label="提示词" rules={[{ required: true }]}>
                        <TextArea rows={4} placeholder="输入智能体提示词..." className="rounded-lg" />
                    </Form.Item>
                </Form>
            </Modal>

            <style>{`
        .custom-table .ant-table-thead > tr > th { 
          background: #f8fafc; 
          border-bottom: none; 
          font-size: 13px; 
          font-weight: 600;
        }
        .custom-table .ant-table-tbody > tr > td { 
          border-bottom: 1px solid #f1f5f9; 
          padding: 12px 16px; 
        }
        .custom-table .ant-table-tbody > tr:hover > td { 
          background: #fbfcfe !important; 
        }
        .custom-modal .ant-modal-content { 
          border-radius: 1rem; 
          padding: 24px; 
        }
        .ant-input-number-input-wrap input { 
          height: 38px !important; 
        }
        .ant-table-cell-fix-left-last::after, 
        .ant-table-cell-fix-right-first::after {
          box-shadow: inset 10px 0 8px -8px rgba(0, 0, 0, 0.05) !important;
        }
      `}</style>
        </>
    );
}
