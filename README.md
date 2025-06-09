# 🐾 Purrse – Personal Expense Tracker App

**Purrse** is a mobile expense tracking app designed to help users monitor their spending habits, set monthly goals, and make smarter financial decisions. This app was created as part of the Open-Source Programming module at Varsity College and is optimized for real devices (Android 10+). With an intuitive UI and Firebase backend, Purrse makes financial awareness fun and accessible.

---

## 🎯 Purpose of the App

Purrse empowers users to:
- Add expenses by category
- Track progress against monthly goals
- Visualize trends with interactive graphs
- Compare savings and spending by month

---

## ✨ Design Considerations

- 💡 User-friendly, centered on ease of use
- 🎨 Clean, accessible color palette
- 🧭 Simple and intuitive navigation
- 📱 Responsive layout for all screen sizes
- ✅ Real-time validation on forms

---

## 📲 Feature-Driven Layout

| Screen | Description |
|--------|-------------|
| **Welcome Screen** | First impression screen with branding and a warm greeting. |
| **Login/Register** | Secure authentication using Firebase. |
| **Home Screen** | Overview of spending, quick access to features. |
| **Add Expense** | Input expenses with category, date, and note. |
| **Add Category** | Create custom categories for tailored budgeting. |
| **Category Management** | Search, view, and manage all categories. |
| **Goals Activity** | Assign total and per-category budgets. |
| **Main Activity** | Central dashboard with income, expenses, trends, and badges. |

---

## 🔐 Data and Security

- 🔒 Firebase Authentication for login/register
- 🔁 Realtime Database for syncing data
- 🧹 Input validation to prevent data injection or errors

---

## 🔧 Core Features

- ➕ Add, edit, delete expenses
- 📅 Categorized expense views
- 🎯 Monthly budget goals (min/max)
- 📉 Graphs for budget vs actuals
- 💾 Data persistence across devices
- 🧮 View savings based on income vs expenses

---

## 📊 Graphs & Visual Feedback

- **Spending Graph**: Shows total spent per category
- **Min/Max Goal Display**: Shows limit lines on graphs
- **Progress Feedback**: Color-coded bars to indicate goal status

> 🖼️ *Example screenshots go here!*

---

## ☁️ Online Data Storage

- All data stored in **Firebase Realtime Database**
- Data syncs across sessions
- Survives app reinstall and login

---

## 🧪 Automated Testing & CI

GitHub Actions used for CI:
- ✅ Build the app
- 📦 Install dependencies
- 🧪 Run automated tests

---

## 🎬 Demonstration Video

📹 **Demo Link:** *[Insert your YouTube or Drive link here]*

**Includes:**
- Real-time app usage on Android device
- Voice-over explanations
- Data saving & retrieval in Firebase
- Walkthrough of all core features

---

## 🛠️ Technologies Used

| Tool | Purpose |
|------|---------|
| **Android Studio** | UI and App Logic |
| **Firebase** | Auth + Database |
| **Figma** | UI Mockups |
| **GitHub** | Version Control |
| **GitHub Actions** | Continuous Integration |
| **VoiceOver Tools** | Demo narration |

---

## ✍️ Authors

- 👩‍💻 Faeeza Reynolds – `st10314608`
- 👩‍💻 Wadiha Boat – `st10303285`
- 🎓 Institution: Varsity College
- 📘 Module: PROG7313 (Open Source)

---

## ⚠️ Additional Notes

- 📶 Internet connection is required for database features
- 📈 Graphs and goals require data to be entered first
- 📱 Best viewed on Android 10 or later
- 💾 Data is saved locally and to the cloud

---

## 🧰 How to Run the App

### ✅ Prerequisites

- Android Studio (latest)
- JDK 11+
- Gradle (included with Android Studio)
- Android Device or Emulator

---

### 🔽 Steps to Run

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/purrse.git
