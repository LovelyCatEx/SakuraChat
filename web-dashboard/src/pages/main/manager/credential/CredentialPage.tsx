import {useEffect, useState} from 'react';
import {
    Button,
    Card,
    Col,
    Form,
    Input,
    message,
    Modal,
    Popconfirm,
    Row,
    Select,
    Space,
    Switch,
    Table,
    Tag, Tooltip
} from 'antd';
import {DeleteOutlined, EditOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons';
import type {Credential} from '../../../../types/credential.types.ts';
import {createCredential, deleteCredential, getCredentialList, searchCredentials, updateCredential} from '../../../../api/credential.api.ts';
import {formatTimestamp} from '../../../../utils/datetime.utils.ts';
import type {ColumnGroupType, ColumnType} from "antd/es/table";

const { TextArea } = Input;

function CredentialActivationSwitch({record, onSwitchChanged}: {
    record: Credential,
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
                updateCredential({ id: record.id, active: checked })
                    .then(() => {
                        void message.success(`凭证${checked ? '启用' : '禁用'}成功`)
                        onSwitchChanged(true, checked);
                    })
                    .catch(() => {
                        void message.error("更新凭证信息失败");
                        onSwitchChanged(false, checked);
                    })
                    .finally(() => {
                        setRequesting(false);
                    })
            }}
        />
    )
}

export function CredentialPage() {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingItem, setEditingItem] = useState<Credential | null>(null);
    const [form] = Form.useForm();
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);
    const [searchKeyword, setSearchKeyword] = useState('');

    const [credentials, setCredentials] = useState<Credential[]>([]);

    const refreshData = () => {
        setRefreshing(true);

        const apiCall = searchKeyword 
            ? searchCredentials(searchKeyword, currentPage, currentPageSize)
            : getCredentialList({page: currentPage, pageSize: currentPageSize});

        apiCall.then((res) => {
            if (res.data) {
                setCredentials(res.data.records);
                setTotal(res.data.total);
            }
        }).finally(() => {
            setRefreshing(false);
        })
    }

    const handleSearch = (keyword: string) => {
        setSearchKeyword(keyword);
        setCurrentPage(1);
    };

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refreshData();
    }, [currentPage, currentPageSize, searchKeyword]);

    const handleAddOrUpdateEdit = (values: Credential) => {
        if (editingItem) {
            updateCredential({
                id: values.id,
                type: values.type,
                data: values.data,
                active: values.active
            }).then(() => {
                refreshData();
                void message.success('更新凭证成功');
            }).catch(() => {
                void message.error('更新凭证失败');
            })
        } else {
            createCredential({
                type: values.type,
                data: values.data
            }).then(() => {
                refreshData();
                void message.success('新增凭证成功');
            }).catch(() => {
                void message.error('新增凭证失败');
            })
        }

        setIsModalVisible(false);
        setEditingItem(null);
        form.resetFields();
    };

    const deleteCredentialItem = (id: string) => {
        deleteCredential(id)
            .then(() => {
                refreshData();
                void message.success('凭证已刪除');
            })
            .catch(() => {
                void message.error('删除凭证失败');
            })
    };

    const openModal = (item: Credential | null = null) => {
        setEditingItem(item);

        if (item) {
            form.setFieldsValue(item);
        } else {
            form.resetFields();
        }

        setIsModalVisible(true);
    };

    const columns: (ColumnGroupType<Credential> | ColumnType<Credential>)[] = [
        {
            title: '凭证信息',
            dataIndex: 'id',
            key: 'id',
            fixed: 'start',
            width: 180,
            render: (id: string, record: Credential) => {
                const credentialTypeMap: Record<number, string> = {
                    0: 'Authorization Bearer',
                    1: 'Authorization Basic'
                };
                return (
                    <Space orientation='vertical' size={0}>
                        <span className="font-bold text-gray-800">{credentialTypeMap[record.type] ?? record.type}</span>
                        <Tooltip title={record.id}>
                            <Tag color="blue" className="m-0 text-[10px] leading-4 h-4 px-1 rounded">ID: {id}</Tag>
                        </Tooltip>
                    </Space>
                );
            },
        },
        {
            title: '凭证数据',
            dataIndex: 'data',
            key: 'data',
            width: 250,
            render: (data: string) => {
                return <Tooltip title={data}>
                    <span className=""
                          style={{
                              maxWidth: '100%',
                              display: 'inline-block',
                              overflow: 'hidden',
                              whiteSpace: 'nowrap',
                              textOverflow: 'ellipsis'
                          }}>{data}</span>
                </Tooltip>
            },
        },
        {
            title: '状态',
            dataIndex: 'active',
            key: 'active',
            width: 80,
            render: (_: unknown, record: Credential) => {
                return <CredentialActivationSwitch record={record} onSwitchChanged={() => refreshData()} />;
            },
        },
        {
            title: '创建时间',
            dataIndex: 'createdTime',
            key: 'createdTime',
            width: 150,
            render: (createdTime: number) => <span>{formatTimestamp(createdTime)}</span>
        },
        {
            title: '修改时间',
            dataIndex: 'modifiedTime',
            key: 'modifiedTime',
            width: 150,
            render: (modifiedTime: number) => <span>{formatTimestamp(modifiedTime)}</span>
        },
        {
            title: '操作',
            key: 'action',
            fixed: 'end',
            width: 100,
            render: (_: unknown, record: Credential) => (
                <Space>
                    <Button type="text" size="small" icon={<EditOutlined />} onClick={() => openModal(record)} />
                    <Popconfirm title="确定要删除此凭证？" onConfirm={() => deleteCredentialItem(record.id)} okText="确认" cancelText="取消">
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
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">凭证管理</h1>
                    <p className="text-gray-500 mt-1">管理系统凭证信息</p>
                </div>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    size="large"
                    className="rounded-xl h-12 shadow-lg shadow-blue-100"
                    onClick={() => openModal()}
                >
                    新增凭证
                </Button>
            </div>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6 flex gap-4">
                    <Input
                        placeholder="搜索凭证..."
                        prefix={<SearchOutlined className="text-gray-400" />}
                        className="max-w-xs rounded-xl h-10"
                        onPressEnter={(e) => handleSearch((e.target as HTMLInputElement).value)}
                        allowClear
                        onChange={(e) => {
                            const value = e.target.value;
                            if (value === '') {
                                handleSearch('');
                            }
                        }}
                    />
                </div>
                <Table
                    columns={columns}
                    dataSource={credentials}
                    rowKey="id"
                    scroll={{ x: 600 }}
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
                title={editingItem ? "编辑凭证" : "新建凭证"}
                open={isModalVisible}
                onCancel={() => setIsModalVisible(false)}
                onOk={() => form.submit()}
                width={600}
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
                        <Col span={24}>
                            <Form.Item name="type" label="凭证类型" rules={[{ required: true }]}>
                                <Select className="w-full rounded-lg h-10 flex items-center" placeholder="选择凭证类型">
                                    <Select.Option value={0}>Authorization Bearer</Select.Option>
                                    <Select.Option value={1}>Authorization Basic</Select.Option>
                                </Select>
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={24}>
                            <Form.Item name="data" label="凭证数据" rules={[{ required: true }]}>
                                <TextArea rows={4} placeholder="输入凭证数据..." className="rounded-lg" />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={24}>
                            <Form.Item name="active" label="状态" initialValue={true}>
                                <Switch />
                            </Form.Item>
                        </Col>
                    </Row>
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
