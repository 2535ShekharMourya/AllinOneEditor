# 📸 Collage Maker – Image Editing App

Collage Maker is a modern Android application that allows users to create beautiful image collages using themed templates such as **Friendship, Love, Birthday, Travel**, and more. The app focuses on performance, clean architecture, and a smooth user experience.

---

## ✨ Features
- 📂 Create image collages with multiple photos
- 🎨 Themed templates (Friendship, Love, Travel, Birthday, etc.)
- ⚡ Smooth image processing with Kotlin Coroutines
- 🌐 Dynamic template loading via REST APIs
- 🧩 Clean and scalable MVVM architecture
- 🚀 Optimized UI for better performance

---

## 🛠 Tech Stack
- **Language:** Kotlin  
- **UI:** XML  
- **Architecture:** MVVM + Clean Architecture  
- **Dependency Injection:** Dagger Hilt  
- **Networking:** Retrofit  
- **Concurrency:** Kotlin Coroutines  
- **Image Loading:** Glide / Coil  
- **Build Tool:** Gradle  

---

## 🏗 Architecture Overview
This project follows **MVVM (Model–View–ViewModel)** architecture with clear separation of concerns:
- **View:** Activities / Fragments (UI layer)
- **ViewModel:** Manages UI state and business logic
- **Repository:** Handles data from APIs
- **Data Source:** REST APIs for templates and assets

This approach improves **testability, scalability, and maintainability**.

