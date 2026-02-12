import {Button, Card, message, Space, Table, Tag} from "antd";
import {ReloadOutlined} from "@ant-design/icons";
import type {ColumnGroupType, ColumnType} from "antd/es/table";
import {formatTimestamp} from "../../../../utils/datetime.utils.ts";
import type {UserPointsLog} from "../../../../types/user-points-log.types.ts";
import {useEffect, useState} from "react";
import {listUserPointsLogs} from "../../../../api/user-points-logs.api.ts";
import {PointsChangesReason} from "../../../../types/enums/points-changes-reason.enum.ts";

export function UserPointsLogPage() {
    const [refreshing, setRefreshing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(20);
    const [total, setTotal] = useState(0);
    const [logs, setLogs] = useState<UserPointsLog[]>([]);

    function refreshData() {
        setRefreshing(true);

        listUserPointsLogs(currentPage, currentPageSize)
            .then(res => {
                if (res.data) {
                    setTotal(res.data.total);
                    setLogs(res.data.records);
                } else {
                    void message.warning("查询积分消耗记录失败")
                }
            })
            .catch(() => {
                void message.error("查询积分消耗记录失败")
            })
            .finally(() => {
                setRefreshing(false);
            })
    }

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refreshData();
    }, [currentPage, currentPageSize]);

    const columns: (ColumnGroupType<UserPointsLog> | ColumnType<UserPointsLog>)[] = [
        {
            title: '类型',
            dataIndex: 'reasonType',
            key: 'reasonType',
            fixed: 'start',
            width: 200,
            render: (reasonType: number) => <span>{PointsChangesReason[reasonType]}</span>,
        },
        {
            title: '积分',
            dataIndex: 'deltaPoints',
            key: 'deltaPoints',
            width: 200,
            render: (deltaPoints: number) => deltaPoints > 0 ? (
                <span>{deltaPoints}</span>
            ) : (
                <span>{deltaPoints}</span>
            )
        },
        {
            title: '关联对象',
            dataIndex: 'relatedTableType1',
            key: 'relatedTableType1',
            width: 200,
            render: (_: string, record: UserPointsLog) => (
                <Space orientation="horizontal" size={8}>
                    {[
                        { type: record.relatedTableType1, id: record.relatedTableId1 },
                        { type: record.relatedTableType2, id: record.relatedTableId2 },
                        { type: record.relatedTableType3, id: record.relatedTableId3 },
                        { type: record.relatedTableType4, id: record.relatedTableId4 },
                    ].filterNot((e) => e.type == null || e.id == null)
                        .map((e) => <Tag>{e.type} / {e.id}</Tag>)}
                </Space>
            )
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
            width: 160,
            render: (_: unknown, record: UserPointsLog) => <span>{formatTimestamp(record.createdTime)}</span>
        },
        {
            title: '修改时间',
            dataIndex: 'modifiedTime',
            key: 'modifiedTime',
            width: 160,
            render: (_: unknown, record: UserPointsLog) => <span>{formatTimestamp(record.modifiedTime)}</span>
        }
    ];

    return (
        <>
            <div className="mb-8 flex justify-between items-end">
                <div>
                    <h1 className="text-2xl font-bold text-gray-800 mb-2">积分消耗记录</h1>
                    <p className="text-gray-500 mt-1">所有积分变更在此处展示</p>
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

            <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
                <Table
                    columns={columns}
                    dataSource={logs}
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