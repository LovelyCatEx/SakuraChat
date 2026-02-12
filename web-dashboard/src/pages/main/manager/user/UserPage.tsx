import {useEffect, useState} from 'react';
import {
    Button,
    Card,
    Col,
    Form,
    Input,
    InputNumber,
    message,
    Modal,
    Popconfirm,
    Row,
    Space,
    Table,
    Tag,
    Tooltip
} from 'antd';
import {DeleteOutlined, EditOutlined, PlusOutlined, SearchOutlined} from '@ant-design/icons';
import type {User} from '../../../../types/user.types.ts';
import {createUser, deleteUser, getUserList, searchUsers, updateUser} from '../../../../api/user.api.ts';
import {formatTimestamp} from '../../../../utils/datetime.utils.ts';
import type {ColumnGroupType, ColumnType} from "antd/es/table";

export function UserPage() {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingItem, setEditingItem] = useState<User | null>(null);
    const [form] = Form.useForm();
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);
    const [searchKeyword, setSearchKeyword] = useState('');

    const [users, setUsers] = useState<User[]>([]);

    const refreshData = () => {
        setRefreshing(true);

        const apiCall = searchKeyword 
            ? searchUsers(searchKeyword, currentPage, currentPageSize)
            : getUserList({page: currentPage, pageSize: currentPageSize});

        apiCall.then((res) => {
            if (res.data) {
                setUsers(res.data.records);
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

    const handleAddOrUpdateEdit = (values: User) => {
        if (editingItem) {
            updateUser({
                id: values.id,
                nickname: values.nickname,
                email: values.email,
                points: values.points
            }).then(() => {
                refreshData();
                void message.success('更新用户成功');
            }).catch(() => {
                void message.error('更新用户失败');
            })
        } else {
            createUser({
                username: values.username,
                password: values.password || '',
                nickname: values.nickname,
                email: values.email
            }).then(() => {
                refreshData();
                void message.success('新增用户成功');
            }).catch(() => {
                void message.error('新增用户失败');
            })
        }

        setIsModalVisible(false);
        setEditingItem(null);
        form.resetFields();
    };

    const deleteUserItem = (id: string) => {
        deleteUser(id)
            .then(() => {
                refreshData();
                void message.success('用户已刪除');
            })
            .catch(() => {
                void message.error('删除用户失败');
            })
    };

    const openModal = (item: User | null = null) => {
        setEditingItem(item);

        if (item) {
            form.setFieldsValue(item);
        } else {
            form.resetFields();
        }

        setIsModalVisible(true);
    };

    const columns: (ColumnGroupType<User> | ColumnType<User>)[] = [
        {
            title: '用户名',
            dataIndex: 'username',
            key: 'username',
            fixed: 'start',
            width: 120,
            render: (username: string, record: User) => (
                <Space orientation='vertical' size={0}>
                    <Tooltip title={username}>
                        <span className="font-bold text-gray-800">{username}</span>
                    </Tooltip>
                    <Tooltip title={record.id}>
                        <Tag color="blue" className="m-0 text-[10px] leading-4 h-4 px-1 rounded">ID: {record.id}</Tag>
                    </Tooltip>
                </Space>
            ),
        },
        {
            title: '昵称',
            dataIndex: 'nickname',
            key: 'nickname',
            width: 120,
            render: (nickname: string) => <Tooltip title={nickname}>
                <span>{nickname}</span>
            </Tooltip>
        },
        {
            title: '邮箱',
            dataIndex: 'email',
            key: 'email',
            ellipsis: true,
            width: 200,
            render: (email: string) => <Tooltip title={email}>
                <span>{email}</span>
            </Tooltip>
        },
        {
            title: '积分',
            dataIndex: 'points',
            key: 'points',
            width: 100,
            render: (points: number) => <span className="font-mono text-orange-600">{points}</span>
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
            render: (_: unknown, record: User) => (
                <Space>
                    <Button type="text" size="small" icon={<EditOutlined/>} onClick={() => openModal(record)}/>
                    <Popconfirm title="确定要删除此用户？" onConfirm={() => deleteUserItem(record.id)} okText="确认"
                                cancelText="取消">
                        <Button type="text" size="small" icon={<DeleteOutlined/>} danger/>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <>
            <div className="mb-8 flex justify-between items-end">
                <div>
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">用户管理</h1>
                    <p className="text-gray-500 mt-1">管理系统用户账户</p>
                </div>
                <Button
                    type="primary"
                    icon={<PlusOutlined/>}
                    size="large"
                    className="rounded-xl h-12 shadow-lg shadow-blue-100"
                    onClick={() => openModal()}
                >
                    新增用户
                </Button>
            </div>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6 flex gap-4">
                    <Input
                        placeholder="搜索用户..."
                        prefix={<SearchOutlined className="text-gray-400"/>}
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
                    dataSource={users}
                    rowKey="id"
                    scroll={{x: 900}}
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
                title={editingItem ? "编辑用户" : "新建用户"}
                open={isModalVisible}
                onCancel={() => setIsModalVisible(false)}
                onOk={() => form.submit()}
                width={600}
                centered
                okButtonProps={{className: "rounded-lg h-10 px-6"}}
                cancelButtonProps={{className: "rounded-lg h-10 px-6"}}
            >
                <Form form={form} layout="vertical" onFinish={handleAddOrUpdateEdit} className="mt-4">
                    {/* Hidden Id field */}
                    <Form.Item name="id" hidden>
                        <Input/>
                    </Form.Item>

                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item name="username" label="用户名" rules={[{required: true}]}>
                                <Input placeholder="输入用户名" className="rounded-lg h-10"/>
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="nickname" label="昵称" rules={[{required: true}]}>
                                <Input placeholder="输入昵称" className="rounded-lg h-10"/>
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item name="email" label="邮箱" rules={[{required: true}]}>
                                <Input placeholder="输入邮箱" className="rounded-lg h-10"/>
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="points" label="积分" initialValue={0}>
                                <InputNumber className="w-full rounded-lg h-10 flex items-center"/>
                            </Form.Item>
                        </Col>
                    </Row>

                    {!editingItem && (
                        <Row gutter={16}>
                            <Col span={12}>
                                <Form.Item name="password" label="密码" rules={[{required: true}]}>
                                    <Input.Password placeholder="输入密码" className="rounded-lg h-10"/>
                                </Form.Item>
                            </Col>
                        </Row>
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
