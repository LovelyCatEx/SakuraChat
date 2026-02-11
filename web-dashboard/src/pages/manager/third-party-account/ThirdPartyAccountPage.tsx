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
    Table,
    Tag
} from 'antd';
import {DeleteOutlined, EditOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons';
import type {ThirdPartyAccount} from "../../../types/third-party-account.types.ts";
import {createThirdPartyAccount, deleteThirdPartyAccount, getThirdPartyAccountList, updateThirdPartyAccount} from "../../../api/third-party-account.api.ts";
import {formatTimestamp} from "../../../utils/datetime.utils.ts";
import type {ColumnGroupType, ColumnType} from "antd/es/table";

export function ThirdPartyAccountPage() {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingItem, setEditingItem] = useState<ThirdPartyAccount | null>(null);
    const [form] = Form.useForm();
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);

    const [accounts, setAccounts] = useState<ThirdPartyAccount[]>([]);

    const refreshData = () => {
        setRefreshing(true);

        getThirdPartyAccountList({
            page: currentPage,
            pageSize: currentPageSize,
        }).then((res) => {
            if (res.data) {
                setAccounts(res.data.records);
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

    const handleAddOrUpdateEdit = (values: ThirdPartyAccount) => {
        if (editingItem) {
            updateThirdPartyAccount({
                id: values.id,
                accountId: values.accountId,
                nickname: values.nickname,
                platform: values.platform
            }).then(() => {
                refreshData();
                void message.success('更新第三方账号成功');
            }).catch(() => {
                void message.error('更新第三方账号失败');
            })
        } else {
            createThirdPartyAccount({
                accountId: values.accountId,
                nickname: values.nickname,
                platform: values.platform,
            }).then(() => {
                refreshData();
                void message.success('新增第三方账号成功');
            }).catch(() => {
                void message.error('新增第三方账号失败');
            })
        }

        setIsModalVisible(false);
        setEditingItem(null);
        form.resetFields();
    };

    const deleteAccount = (id: string) => {
        deleteThirdPartyAccount(id)
            .then(() => {
                refreshData();
                void message.success('第三方账号已刪除');
            })
            .catch(() => {
                void message.error('删除第三方账号失败');
            })
    };

    const openModal = (item: ThirdPartyAccount | null = null) => {
        setEditingItem(item);

        if (item) {
            form.setFieldsValue(item);
        } else {
            form.resetFields();
        }

        setIsModalVisible(true);
    };

    const columns: (ColumnGroupType<ThirdPartyAccount> | ColumnType<ThirdPartyAccount>)[] = [
        {
            title: '账号名称',
            dataIndex: 'nickname',
            key: 'nickname',
            fixed: 'start',
            width: 150,
            render: (nickname: string, record: ThirdPartyAccount) => (
                <Space orientation='vertical' size={0}>
                    <span className="font-bold text-gray-800">{nickname}</span>
                    <Tag color="blue" className="m-0 text-[10px] leading-4 h-4 px-1 rounded">ID: {record.id}</Tag>
                </Space>
            ),
        },
        {
            title: '账号 ID',
            dataIndex: 'accountId',
            key: 'accountId',
            ellipsis: true,
            width: 200,
            render: (accountId: string) => <span className="text-gray-600 font-mono">{accountId}</span>
        },
        {
            title: '平台',
            dataIndex: 'platform',
            key: 'platform',
            width: 80,
            render: (platform: number) => {
                const platformMap: Record<number, string> = {
                    1: 'NapCat OICQ',
                    2: 'Lark'
                };
                return <Tag color="green" className="m-0 text-[10px] leading-4 h-4 px-1 rounded">{platformMap[platform] ?? platform}</Tag>
            }
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
            render: (_: unknown, record: ThirdPartyAccount) => (
                <Space>
                    <Button type="text" size="small" icon={<EditOutlined />} onClick={() => openModal(record)} />
                    <Popconfirm title="确定要删除此第三方账号？" onConfirm={() => deleteAccount(record.id)} okText="确认" cancelText="取消">
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
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">第三方账号管理</h1>
                    <p className="text-gray-500 mt-1">管理系统第三方账号</p>
                </div>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    size="large"
                    className="rounded-xl h-12 shadow-lg shadow-blue-100"
                    onClick={() => openModal()}
                >
                    新增账号
                </Button>
            </div>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6 flex gap-4">
                    <Input
                        placeholder="搜索第三方账号..."
                        prefix={<SearchOutlined className="text-gray-400" />}
                        className="max-w-xs rounded-xl h-10"
                    />
                </div>
                <Table
                    columns={columns}
                    dataSource={accounts}
                    rowKey="id"
                    scroll={{ x: 800 }}
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
                title={editingItem ? "编辑第三方账号" : "新建第三方账号"}
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
                        <Col span={12}>
                            <Form.Item name="nickname" label="昵称" rules={[{ required: true }]}>
                                <Input placeholder="例如: 用户昵称" className="rounded-lg h-10" />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="platform" label="平台" rules={[{ required: true }]}>
                                <Select className="w-full rounded-lg h-10 flex items-center" placeholder="选择平台">
                                    <Select.Option value={1}>NapCat OICQ</Select.Option>
                                    <Select.Option value={2}>Lark</Select.Option>
                                </Select>
                            </Form.Item>
                        </Col>
                    </Row>

                    <Form.Item name="accountId" label="账号 ID" rules={[{ required: true }]}>
                        <Input placeholder="例如: 12345678" className="rounded-lg h-10" />
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
