# KasApp - Akıllı Gıda Tedarik Zinciri, EKP Maliyet Muhasebesi, Satış ve Lot İzlenebilirlik Platformu

**KasApp**, et ve şarküteri endüstrisinde faaliyet gösteren paydaşlar (hayvancı/işletme, fason sucuk üreticisi, perakendeci/market ve sistem yöneticisi) arasındaki hammadde takibi, karkas parçalama, fason üretim, **EKP (Eşdeğer Kalite Puanlı)** maliyet muhasebesi, resmi e-fatura/belge yönetimi, uçtan uca çift yönlü lot izlenebilirliği, acil ürün geri çağırma (**recall**) ve yapay zeka destekli verim tahmin/doğrulama süreçlerini tek bir çatı altında birleştiren yeni nesil kurumsal platformdur.

Platform; modern bir **Web Paneli**, sahada kesintisiz çalışabilen **Offline-First Android Mobil Uygulaması**, yüksek performanslı **Node.js REST API**, **FastAPI Tabanlı Makine Öğrenmesi (ML) Servisi** ve **MySQL 8.0** veritabanı altyapısıyla tam entegre çalışır.

---

## Mimari & Teknoloji Yığını

| Katman | Teknoloji | Açıklama |
|---|---|---|
| **Mobil Uygulama (Android)** | Kotlin, Jetpack Compose, Material 3, Room (SQLite), Retrofit, OkHttp, Coroutines | Çevrimdışı (Offline-First) yerel veri saklama, otomatik arka plan senkronizasyonu, pull-to-refresh ve modern UI |
| **Web Frontend** | React 18, Vite, TypeScript, Ant Design, CSS Modules | Rol bazlı paneller (İşletme, Üretici, Market, Admin), E-Fatura/PDF çıktı motoru, interaktif lot izleme ağacı, canlı KPI grafikleri |
| **Backend API** | Node.js, Express, TypeScript, MySQL2, JWT | RESTful servis mimarisi, JWT kimlik doğrulama, rol bazlı yetkilendirme (RBAC), ACID işlem yönetimi |
| **Veritabanı** | MySQL 8.0 (InnoDB, UTF-8 MB4) | İlişkisel veri modeli, foreign key bütünlüğü, lot hareketleri ve faturalandırma |
| **Yapay Zeka (ML)** | Python 3.10+, FastAPI, scikit-learn, Uvicorn | Üretim verim tahmini (Model A), Karkas parçalama tahmini (Model B), canlı model doğrulama metrikleri |
| **Konteynerizasyon** | Docker, Docker Compose | Tek komutla orkestre edilen izole mikroservis mimarisi |
| **Çoklu Dil & Para Birimi** | i18n, Multi-Currency Engine | Türkçe, İngilizce, Almanca dil desteği; EUR (€), TL (₺), USD ($), GBP (£) para birimi desteği |
| **Test Altyapısı** | Jest, ts-jest, JUnit | EKP formülleri, fatura hesaplamaları ve lot üretim kurallarını kapsayan birim testler |

---

## Hızlı Başlangıç (Docker ile Çalıştırma)

Tüm backend, frontend, veritabanı ve yapay zeka servislerini tek bir komutla ayağa kaldırmak için:

```bash
docker compose up -d --build
```

### Servis Portları ve Bağlantılar:
* **Web Arayüzü (Frontend):** [http://localhost:5173](http://localhost:5173)
* **Backend API Servisi:** [http://localhost:3000](http://localhost:3000) *(Sağlık Kontrolü: `/health`)*
* **Yapay Zeka / ML API:** [http://localhost:8000](http://localhost:8000) *(Swagger Dokümantasyonu: `/docs`)*
* **MySQL Veritabanı:** `localhost:3306`
* **phpMyAdmin Veritabanı Yöneticisi:** [http://localhost:8080](http://localhost:8080)
  * *Kullanıcı Adı:* `avatar_user`
  * *Şifre:* `avatar_pass_2026`
  * *Veritabanı:* `avatar_db`

---

## Android Mobil Uygulama (KasApp Mobile)

Saha operasyonları için geliştirilen yerel Android uygulaması, işletme sahiplerinin mezbaha, depo ve üretim hatlarında kesintisiz çalışabilmesini sağlar:

* **Çevrimdışı Öncelikli (Offline-First):** İnternet bağlantısı koptuğunda veya zayıf olduğunda Room SQLite üzerinde yerel olarak çalışmaya devam eder. İnternet sağlandığında bekleyen işlemler arka planda otomatik olarak sunucuya aktarılır (`SyncManager`).
* **Akıllı Kamera & OCR Fiş Okuma:** Alınan hammadde fiş veya irsaliyelerini telefon kamerasıyla tarayarak malzeme adı, miktar, birim fiyat ve tedarikçiyi form alanlarına otomatik doldurur.
* **Modern UI & Pull-to-Refresh:** Akıcı liste kaydırma, üst yeşil bara entegre çekip-yenileme (pull-to-refresh) animasyonu ve sağ üst yenileme butonu.
* **Hazır APK:** Derlenmiş debug APK'sı `android/app/build/outputs/apk/debug/app-debug.apk` yolunda yer alır.

### Mobil Derleme:
```bash
cd android
./gradlew assembleDebug
```

---

## Fatura ve Belge Akışı (İş Kuralları)

Sistemdeki fatura ve maliyet akışı gerçek gıda sanayii standartlarına ve yarışma şartnamesine tam uyumlu tasarlanmıştır:

1. **Hammadde Girişi (İşletme Stoğu):**
   * İşletme dış tedarikçilerden bağımsız hammadde alımlarını (ör. baharat, bağırsak) doğrudan birim maliyetleriyle kendi stoğuna işler.
2. **Fason Üretim / İşçilik Hizmeti Faturası (`FTR-HZM-...`):**
   * **Kimden Kime:** Sucukçu (Fason Üretici) ➔ Hayvancı (İşletme).
   * **İçerik:** Hammaddeler işletmeye ait olduğundan faturada yalnızca fason işleme/işçilik hizmet bedeli (ör. 85 paket × 5.50 € = 467.50 €) yer alır.
   * **Erişim & PDF:** Hem sucukçu hem de hayvancı faturayı sistemde görüntüler ve resmi e-fatura formatında PDF olarak indirebilir.
3. **Toptan Mamul Satış Faturası (`FTR-SAT-...`):**
   * **Kimden Kime:** Hayvancı (İşletme) ➔ Perakendeci (Market).
   * **İçerik:** Üretilen mamul sucukların marketlere satış bedelidir (ör. 30 paket × 25.00 € = 750.00 €).
   * **Erişim & PDF:** Hem hayvancı hem de market tarafından görüntülenebilir ve PDF olarak indirilebilir.

---

## Parçalama Maliyet Hesabı (25 kg Karkas / 22 kg Kullanılabilir Parça)

Karkas parçalama ve maliyet dağıtımında karşılaşılan **25 kg toplam karkas ağırlığı** ile **22 kg kullanılabilir parça / 351 €** ilişkisi aşağıdaki maliyet muhasebesi prensibiyle çözümlenmiştir:

### 1. Durum Tespiti:
* Hayvan karkas ağırlığı: **25 kg**, toplam maliyet: **351.00 €**.
* Parçalama sonrası elde edilen satılabilir et parçaları (But, Sırt, Omuz, Kaburga, Kıyma, Sakatat): **22.00 kg** (%88).
* Parçalama firesi (Kemik, yağ kırpıntısı, buharlaşma): **3.00 kg** (%12).

### 2. Maliyet Dağıtım Modeli (Absorbsiyon Yöntemi):
* Firenin ekonomik değeri sıfırdır ($0\ €$). Karkasın 351 € olan toplam maliyeti, **yalnızca satılabilir 22 kg et üzerine paylaştırılır**:
  $$\text{Efektif Kullanılabilir Birim Maliyet} = \frac{351.00\ €}{22.00\ \text{kg}} = 15.9545\ €/\text{kg}$$
* Sistemdeki parçalama reçetesi (`butchery_recipes`) her parçanın piyasa katsayısını ve fire oranını dikkate alarak maliyeti ağırlıklı olarak paylaştırır. Böylece parçaların toplam maliyeti tam olarak **351.00 €**'ya eşitlenir ($\sum \text{Parça Maliyetleri} = 351.00\ €$).

---

## EKP (Eşdeğer Kalite Puanlı Maliyet) ve Fiyatlandırma Formülü

Sucuk üretiminde EKP; işletmenin hammadde maliyetleri ile sucukçunun fason işçilik bedelinin toplamının net üretilen paket sayısına bölünmesiyle elde edilir:

$$\text{Toplam Hammadde Maliyeti} = \sum (\text{Miktar}_i \times \text{Birim Fiyat}_i)$$
$$\text{Toplam Üretim Maliyeti} = \text{Toplam Hammadde Maliyeti} + \text{Fason Hizmet Bedeli}$$
$$\text{EKP (Birim Maliyet)} = \frac{\text{Toplam Üretim Maliyeti}}{\text{Gerçek Üretilen Paket Sayısı}}$$

### Örnek Yarışma Senaryosu:
* **Hammadde:** 100 kg Et (10 €/kg = 1000 €) + 2 kg Baharat (5 €/kg = 10 €) + 5 kg Bağırsak (3 €/kg = 15 €) = **1025.00 €**
* **Fason İşçilik (Sucukçu Faturası):** 85 paket × 5.50 € = **467.50 €**
* **Toplam Maliyet:** 1025.00 € + 467.50 € = **1492.50 €**
* **EKP (Birim Maliyet):** $1492.50\ € / 85\ \text{paket} = \mathbf{17.5588\ €/\text{paket}}$
* **VKP (%38 Kâr Marjı):** $17.5588 \times 1.38 = \mathbf{24.2312\ €}$
* **Toptan Satış Fiyatı:** 25.00 €/paket $\rightarrow$ **Net Birim Kâr:** $7.4412\ €$ (Brüt Marj: %29.76).

---

## Yapay Zeka (ML) Modelleri ve Doğrulama

Sistemde iki adet eğitilmiş makine öğrenmesi modeli bulunmaktadır:

1. **Model A (Üretim Verim ve Fire Tahmini - Gradient Boosting Regressor):**
   * **Girdi:** Et miktarı, yağ oranı, nem, ortam sıcaklığı, fermantasyon süresi.
   * **Çıktı:** Beklenen mamul paket sayısı, tahmini fire oranı, tahmini EKP.
   * **Doğrulama Sonucu:** Sabit kural tabanlı (%85 kuralı) yaklaşıma kıyasla **hata oranını %77.7 (adet) ve %96.2 (EKP)** azaltmıştır ($R^2 = 0.984$).
2. **Model B (Karkas Parçalama Verimi - Multi-Output Regressor):**
   * **Girdi:** Hayvan türü, karkas soğuk ağırlığı, ırk, yaş.
   * **Çıktı:** But, sırt, omuz, kaburga, kıyma, sakatat ve fire ağırlıkları.
   * **Doğrulama Sonucu:** Sabit reçete oranlarına kıyasla ortalama mutlak hatayı (MAE) **%44.5** düşürmüştür.

> Canlı doğrulama metrikleri (MAE, RMSE, MAPE, $R^2$) arayüzdeki **Raporlar ve Analiz ➔ Yapay Zeka (ML) Model Doğrulama** sekmesinden gerçek zamanlı olarak izlenebilir.

---

## Çift Yönlü Lot İzlenebilirliği ve Acil Geri Çağırma (Recall)

* **Geriye Doğru İzleme (Trace Back):** Bir market reyonundaki sucuk paketinin lot numarası (`SUC-2026-045`) okutulduğunda; hangi üretim partisine (`URT-2026-045`), hangi fason sucukçuya, hangi et lotuna (`ET-2026-001`), hangi hayvana ve hangi mezbaha parçalama işlemine dayandığı saniyeler içinde görsel ağaç diyagramı olarak dökülür.
* **İleriye Doğru İzleme (Trace Forward):** Belirli bir baharat veya et partisinde hijyen veya kalite problemi tespit edildiğinde; bu partinin hangi sucuk partilerine girdiği, hangi marketlere ne zaman faturalandığı ve satıldığı anında listelenir.
* **Tek Tıkla Geri Çağırma:** Hayvancı veya Üretici panelinden parti seçilerek **"Geri Çağır"** dendiğinde; o partiyi satın almış tüm marketlere anlık acil durum bildirimi düşer ve marketlerin ekranında ilgili ürünler otomatik olarak **"Reyondan Çekildi"** statüsüne geçer.

---

## Birim ve Entegrasyon Testleri

Backend testlerini çalıştırmak için:

```bash
cd backend
npm test
```

Testler; lot numarası üretimi/ayrıştırması, EKP maliyet formülleri, fatura tutarları ve kâr marjı kurallarını tam kapsamlı olarak doğrular.
