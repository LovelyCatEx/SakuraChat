import {Button, Card, Input, message, Modal, Space, Table, Tag} from 'antd';
import {GiftOutlined, ReloadOutlined, WalletOutlined} from '@ant-design/icons';
import type {ColumnType} from 'antd/es/table';
import {formatTimestamp} from '../../../../utils/datetime.utils.ts';
import type {UserPointsLog} from '../../../../types/user-points-log.types.ts';
import {useEffect, useState} from 'react';
import {listUserPointsLogs} from '../../../../api/user-points-logs.api.ts';
import {PointsChangesReason} from '../../../../types/enums/points-changes-reason.enum.ts';
import {getMyPoints, redeemCdKey} from '../../../../api/user.api.ts';

export function UserPointsLogPage() {
  const [refreshing, setRefreshing] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [currentPageSize, setCurrentPageSize] = useState(20);
  const [total, setTotal] = useState(0);
  const [logs, setLogs] = useState<UserPointsLog[]>([]);
  const [myPoints, setMyPoints] = useState<number>(0);
  const [cdKeyModalVisible, setCdKeyModalVisible] = useState(false);
  const [cdKeyInput, setCdKeyInput] = useState('');
  // const [agents, setAgents] = useState<Map<number, Agent>>(new Map());

  function refreshData() {
    setRefreshing(true);

    Promise.all([
      listUserPointsLogs(currentPage, currentPageSize),
      getMyPoints(),
      //getAgentList({ page: 1, pageSize: 1000 }) // 获取足够多的智能体，避免分页问题
    //]).then(([logsRes, pointsRes, agentsRes]) => {
    ]).then(([logsRes, pointsRes]) => {
      if (logsRes.data) {
        setTotal(logsRes.data.total);
        setLogs(logsRes.data.records);
      } else {
        void message.warning('查询积分消耗记录失败');
      }

      if (pointsRes.data !== undefined && pointsRes.data !== null) {
        setMyPoints(pointsRes.data);
      } else {
        void message.warning('查询积分余额失败');
      }

      /*if (agentsRes.data) {
        const agentMap = new Map<number, Agent>();
        agentsRes.data.records.forEach(agent => {
          agentMap.set(Number(agent.id), agent);
        });
        setAgents(agentMap);
      }*/
    }).catch(() => {
      void message.error('查询积分消耗记录失败');
    }).finally(() => {
      setRefreshing(false);
    });
  }

  async function handleRedeemCdkey() {
    if (!cdKeyInput.trim()) {
      message.warning('请输入激活码');
      return;
    }

    try {
      const response = await redeemCdKey(cdKeyInput.trim());
      if (response.data) {
        message.success(`兑换成功，获得${response.data.points}积分`);
        setCdKeyModalVisible(false);
        setCdKeyInput('');
        refreshData();
      } else {
        message.error('兑换失败');
      }
    } catch {
      message.error('兑换失败，请检查激活码是否正确');
    }
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    refreshData();
  }, [currentPage, currentPageSize]);

  const columns: ColumnType<UserPointsLog>[] = [
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
      width: 80,
      render: (deltaPoints: number) => deltaPoints > 0 ? (
          <span className="text-green-600">+{deltaPoints}</span>
      ) : (
          <span className="text-orange-600">{deltaPoints}</span>
      ),
    },
    {
      title: '余额',
      dataIndex: 'afterBalance',
      key: 'afterBalance',
      width: 120,
      render: (afterBalance: number) => <span>{afterBalance}</span>,
    },
    {
      title: '关联对象',
      dataIndex: 'relatedTableType1',
      key: 'relatedTableType1',
      width: 200,
      render: (_: string, record: UserPointsLog) => (
          <Space orientation="vertical" size={8} wrap>
            {
              [
                { type: record.relatedTableType1, id: record.relatedTableId1 },
                { type: record.relatedTableType2, id: record.relatedTableId2 },
                { type: record.relatedTableType3, id: record.relatedTableId3 },
                { type: record.relatedTableType4, id: record.relatedTableId4 },
              ].filter((e) => e.type != null && e.id != null)
                  .map((e) => {
                    /*const tableType = mapToDatabaseTableType(e.type);
                    if (tableType === DatabaseTableType.AGENTS && e.id != null) {
                      const agent = agents.get(Number(e.id));
                      return <Tag>{agent?.name || e.id}</Tag>;
                    }*/
                    return <Tag>{e.type} / {e.id}</Tag>;
                  })
            }
          </Space>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 160,
      render: (_: unknown, record: UserPointsLog) => <span>{formatTimestamp(record.createdTime)}</span>,
    },
    {
      title: '修改时间',
      dataIndex: 'modifiedTime',
      key: 'modifiedTime',
      width: 160,
      render: (_: unknown, record: UserPointsLog) => <span>{formatTimestamp(record.modifiedTime)}</span>,
    },
  ];

  return (
      <div className="flex flex-col gap-6">
        <div className="mb-8 flex justify-between items-end">
          <div>
            <h1 className="text-2xl font-bold text-gray-800 mb-2">积分变更记录</h1>
            <p className="text-gray-500 mt-1">所有积分变更在此处展示</p>
          </div>
          <Button
              type="primary"
              icon={<ReloadOutlined />}
              size="large"
              className="rounded-xl h-12 shadow-lg"
              onClick={() => refreshData()}
          >
            刷新
          </Button>
        </div>

        <Card className="border-none shadow-sm rounded-2xl overflow-hidden">
          <div className="mb-6 flex gap-4 items-center">
            <p className="text-lg"><WalletOutlined className="text-orange-600" />&nbsp;积分余额：{myPoints}</p>
            <Button
                type="default"
                icon={<GiftOutlined />}
                className="rounded-xl"
                onClick={() => setCdKeyModalVisible(true)}
            >
              兑换激活码
            </Button>
          </div>

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
                },
              }}
              loading={refreshing}
          />
        </Card>

        <Modal
            title="兑换激活码"
            open={cdKeyModalVisible}
            onCancel={() => setCdKeyModalVisible(false)}
            footer={[
              <Button key="cancel" onClick={() => setCdKeyModalVisible(false)}>
                取消
              </Button>,
              <Button key="submit" type="primary" onClick={handleRedeemCdkey}>
                兑换
              </Button>,
            ]}
            className="custom-modal"
        >
          <div className="mb-4">
            <label className="block text-gray-700 mb-2">激活码</label>
            <Input
                placeholder="请输入激活码"
                value={cdKeyInput}
                onChange={(e) => setCdKeyInput(e.target.value)}
                className="w-full"
            />
          </div>
          <p className="text-gray-500 text-sm">
            输入激活码兑换积分，兑换成功后积分会自动添加到您的账户
          </p>
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
      `}</style>
      </div>
  );
}
