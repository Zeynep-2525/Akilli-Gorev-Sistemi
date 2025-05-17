# Akıllı-Görev-Sistemi

-------------------------------------------------

## Açıklama

---------------------------------------------------

- Akıllı Görev Sistemi, kullanıcıların günlük görevlerini kolayca yönetmelerini sağlayan, öncelikli görevler için otomatik e-posta hatırlatmaları gönderen ve günlük istatistiklerle kullanıcıların verimliliğini analiz etmesine yardımcı olan bir görev takip uygulamasıdır.

## Özellikler

--------------------------------------------------------

- Öncelikli görevler için otomatik e-posta hatırlatmaları
- Tamamlanan görevler için log tutma
- Görev CRUD (Create, Read, Update, Delete) işlemleri
- Otomatik PDF raporlama modülü
- Günlük ve haftalık performans istatistikleri

## Kullanılan Bağımlılıklar

-----------------------------------------------

### Backend
- **Java 17**
- **Spring Boot 3.2.5**
- **Hibernate (JPA)**

###  PDF & Excel Raporlama
- **iText 7 Core** `7.2.5` 

###  Veritabanları
- **H2** (Geliştirme/Test için) `2.2.224`
- **MySQL** (Üretim için) `8.0.33`

### Arayüz
- **JavaFX 21.0.2**
    - `javafx-controls`
    - `javafx-fxml`

###  Loglama
- **Logback Classic** `1.4.14`
- **SLF4J Simple Logger** `1.7.36`

###  JSON İşleme
- **Jackson Databind**

###  Geliştirici Araçları & Yardımcılar
- **Lombok** `1.18.30` – Boilerplate azaltma

###  Test Frameworkleri
- **JUnit 5 (Jupiter)** `5.10.2`
- **Mockito Core** `5.11.0`
- **Spring Boot Test Starter`

###  Maven Plugin'leri
- `spring-boot-maven-plugin`
- `maven-compiler-plugin`
- `javafx-maven-plugin`

## Kurulum ve Çalıştırma

----------------------------------------------

### Ön Koşullar
- **Java 17 JDK** yüklü olmalı
- **Maven 3.8+** yüklü olmalı
- Veritabanı:
  - Geliştirme ortamında **H2**
  - Üretim ortamında **MySQL** kurulu olmalı

### Adım Adım Kurulum

1. **Depoyu Klonlayın**:
   ```bash
   git clone https://github.com/Zeynep-2525/Akilli-Gorev-Sistemi.git
   ```
2. **Bağımlılıkları Yükleyin**:
   ```bash
    mvn clean install
   ```
3. **Uygulamayı Başlatın**:
   ```bash
   mvn spring-boot:run
   ```
4. **JavaFX Arayüzü**:
  - javafx-maven-plugin ile ayrıca başlatılabilir.

## Proje Yapısı

--------------------------

## Katkıda Bulunma

-------------------------------------------------------

1. **Forklayın ve depoyu klonlayın.**
2. **Yeni bir branch oluşturun:**
   ```bash
   git checkout -b feature/your-feature
   ```
3. **Değişiklikleri commit edin:**
   ```bash
   git commit -m "Add your feature"
   ```
4. **Pushlayın ve Pull Request açın.**
## Ekip Görevleri

---------------------------

## 1.) Sena Naz Kaya

   ### Veritabanı İşlemleri
   - Görev bilgilerini tutacak Entity sınıfının tasarlanması
   - CRUD işlemleri için Repository, Service ve Controller katmanlarının implementasyonu

   ### Loglama Sistemi
   - Görev tamamlanma bilgilerinin `log.txt` dosyasına kaydedilmesi
   - Dosya işlemleri için `FileWriter` ve `BufferedReader` kullanımı

   ### E-posta Bildirimleri
   - Gmail SMTP kullanarak e-posta gönderim özelliği
   - Test amaçlı mock e-posta sisteminin geliştirilmesi

   ### Genel Sorumluluklar
   - Backend API geliştirme süreçlerinin yönetilmesi
   - Sistem entegrasyonlarının sağlanması

## 2.) Zeynep Topal

### Proje Kurulum
   - Spring Boot proje yapısının oluşturulması (Spring Web, JPA, Mail ve Lombok bağımlılıklarıyla)
   - Gerekli bağımlılıkların `pom.xml` dosyasına eklenmesi

### Zamanlayıcı Geliştirme
- Günlük e-posta hatırlatıcı sisteminin kurulumu
- Belirlenen saatte tetiklenen zamanlayıcı ayarları

### Harici API Entegrasyonu
- Motivasyon sözleri API bağlantısının sağlanması
- RestTemplate kullanarak veri alımı

### Kullanıcı Arayüzü
- Thymeleaf tabanlı görev yönetim formları
- Model-View-Controller mimarisi uygulaması

### Kalite Güvence
- Servis katmanı için birim testleri
- Temel işlevsellik test senaryoları

## 3.) Selinay Seri

### Raporlama Modülü
- PDF/Excel rapor oluşturma sistemi
- Apache POI kütüphanesi entegrasyonu
- Günlük görev istatistiklerini içeren raporlar
- Oluşturulan raporların `reports/` klasörüne kaydedilmesi

### Test Süreçleri
- JUnit ile servis katmanı testleri
- CRUD operasyonları için test senaryoları
- Hata durumlarının test edilmesi

### Dokümantasyon
- Kullanım kılavuzu hazırlama
- Test senaryoları dokümantasyonu
- Rapor örneklerinin belgelenmesi

## Lisans

---------------------------
Bu proje MIT Lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasını inceleyebilirsiniz.
