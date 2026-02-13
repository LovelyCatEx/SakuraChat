import React, {useEffect, useRef, useState} from 'react';
import {AutoComplete, Button, Input, Modal, Pagination, Space, Table, Tag} from 'antd';
import {SearchOutlined} from '@ant-design/icons';
import type {PaginatedResponseData, ApiResponse} from '../../api/sakurachat-request';

interface Entity {
    id: string;
}

interface EntitySelectorProps<T extends Entity> {
    value?: string;
    onChange?: (value: string) => void;
    placeholder?: string;
    fetchOptions: (keyword: string, page: number, pageSize: number) => Promise<PaginatedResponseData<T> | ApiResponse<PaginatedResponseData<T>>>;
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
    const [modalPage, setModalPage] = useState(1);
    const [modalTotal, setModalTotal] = useState(0);
    const [focused, setFocused] = useState(false);
    const [displayLabel, setDisplayLabel] = useState<string>('');
    const initializedRef = useRef(false);

    const unwrapResponse = <T,>(response: PaginatedResponseData<T> | ApiResponse<PaginatedResponseData<T>>): PaginatedResponseData<T> => {
        if ('data' in response) {
            return response.data || {page: 1, pageSize: 5, total: 0, totalPages: 0, records: []};
        }
        return response;
    };

    const updateDisplayLabel = async (id: string) => {
        if (id && fetchById) {
            try {
                const item = await fetchById(id);
                if (item) {
                    setDisplayLabel(renderLabel(item));
                }
            } catch (error) {
                console.error('Fetch by id failed:', error);
                setDisplayLabel(id);
            }
        } else {
            setDisplayLabel('');
        }
    };

    useEffect(() => {
        if (value && !initializedRef.current && fetchById) {
            initializedRef.current = true;
            void updateDisplayLabel(value);
        }
    }, [fetchById, updateDisplayLabel, value]);

    const handleFocus = () => {
        setFocused(true);
        if (value) {
            void handleSearch('');
        }
    };

    const handleBlur = async () => {
        setFocused(false);
        if (value && fetchById) {
            try {
                const item = await fetchById(value);
                if (item) {
                    setOptions([{value: item.id, label: renderLabel(item)}]);
                    setDisplayLabel(renderLabel(item));
                }
            } catch (error) {
                console.error('Fetch by id failed:', error);
            }
        }
    };

    const handleChange = (newValue: string) => {
        onChange?.(newValue);
        const selectedOption = options.find(opt => opt.value === newValue);
        if (selectedOption) {
            setDisplayLabel(selectedOption.label);
        }
    };

    const handleSearch = async (keyword: string) => {
        setSearching(true);
        try {
            const results = unwrapResponse(await fetchOptions(keyword, 1, 5));
            setOptions(results.records.map(item => ({
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

    const handleOpenModal = async () => {
        setIsModalVisible(true);
        setModalLoading(true);
        setModalPage(1);
        try {
            const results = unwrapResponse(await fetchOptions('', 1, 5));
            setModalData(results.records);
            setModalTotal(results.total);
        } catch (error) {
            console.error('Fetch data failed:', error);
            setModalData([]);
            setModalTotal(0);
        } finally {
            setModalLoading(false);
        }
    };

    const handleModalSearch = async () => {
        setModalLoading(true);
        try {
            const results = unwrapResponse(await fetchOptions(modalSearchKeyword, 1, 5));
            setModalData(results.records);
            setModalTotal(results.total);
            setModalPage(1);
        } catch (error) {
            console.error('Search failed:', error);
            setModalData([]);
            setModalTotal(0);
        } finally {
            setModalLoading(false);
        }
    };

    const handleSelect = (id: string) => {
        onChange?.(id);
        setIsModalVisible(false);
        setModalSearchKeyword('');
        const selectedItem = modalData.find(item => item.id === id);
        if (selectedItem) {
            setDisplayLabel(renderLabel(selectedItem));
        }
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
                <Space orientation="vertical" size={0}>
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
                {focused ? (
                    <AutoComplete
                        value={value}
                        onChange={handleChange}
                        onFocus={handleFocus}
                        onSearch={handleSearch}
                        onBlur={handleBlur}
                        options={options}
                        placeholder={placeholder}
                        disabled={disabled}
                        notFoundContent={searching ? '搜索中...' : '无结果'}
                        allowClear
                        style={{flex: 1}}
                        filterOption={false}
                        autoFocus
                    />
                ) : (
                    <div
                        onClick={() => !disabled && setFocused(true)}
                        className="flex-1 flex items-center px-3 py-1 bg-white border border-gray-300 rounded-l-md cursor-pointer disabled:cursor-not-allowed disabled:bg-gray-100 min-h-8 transition-all duration-300 hover:border-primary hover:z-10 -mr-px"
                    >
                        {displayLabel ? (
                            <Tag color="pink" className="m-0">{displayLabel}</Tag>
                        ) : (
                            <span className="text-gray-400">{placeholder}</span>
                        )}
                    </div>
                )}
                <Button
                    icon={<SearchOutlined />}
                    onClick={handleOpenModal}
                    disabled={disabled}
                    className="hover:z-10"
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
                    pagination={false}
                    size="small"
                />
                <div className="mt-4 flex justify-end">
                    <Pagination
                        current={modalPage}
                        pageSize={5}
                        total={modalTotal}
                        onChange={(page) => {
                            setModalLoading(true);
                            setModalPage(page);
                            fetchOptions(modalSearchKeyword, page, 5)
                                .then(results => {
                                    const unwrapped = unwrapResponse(results);
                                    setModalData(unwrapped.records);
                                    setModalTotal(unwrapped.total);
                                })
                                .catch(error => {
                                    console.error('Pagination failed:', error);
                                    setModalData([]);
                                    setModalTotal(0);
                                })
                                .finally(() => {
                                    setModalLoading(false);
                                });
                        }}
                        showSizeChanger={false}
                        showQuickJumper={false}
                        showTotal={(total) => `共 ${total} 条`}
                    />
                </div>
            </Modal>
        </>
    );
}
