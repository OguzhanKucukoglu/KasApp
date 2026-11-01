import { useEffect, useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Avatar, Dropdown, Badge, Typography, Space, Popover, List, Button, Tag } from 'antd';
import {
  BellOutlined, UserOutlined, LogoutOutlined,
  AppstoreOutlined, ShopOutlined, ArrowLeftOutlined,
} from '@ant-design/icons';
import { useAuthStore } from '../../store/authStore';
import { notificationService } from '../../services/api';
interface NotificationItem {
  id: number;
  type: string;
  title: string;
  message: string;
  is_read: boolean | number;
}
import { useTranslation, LanguageSwitcher } from '../../i18n';

const { Header, Content } = Layout;
const { Text } = Typography;

const roleColors: Record<string, string> = {
  admin: '#722ed1',
  hayvanci: '#3E5902',
  sucukcu: '#5A7302',
  market: '#8DE8F2',
};

export default function MainLayout() {
  const { t } = useTranslation();
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();

  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [popoverOpen, setPopoverOpen] = useState(false);

  useEffect(() => {
    loadNotifications();
    const interval = setInterval(loadNotifications, 30000);
    return () => clearInterval(interval);
  }, []);

  async function loadNotifications() {
    try {
      const { data } = await notificationService.getAll();
      setNotifications(data);
      setUnreadCount(data.filter((n: NotificationItem) => !n.is_read).length);
    } catch {
      // sessizce geç
    }
  }

  async function handleMarkAllRead() {
    try {
      await notificationService.markAll();
      setNotifications(prev => prev.map(n => ({ ...n, is_read: 1 })));
      setUnreadCount(0);
    } catch {
      // sessizce geç
    }
  }

  async function handleNotificationClick(item: NotificationItem) {
    if (!item.is_read) {
      try {
        await notificationService.markRead(item.id);
        setNotifications(prev => prev.map(n => n.id === item.id ? { ...n, is_read: 1 } : n));
        setUnreadCount(prev => Math.max(0, prev - 1));
      } catch {
        // sessizce geç
      }
    }

    if (item.type === 'recall' || item.type === 'quality_alert') {
      setPopoverOpen(false);
      navigate('/recalls');
    }
  }

  const userMenu = [
    {
      key: 'user-info',
      disabled: true,
      label: (
        <div style={{ padding: '4px 0' }}>
          <Text strong style={{ display: 'block', color: '#274001' }}>{user?.name}</Text>
          <Text type="secondary" style={{ fontSize: 12 }}>{user?.email}</Text>
        </div>
      ),
    },
    { type: 'divider' as const },
    {
      key: 'dashboard',
      icon: <AppstoreOutlined />,
      label: 'Dashboard',
      onClick: () => navigate('/'),
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      danger: true,
      label: t('nav.logout'),
      onClick: () => {
        logout();
        navigate('/login');
      },
    },
  ];

  const notificationContent = (
    <div style={{ width: 340, maxHeight: 420, display: 'flex', flexDirection: 'column' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8, paddingBottom: 8, borderBottom: '1px solid #f0f0f0' }}>
        <Text strong>{t('nav.notifications')}</Text>
        {unreadCount > 0 && (
          <Button type="link" size="small" onClick={handleMarkAllRead} style={{ padding: 0, color: '#3E5902' }}>
            {t('nav.markAllRead')}
          </Button>
        )}
      </div>
      <div style={{ overflowY: 'auto', flex: 1 }}>
        {notifications.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '24px 0', color: '#999' }}>
            {t('common.noData')}
          </div>
        ) : (
          <List
            size="small"
            dataSource={notifications}
            renderItem={item => {
              const isRecall = item.type === 'recall';
              const isQualityAlert = item.type === 'quality_alert';
              return (
                <List.Item
                  onClick={() => handleNotificationClick(item)}
                  style={{
                    padding: '8px 4px',
                    cursor: 'pointer',
                    background: item.is_read ? 'transparent' : '#f6ffed',
                    borderLeft: isRecall ? '3px solid #ff4d4f' : isQualityAlert ? '3px solid #fa8c16' : undefined,
                  }}
                >
                  <div style={{ width: '100%' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Text strong={!item.is_read} style={{ fontSize: 13, color: isRecall ? '#ff4d4f' : undefined }}>
                        {item.title}
                      </Text>
                      {!item.is_read && <Badge status="processing" color="#3E5902" />}
                    </div>
                    <Text type="secondary" style={{ fontSize: 12, display: 'block', marginTop: 2 }}>
                      {item.message}
                    </Text>
                  </div>
                </List.Item>
              );
            }}
          />
        )}
      </div>
    </div>
  );

  const isDashboard = location.pathname === '/';

  return (
    <Layout style={{
      minHeight: '100vh',
      background: 'transparent',
    }}>
      {/* Üst Gezinme Çubuğu (Header) */}
      <Header
        style={{
          background: isDashboard ? 'transparent' : 'rgba(255, 255, 255, 0.90)',
          backdropFilter: 'blur(8px)',
          padding: '0 24px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          boxShadow: '0 1px 6px rgba(0,0,0,0.06)',
          position: 'sticky',
          top: 0,
          zIndex: 1000,
          borderBottom: '1px solid #E5E7EB',
          height: 64,
        }}
      >
        {/* Sol Logo ve Dashboard Butonu */}
        <Space size="middle" align="center">
          <div
            onClick={() => navigate('/')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: 10,
              cursor: 'pointer',
              userSelect: 'none',
            }}
          >
            <img src="/logo.png" alt="KasApp Logo" style={{ width: 38, height: 38, objectFit: "contain" }} />
            <div>
              <Text strong style={{ fontSize: 20, color: '#274001', letterSpacing: -0.5, lineHeight: 1 }}>
                KasApp
              </Text>
            </div>
          </div>

          <Tag
            color="#3E5902"
            style={{
              borderRadius: 6,
              fontWeight: 600,
              fontSize: 12,
              padding: '2px 8px',
              marginLeft: 4,
            }}
          >
            {t(`roles.${user?.role || 'hayvanci'}`)}
          </Tag>

          {!isDashboard && (
            <Button
              type="primary"
              ghost
              icon={<ArrowLeftOutlined />}
              onClick={() => navigate('/')}
              style={{
                borderRadius: 8,
                fontWeight: 600,
                color: '#3E5902',
                borderColor: '#3E5902',
                marginLeft: 8,
              }}
            >
              Dashboard'a Dön
            </Button>
          )}
        </Space>

        {/* Sağ Kullanıcı Araçları */}
        <Space size="large" align="center">
          <LanguageSwitcher />

          <Popover
            content={notificationContent}
            trigger="click"
            placement="bottomRight"
            open={popoverOpen}
            onOpenChange={setPopoverOpen}
          >
            <Badge count={unreadCount} overflowCount={99}>
              <BellOutlined style={{ fontSize: 20, cursor: 'pointer', color: unreadCount > 0 ? '#ff4d4f' : '#274001' }} />
            </Badge>
          </Popover>

          <Dropdown menu={{ items: userMenu }} placement="bottomRight" arrow>
            <Space style={{ cursor: 'pointer' }} align="center">
              <Avatar
                icon={<UserOutlined />}
                style={{ backgroundColor: roleColors[user?.role || ''] || '#3E5902' }}
              />
              <Text strong style={{ color: '#274001' }}>{user?.name || user?.email}</Text>
            </Space>
          </Dropdown>
        </Space>
      </Header>

      {/* Sayfa İçeriği */}
      <Content
        style={{
          margin: '20px auto',
          padding: '24px',
          background: isDashboard ? 'transparent' : 'rgba(255, 255, 255, 0.82)',
          backdropFilter: isDashboard ? 'none' : 'blur(8px)',
          borderRadius: 14,
          maxWidth: 1440,
          width: 'calc(100% - 40px)',
          minHeight: 'calc(100vh - 104px)',
          boxShadow: isDashboard ? 'none' : '0 4px 20px rgba(0,0,0,0.05)',
        }}
      >
        <Outlet />
      </Content>
    </Layout>
  );
}
