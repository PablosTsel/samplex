# Samplex

Samplex is an educational app designed to help students create personalized study plans and quizzes based on their specific learning needs. The app allows users to upload study materials, track their improvement, and prepare efficiently for exams.

## Features

- **User Authentication**: Secure login and registration system powered by Firebase Auth
- **Exam Roadmaps**: Create personalized study plans for each upcoming exam
- **PDF Processing**: Upload and analyze study materials to generate content
- **Emergency Exam Tomorrow**: Special feature for last-minute exam preparation
- **Offline Mode**: Study without an internet connection
- **Progress Tracking**: Monitor your improvement over time

## Architecture

The application follows the MVVM (Model-View-ViewModel) architecture pattern:

### Layers

1. **UI Layer (View)**
   - Located in `app/src/main/java/es/uc3m/android/samplex/ui/screens/`
   - Contains Jetpack Compose UI components
   - Each screen is in its own file for better organization

2. **ViewModel Layer**
   - Located in `app/src/main/java/es/uc3m/android/samplex/viewmodel/`
   - Handles business logic and UI state management
   - Uses Kotlin Flows for reactive state updates

3. **Repository Layer**
   - Located in `app/src/main/java/es/uc3m/android/samplex/repository/`
   - Manages data operations and abstracts data sources
   - Handles communication with Firebase services

4. **Model Layer**
   - Defined in the ViewModels and Repository files
   - Data classes that represent the domain entities

### Key Components

- **MainActivity**: Entry point that manages navigation between authenticated and non-authenticated states
- **AuthScreen**: Handles user login and registration
- **HomeScreen**: The main interface after authentication, showing exam roadmaps and options
- **AuthViewModel**: Manages authentication state and operations
- **ExamViewModel**: Handles exam creation and management
- **UserRepository**: Manages user profile data in Firestore

## Firebase Integration

The app uses several Firebase services:

- **Firebase Authentication**: For user account management
- **Cloud Firestore**: For storing user profiles and exam data
- **Firebase Storage**: For storing uploaded study materials

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Connect the app to your Firebase project by replacing the `google-services.json` file
4. Build and run the application

## Future Development

Planned features include:

- Google Sign-In integration
- PDF content extraction with AI analysis
- Comprehensive quiz generation system
- Achievement badges and gamification elements
- Advanced analytics for tracking study progress 