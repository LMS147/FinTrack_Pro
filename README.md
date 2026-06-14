# FinTrack Pro

FinTrack Pro is a personal finance management application for Android designed to help users track their expenses, manage budgets, and visualize their spending habits. The app supports multi-user authentication with strict data isolation, ensuring each user's financial information remains private.

## Features

- **User Authentication**: Secure login and registration system with password hashing.
- **Multi-User Support**: Multiple users can use the app on the same device without seeing each other's data.
- **Dashboard**: A quick overview of total balance, income, and recent transactions.
- **Expense Tracking**: Add and manage transactions with categories, dates, and descriptions.
- **Receipt Attachments**: Capture or select photos of receipts to attach to expenses.
- **Budget Management**: Set monthly spending goals and monitor progress with real-time progress bars.
- **Visual Reports**: View a breakdown of spending by category using interactive pie charts.
- **Profile Management**: Customize display name, email, and default currency (ZAR, USD, EUR).

## Getting Started

### Prerequisites

- Android Studio Flamingo or newer.
- Android SDK 24 (Nougat) or higher.
- Gradle 8.0 or newer.

### Installation

1. Clone the repository to your local machine.
2. Open the project in Android Studio.
3. Wait for the Gradle sync to complete.
4. Run the app on an emulator or physical device (API Level 24+).

## How to Use the App

### 1. Registration & Login
- When you first open the app, you will be directed to the **Login** screen.
- If you don't have an account, tap **"Register here"**.
- Fill in your details (Display Name, Username, Email, Password) and select your **Default Currency**.
- After registering, log in with your credentials.

### 2. Dashboard
- Upon logging in, you'll see the **Dashboard**.
- View your **Total Balance**, **Total Income**, and **Total Expenses**.
- The **Recent Transactions** list shows your last few entries.
- Tap the **Floating Action Button (+)** to quickly add a new transaction.

### 3. Adding Expenses
- On the **Add Expense** screen, enter the amount and description.
- Select a **Category** from the dropdown.
- Pick a **Date** and optional **Start/End Times**.
- Tap the **Camera** or **Gallery** icons to attach a receipt photo.
- Toggle the **Income** chip if the transaction is an income.
- Tap **Save** to add the transaction.

### 4. Setting Budgets
- Navigate to the **Budget** tab from the bottom navigation.
- Tap **"Set Monthly Budget"**.
- Use the sliders to set your **Minimum Spending Goal** and **Maximum Spending Goal**.
- Tap **Save Budget**. The budget screen will now show a progress bar indicating how much of your maximum goal you have spent.

### 5. Viewing Reports
- Navigate to the **Reports** tab.
- View a **Pie Chart** summarizing your spending across different categories.
- You can filter the report by selecting a **Start Date** and **End Date**.
- A detailed breakdown list is provided below the chart.

### 6. Profile & Settings
- Navigate to the **Profile** tab.
- Tap on your **Profile Picture** to update it.
- Tap **Edit Profile** to change your name or email.
- Tap **Default Currency** to switch between ZAR, USD, and EUR.
- Toggle **Notifications** or **Biometrics** (Note: these are UI placeholders for future implementation).
- Tap **Logout** to securely end your session and return to the login screen.

## Technical Details

- **Language**: Kotlin
- **UI Framework**: Android ViewBinding, XML Layouts
- **Database**: Room (Local SQLite) with KSP
- **Architecture**: MVVM (Model-ViewModel-View)
- **Networking**: N/A (Offline-first architecture)
- **External Libraries**: 
  - [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) for data visualization.
  - Navigation Component for fragment transitions.
  - Material Design Components for modern UI elements.

---
Developed for personal finance enthusiasts.
