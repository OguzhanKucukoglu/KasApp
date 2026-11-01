import { useEffect, useState } from 'react';
import { Row, Col, Card, Typography, Alert, Button, Tag } from 'antd';
import {
  ScissorOutlined, InboxOutlined, ShoppingCartOutlined,
  TagsOutlined, DollarCircleOutlined, AuditOutlined, WarningOutlined,
  ForkOutlined, BarChartOutlined, FileTextOutlined,
  TeamOutlined, ArrowRightOutlined, ShopOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { notificationService } from '../services/api';
import { useTranslation } from '../i18n';

const { Title, Text, Paragraph } = Typography;

interface NotificationItem {
  id: number;
  type: string;
  title: string;
  message: string;
  reference_id?: number;
  is_read: boolean | number;
}

interface ModuleTile {
  key: string;
  title: string;
  description: string;
  path: string;
  icon: React.ReactNode;
  accent: string;
}

export default function DashboardPage() {
  const { user } = useAuthStore();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [qualityAlerts, setQualityAlerts] = useState<NotificationItem[]>([]);

  useEffect(() => {
    notificationService.getAll()
      .then(res => {
        const notifs = (res.data.notifications || []) as NotificationItem[];
        setQualityAlerts(notifs.filter(n => n.type === 'kalite_sorunu' && !n.is_read));
      })
      .catch(() => {});
  }, []);

  const roleWelcome: Record<string, string> = {
    hayvanci: t('dashboard.welcomeHayvanci') || 'Karkas parçalama yapın, üreticilere fason üretim siparişi verin ve marketlere toptan satış yapın.',
    sucukcu: t('dashboard.welcomeSucukcu') || 'Gelen fason üretim siparişlerini yönetin, üretim partileri oluşturun ve EKP hesaplayın.',
    market: t('dashboard.welcomeMarket') || 'İşletmelerden üretilen güvenilir mamulleri lot bazlı temin edin ve satın alma operasyonlarını yürütün.',
    admin: 'Sistem yönetimi, kullanıcı denetimi, muhasebe ve duyuru merkezi',
  };

  const role = user?.role || 'hayvanci';

  // Role özel modül kareleri
  const modulesByRole: Record<string, ModuleTile[]> = {
    hayvanci: [
      {
        key: 'butchery',
        title: t('nav.butchery') || 'Parçalama & Kesim',
        description: 'Karkas girişi, et parçalama ve kütle dengesi hesaplama',
        path: '/butchery',
        icon: <ScissorOutlined />,
        accent: '#3E5902',
      },
      {
        key: 'rawMaterials',
        title: t('nav.rawMaterials') || 'Hammadde Stoğu',
        description: 'Et, karkas, baharat ve ambalaj stok envanteri yönetimi',
        path: '/raw-materials',
        icon: <InboxOutlined />,
        accent: '#5A7302',
      },
      {
        key: 'producerListings',
        title: t('nav.producerListings') || 'Üretici İlanları',
        description: 'Fason üretim tarifelerini inceleyin ve işleme siparişi verin',
        path: '/producer-listings',
        icon: <ShopOutlined />,
        accent: '#274001',
      },
      {
        key: 'orders',
        title: t('nav.orders') || 'Siparişlerim',
        description: 'Üreticilere iletilen fason üretim emirlerinin takip durumu',
        path: '/orders',
        icon: <ShoppingCartOutlined />,
        accent: '#3E5902',
      },
      {
        key: 'farmerListings',
        title: t('nav.farmerListings') || 'İlanlarım',
        description: 'Marketlere satılacak mamul sucuk, salam, sosis ilanları açın',
        path: '/farmer-listings',
        icon: <TagsOutlined />,
        accent: '#5A7302',
      },
      {
        key: 'sales',
        title: t('nav.sales') || 'Satışlarım',
        description: 'Marketlerden gelen siparişler ve toptan satış faturaları',
        path: '/sales',
        icon: <DollarCircleOutlined />,
        accent: '#274001',
      },
      {
        key: 'lots',
        title: t('nav.lots') || 'Lot Yönetimi',
        description: 'Karkastan mamule tam geriye ve ileriye lot izlenebilirliği',
        path: '/lots',
        icon: <AuditOutlined />,
        accent: '#3E5902',
      },
      {
        key: 'recalls',
        title: t('nav.recalls') || 'Geri Çağırmalar',
        description: 'Kalite kontrol bildirimleri ve acil parti geri çağırmaları',
        path: '/recalls',
        icon: <WarningOutlined />,
        accent: '#FA8C16',
      },
      {
        key: 'invoices',
        title: t('nav.invoices') || 'Faturalar & İrsaliyeler',
        description: 'Fason hizmet ve mamul satış e-faturaları ve irsaliyeler',
        path: '/invoices',
        icon: <FileTextOutlined />,
        accent: '#274001',
      },
      {
        key: 'reports',
        title: t('nav.reports') || 'Raporlar ve Analiz',
        description: 'Fire analizi, kütle korunum dengesi ve maliyet dağılımı',
        path: '/reports',
        icon: <BarChartOutlined />,
        accent: '#3E5902',
      },
    ],
    sucukcu: [
      {
        key: 'producerListingsManage',
        title: 'Fason İlanlarım',
        description: 'Fason et işleme tarifeleri ve kapasite ilanlarınızı yönetin',
        path: '/producer-listings-manage',
        icon: <TagsOutlined />,
        accent: '#3E5902',
      },
      {
        key: 'producerOrders',
        title: t('nav.producerOrders') || 'Gelen Siparişler',
        description: 'İşletmelerden gelen fason siparişleri onaylayın ve yönetin',
        path: '/producer-orders',
        icon: <ShoppingCartOutlined />,
        accent: '#5A7302',
      },
      {
        key: 'production',
        title: t('nav.production') || 'Üretim Yönetimi (EKP)',
        description: 'Üretim partileri, baharat reçeteleri ve EKP maliyet hesabı',
        path: '/production',
        icon: <ForkOutlined />,
        accent: '#274001',
      },
      {
        key: 'invoices',
        title: t('nav.invoices') || 'Faturalar & İrsaliyeler',
        description: 'Düzenlenen fason işçilik faturaları ve sevk irsaliyeleri',
        path: '/invoices',
        icon: <FileTextOutlined />,
        accent: '#3E5902',
      },
      {
        key: 'reports',
        title: t('nav.reports') || 'Raporlar ve Analiz',
        description: 'Üretim hacimleri, işçilik gelirleri ve operasyonel veriler',
        path: '/reports',
        icon: <BarChartOutlined />,
        accent: '#5A7302',
      },
    ],
    market: [
      {
        key: 'marketListings',
        title: t('nav.marketListings') || 'Ürün Pazarı',
        description: 'İşletmelerin satışa sunduğu mamul lotları inceleyin ve satın alın',
        path: '/market-listings',
        icon: <ShopOutlined />,
        accent: '#3E5902',
      },
      {
        key: 'purchases',
        title: t('nav.purchases') || 'Satın Almalarım',
        description: 'Verilen toptan ürün siparişleri ve sevk irsaliyesi takibi',
        path: '/purchases',
        icon: <ShoppingCartOutlined />,
        accent: '#5A7302',
      },
      {
        key: 'invoices',
        title: t('nav.invoices') || 'Faturalar & İrsaliyeler',
        description: 'Gelen toptan mamul faturaları ve muhasebe kayıtları',
        path: '/invoices',
        icon: <FileTextOutlined />,
        accent: '#274001',
      },
      {
        key: 'reports',
        title: t('nav.reports') || 'Raporlar ve Analiz',
        description: 'Satın alma hacmi, ürün bazlı harcama ve tedarik raporları',
        path: '/reports',
        icon: <BarChartOutlined />,
        accent: '#3E5902',
      },
    ],
    admin: [
      {
        key: 'adminUsers',
        title: t('nav.adminUsers') || 'Kullanıcı Yönetimi',
        description: 'İşletme, üretici ve market hesaplarını denetleyin ve yönetin',
        path: '/admin/users',
        icon: <TeamOutlined />,
        accent: '#274001',
      },
      {
        key: 'invoices',
        title: t('nav.invoices') || 'Faturalar & İrsaliyeler',
        description: 'Sistemdeki tüm alım-satım ve hizmet faturaları',
        path: '/invoices',
        icon: <FileTextOutlined />,
        accent: '#3E5902',
      },
      {
        key: 'reports',
        title: t('nav.reports') || 'Raporlar ve Analiz',
        description: 'Sistem geneli kütle dengesi, fire analizleri ve denetim',
        path: '/reports',
        icon: <BarChartOutlined />,
        accent: '#5A7302',
      },
    ],
  };

  const activeModules = modulesByRole[role] || modulesByRole['hayvanci'];

  return (
    <div>
        {/* Üst Karşılama ve Rol Başlığı */}
      <div
        style={{
          background: 'linear-gradient(135deg, #274001 0%, #3E5902 100%)',
          padding: '24px 28px',
          borderRadius: 14,
          color: '#FFFFFF',
          marginBottom: 24,
          boxShadow: '0 4px 14px rgba(39, 64, 1, 0.15)',
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 12 }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 6 }}>
              <Title level={2} style={{ margin: 0, color: '#FFFFFF', fontWeight: 800 }}>
                {t('dashboard.welcome')}, {user?.name}
              </Title>
              <Tag
                style={{
                  background: 'transparent',
                  color: '#8DE8F2',
                  border: '1px solid #8DE8F2',
                  borderRadius: 6,
                  fontWeight: 700,
                  fontSize: 12,
                }}
              >
                {t(`roles.${role}`)}
              </Tag>
            </div>
            <Paragraph style={{ margin: 0, color: 'rgba(255, 255, 255, 0.85)', fontSize: 14 }}>
              {roleWelcome[role]}
            </Paragraph>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <div
              style={{
                background: 'rgba(255, 255, 255, 0.12)',
                padding: '8px 16px',
                borderRadius: 10,
                textAlign: 'right',
                backdropFilter: 'blur(4px)',
              }}
            >
              <Text style={{ display: 'block', fontSize: 11, color: '#8DE8F2', fontWeight: 600 }}>SİSTEM DURUMU</Text>
              <Text style={{ color: '#FFFFFF', fontWeight: 700, fontSize: 13 }}>Aktif ve Senkronize</Text>
            </div>
          </div>
        </div>
      </div>

      {/* Uyarılar */}
      {qualityAlerts.length > 0 && (
        <Alert
          style={{ marginBottom: 20, borderRadius: 10 }}
          message={
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span><strong>Kalite Bildirimi:</strong> {qualityAlerts.length} adet parti inceleme uyarısı mevcut</span>
              <Button size="small" danger onClick={() => navigate('/recalls')}>
                İncele
              </Button>
            </div>
          }
          type="warning"
          showIcon
        />
      )}

      {/* Modül Seçim Başlığı */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
        <div>
          <Title level={4} style={{ margin: 0, color: '#274001', fontWeight: 700 }}>
            Uygulama Modülleri
          </Title>
          <Text type="secondary" style={{ fontSize: 13 }}>
            İşlem yapmak istediğiniz modülü seçin
          </Text>
        </div>
        <Tag style={{ background: 'transparent', border: '1px solid #d9d9d9', color: '#555', fontWeight: 600 }}>
          {activeModules.length} Modül
        </Tag>
      </div>

      {/* Modül Kareleri (Tiles Grid) */}
      <Row gutter={[16, 16]}>
        {activeModules.map(mod => (
          <Col key={mod.key} xs={24} sm={12} md={8} lg={6}>
            <Card
              hoverable
              onClick={() => navigate(mod.path)}
              style={{
                borderRadius: 12,
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                border: '1px solid #E5E7EB',
                transition: 'all 0.25s ease',
                cursor: 'pointer',
              }}
              styles={{
                body: {
                  padding: 20,
                  display: 'flex',
                  flexDirection: 'column',
                  height: '100%',
                },
              }}
            >
              <div style={{ display: 'flex', alignItems: 'flex-start', gap: 14, marginBottom: 12 }}>
                <div
                  style={{
                    width: 48,
                    height: 48,
                    borderRadius: 12,
                    background: `${mod.accent}15`,
                    color: mod.accent,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: 22,
                    flexShrink: 0,
                  }}
                >
                  {mod.icon}
                </div>
                <div>
                  <Text strong style={{ fontSize: 16, color: '#274001', display: 'block', lineHeight: 1.25 }}>
                    {mod.title}
                  </Text>
                </div>
              </div>

              <Paragraph
                type="secondary"
                style={{
                  fontSize: 12,
                  marginBottom: 16,
                  flex: '1 0 auto',
                  lineHeight: 1.45,
                }}
              >
                {mod.description}
              </Paragraph>

              <div
                style={{
                  marginTop: 'auto',
                  paddingTop: 10,
                  borderTop: '1px solid #F3F4F6',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  color: mod.accent,
                  fontWeight: 600,
                  fontSize: 12,
                }}
              >
                <span>Sayfayı Aç</span>
                <ArrowRightOutlined />
              </div>
            </Card>
          </Col>
        ))}
      </Row>
    </div>
  );
}
