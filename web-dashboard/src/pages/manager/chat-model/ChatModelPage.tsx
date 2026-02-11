import {useEffect, useState} from 'react';
import {
    Button,
    Card,
    Col,
    Divider,
    Form,
    Input,
    InputNumber,
    message,
    Modal,
    Popconfirm,
    Row,
    Space,
    Switch,
    Table,
    Tag, Tooltip
} from 'antd';
import {DeleteOutlined, EditOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons';
import type {ChatModel} from "../../../types/chat-model.types.ts";
import {createChatModel, deleteChatModel, getChatModelList, updateChatModel} from "../../../api/chat-model.api.ts";
import {formatTimestamp} from "../../../utils/datetime.utils.ts";
import type {ColumnGroupType, ColumnType} from "antd/es/table";
import {EntitySelector} from "../../../components/common/EntitySelector.tsx";
import {getProviderById, searchProviders} from "../../../api/provider.api.ts";
import {getCredentialById, searchCredentials} from "../../../api/credential.api.ts";
import {getUrlHostname} from "../../../utils/url.utils.ts";
import type {Provider} from "../../../types/provider.types.ts";
import type {Credential} from "../../../types/credential.types.ts";
import type {ApiResponse} from "../../../api/sakurachat-request.ts";

const { TextArea } = Input;

function ModelActivationSwitch({record, onSwitchChanged}: {
    record: ChatModel,
    onSwitchChanged: (success: boolean, to: boolean) => void
}) {
    const [requesting, setRequesting] = useState(false);

    return (
        <Switch
            size="small"
            checked={record.active}
            loading={requesting}
            onChange={(checked) => {
                setRequesting(true);
                updateChatModel({ id: record.id, active: checked })
                    .then(() => {
                        void message.success(`模型${checked ? '启用' : '禁用'}成功`)
                        onSwitchChanged(true, checked);
                    })
                    .catch(() => {
                        void message.error("更新模型信息失败");
                        onSwitchChanged(false, checked);
                    })
                    .finally(() => {
                        setRequesting(false);
                    })
            }}
        />
    )
}

export function ChatModelPage() {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingItem, setEditingItem] = useState<ChatModel | null>(null);
    const [form] = Form.useForm();
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);

    const [models, setModels] = useState<ChatModel[]>([]);
    const [providers, setProviders] = useState<Record<string, {name: string, chatCompletionsUrl: string}>>({});
    const [credentials, setCredentials] = useState<Record<string, {data: string, type: number}>>({});

    const fetchProviders = async (keyword: string, page: number = 1, pageSize: number = 5) => {
        const res = await searchProviders(keyword, page, pageSize);
        if (res.data) {
            const providerMap: Record<string, {name: string, chatCompletionsUrl: string}> = {};
            res.data.records.forEach(p => {
                providerMap[p.id] = {name: p.name, chatCompletionsUrl: p.chatCompletionsUrl};
            });
            setProviders(providerMap);
            return res.data;
        }
        return {page: 1, pageSize: 5, total: 0, totalPages: 0, records: []};
    };

    const fetchCredentials = async (keyword: string, page: number = 1, pageSize: number = 5) => {
        const res = await searchCredentials(keyword, page, pageSize);
        if (res.data) {
            const credentialMap: Record<string, {data: string, type: number}> = {};
            res.data.records.forEach(c => {
                credentialMap[c.id] = {data: c.data, type: c.type};
            });
            setCredentials(credentialMap);
            return res.data;
        }
        return {page: 1, pageSize: 5, total: 0, totalPages: 0, records: []};
    };

    const refreshData = () => {
        if (refreshing) {
            return;
        }

        setRefreshing(true);

        Promise.all([
            getChatModelList({page: currentPage, pageSize: currentPageSize})
        ]).then(([modelRes]) => {
            if (modelRes.data) {
                setModels(modelRes.data.records);
                setTotal(modelRes.data.total);

                const providerIds = new Set<string>();
                const credentialIds = new Set<string>();
                modelRes.data.records.forEach(m => {
                    providerIds.add(m.providerId);
                    credentialIds.add(m.credentialId);
                });

                const providerPromises = Array.from(providerIds).map(id => getProviderById(id));
                const credentialPromises = Array.from(credentialIds).map(id => getCredentialById(id));

                if (providerPromises.length === 0 && credentialPromises.length === 0) {
                    setProviders({});
                    setCredentials({});
                    return;
                }

                Promise.all([
                    providerPromises.length > 0 ? Promise.all(providerPromises) : Promise.resolve([]),
                    credentialPromises.length > 0 ? Promise.all(credentialPromises) : Promise.resolve([])
                ]).then(([providerResults, credentialResults]) => {
                    const providerMap: Record<string, {name: string, chatCompletionsUrl: string}> = {};
                    providerResults.forEach((res: ApiResponse<Provider>) => {
                        if (res.data) {
                            providerMap[res.data.id] = {name: res.data.name, chatCompletionsUrl: res.data.chatCompletionsUrl};
                        }
                    });
                    setProviders(providerMap);

                    const credentialMap: Record<string, {data: string, type: number}> = {};
                    credentialResults.forEach((res: ApiResponse<Credential>) => {
                        if (res.data) {
                            credentialMap[res.data.id] = {data: res.data.data, type: res.data.type};
                        }
                    });
                    setCredentials(credentialMap);
                });
            }
        }).finally(() => {
            setRefreshing(false);
        })
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refreshData();
    }, [currentPage, currentPageSize]);

    const handleAddOrUpdateEdit = (values: ChatModel) => {
        if (editingItem) {
            updateChatModel({
                id: values.id,
                name: values.name,
                description: values.description,
                qualifiedName: values.qualifiedName,
                providerId: values.providerId,
                credentialId: values.credentialId,
                maxContextTokens: values.maxContextTokens,
                maxTokens: values.maxTokens,
                temperature: values.temperature,
                inputTokenPointRate: values.inputTokenPointRate,
                outputTokenPointRate: values.outputTokenPointRate,
                cachedInputTokenPointRate: values.cachedInputTokenPointRate,
                active: values.active
            }).then(() => {
                refreshData();
                void message.success('更新模型成功');
            }).catch(() => {
                void message.error('更新模型失败');
            })
        } else {
            createChatModel({
                name: values.name,
                description: values.description,
                qualifiedName: values.qualifiedName,
                providerId: values.providerId,
                credentialId: values.credentialId,
                maxContextTokens: values.maxContextTokens,
                maxTokens: values.maxTokens,
                temperature: values.temperature,
                inputTokenPointRate: values.inputTokenPointRate,
                outputTokenPointRate: values.outputTokenPointRate,
                cachedInputTokenPointRate: values.cachedInputTokenPointRate,
            }).then(() => {
                refreshData();
                void message.success('新增模型成功');
            }).catch(() => {
                void message.error('新增模型失败');
            })
        }

        setIsModalVisible(false);
        setEditingItem(null);
        form.resetFields();
    };

    const deleteModel = (id: string) => {
        deleteChatModel(id)
            .then(() => {
                refreshData();
                void message.success('模型已刪除');
            })
            .catch(() => {
                void message.error('删除模型失败');
            })
    };

    const openModal = (item: ChatModel | null = null) => {
        setEditingItem(item);

        if (item) {
            form.setFieldsValue(item);
        } else {
            form.resetFields();
        }

        setIsModalVisible(true);
    };

    const columns: (ColumnGroupType<ChatModel> | ColumnType<ChatModel>)[] = [
        {
            title: '模型信息',
            dataIndex: 'name',
            key: 'name',
            fixed: 'start',
            width: 200,
            render: (name: string, record: ChatModel) => (
                <Space orientation='vertical' size={0}>
                    <span className="font-bold text-gray-800">{name}</span>
                    <span className="text-xs text-gray-400 font-mono">{record.qualifiedName}</span>
                    <Tag color="blue" className="m-0 text-[10px] leading-4 h-4 px-1 rounded">ID: {record.id}</Tag>
                </Space>
            ),
        },
        {
            title: '提供商',
            dataIndex: 'providerId',
            key: 'providerId',
            width: 200,
            render: (id: string) => (
                <Space orientation="vertical" size={0}>
                    <span className="text-gray-600">{providers[id]?.name ?? `#${id}`}</span>
                    <Tooltip title={providers[id]?.chatCompletionsUrl}>
                        <span className="text-xs text-gray-400 truncate max-w-[100px]">{getUrlHostname(providers[id]?.chatCompletionsUrl ?? '')}</span>
                    </Tooltip>
                </Space>
            )
        },
        {
            title: '凭证',
            dataIndex: 'credentialId',
            key: 'credentialId',
            width: 280,
            render: (id: string) => (
                <Space orientation="vertical" size={0}>
                    <span className="text-gray-600">凭证 #{id}</span>
                    <Tooltip title={credentials[id]?.data}>
                        <span className="text-xs text-gray-400 truncate w-full">{credentials[id]?.data.substring(0, 20) ?? ''}...</span>
                    </Tooltip>
                </Space>
            )
        },
        {
            title: '最大上下文长度',
            dataIndex: 'maxContextTokens',
            key: 'maxContextTokens',
            width: 120,
            render: (val: number) => val === -1 ? '无限制' : `${(val/1000).toFixed(0)}k`
        },
        {
            title: '最大输出 Token',
            dataIndex: 'maxTokens',
            key: 'maxTokens',
            width: 100,
            render: (val: number) => val === -1 ? '无限制' : val
        },
        {
            title: '温度',
            dataIndex: 'temperature',
            key: 'temperature',
            width: 80,
            render: (val: number) => val === -1 ? '未设置' : (val/100).toFixed(1)
        },
        {
            title: '输入 Token 费率',
            dataIndex: 'inputTokenPointRate',
            key: 'inputTokenPointRate',
            width: 120,
            render: (val: number) => <span className="text-blue-600 font-mono">{val}</span>
        },
        {
            title: '输出 Token 费率',
            dataIndex: 'outputTokenPointRate',
            key: 'outputTokenPointRate',
            width: 120,
            render: (val: number) => <span className="text-orange-600 font-mono">{val}</span>
        },
        {
            title: '缓存输入 Token 费率',
            dataIndex: 'cachedInputTokenPointRate',
            key: 'cachedInputTokenPointRate',
            width: 120,
            render: (val: number) => <span className="text-gray-400 font-mono">{val}</span>
        },
        {
            title: '状态',
            dataIndex: 'active',
            key: 'active',
            width: 80,
            render: (_: unknown, record: ChatModel) => {
                return <ModelActivationSwitch record={record} onSwitchChanged={() => refreshData()} />;
            },
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
            width: 160,
            render: (_: unknown, record: ChatModel) => <span>{formatTimestamp(record.createdTime)}</span>
        },
        {
            title: '修改时间',
            dataIndex: 'modifiedTime',
            key: 'modifiedTime',
            width: 160,
            render: (_: unknown, record: ChatModel) => <span>{formatTimestamp(record.modifiedTime)}</span>
        },
        {
            title: '操作',
            key: 'action',
            fixed: 'end',
            width: 100,
            render: (_: unknown, record: ChatModel) => (
                <Space>
                    <Button type="text" size="small" icon={<EditOutlined />} onClick={() => openModal(record)} />
                    <Popconfirm title="确定要删除此模型？" onConfirm={() => deleteModel(record.id)} okText="确认" cancelText="取消">
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
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">语言模型管理</h1>
                    <p className="text-gray-500 mt-1">配置系统模型参数</p>
                </div>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    size="large"
                    className="rounded-xl h-12 shadow-lg shadow-blue-100"
                    onClick={() => openModal()}
                >
                    新增模型
                </Button>
            </div>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6 flex gap-4">
                    <Input
                        placeholder="搜索模型..."
                        prefix={<SearchOutlined className="text-gray-400" />}
                        className="max-w-xs rounded-xl h-10"
                    />
                </div>
                <Table
                    columns={columns}
                    dataSource={models}
                    rowKey="id"
                    scroll={{ x: 1400 }}
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
                        }
                    }}
                    loading={refreshing}
                />
            </Card>

            <Modal
                title={editingItem ? "编辑模型配置" : "新建模型配置"}
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
                            <Form.Item name="name" label="模型展示名称" rules={[{ required: true }]}>
                                <Input placeholder="例如: GPT-4o" className="rounded-lg h-10" />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="qualifiedName" label="模型限定名称 (Qualified Name)" rules={[{ required: true }]}>
                                <Input placeholder="例如: gpt-4o-v1" className="rounded-lg h-10" />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item name="providerId" label="提供商" rules={[{ required: true }]}>
                                <EntitySelector
                                    placeholder="选择提供商"
                                    fetchOptions={fetchProviders}
                                    fetchById={async (id) => {
                                        const res = await getProviderById(id);
                                        return res.data || null;
                                    }}
                                    renderLabel={(provider) => provider.name}
                                    renderExtra={(provider) => (
                                        <span className="text-xs text-gray-400">{provider.chatCompletionsUrl}</span>
                                    )}
                                />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="credentialId" label="凭证" rules={[{ required: true }]}>
                                <EntitySelector
                                    placeholder="选择凭证"
                                    fetchOptions={fetchCredentials}
                                    fetchById={async (id) => {
                                        const res = await getCredentialById(id);
                                        return res.data || null;
                                    }}
                                    renderLabel={(credential) => `凭证 #${credential.id}`}
                                    renderExtra={(credential) => (
                                        <span className="text-xs text-gray-400">{credential.data.substring(0, 30)}...</span>
                                    )}
                                />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Divider orientation="horizontal" className="text-xs text-gray-400 font-normal">模型信息</Divider>

                    <Row gutter={16}>
                        <Col span={8}>
                            <Form.Item name="maxContextTokens" label="最大上下文长度" initialValue={-1}>
                                <InputNumber className="w-full rounded-lg h-10 flex items-center" />
                            </Form.Item>
                        </Col>
                        <Col span={8}>
                            <Form.Item name="maxTokens" label="最大输出 Token" initialValue={-1}>
                                <InputNumber className="w-full rounded-lg h-10 flex items-center" />
                            </Form.Item>
                        </Col>
                        <Col span={8}>
                            <Form.Item name="temperature" label="温度 (-1 ~ 200)" initialValue={-1}>
                                <InputNumber min={-1} max={200} className="w-full rounded-lg h-10 flex items-center" />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Divider orientation='horizontal' className="text-xs text-gray-400 font-normal">计费倍率 (10000:1)</Divider>

                    <Row gutter={16}>
                        <Col span={8}>
                            <Form.Item name="inputTokenPointRate" label="输入 Token 费率" initialValue={10000}>
                                <InputNumber className="w-full rounded-lg h-10 flex items-center" />
                            </Form.Item>
                        </Col>
                        <Col span={8}>
                            <Form.Item name="outputTokenPointRate" label="输出 Token 费率" initialValue={10000}>
                                <InputNumber className="w-full rounded-lg h-10 flex items-center" />
                            </Form.Item>
                        </Col>
                        <Col span={8}>
                            <Form.Item name="cachedInputTokenPointRate" label="缓存输入 Token 费率" initialValue={10000}>
                                <InputNumber className="w-full rounded-lg h-10 flex items-center" />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Form.Item name="description" label="描述信息">
                        <TextArea rows={2} placeholder="输入模型描述..." className="rounded-lg" />
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