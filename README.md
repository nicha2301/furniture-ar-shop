# Furniture AR Store

Ứng dụng mua sắm nội thất với công nghệ Thực tế Tăng cường (AR), cho phép người dùng trải nghiệm và mua sắm nội thất một cách trực quan và chân thực.

## Tính năng chính

- Xem nội thất trong AR: Người dùng có thể xem các sản phẩm nội thất trong không gian thực tế của họ
- Tùy chỉnh sản phẩm: Thay đổi màu sắc, kích thước và vị trí của nội thất trong AR
- Mua sắm trực tuyến: Tích hợp thanh toán và đặt hàng trực tiếp từ ứng dụng
- Danh mục sản phẩm: Duyệt qua bộ sưu tập nội thất đa dạng
- Lưu trữ yêu thích: Lưu các sản phẩm yêu thích để xem sau
- Chia sẻ: Chia sẻ thiết kế AR với bạn bè và gia đình

## Yêu cầu hệ thống

- Android 7.0 trở lên (minSdkVersion 21)
- Camera AR-compatible
- Kết nối internet
- Ứng dụng ARCore (cho Android)

## Cài đặt

1. Clone repository:
```bash
git clone https://github.com/nicha2301/furniture-ar-store.git
```

2. Mở project trong Android Studio

3. Cấu hình:
   - Thêm file `google-services.json` vào thư mục `app/`
   - Cấu hình OneSignal App ID trong `build.gradle`

4. Đồng bộ Gradle và chạy ứng dụng

## Công nghệ sử dụng

- Kotlin
- Android SDK
- ARCore
- Firebase
  - Authentication
  - Analytics
  - Crashlytics
- OneSignal (Push Notifications)
- Retrofit (API calls)
- Glide (Image loading)
- Stripe & Razorpay (Payment)
- Material Design Components

## Cấu trúc dự án

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/nicha/furnier/
│   │   │   ├── activity/         # Activities
│   │   │   ├── fragments/        # Fragments
│   │   │   ├── models/           # Data models
│   │   │   ├── network/          # API services
│   │   │   ├── utils/            # Utility functions
│   │   │   └── adapter/          # RecyclerView adapters
│   │   ├── res/                  # Resources
│   │   └── AndroidManifest.xml
├── build.gradle                  # App level build config
└── google-services.json          # Firebase config
```

## Cấu hình API

- WooCommerce API endpoint
- JWT Authentication
- OAuth 1.0a for WooCommerce

## Đóng góp

Mọi đóng góp đều được chào đón! Vui lòng:

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push lên branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## Giấy phép

Dự án này được phân phối dưới giấy phép MIT. Xem LICENSE để biết thêm thông tin.

## Liên hệ

- Email: nqt230103@gmail.com
- GitHub: https://github.com/nicha2301

## Tác giả

[Your Name]

## Lời cảm ơn

- Android Studio
- Kotlin
- ARCore
- Firebase
- WooCommerce
- OneSignal

