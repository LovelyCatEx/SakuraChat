import {useEffect, useState} from 'react';
import {Button, Card, Col, Form, Input, message, Modal, Row, Select, Space, Table, Tag} from 'antd';
import {EditOutlined, SearchOutlined} from '@ant-design/icons';
import {
    getUserRoleRelationList,
    getUserRoles,
    searchUserRoleRelations,
    updateUserRoleRelations
} from '../../../../../api/user-role-relation.api.ts';
import {getUserRoleList} from '../../../../../api/user-role.api.ts';
import {getUserProfile} from '../../../../../api/user.api.ts';
import type {ColumnGroupType, ColumnType} from "antd/es/table";
import type {UserRole, UserRoleRelation} from '../../../../../types/user-role.types.ts';
import type {UserProfileVO} from '../../../../../types/user.types.ts';

const { Option } = Select;

export function UserRoleRelationPage() {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingUserId, setEditingUserId] = useState<number | null>(null);
    const [form] = Form.useForm();
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);
    const [searchKeyword, setSearchKeyword] = useState('');
    const [userRoleRelations, setUserRoleRelations] = useState<UserRoleRelation[]>([]);
    const [allRoles, setAllRoles] = useState<UserRole[]>([]);
    const [users, setUsers] = useState<Record<number, UserProfileVO>>({});

    const refreshData = () => {
        setRefreshing(true);

        const apiCall = searchKeyword 
            ? searchUserRoleRelations(searchKeyword, currentPage, currentPageSize)
            : getUserRoleRelationList({page: currentPage, pageSize: currentPageSize});

        apiCall.then((res) => {
            if (res.data) {
                setUserRoleRelations(res.data.records);
                setTotal(res.data.total);

                const userIds = res.data.records.map(record => record.first);
                
                const userPromises = userIds.map(userId => getUserProfile(userId));
                
                if (userPromises.length > 0) {
                    Promise.all(userPromises).then(userResults => {
                        const userMap: Record<number, UserProfileVO> = {};
                        userResults.forEach((userRes, index) => {
                            if (userRes.data) {
                                userMap[userIds[index]] = userRes.data;
                            }
                        });
                        setUsers(userMap);
                    }).catch(() => {
                        void message.error('获取用户信息失败');
                    });
                } else {
                    setUsers({});
                }
            }
        }).finally(() => {
            setRefreshing(false);
        })
    }

    const refreshRoles = async () => {
        const pageSize = 20;
        let allRecords: UserRole[] = [];
        let totalPages = 1;

        try {
            const firstPageRes = await getUserRoleList({ page: 1, pageSize: pageSize });

            if (firstPageRes.data) {
                allRecords = [...firstPageRes.data.records];
                totalPages = firstPageRes.data.totalPages || 1;

                for (let page = 2; page <= totalPages; page++) {
                    const res = await getUserRoleList({ page: page, pageSize: pageSize });
                    if (res.data && res.data.records) {
                        allRecords = [...allRecords, ...res.data.records];
                    }
                }
            }

            setAllRoles(allRecords);
        } catch {
            void message.error('获取用户角色列表失败');
        }
    };


    const handleSearch = (keyword: string) => {
        setSearchKeyword(keyword);
        setCurrentPage(1);
    };

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refreshData();
        void refreshRoles();
    }, [currentPage, currentPageSize, searchKeyword]);

    const handleUpdateUserRoles = (values: { roleIds: (number | string)[] }) => {
        if (editingUserId) {
            const roleIds = values.roleIds.map(id => typeof id === 'number' ? id.toString() : id);
            updateUserRoleRelations(editingUserId, roleIds).then(() => {
                refreshData();
                void message.success('更新用户角色成功');
            }).catch(() => {
                void message.error('更新用户角色失败');
            })
        }

        setIsModalVisible(false);
        setEditingUserId(null);
        form.resetFields();
    };

    const openEditModal = (userId: number) => {
        setEditingUserId(userId);

        getUserRoles(userId).then((res) => {
            if (res.data) {
                form.setFieldsValue({ roleIds: res.data });
            }
        });

        setIsModalVisible(true);
    };

    const columns: (ColumnGroupType<UserRoleRelation> | ColumnType<UserRoleRelation>)[] = [
        {
            title: '用户',
            dataIndex: 'first',
            key: 'userId',
            fixed: 'start',
            width: 150,
            render: (userId: number) => {
                const user = users[userId];
                return (
                    <Space orientation="vertical" size={0}>
                        <span className="text-gray-600">{user?.nickname ?? `用户 ${userId}`}</span>
                        <span className="text-xs text-gray-400">@{user?.username ?? ''}</span>
                    </Space>
                );
            },
        },
        {
            title: '拥有的角色',
            dataIndex: 'second',
            key: 'roleIds',
            width: 400,
            render: (roleIds: string[]) => (
                <Space wrap size={4}>
                    {roleIds.length > 0 ? (
                        roleIds.map((roleId) => {
                            const role = allRoles.find(r => r.id === roleId);
                            return (
                                <Tag key={roleId} color="green" className="m-0 leading-4 h-6 px-2 rounded">
                                    {role ? role.name : roleId}
                                </Tag>
                            );
                        })
                    ) : (
                        <Tag color="gray" className="m-0 leading-4 h-6 px-2 rounded">无角色</Tag>
                    )}
                </Space>
            ),
        },
        {
            title: '操作',
            key: 'action',
            fixed: 'end',
            width: 100,
            render: (_: unknown, record: UserRoleRelation) => (
                <Space>
                    <Button 
                        type="text" 
                        size="small" 
                        icon={<EditOutlined />} 
                        onClick={() => openEditModal(record.first)} 
                    />
                </Space>
            ),
        },
    ];

    return (
        <>
            <div className="mb-8 flex justify-between items-end">
                <div>
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">用户角色关系管理</h1>
                    <p className="text-gray-500 mt-1">管理用户与角色的关联关系</p>
                </div>
            </div>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6 flex gap-4">
                    <Input
                        placeholder="搜索用户..."
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
                    dataSource={userRoleRelations}
                    rowKey="first"
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
                title={`编辑用户角色 - 用户ID: ${editingUserId}`}
                open={isModalVisible}
                onCancel={() => setIsModalVisible(false)}
                onOk={() => form.submit()}
                width={500}
                centered
                okButtonProps={{ className: "rounded-lg h-10 px-6" }}
                cancelButtonProps={{ className: "rounded-lg h-10 px-6" }}
            >
                <Form form={form} layout="vertical" onFinish={handleUpdateUserRoles} className="mt-4">
                    <Row gutter={16}>
                        <Col span={24}>
                            <Form.Item 
                                name="roleIds" 
                                label="选择角色" 
                                rules={[{ required: true, message: '请至少选择一个角色' }]}
                            >
                                <Select
                                    mode="multiple"
                                    placeholder="选择用户角色"
                                    className="w-full rounded-lg"
                                    allowClear
                                >
                                    {allRoles.map((role) => (
                                        <Option key={role.id} value={role.id}>
                                            {role.name}
                                        </Option>
                                    ))}
                                </Select>
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
