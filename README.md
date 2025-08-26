# 🍽️ UniBites - Your All-in-One Food Ordering Solution

<div align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/Language-Java-orange.svg" alt="Language">
  <img src="https://img.shields.io/badge/Backend-Firebase-yellow.svg" alt="Backend">
  <img src="https://img.shields.io/badge/Min%20SDK-24-blue.svg" alt="Min SDK">
  <img src="https://img.shields.io/badge/Target%20SDK-35-blue.svg" alt="Target SDK">
  <img src="https://img.shields.io/badge/License-MIT-red.svg" alt="License">
</div>

<p align="center">
  <strong>UniBites is a modern, feature-rich Android food ordering application that brings your favorite meals right to your fingertips. Built with cutting-edge technology and designed for seamless user experience.</strong>
</p>

---

## 📱 Features

### 🔐 **Authentication & Security**
- **Multi-Authentication Support**: Email/Password and Google Sign-In
- **Secure Firebase Authentication**: Industry-standard security protocols
- **Password Recovery**: Easy password reset functionality
- **Session Management**: Automatic login state persistence

### 🍕 **Food Catalog & Discovery**
- **Categorized Menu**: Browse by Breakfast, Lunch, Snacks, and Dinner
- **Rich Product Details**: High-quality images, descriptions, and pricing
- **Real-time Data**: Live updates from Firebase Firestore
- **Search & Filter**: Easy product discovery (Coming Soon)

### 🛒 **Smart Shopping Cart**
- **Dynamic Cart Management**: Add, remove, and modify quantities
- **Real-time Calculations**: Automatic subtotal, tax (10%), and total computation
- **Persistent Storage**: Cart items saved across sessions
- **User-specific Carts**: Individual cart management per user

### 🎨 **Modern UI/UX**
- **Material Design 3**: Latest Google design principles
- **Responsive Layout**: Optimized for all screen sizes
- **Intuitive Navigation**: Bottom navigation with fragment architecture
- **Smooth Animations**: Enhanced user experience with fluid transitions

### 💳 **Payment Integration** *(Coming Soon)*
- Multiple payment methods (UPI, QR Code, Cash)
- Secure transaction processing
- Order history and tracking

---

## 🏗️ Technical Architecture

### **Tech Stack**
```
Frontend: Android (Java)
Backend: Firebase (Authentication + Firestore)
UI Framework: Material Design Components
Image Loading: Glide Library
Architecture: MVVM with Fragment-based Navigation
```

### **Project Structure**
```
app/src/main/java/com/example/unibites/
├── 📁 Activities/
│   ├── MainActivity.java          # Main container with bottom navigation
│   ├── LoginActivity.java         # User authentication
│   ├── SignUpActivity.java        # User registration
│   ├── ProfileActivity.java       # User profile management
│   └── PaymentActivity.java       # Payment processing (WIP)
├── 📁 Fragments/
│   ├── HomeFragment.java          # Home screen with featured items
│   ├── FoodFragment.java          # Complete food catalog
│   ├── CartFragment.java          # Shopping cart management
│   └── HistoryFragment.java       # Order history (WIP)
├── 📁 Models/
│   ├── Product.java               # Product data model
│   └── CartItem.java              # Cart item data model
├── 📁 Adapters/
│   ├── ProductAdapter.java        # Product grid display
│   └── CartAdapter.java           # Cart items list
└── 📁 Utils/
    └── [Firebase configuration files]
```

---

## 🚀 Getting Started

### **Prerequisites**
- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK (API level 24+)
- Firebase project setup

### **Installation Steps**

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/unibites-android.git
   cd unibites-android
   ```

2. **Firebase Setup**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Enable Authentication (Email/Password & Google Sign-In)
   - Create a Firestore database
   - Download `google-services.json` and place it in `app/` directory

3. **Configure Authentication**
   - In Firebase Console, go to Authentication > Sign-in method
   - Enable Email/Password and Google Sign-In providers
   - For Google Sign-In, add your app's SHA-1 fingerprint

4. **Firestore Database Structure**
   ```
   📁 Products/
   ├── 📄 {productId}
   │   ├── Productname: "Product Name"
   │   ├── ProductPrice: 99.99
   │   ├── description: "Product description"
   │   ├── Productimage: "image_url"
   │   └── ProductCategory: "breakfast|lunch|snacks|dinner"
   
   📁 carts/
   ├── 📁 {userId}/
   │   └── 📁 items/
   │       └── 📄 {itemId}
   │           ├── productId: "product_reference"
   │           ├── name: "Product Name"
   │           ├── price: 99.99
   │           ├── quantity: 1
   │           └── timestamp: 1234567890
   ```

5. **Build and Run**
   ```bash
   # Open in Android Studio
   # Sync project with Gradle files
   # Run on device or emulator
   ```

## 🔧 Configuration

### **Firebase Configuration**
1. Add your `google-services.json` file to the `app/` directory
2. Ensure Firebase dependencies are up to date in `build.gradle.kts`
3. Configure Firestore security rules:

---

## 🛠️ Development

### **Adding New Features**
1. Create feature branch: `git checkout -b feature/new-feature`
2. Follow existing code patterns and architecture
3. Add appropriate error handling and loading states
4. Test on multiple devices and screen sizes
5. Submit pull request with detailed description

### **Code Style Guidelines**
- Follow Android coding conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Implement proper error handling
- Use Firebase best practices for data operations

---

## 🚧 Roadmap

### **Phase 1** ✅
- [x] User Authentication (Email/Password, Google)
- [x] Product Catalog with Categories
- [x] Shopping Cart Functionality
- [x] Firebase Integration

### **Phase 2** 🚧
- [ ] Payment Gateway Integration
- [ ] Order Management System
- [ ] Push Notifications
- [ ] User Profile Enhancement

### **Phase 3** 📋
- [ ] Order Tracking
- [ ] Reviews & Ratings
- [ ] Favorites & Wishlist
- [ ] Advanced Search & Filters
- [ ] Delivery Address Management
- [ ] Promotional Codes & Discounts

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit your changes** (`git commit -m 'Add some AmazingFeature'`)
4. **Push to the branch** (`git push origin feature/AmazingFeature`)
5. **Open a Pull Request**

### **Contribution Guidelines**
- Ensure code follows project conventions
- Add tests for new features
- Update documentation as needed
- Verify Firebase integration works correctly

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Firebase team for excellent backend services
- Material Design team for beautiful UI components
- Glide library for efficient image loading
- Android development community for continuous support

---

<div align="center">
  <p><strong>Made with ❤️ for food lovers everywhere</strong></p>
  <p>⭐ Star this repo if you found it helpful!</p>
</div> 