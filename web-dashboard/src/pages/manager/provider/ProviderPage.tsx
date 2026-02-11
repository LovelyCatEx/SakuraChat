import {useEffect, useState} from 'react';
import {Button, Card, Col, Form, Input, InputNumber, message, Modal, Popconfirm, Row, Select, Space, Table, Tag} from 'antd';
import {DeleteOutlined, EditOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons';
import type {Provider} from "../../../types/provider.types.ts";
import {createProvider, deleteProvider, getProviderList, updateProvider} from "../../../api/provider.api.ts";
import {formatTimestamp} from "../../../utils/datetime.utils.ts";
import type {ColumnGroupType, ColumnType} from "antd/es/table";

const { TextArea } = Input;

export function ProviderPage() {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingItem, setEditingItem] = useState<Provider | null>(null);
    const [form] = Form.useForm();
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);

    const [providers, setProviders] = useState<Provider[]>([]);

    const refreshData = () => {
        setRefreshing(true);

        getProviderList({
            page: 1,
            pageSize: 20,
        }).then((res) => {
            if (res.data) {
                setProviders(res.data.records);
                setTotal(res.data.total);
            }
        }).finally(() => {
            setRefreshing(false);
        })
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refreshData();
    }, []);

    const handleAddOrUpdateEdit = (values: Provider) => {
        if (editingItem) {
            updateProvider({
                id: values.id,
                name: values.name,
                description: values.description,
                chatCompletionsUrl: values.chatCompletionsUrl,
                apiType: values.apiType
            }).then(() => {
                refreshData();
                void message.success('更新供应商成功');
            }).catch(() => {
                void message.error('更新供应商失败');
            })
        } else {
            createProvider({
                name: values.name,
                description: values.description,
                chatCompletionsUrl: values.chatCompletionsUrl,
                apiType: values.apiType
            }).then(() => {
                refreshData();
                void message.success('新增供应商成功');
            }).catch(() => {
                void message.error('新增供应商失败');
            })
        }

        setIsModalVisible(false);
        setEditingItem(null);
        form.resetFields();
    };

    const deleteProviderItem = (id: string) => {
        deleteProvider(id)
            .then(() => {
                refreshData();
                void message.success('供应商已刪除');
            })
            .catch(() => {
                void message.error('删除供应商失败');
            })
    };

    const openModal = (item: Provider | null = null) => {
        setEditingItem(item);

        if (item) {
            form.setFieldsValue(item);
        } else {
            form.resetFields();
        }

        setIsModalVisible(true);
    };

    const columns: (ColumnGroupType<Provider> | ColumnType<Provider>)[] = [
        {
            title: '供应商信息',
            dataIndex: 'name',
            key: 'name',
            fixed: 'start',
            width: 200,
            render: (name: string, record: Provider) => (
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
            width: 200,
            render: (description: string | null) => <span className="text-gray-500">{description || '-'}</span>
        },
        {
            title: 'API 类型',
            dataIndex: 'apiType',
            key: 'apiType',
            width: 100,
            render: (apiType: number) => {
                const apiTypeMap: Record<number, string> = {
                    0: 'OpenAI',
                    1: 'Claude',
                    2: 'Gemini'
                };
                return <span className="text-gray-600">{apiTypeMap[apiType] ?? apiType}</span>
            }
        },
        {
            title: 'API URL',
            dataIndex: 'chatCompletionsUrl',
            key: 'chatCompletionsUrl',
            width: 300,
            render: (url: string) => <span className="text-gray-600 font-mono text-sm">{url}</span>
        },
        {
            title: '创建时间',
            dataIndex: 'createdTime',
            key: 'createdTime',
            width: 100,
            render: (createdTime: number) => <span>{formatTimestamp(createdTime)}</span>
        },
        {
            title: '修改时间',
            dataIndex: 'modifiedTime',
            key: 'modifiedTime',
            width: 100,
            render: (modifiedTime: number) => <span>{formatTimestamp(modifiedTime)}</span>
        },
        {
            title: '操作',
            key: 'action',
            fixed: 'end',
            width: 100,
            render: (_: unknown, record: Provider) => (
                <Space>
                    <Button type="text" size="small" icon={<EditOutlined />} onClick={() => openModal(record)} />
                    <Popconfirm title="确定要删除此供应商？" onConfirm={() => deleteProviderItem(record.id)} okText="确认" cancelText="取消">
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
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">模型供应商管理</h1>
                    <p className="text-gray-500 mt-1">配置模型服务提供商</p>
                </div>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    size="large"
                    className="rounded-xl h-12 shadow-lg shadow-blue-100"
                    onClick={() => openModal()}
                >
                    新增供应商
                </Button>
            </div>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6 flex gap-4">
                    <Input
                        placeholder="搜索供应商..."
                        prefix={<SearchOutlined className="text-gray-400" />}
                        className="max-w-xs rounded-xl h-10"
                    />
                </div>
                <Table
                    columns={columns}
                    dataSource={providers}
                    rowKey="id"
                    scroll={{ x: 1000 }}
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
                title={editingItem ? "编辑供应商配置" : "新建供应商配置"}
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
                            <Form.Item name="name" label="供应商名称" rules={[{ required: true }]}>
                                <Input placeholder="例如: OpenAI" className="rounded-lg h-10" />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="apiType" label="API 类型" rules={[{ required: true }]}>
                                <Select className="w-full rounded-lg h-10 flex items-center" placeholder="选择API类型">
                                    <Select.Option value={0}>OpenAI</Select.Option>
                                    <Select.Option value={1}>Claude</Select.Option>
                                    <Select.Option value={2}>Gemini</Select.Option>
                                </Select>
                            </Form.Item>
                        </Col>
                    </Row>

                    <Form.Item name="chatCompletionsUrl" label="Chat Completions URL" rules={[{ required: true }]}>
                        <Input placeholder="例如: https://api.openai.com/v1/chat/completions" className="rounded-lg h-10" />
                    </Form.Item>

                    <Form.Item name="description" label="描述信息">
                        <TextArea rows={2} placeholder="输入供应商描述..." className="rounded-lg" />
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
