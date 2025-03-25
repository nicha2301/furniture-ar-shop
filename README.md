# Furniture AR Store

Ứng dụng mua sắm nội thất với công nghệ Thực tế Tăng cường (AR), cho phép người dùng trải nghiệm và mua sắm nội thất một cách trực quan và chân thực.

## Tính năng chính

- **Xem nội thất trong AR**: Người dùng có thể xem các sản phẩm nội thất trong không gian thực tế của họ
- **Tùy chỉnh sản phẩm**: Thay đổi màu sắc, kích thước và vị trí của nội thất trong AR
- **Mua sắm trực tuyến**: Tích hợp thanh toán và đặt hàng trực tiếp từ ứng dụng
- **Danh mục sản phẩm**: Duyệt qua bộ sưu tập nội thất đa dạng
- **Lưu trữ yêu thích**: Lưu các sản phẩm yêu thích để xem sau
- **Chia sẻ**: Chia sẻ thiết kế AR với bạn bè và gia đình

## Yêu cầu hệ thống

- Android 7.0 trở lên
- Camera AR-compatible
- Kết nối internet
- Ứng dụng ARCore (cho Android)

## Cài đặt

1. Clone repository:
```bash
git clone https://github.com/yourusername/furniture-ar-store.git
```

2. Cài đặt dependencies:
```bash
npm install
# hoặc
yarn install
```

3. Chạy ứng dụng:
```bash
# Android
npm run android
# hoặc
yarn android

# iOS
npm run ios
# hoặc
yarn ios
```

## Công nghệ sử dụng

- React Native
- ARCore (Android)
- Redux
- React Navigation
- Firebase
- Stripe Payment

## Cấu trúc dự án

```
furniture-ar-store/
├── android/                 # Android native code
├── ios/                    # iOS native code
├── src/
│   ├── components/         # Reusable components
│   ├── containers/         # Screen components
│   ├── navigation/         # Navigation configuration
│   ├── redux/             # Redux actions & reducers
│   ├── services/          # API services
│   └── utils/             # Utility functions
├── assets/                # Images, fonts, etc.
└── App.js                 # Root component
```

## Đóng góp

Mọi đóng góp đều được chào đón! Vui lòng:

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push lên branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## Giấy phép

Dự án này được phân phối dưới giấy phép MIT. Xem `LICENSE` để biết thêm thông tin.

## Liên hệ

- Website: [your-website]
- Email: [your-email]
- LinkedIn: [your-linkedin]
- GitHub: [your-github]

## Tác giả

[Your Name]

## Lời cảm ơn

- [React Native](https://reactnative.dev/)
- [ARCore](https://developers.google.com/ar)
- [Redux](https://redux.js.org/)
- [Firebase](https://firebase.google.com/) 