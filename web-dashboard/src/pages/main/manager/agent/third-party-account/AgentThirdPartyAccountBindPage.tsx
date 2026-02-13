import {useEffect, useState} from 'react';
import {Button, message, Modal, Popconfirm, Space, Table, Tag, Tooltip} from 'antd';
import {DeleteOutlined, ReloadOutlined} from '@ant-design/icons';
import type {ColumnType} from "antd/es/table";
import {
    bindAgentThirdPartyAccount,
    getAgentThirdPartyAccounts,
    getUnboundAgentThirdPartyAccounts,
    unbindAgentThirdPartyAccount
} from "../../../../../api/agent-third-party-account.api";
import type {ThirdPartyAccount} from "../../../../../types/third-party-account.types.ts";
import {formatTimestamp} from "../../../../../utils/datetime.utils.ts";
import type {ApiResponse, PaginatedResponseData} from "../../../../../api/sakurachat-request.ts";

interface AgentThirdPartyAccountBindPageProps {
    agentId: string;
    agentName: string;
}

export function AgentThirdPartyAccountBindPage({ agentId, agentName }: AgentThirdPartyAccountBindPageProps) {
    const [refreshing, setRefreshing] = useState(false);
    const [unboundRefreshing, setUnboundRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);
    const [accounts, setAccounts] = useState<ThirdPartyAccount[]>([]);
    const [unboundCurrentPage, setUnboundCurrentPage] = useState(1);
    const [unboundCurrentPageSize, setUnboundCurrentPageSize] = useState(20);
    const [unboundTotal, setUnboundTotal] = useState(0);
    const [unboundAccounts, setUnboundAccounts] = useState<ThirdPartyAccount[]>([]);
    const [unboundModalVisible, setUnboundModalVisible] = useState(false);
    const [detailModalVisible, setDetailModalVisible] = useState(false);
    const [selectedAccount, setSelectedAccount] = useState<ThirdPartyAccount | null>(null);

    function refreshData() {
        setRefreshing(true);

        getAgentThirdPartyAccounts(agentId, currentPage, currentPageSize)
            .then((res: ApiResponse<PaginatedResponseData<ThirdPartyAccount>>) => {
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

    function refreshUnboundData() {
        setUnboundRefreshing(true);

        getUnboundAgentThirdPartyAccounts(agentId, unboundCurrentPage, unboundCurrentPageSize)
            .then((res: ApiResponse<PaginatedResponseData<ThirdPartyAccount>>) => {
                if (res.data) {
                    setUnboundTotal(res.data.total);
                    setUnboundAccounts(res.data.records);
                } else {
                    void message.warning("查询未绑定账号失败")
                }
            })
            .catch(() => {
                void message.error("查询未绑定账号失败")
            })
            .finally(() => {
                setUnboundRefreshing(false);
            })
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refreshData();
    }, [agentId, currentPage, currentPageSize]);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refreshUnboundData();
    }, [agentId, unboundCurrentPage, unboundCurrentPageSize]);

    const handleBind = (id: string) => {
        bindAgentThirdPartyAccount(agentId, id)
            .then(() => {
                void message.success("绑定成功");
                refreshData();
                refreshUnboundData();
                setUnboundModalVisible(false);
            })
            .catch(() => {
                void message.error("绑定失败");
            });
    };

    const handleUnbind = (id: string) => {
        unbindAgentThirdPartyAccount(agentId, id)
            .then(() => {
                void message.success("解绑成功");
                refreshData();
                refreshUnboundData();
            })
            .catch(() => {
                void message.error("解绑失败");
            });
    };

    const handleAccountClick = (account: ThirdPartyAccount) => {
        setSelectedAccount(account);
        setDetailModalVisible(true);
    };

    const columns: ColumnType<ThirdPartyAccount>[] = [
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
                            className="font-bold text-gray-800 cursor-pointer hover:text-blue-600"
                            style={{
                                maxWidth: '100%',
                                display: 'inline-block',
                                overflow: 'hidden',
                                whiteSpace: 'nowrap',
                                textOverflow: 'ellipsis'
                            }}
                            onClick={() => handleAccountClick(record)}
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

    const unboundColumns: ColumnType<ThirdPartyAccount>[] = [
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
                            className="font-bold text-gray-800 cursor-pointer hover:text-blue-600"
                            style={{
                                maxWidth: '100%',
                                display: 'inline-block',
                                overflow: 'hidden',
                                whiteSpace: 'nowrap',
                                textOverflow: 'ellipsis'
                            }}
                            onClick={() => {
                                setSelectedAccount(record);
                                setDetailModalVisible(true);
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
            width: 80,
            render: (_: unknown, record: ThirdPartyAccount) => (
                <Button 
                    type="primary" 
                    size="small" 
                    onClick={() => handleBind(record.id)}
                >
                    绑定
                </Button>
            )
        },
    ];

    return (
        <>
            <div className="mb-8 flex justify-between items-end">
                <div>
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">智能体第三方账号绑定</h1>
                    <p className="text-gray-500 mt-1">为智能体 {agentName} 绑定第三方账号以获取更多功能</p>
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

            <div className="border-none shadow-sm rounded-2xl overflow-hidden mb-8">
                <div className="mb-6">
                    <h2 className="text-lg font-semibold text-gray-800 mb-4">绑定新账号</h2>
                    <Button 
                        type="primary" 
                        onClick={() => setUnboundModalVisible(true)}
                        className="rounded-lg h-10 px-6"
                    >
                        选择账号绑定
                    </Button>
                </div>
            </div>

            <div className="border-none shadow-sm rounded-2xl overflow-hidden">
                <div className="mb-6">
                    <h2 className="text-lg font-semibold text-gray-800 mb-4">已绑定账号</h2>
                    {accounts.length === 0 ? (
                        <div className="text-center py-12">
                            <Tag color="orange" className="text-base px-4 py-2 rounded-lg">未绑定任何第三方账号</Tag>
                        </div>
                    ) : (
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
                    )}
                </div>
            </div>

            <Modal
                title="选择第三方账号绑定"
                open={unboundModalVisible}
                onCancel={() => setUnboundModalVisible(false)}
                footer={[
                    <Button key="close" onClick={() => setUnboundModalVisible(false)}>
                        关闭
                    </Button>
                ]}
                width={1000}
                centered
            >
                <div className="border-none shadow-sm rounded-2xl overflow-hidden">
                    <div className="mb-6">
                        <h2 className="text-lg font-semibold text-gray-800 mb-4">未绑定账号列表</h2>
                        {unboundAccounts.length === 0 ? (
                            <div className="text-center py-12">
                                <Tag color="orange" className="text-base px-4 py-2 rounded-lg">没有可绑定的第三方账号</Tag>
                            </div>
                        ) : (
                            <Table
                                columns={unboundColumns}
                                dataSource={unboundAccounts}
                                rowKey="id"
                                scroll={{ x: 800 }}
                                className="custom-table"
                                pagination={{
                                    showSizeChanger: true,
                                    defaultPageSize: 20,
                                    className: "pr-6",
                                    current: unboundCurrentPage,
                                    total: unboundTotal,
                                    pageSize: unboundCurrentPageSize,
                                    pageSizeOptions: [5, 10, 15, 20],
                                    onChange: (page: number, pageSize: number) => {
                                        setUnboundCurrentPage(page);
                                        setUnboundCurrentPageSize(pageSize);
                                    }
                                }}
                                loading={unboundRefreshing}
                            />
                        )}
                    </div>
                </div>
            </Modal>

            <Modal
                title="第三方账号详情"
                open={detailModalVisible}
                onCancel={() => setDetailModalVisible(false)}
                footer={[
                    <Button key="close" onClick={() => setDetailModalVisible(false)}>
                        关闭
                    </Button>
                ]}
                className="custom-modal"
            >
                {selectedAccount && (
                    <div className="space-y-4">
                        <div>
                            <Tag color="blue" className="mb-2">账号信息</Tag>
                            <div className="grid grid-cols-2 gap-4 mt-2">
                                <div>
                                    <p className="text-gray-600 text-sm">账号名称</p>
                                    <p className="font-bold">{selectedAccount.nickname}</p>
                                </div>
                                <div>
                                    <p className="text-gray-600 text-sm">账号 ID</p>
                                    <p className="font-mono">{selectedAccount.id}</p>
                                </div>
                                <div>
                                    <p className="text-gray-600 text-sm">平台 ID</p>
                                    <p>{selectedAccount.platform}</p>
                                </div>
                                <div>
                                    <p className="text-gray-600 text-sm">平台名称</p>
                                    <p>
                                        {(() => {
                                            const platformMap: Record<number, string> = {
                                                1: 'NapCat OICQ',
                                                2: 'Lark'
                                            };
                                            return platformMap[selectedAccount.platform] ?? selectedAccount.platform;
                                        })()}
                                    </p>
                                </div>
                                <div>
                                    <p className="text-gray-600 text-sm">创建时间</p>
                                    <p>{formatTimestamp(selectedAccount.createdTime)}</p>
                                </div>
                                <div>
                                    <p className="text-gray-600 text-sm">修改时间</p>
                                    <p>{formatTimestamp(selectedAccount.modifiedTime)}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                )}
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
    )
}
