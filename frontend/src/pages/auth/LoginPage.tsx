import { Form, Input, Button, Card, Typography, App as AntApp, Space } from 'antd';
import { UserOutlined, LockOutlined, ShopOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../../services/api';
import { useAuthStore } from '../../store/authStore';
import { useTranslation, LanguageSwitcher } from '../../i18n';

const { Title, Text } = Typography;

export default function LoginPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { setAuth } = useAuthStore();
  const { message } = AntApp.useApp();
  const [form] = Form.useForm();

  async function onFinish(values: { email: string; password: string }) {
    try {
      const { data } = await authService.login(values.email, values.password);
      setAuth(data.user, data.token);
      message.success(t('auth.loginSuccess'));
      if (data.user?.role === 'admin') {
        navigate('/admin/users');
      } else {
        navigate('/');
      }
    } catch (err: unknown) {
      const errorMsg = (err as { response?: { data?: { error?: string } } })?.response?.data?.error || t('auth.loginFailed');
      message.error(errorMsg);
    }
  }

  return (
    <div
      style={{
        minHeight: '100vh',
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '24px',
        overflow: 'hidden',
      }}
    >
      {/* Video Arka Planı */}
      <video
        autoPlay
        loop
        muted
        playsInline
        style={{
          position: 'fixed',
          top: 0,
          left: 0,
          width: '100vw',
          height: '100vh',
          objectFit: 'cover',
          zIndex: 0,
          filter: 'brightness(0.65) contrast(1.1)',
        }}
      >
        <source src="/dashboard_bg.mp4" type="video/mp4" />
      </video>

      {/* Karartma Katmanı */}
      <div
        style={{
          position: 'fixed',
          top: 0,
          left: 0,
          width: '100vw',
          height: '100vh',
          background: 'rgba(10, 18, 5, 0.45)',
          zIndex: 0,
        }}
      />

      <div style={{ position: 'absolute', top: 20, right: 24, zIndex: 2 }}>
        <LanguageSwitcher />
      </div>

      <Card
        style={{
          width: 440,
          maxWidth: '100%',
          borderRadius: 16,
          boxShadow: '0 12px 36px rgba(0,0,0,0.5)',
          background: 'rgba(255, 255, 255, 0.94)',
          backdropFilter: 'blur(12px)',
          border: '1px solid rgba(255, 255, 255, 0.6)',
          position: 'relative',
          zIndex: 1,
        }}
      >
        <Space direction="vertical" size="middle" style={{ width: '100%', textAlign: 'center' }}>
          <div style={{ textAlign: 'center' }}>
            <img src="/logo.png" alt="KasApp Logo" style={{ width: 84, height: 84, objectFit: 'contain', marginBottom: 6, display: 'inline-block' }} />
            <Title level={2} style={{ marginBottom: 4, color: '#274001', fontWeight: 800, letterSpacing: -0.5 }}>
              KasApp
            </Title>
            <Text type="secondary">{t('auth.systemSubtitle') || 'Et & Şarküteri Tedarik Zinciri ve Lot Takip Platformu'}</Text>
          </div>

          <Form form={form} onFinish={onFinish} layout="vertical" size="large">
            <Form.Item name="email" rules={[{ required: true, type: 'email' }]}>
              <Input prefix={<UserOutlined style={{ color: '#3E5902' }} />} placeholder={t('auth.email')} />
            </Form.Item>
            <Form.Item name="password" rules={[{ required: true }]}>
              <Input.Password prefix={<LockOutlined style={{ color: '#3E5902' }} />} placeholder={t('auth.password')} />
            </Form.Item>
            <Form.Item style={{ marginBottom: 12 }}>
              <Button
                type="primary"
                htmlType="submit"
                block
                style={{
                  background: '#3E5902',
                  borderColor: '#3E5902',
                  borderRadius: 8,
                  height: 44,
                  fontWeight: 600,
                  fontSize: 15,
                }}
              >
                {t('auth.login')}
              </Button>
            </Form.Item>
          </Form>

          <Text>
            {t('auth.noAccount')} <Link to="/register" style={{ color: '#3E5902', fontWeight: 600 }}>{t('auth.register')}</Link>
          </Text>
        </Space>
      </Card>
    </div>
  );
}
