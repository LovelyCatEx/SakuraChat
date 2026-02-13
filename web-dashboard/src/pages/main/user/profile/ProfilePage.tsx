import {Avatar, Card, Descriptions, Divider, Typography} from 'antd';
import {UserOutlined} from '@ant-design/icons';
import {useLoggedUser} from '../../../../compositions/use-logged-user.ts';

const { Title, Text } = Typography;

export function ProfilePage() {
  const { userProfile } = useLoggedUser();

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center gap-4">
        <Title level={2}>个人中心</Title>
      </div>

      <Card className="w-full">
        <div className="flex flex-col items-center gap-6 p-6">
          <Avatar 
            size={128} 
            icon={<UserOutlined />} 
            style={{ backgroundColor: '#2563eb' }} 
          />
          
          <div className="flex flex-col items-center gap-2">
            <Title level={4}>{userProfile?.nickname || '未设置昵称'}</Title>
            <Text type="secondary">@{userProfile?.username || '未知用户'}</Text>
          </div>
        </div>

        <Divider />

        <Descriptions column={2} size="middle">
          <Descriptions.Item label="用户ID">
            <Text>{userProfile?.userId || '-'}</Text>
          </Descriptions.Item>
          
          <Descriptions.Item label="用户名">
            <Text>{userProfile?.username || '-'}</Text>
          </Descriptions.Item>
          
          <Descriptions.Item label="昵称">
            <Text>{userProfile?.nickname || '未设置昵称'}</Text>
          </Descriptions.Item>
          
          <Descriptions.Item label="邮箱">
            <Text>{userProfile?.email || '未设置邮箱'}</Text>
          </Descriptions.Item>
        </Descriptions>
      </Card>
    </div>
  );
}
