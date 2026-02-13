import {useEffect, useState} from 'react';
import {Button, Card, Col, Form, Input, message, Popconfirm, Row, Space, Table, Tag, Tooltip} from 'antd';
import {DeleteOutlined, ReloadOutlined} from '@ant-design/icons';
import type {ColumnGroupType, ColumnType} from "antd/es/table";
import {
    bindUserThirdPartyAccount,
    getUserThirdPartyAccounts,
    unbindUserThirdPartyAccount
} from "../../../../api/user-third-party-account.api";
import type {ThirdPartyAccount} from "../../../../types/third-party-account.types.ts";
import {formatTimestamp} from "../../../../utils/datetime.utils.ts";

export function UserThirdPartyAccountBindPage() {
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);
    const [accounts, setAccounts] = useState<ThirdPartyAccount[]>([]);
    const [form] = Form.useForm();

    function refreshData() {
        setRefreshing(true);

        getUserThirdPartyAccounts(currentPage, currentPageSize)
            .then(res => {
                if (res.data) {
                    setTotal(res.data.total);
                    setAccounts(res.data.records);
                } else {
                    void message.warning("查询第三方账号失败")
                }
            })
            .catch(() => {
                void message.error("查询第三方账号失败")
            })
            .finally(() => {
                setRefreshing(false);
            })
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refreshData();
    }, [currentPage, currentPageSize]);

    const handleBind = (values: { code: string }) => {
        bindUserThirdPartyAccount(values.code)
            .then(() => {
                void message.success("绑定成功");
                form.resetFields();
                refreshData();
            })
            .catch(() => {
                void message.error("绑定失败");
            });
    };

    const handleUnbind = (id: string) => {
        unbindUserThirdPartyAccount(id)
            .then(() => {
                void message.success("解绑成功");
                refreshData();
            })
            .catch(() => {
                void message.error("解绑失败");
            });
    };

    const columns: (ColumnGroupType<ThirdPartyAccount> | ColumnType<ThirdPartyAccount>)[] = [
        {
            title: '账号名称',
            dataIndex: 'nickname',
            key: 'nickname',
            fixed: 'start',
            width: 160,
            ellipsis: true,
            render: (nickname: string, record: ThirdPartyAccount) => (
                <Space orientation='vertical' size={0} style={{ width: '100%' }}>
                    <Tooltip title={nickname}>
                        <span 
                            className="font-bold text-gray-800" 
                            style={{
                                maxWidth: '100%',
                                display: 'inline-block',
                                overflow: 'hidden',
                                whiteSpace: 'nowrap',
                                textOverflow: 'ellipsis'
                            }}
                        >
                            {nickname}
                        </span>
                    </Tooltip>
                    <Tooltip title={record.id}>
                        <Tag color="blue" className="m-0 text-[10px] leading-4 h-4 px-1 rounded">ID: {record.id}</Tag>
                    </Tooltip>
                </Space>
            ),
        },
        {
            title: '账号 ID',
            dataIndex: 'accountId',
            key: 'accountId',
            ellipsis: true,
            width: 200,
            render: (accountId: string) => (
                <Tooltip title={accountId}>
                    <span 
                        className="text-gray-600 font-mono" 
                        style={{
                            maxWidth: '100%',
                            display: 'inline-block',
                            overflow: 'hidden',
                            whiteSpace: 'nowrap',
                            textOverflow: 'ellipsis'
                        }}
                    >
                        {accountId}
                    </span>
                </Tooltip>
            )
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
                return <Tag color="green" className="m-0 leading-4 h-4 px-1 rounded">{platformMap[platform] ?? platform}</Tag>
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
                    <Popconfirm title="确定要解绑此账号？" onConfirm={() => handleUnbind(record.id)} okText="确认" cancelText="取消">
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
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">第三方账号绑定</h1>
                    <p className="text-gray-500 mt-1">绑定第三方账号以获取更多功能</p>
                </div>
                <Button
                    type="primary"
                    icon={<ReloadOutlined />}
                    size="large"
                    className="rounded-xl h-12 shadow-lg shadow-blue-100"
                    onClick={() => refreshData()}
                >
                    刷新
                </Button>
            </div>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden mb-8">
                <div className="mb-6">
                    <h2 className="text-lg font-semibold text-gray-800 mb-4">绑定新账号</h2>
                    <Form form={form} layout="horizontal" onFinish={handleBind} className="max-w-md">
                        <Row gutter={16}>
                            <Col span={16}>
                                <Form.Item name="code" label="绑定码" rules={[{ required: true, message: '请输入绑定码' }]}>
                                    <Input placeholder="输入绑定码" className="rounded-lg h-10" />
                                </Form.Item>
                            </Col>
                            <Col span={8} className="flex items-end">
                                <Form.Item>
                                    <Button type="primary" htmlType="submit" className="w-full rounded-lg h-10">
                                        绑定
                                    </Button>
                                </Form.Item>
                            </Col>
                        </Row>
                    </Form>
                </div>
            </Card>

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6">
                    <h2 className="text-lg font-semibold text-gray-800 mb-4">已绑定账号</h2>
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
                        }
                    }}
                    loading={refreshing}
                />
            </Card>

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
    )
}