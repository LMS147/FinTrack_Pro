# FinTrack Pro

FinTrack Pro is a personal finance management application for Android designed to help users track their expenses, manage budgets, and visualize their spending habits. The app supports multi-user authentication with strict data isolation and includes innovative features like Shared Wallets for collaborative expense tracking.

## Features

- **User Authentication**: Secure login and registration system with password hashing.
- **Multi-User Support**: Multiple users can use the app on the same device without seeing each other's data.
- **Dashboard**: A quick overview of total balance, income, and recent transactions, automatically converted to your preferred currency.
- **Expense Tracking**: Add and manage transactions with categories, dates, and descriptions.
- **Shared Wallets**: Create or join collaborative wallets to track shared expenses with other users via invite codes.
- **Currency Conversion**: Support for multiple currencies (ZAR, USD, EUR, etc.) with real-time conversion for balances and reports.
- **Receipt Attachments**: Capture or select photos of receipts to attach to expenses.
- **Budget Management**: Set monthly spending goals and monitor progress with real-time progress bars.
- **Visual Reports**: View a breakdown of spending by category using interactive pie charts.
- **Profile Management**: Customize display name, email, and preferred currency (Default: ZAR).

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
- Fill in your details (Full Name, Email, Password). The app defaults to **ZAR** as your currency.
- After registering, log in with your credentials.

### 2. Dashboard
- Upon logging in, you'll see the **Dashboard**.
- View your **Total Balance**, **Monthly Income**, and **Monthly Expenses**. 
- All amounts on the dashboard are automatically converted to your preferred currency selected in Settings.
- Tap the **Floating Action Button (+)** to quickly add a new transaction.

### 3. Shared Wallets
- Navigate to the **Shared Wallets** section.
- **Create** a new wallet and share the generated **Invite Code** with friends or family.
- **Join** a wallet by entering an invite code shared with you.
- Balances in shared wallets are tracked collectively and visible to all members.

### 4. Adding Accounts & Expenses
- Go to the **Accounts** tab to manage different bank accounts or cash wallets.
- On the **Add Expense** screen, enter the amount and description.
- Select a **Category** from the dropdown.
- Pick a **Date** and optional **Start/End Times**.
- Toggle the **Income** chip if the transaction is an income.

### 5. Setting Budgets
- Navigate to the **Budget** tab from the bottom navigation.
- Tap **"Set Monthly Budget"**.
- Set your spending goals and tap **Save**. The progress bar will indicate how much of your budget is remaining.

### 6. Viewing Reports
- Navigate to the **Reports** tab.
- View a **Pie Chart** summarizing your spending across different categories.
- Filter by date range to see specific spending habits.

### 7. Profile & Settings
- Navigate to the **Settings** screen (accessible via the profile icon on the Dashboard).
- Tap **Select Currency** to change your preferred display currency (ZAR, USD, EUR, etc.).
- Balances across the app will update immediately to reflect the new currency conversion.
- Tap **Logout** to securely end your session.

## Technical Details

- **Language**: Kotlin
- **UI Framework**: Android ViewBinding, XML Layouts
- **Database**: Room (Local SQLite) with KSP
- **Architecture**: MVVM (Model-ViewModel-View)
- **Networking**: N/A (Offline-first architecture with mock exchange rates)
- **External Libraries**: 
  - [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) for data visualization.
  - [Navigation Component](https://developer.android.com/guide/navigation) for fragment transitions.
  - [Material Design Components](https://material.io/develop/android) for modern UI elements.
  - [Coil](https://coil-kt.github.io/coil/) for image loading.

---
Developed for personal finance enthusiasts.
