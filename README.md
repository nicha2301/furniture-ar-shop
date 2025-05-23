# Furnier - Ứng Dụng Nội Thất 3D/AR

Ứng dụng mua sắm nội thất với công nghệ Thực tế Tăng cường (AR) và Thiết kế Nội thất Ảo, cho phép người dùng trải nghiệm và mua sắm nội thất một cách trực quan và chân thực.

## Screenshots

<p align="center">
  <img src="screenshots/home_screen.jpg" width="200" alt="Màn hình chính">
  <img src="screenshots/virtual_room.jpg" width="200" alt="Phòng ảo 3D">
  <img src="screenshots/ar_view.jpg" width="200" alt="Xem sản phẩm trong AR">
  <img src="screenshots/product_detail.jpg" width="200" alt="Chi tiết sản phẩm">
</p>

## Tính năng chính

- **Thiết kế Nội thất Ảo**: Xem nhiều món đồ nội thất trong không gian phòng 3D
- **Xem nội thất trong AR**: Người dùng có thể xem các sản phẩm nội thất trong không gian thực tế của họ
- **Tùy chỉnh sản phẩm**: Thay đổi màu sắc, kích thước và vị trí của nội thất trong AR
- **Mua sắm trực tuyến**: Tích hợp thanh toán và đặt hàng trực tiếp từ ứng dụng
- **Danh mục sản phẩm**: Duyệt qua bộ sưu tập nội thất đa dạng (Bàn, Ghế, Trang trí, Giường, Tủ, Sofa)
- **Lưu trữ yêu thích**: Lưu các sản phẩm yêu thích để xem sau

## Yêu cầu hệ thống

- Android 7.0 trở lên (minSdkVersion 24)
- Camera AR-compatible
- Kết nối internet
- Ứng dụng ARCore (cho Android)

### Yêu cầu phần cứng cụ thể

- RAM: Tối thiểu 3GB (khuyến nghị 4GB trở lên)
- Bộ nhớ: Tối thiểu 100MB cho ứng dụng (không bao gồm tài nguyên 3D cache)
- GPU: Hỗ trợ OpenGL ES 3.1 trở lên
- Thiết bị phải trong [danh sách hỗ trợ ARCore của Google](https://developers.google.com/ar/devices)

## Cài đặt

1. Clone repository:
```bash
git clone https://github.com/nicha2301/furniture-ar-store.git
```

2. Mở project trong Android Studio

3. Cấu hình:
   - Thêm file `google-services.json` vào thư mục `app/`
   - Cấu hình Firebase trong `build.gradle`

4. Đồng bộ Gradle và chạy ứng dụng

## Công nghệ sử dụng

- Kotlin
- Android SDK
- SceneView/Filament (3D rendering)
- GLB models (3D furniture models)
- ARCore
- Firebase
  - Authentication
  - Analytics
  - Crashlytics
- Material Design Components
- RecyclerView và Adapters
- BottomSheetBehavior
- Internationalization (i18n)

## Cấu trúc dự án

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/nicha/furnier/
│   │   │   ├── AppBaseActivity.kt # Base Activity class với các chức năng chung
│   │   │   ├── activity/          # Activities (VirtualRoomActivity, InteriorDesignActivity, etc.)
│   │   │   ├── adapter/           # RecyclerView adapters (SelectedFurnitureAdapter, etc.)
│   │   │   ├── models/            # Data models (FurnitureItem, FurnitureCategory, etc.)
│   │   │   └── utils/             # Utility functions
│   │   │       ├── Constants.kt   # Các hằng số được sử dụng trong app
│   │   │       └── extensions/    # Kotlin extensions functions
│   │   ├── assets/                # 3D models (GLB) và environments
│   │   │   ├── models/            # Furniture 3D models
│   │   │   └── environments/      # Room environments và HDR files
│   │   ├── res/
│   │   │   ├── layout/            # Layout files
│   │   │   ├── drawable/          # Icons và background resources
│   │   │   ├── values/            # String resources, styles, colors
│   │   │   ├── values-vi/         # Vietnamese translations
│   │   │   ├── values-fr/         # French translations
│   │   │   ├── values-es/         # Spanish translations
│   │   │   └── values-de/         # German translations
│   │   └── AndroidManifest.xml
├── build.gradle                   # App level build config
└── google-services.json           # Firebase config
```

## Chức năng Thiết kế Nội thất Ảo

- Xem phòng 3D với nhiều món đồ nội thất
- Điều hướng camera để xem phòng từ các góc độ khác nhau
- Tập trung vào từng món đồ nội thất
- Tùy chỉnh vị trí và góc xem
- Hỗ trợ nhiều danh mục nội thất: Bàn, Ghế, Trang trí, Giường, Tủ, Sofa
- Giao diện BottomSheet để quản lý nội thất đã chọn

## Hướng dẫn sử dụng

### Thiết kế nội thất ảo

1. Từ màn hình chính, chọn "Thiết kế Nội thất Ảo"
2. Chọn các món đồ nội thất từ danh mục sản phẩm
3. Nhấn "Xem Thiết Kế" để xem không gian phòng với các món đồ đã chọn
4. Sử dụng thao tác vuốt để xoay góc nhìn camera
5. Nhấn vào món đồ trong danh sách bên dưới để tập trung vào món đồ đó

### Xem sản phẩm trong AR

1. Chọn một sản phẩm từ danh mục
2. Nhấn nút "Xem trong AR"
3. Di chuyển camera để quét không gian xung quanh
4. Nhấn vào màn hình để đặt sản phẩm
5. Sử dụng các cử chỉ để xoay, di chuyển hoặc thay đổi kích cỡ sản phẩm

## Khắc phục sự cố

### Vấn đề với AR

- **Camera không hoạt động**: Đảm bảo đã cấp quyền camera cho ứng dụng
- **Không thể phát hiện mặt phẳng**: Di chuyển thiết bị chậm hơn trong không gian đủ sáng
- **Lỗi "Device not supported"**: Kiểm tra xem thiết bị có trong danh sách hỗ trợ ARCore

### Vấn đề với chế độ xem 3D

- **Hiển thị model chậm**: Đảm bảo kết nối internet tốt để tải model nhanh chóng
- **Không thấy nội thất**: Kiểm tra danh mục đã chọn, một số model chỉ hoạt động với danh mục cụ thể
- **Ứng dụng bị đóng đột ngột**: Giảm số lượng model 3D hiển thị cùng lúc

## Hiệu năng và Tối ưu hóa

- Sử dụng GLB models nhỏ gọn để tải nhanh
- Cache tài nguyên 3D để giảm thời gian tải
- Giới hạn số lượng đối tượng 3D hiển thị đồng thời
- Tối ưu hóa ánh sáng và đổ bóng cho hiệu suất tốt hơn

## Đóng góp

Mọi đóng góp đều được chào đón! Vui lòng:

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/TinhNangMoi`)
3. Commit changes (`git commit -m 'Thêm tính năng mới'`)
4. Push lên branch (`git push origin feature/TinhNangMoi`)
5. Mở Pull Request

## Giấy phép

Dự án này được phân phối dưới giấy phép MIT. Xem LICENSE để biết thêm thông tin.

## Liên hệ

- Email: nqt230103@gmail.com
- GitHub: https://github.com/nicha2301

## Tác giả

Nicha

## Lời cảm ơn

- Android Studio
- Kotlin
- SceneView/Filament
- ARCore
- Firebase

