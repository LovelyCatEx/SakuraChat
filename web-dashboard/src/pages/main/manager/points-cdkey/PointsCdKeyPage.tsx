import {useEffect, useState} from 'react';
import {Button, Card, Col, Form, Input, InputNumber, message, Modal, Popconfirm, Row, Space, Table, Tag} from 'antd';
import {DeleteOutlined, EditOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons';
import type {PointsCdKey} from "../../../../types/points-cdkey.types.ts";
import {
    createPointsCdKey,
    deletePointsCdKey,
    getPointsCdKeyList,
    searchPointsCdKeys,
    updatePointsCdKey
} from "../../../../api/points-cdkey.api.ts";
import {formatTimestamp} from "../../../../utils/datetime.utils.ts";
import type {ColumnGroupType, ColumnType} from "antd/es/table";
import type {ApiResponse, PaginatedResponseData} from "../../../../api/sakurachat-request.ts";

export function PointsCdKeyPage() {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingItem, setEditingItem] = useState<PointsCdKey | null>(null);
    const [form] = Form.useForm();
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);
    const [searchKeyword, setSearchKeyword] = useState('');

    const [pointsCdKeys, setPointsCdKeys] = useState<PointsCdKey[]>([]);

    const refreshData = () => {
        setRefreshing(true);

        const apiCall = searchKeyword 
            ? searchPointsCdKeys(searchKeyword, currentPage, currentPageSize)
            : getPointsCdKeyList({page: currentPage, pageSize: currentPageSize});

        apiCall.then((res: ApiResponse<PaginatedResponseData<PointsCdKey>>) => {
            if (res.data) {
                setPointsCdKeys(res.data.records);
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

    const handleAddOrUpdateEdit = (values: PointsCdKey) => {
        if (editingItem) {
            updatePointsCdKey({
                id: values.id,
                cdKey: values.cdKey,
                points: values.points,
                generatedBy: values.generatedBy,
                usedBy: values.usedBy
            }).then(() => {
                refreshData();
                void message.success('更新兑换码成功');
            }).catch(() => {
                void message.error('更新兑换码失败');
            })
        } else {
            createPointsCdKey({
                cdKey: values.cdKey,
                points: values.points,
                generatedBy: values.generatedBy
            }).then(() => {
                refreshData();
                void message.success('新增兑换码成功');
            }).catch(() => {
                void message.error('新增兑换码失败');
            })
        }

        setIsModalVisible(false);
        setEditingItem(null);
        form.resetFields();
    };

    const deletePointsCdKeyItem = (id: string) => {
        deletePointsCdKey(id)
            .then(() => {
                refreshData();
                void message.success('兑换码已刪除');
            })
            .catch(() => {
                void message.error('删除兑换码失败');
            })
    };

    const openModal = (item: PointsCdKey | null = null) => {
        setEditingItem(item);

        if (item) {
            form.setFieldsValue(item);
        } else {
            form.resetFields();
        }

        setIsModalVisible(true);
    };

    const columns: (ColumnGroupType<PointsCdKey> | ColumnType<PointsCdKey>)[] = [
        {
            title: '兑换码信息',
            dataIndex: 'cdKey',
            key: 'cdKey',
            fixed: 'start',
            width: 300,
            render: (cdKey: string, record: PointsCdKey) => (
                <Space orientation='vertical' size={0}>
                    <span className="font-bold text-gray-800">{cdKey}</span>
                    <Tag color="blue" className="m-0 text-[10px] leading-4 h-4 px-1 rounded">ID: {record.id}</Tag>
                </Space>
            ),
        },
        {
            title: '积分数量',
            dataIndex: 'points',
            key: 'points',
            width: 120,
            render: (points: number) => <span className="text-gray-800 font-medium">{points}</span>
        },
        {
            title: '生成者ID',
            dataIndex: 'generatedBy',
            key: 'generatedBy',
            width: 120,
            render: (generatedBy: number) => <span className="text-gray-600">{generatedBy}</span>
        },
        {
            title: '使用者ID',
            dataIndex: 'usedBy',
            key: 'usedBy',
            width: 120,
            render: (usedBy: number | null) => <span className="text-gray-600">{usedBy || '-'}</span>
        },
        {
            title: '状态',
            dataIndex: 'usedBy',
            key: 'status',
            width: 100,
            render: (usedBy: number | null) => (
                <Tag color={usedBy ? "green" : "blue"} className="m-0">
                    {usedBy ? "已使用" : "未使用"}
                </Tag>
            )
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
            render: (_: unknown, record: PointsCdKey) => (
                <Space>
                    <Button type="text" size="small" icon={<EditOutlined />} onClick={() => openModal(record)} />
                    <Popconfirm title="确定要删除此兑换码？" onConfirm={() => deletePointsCdKeyItem(record.id)} okText="确认" cancelText="取消">
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
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">积分兑换码管理</h1>
                    <p className="text-gray-500 mt-1">管理积分兑换码的创建和使用</p>
                </div>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    size="large"
                    className="rounded-xl h-12 shadow-lg"
                    onClick={() => openModal()}
                >
                    新增兑换码
                </Button>
            </div>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6 flex gap-4">
                    <Input
                        placeholder="搜索兑换码..."
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
                    dataSource={pointsCdKeys}
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
                        }
                    }}
                    loading={refreshing}
                />
            </Card>

            <Modal
                title={editingItem ? "编辑兑换码" : "新建兑换码"}
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
                        <Col span={24}>
                            <Form.Item name="cdKey" label="兑换码" rules={[{ required: true }]}>
                                <Input placeholder="输入兑换码" className="rounded-lg h-10" />
                            </Form.Item>
                        </Col>
                    </Row>

                    {editingItem ? (
                        <>
                            <Row gutter={16}>
                                <Col span={12}>
                                    <Form.Item name="points" label="积分数量" rules={[{ required: true }]}>
                                        <InputNumber placeholder="输入积分数量" className="w-full rounded-lg h-10" min={1} />
                                    </Form.Item>
                                </Col>
                                <Col span={12}>
                                    <Form.Item name="generatedBy" label="生成者ID" rules={[{ required: true }]}>
                                        <InputNumber placeholder="输入生成者ID" className="w-full rounded-lg h-10" min={1} />
                                    </Form.Item>
                                </Col>
                            </Row>

                            <Row gutter={16}>
                                <Col span={24}>
                                    <Form.Item name="usedBy" label="使用者ID">
                                        <InputNumber placeholder="输入使用者ID（可选）" className="w-full rounded-lg h-10" min={1} />
                                    </Form.Item>
                                </Col>
                            </Row>
                        </>
                    ): (
                        <>
                            <Row gutter={16}>
                                <Col span={24}>
                                    <Form.Item name="points" label="积分数量" rules={[{ required: true }]}>
                                        <InputNumber placeholder="输入积分数量" className="w-full rounded-lg h-10" min={1} />
                                    </Form.Item>
                                </Col>
                            </Row>
                        </>
                    )}
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
