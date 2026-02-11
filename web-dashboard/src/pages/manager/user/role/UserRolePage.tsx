import {useEffect, useState} from 'react';
import {Button, Card, Col, Form, Input, message, Modal, Popconfirm, Row, Space, Table, Tag} from 'antd';
import {DeleteOutlined, EditOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons';
import type {UserRole} from '../../../../types/user-role.types.ts';
import {createUserRole, deleteUserRole, getUserRoleList, updateUserRole} from '../../../../api/user-role.api.ts';
import {formatTimestamp} from '../../../../utils/datetime.utils.ts';
import type {ColumnGroupType, ColumnType} from "antd/es/table";

const { TextArea } = Input;

export function UserRolePage() {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingItem, setEditingItem] = useState<UserRole | null>(null);
    const [form] = Form.useForm();
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);

    const [userRoles, setUserRoles] = useState<UserRole[]>([]);

    const refreshData = () => {
        setRefreshing(true);

        getUserRoleList({
            page: 1,
            pageSize: 20,
        }).then((res) => {
            if (res.data) {
                setUserRoles(res.data.records);
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

    const handleAddOrUpdateEdit = (values: UserRole) => {
        if (editingItem) {
            updateUserRole({
                id: values.id,
                name: values.name,
                description: values.description
            }).then(() => {
                refreshData();
                void message.success('更新角色成功');
            }).catch(() => {
                void message.error('更新角色失败');
            })
        } else {
            createUserRole({
                name: values.name,
                description: values.description
            }).then(() => {
                refreshData();
                void message.success('新增角色成功');
            }).catch(() => {
                void message.error('新增角色失败');
            })
        }

        setIsModalVisible(false);
        setEditingItem(null);
        form.resetFields();
    };

    const deleteUserRoleItem = (id: string) => {
        deleteUserRole(id)
            .then(() => {
                refreshData();
                void message.success('角色已刪除');
            })
            .catch(() => {
                void message.error('删除角色失败');
            })
    };

    const openModal = (item: UserRole | null = null) => {
        setEditingItem(item);

        if (item) {
            form.setFieldsValue(item);
        } else {
            form.resetFields();
        }

        setIsModalVisible(true);
    };

    const columns: (ColumnGroupType<UserRole> | ColumnType<UserRole>)[] = [
        {
            title: '角色信息',
            dataIndex: 'name',
            key: 'name',
            fixed: 'start',
            width: 200,
            render: (name: string, record: UserRole) => (
                <Space orientation='vertical' size={0}>
                    <span className="font-bold text-gray-800">{name}</span>
                    <span className="text-xs text-gray-400">{record.description || '无描述'}</span>
                    <Tag color="blue" className="m-0 text-[10px] leading-4 h-4 px-1 rounded">ID: {record.id}</Tag>
                </Space>
            ),
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
            render: (_: unknown, record: UserRole) => (
                <Space>
                    <Button type="text" size="small" icon={<EditOutlined />} onClick={() => openModal(record)} />
                    <Popconfirm title="确定要删除此角色？" onConfirm={() => deleteUserRoleItem(record.id)} okText="确认" cancelText="取消">
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
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">用户角色管理</h1>
                    <p className="text-gray-500 mt-1">管理系统用户角色</p>
                </div>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    size="large"
                    className="rounded-xl h-12 shadow-lg shadow-blue-100"
                    onClick={() => openModal()}
                >
                    新增角色
                </Button>
            </div>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6 flex gap-4">
                    <Input
                        placeholder="搜索角色..."
                        prefix={<SearchOutlined className="text-gray-400" />}
                        className="max-w-xs rounded-xl h-10"
                    />
                </div>
                <Table
                    columns={columns}
                    dataSource={userRoles}
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
                            refreshData();
                        }
                    }}
                    loading={refreshing}
                />
            </Card>

            <Modal
                title={editingItem ? "编辑角色" : "新建角色"}
                open={isModalVisible}
                onCancel={() => setIsModalVisible(false)}
                onOk={() => form.submit()}
                width={500}
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
                            <Form.Item name="name" label="角色名称" rules={[{ required: true }]}>
                                <Input placeholder="输入角色名称" className="rounded-lg h-10" />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={24}>
                            <Form.Item name="description" label="角色描述">
                                <TextArea rows={3} placeholder="输入角色描述..." className="rounded-lg" />
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
