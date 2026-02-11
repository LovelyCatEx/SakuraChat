import React, {useState} from 'react';
import {AutoComplete, Button, Input, Modal, Space, Table, Tag} from 'antd';
import {SearchOutlined} from '@ant-design/icons';

interface Entity {
    id: string;
}

interface EntitySelectorProps<T extends Entity> {
    value?: string;
    onChange?: (value: string) => void;
    placeholder?: string;
    fetchOptions: (keyword: string) => Promise<T[]>;
    fetchById?: (id: string) => Promise<T | null>;
    renderLabel: (item: T) => string;
    renderExtra?: (item: T) => React.ReactNode;
    disabled?: boolean;
}

export function EntitySelector<T extends Entity>({
    value,
    onChange,
    placeholder = '请选择或输入',
    fetchOptions,
    fetchById,
    renderLabel,
    renderExtra,
    disabled = false,
}: EntitySelectorProps<T>) {
    const [options, setOptions] = useState<{value: string, label: string}[]>([]);
    const [searching, setSearching] = useState(false);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [modalData, setModalData] = useState<T[]>([]);
    const [modalSearchKeyword, setModalSearchKeyword] = useState('');
    const [modalLoading, setModalLoading] = useState(false);

    const handleSearch = async (keyword: string) => {
        setSearching(true);
        try {
            const results = await fetchOptions(keyword);
            setOptions(results.map(item => ({
                value: item.id,
                label: renderLabel(item)
            })));
        } catch (error) {
            console.error('Search failed:', error);
            setOptions([]);
        } finally {
            setSearching(false);
        }
    };

    const handleBlur = async () => {
        if (value && fetchById) {
            try {
                const item = await fetchById(value);
                if (item) {
                    setOptions([{value: item.id, label: renderLabel(item)}]);
                }
            } catch (error) {
                console.error('Fetch by id failed:', error);
            }
        }
    };

    const handleOpenModal = async () => {
        setIsModalVisible(true);
        setModalLoading(true);
        try {
            const results = await fetchOptions('');
            setModalData(results);
        } catch (error) {
            console.error('Fetch data failed:', error);
            setModalData([]);
        } finally {
            setModalLoading(false);
        }
    };

    const handleModalSearch = async () => {
        setModalLoading(true);
        try {
            const results = await fetchOptions(modalSearchKeyword);
            setModalData(results);
        } catch (error) {
            console.error('Search failed:', error);
        } finally {
            setModalLoading(false);
        }
    };

    const handleSelect = (id: string) => {
        onChange?.(id);
        setIsModalVisible(false);
        setModalSearchKeyword('');
    };

    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            width: 80,
            render: (id: string) => <Tag color="blue" className="m-0 text-[10px] leading-4 h-4 px-1 rounded">{id}</Tag>
        },
        {
            title: '名称',
            key: 'name',
            render: (_: unknown, record: T) => (
                <Space direction="vertical" size={0}>
                    <span className="font-medium">{renderLabel(record)}</span>
                    {renderExtra && renderExtra(record)}
                </Space>
            )
        },
        {
            title: '操作',
            key: 'action',
            width: 80,
            render: (_: unknown, record: T) => (
                <Button
                    type="primary"
                    size="small"
                    onClick={() => handleSelect(record.id)}
                    disabled={record.id === value}
                >
                    {record.id === value ? '已选择' : '选择'}
                </Button>
            )
        }
    ];

    return (
        <>
            <Space.Compact style={{width: '100%'}}>
                <AutoComplete
                    value={value}
                    onChange={onChange}
                    onSearch={handleSearch}
                    onBlur={handleBlur}
                    options={options}
                    placeholder={placeholder}
                    disabled={disabled}
                    notFoundContent={searching ? '搜索中...' : '无结果'}
                    allowClear
                    style={{flex: 1}}
                    filterOption={false}
                />
                <Button
                    icon={<SearchOutlined />}
                    onClick={handleOpenModal}
                    disabled={disabled}
                />
            </Space.Compact>

            <Modal
                title="选择项目"
                open={isModalVisible}
                onCancel={() => {
                    setIsModalVisible(false);
                    setModalSearchKeyword('');
                }}
                footer={null}
                width={600}
                centered
            >
                <div className="mb-4">
                    <Input
                        value={modalSearchKeyword}
                        onChange={(e) => setModalSearchKeyword(e.target.value)}
                        placeholder="搜索..."
                        style={{width: '100%'}}
                        onPressEnter={handleModalSearch}
                    />
                </div>
                <Table
                    columns={columns}
                    dataSource={modalData}
                    rowKey="id"
                    loading={modalLoading}
                    pagination={{
                        pageSize: 10,
                        showSizeChanger: false
                    }}
                    size="small"
                />
            </Modal>
        </>
    );
}
