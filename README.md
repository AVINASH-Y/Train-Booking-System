# Train Booking System

A command-line Java application for booking train tickets with user authentication, train search, seat selection, and booking management.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)
- [Dependencies](#dependencies)
- [Requirements](#requirements)
- [Installation & Setup](#installation--setup)
- [How to Run](#how-to-run)
- [Architecture](#architecture)
- [Data Storage](#data-storage)
- [Usage Guide](#usage-guide)
- [Security Features](#security-features)

## 🎯 Overview

The Train Booking System is a Java-based console application that allows users to:
- Create accounts and authenticate securely
- Search for trains between stations (bidirectional search)
- View available seats in a grid layout
- Book and cancel train tickets
- Manage their booking history

The application uses JSON files for data persistence, making it lightweight and easy to set up without requiring a database.

## ✨ Features

### User Management
- **User Registration**: Create new accounts with username and password
- **Secure Authentication**: Password hashing using BCrypt
- **Session Management**: Login/logout functionality

### Train Operations
- **Bidirectional Search**: Search trains in both forward and reverse directions
- **Route Display**: View complete train routes with station times
- **Seat Availability**: Real-time seat availability display
- **Seat Selection**: Interactive seat booking with visual grid

### Booking Management
- **Ticket Booking**: Book seats on selected trains
- **Booking History**: View all past and current bookings
- **Booking Cancellation**: Cancel existing bookings by ticket ID or number

### Additional Features
- **View-Only Mode**: Browse trains without logging in
- **Input Validation**: Comprehensive validation for all user inputs
- **Error Handling**: Graceful error handling with user-friendly messages

## 📁 Project Structure

```
Train-Booking-System/
├── app/
│   ├── build.gradle                    # Gradle build configuration
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── ticket/
│   │   │   │       └── booking/
│   │   │   │           ├── App.java                    # Main application entry point
│   │   │   │           ├── entities/                  # Domain models
│   │   │   │           │   ├── User.java              # User entity
│   │   │   │           │   ├── Train.java            # Train entity
│   │   │   │           │   └── Ticket.java           # Ticket entity
│   │   │   │           ├── service/                   # Business logic layer
│   │   │   │           │   ├── TrainService.java     # Train operations
│   │   │   │           │   └── UserBookingService.java # User & booking operations
│   │   │   │           ├── util/                      # Utility classes
│   │   │   │           │   └── UserServiceUtil.java  # Password hashing utilities
│   │   │   │           └── localDB/                   # JSON data storage
│   │   │   │               ├── trains.json            # Train data
│   │   │   │               └── users.json             # User data
│   │   │   └── resources/
│   │   └── test/
│   │       └── java/
│   │           └── ticket/
│   │               └── booking/
│   │                   └── AppTest.java               # Unit tests
│   └── build/                          # Compiled classes (generated)
├── gradle/
│   ├── libs.versions.toml             # Dependency version catalog
│   └── wrapper/                        # Gradle wrapper files
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.properties                  # Gradle configuration
├── settings.gradle                    # Gradle project settings
├── gradlew                            # Gradle wrapper (Unix/Mac)
├── gradlew.bat                        # Gradle wrapper (Windows)
└── README.md                          # This file
```

## 🛠 Technology Stack

- **Language**: Java 8
- **Build Tool**: Gradle 8.5
- **JSON Processing**: Jackson Databind 2.12.6
- **Password Hashing**: jBCrypt 0.4
- **Utilities**: Google Guava 32.1.2-jre
- **Code Generation**: Lombok 1.18.22
- **Testing**: JUnit 4.13.2

## 📦 Dependencies

### Core Dependencies

1. **Jackson Databind** (`com.fasterxml.jackson.core:jackson-databind:2.12.6`)
   - Purpose: JSON serialization/deserialization for data persistence
   - Used for: Reading/writing train and user data to JSON files

2. **jBCrypt** (`org.mindrot:jbcrypt:0.4`)
   - Purpose: Secure password hashing using BCrypt algorithm
   - Used for: Hashing passwords during registration and verifying during login

3. **Lombok** (`org.projectlombok:lombok:1.18.22`)
   - Purpose: Reduces boilerplate code with annotations
   - Used for: Auto-generating getters, setters, and builders

4. **Google Guava** (`com.google.guava:guava:32.1.2-jre`)
   - Purpose: Core Java utilities and collections
   - Used for: Additional utility functions and collections

### Test Dependencies

- **JUnit 4.13.2**: Unit testing framework

## 📋 Requirements

### System Requirements
- **Java Development Kit (JDK)**: Version 8 or higher
- **Gradle**: Version 8.5+ (included via wrapper, no separate installation needed)
- **Operating System**: Windows, macOS, or Linux

### Java Version
The project is configured to use Java 8 (JavaLanguageVersion.of(8)) as specified in `build.gradle`.

### File System
- Read/write permissions for the project directory
- The application creates/updates JSON files in `app/src/main/java/ticket/booking/localDB/`

## 🚀 Installation & Setup

### Prerequisites Check

1. **Verify Java Installation**:
   ```bash
   java -version
   ```
   Should show Java 8 or higher.

2. **Verify Gradle Wrapper** (optional):
   ```bash
   ./gradlew --version
   ```

### Setup Steps

1. **Clone or Download the Project**:
   ```bash
   cd Train-Booking-System
   ```

2. **Ensure JSON Data Files Exist**:
   - `app/src/main/java/ticket/booking/localDB/trains.json` - Contains train data
   - `app/src/main/java/ticket/booking/localDB/users.json` - Will be created automatically on first run

3. **Build the Project** (optional, for verification):
   ```bash
   ./gradlew build
   ```

## ▶️ How to Run

### Using Gradle Wrapper (Recommended)

**On Unix/macOS:**
```bash
./gradlew run
```

**On Windows:**
```bash
gradlew.bat run
```

### Using Executable JAR

1. **Build the JAR**:
   ```bash
   ./gradlew build
   ```

2. **Run the JAR**:
   ```bash
   java -jar app/build/libs/app.jar
   ```

### Direct Java Execution

1. **Compile the project**:
   ```bash
   ./gradlew compileJava
   ```

2. **Run the main class**:
   ```bash
   java -cp "app/build/classes/java/main:app/build/libs/*" ticket.booking.App
   ```

## 🏗 Architecture

### Design Pattern
The application follows a **layered architecture**:

1. **Presentation Layer**: `App.java` - Handles user interaction via console
2. **Service Layer**: 
   - `UserBookingService.java` - User authentication and booking operations
   - `TrainService.java` - Train search and management
3. **Entity Layer**: `User.java`, `Train.java`, `Ticket.java` - Domain models
4. **Utility Layer**: `UserServiceUtil.java` - Helper functions (password hashing)
5. **Data Layer**: JSON files for persistence

### Key Components

#### 1. App.java (Main Application)
- Entry point of the application
- Manages console-based menu system
- Handles user input and navigation
- Coordinates between services

#### 2. UserBookingService.java
- User registration and authentication
- Booking creation and cancellation
- User ticket management
- Delegates train operations to TrainService

#### 3. TrainService.java
- Train data loading from JSON
- Bidirectional train search (forward/reverse)
- Train direction detection
- Seat management

#### 4. Entities
- **User**: Stores user credentials, hashed password, and booked tickets
- **Train**: Contains train details, route, stations, times, and seat matrix
- **Ticket**: Represents a booking with source, destination, date, and train reference

## 💾 Data Storage

### JSON File Structure

#### trains.json
Stores train information in JSON array format:
```json
[
  {
    "train_id": "RAJDHANI001",
    "train_no": "12301",
    "seats": [[0,0,0,0,0,0], [0,0,0,0,0,0], ...],
    "station_times": {
      "bangalore": "08:00:00",
      "chennai": "12:30:00"
    },
    "stations": ["bangalore", "chennai", "hyderabad", "mumbai"]
  }
]
```

- **seats**: 2D array where `0` = available, `1` = booked
- **station_times**: Map of station names to arrival/departure times
- **stations**: Ordered list of stations in the route

#### users.json
Stores user accounts and bookings:
```json
[
  {
    "name": "username",
    "hashed_password": "$2a$10$...",
    "tickets_booked": [...],
    "user_id": "uuid"
  }
]
```

### Data Persistence
- Data is automatically saved after:
  - User registration
  - Ticket booking
  - Booking cancellation
  - Train seat updates

## 📖 Usage Guide

### Starting the Application

1. Run the application using `./gradlew run`
2. You'll see the welcome menu

### Menu Options (Not Logged In)

1. **Sign Up**: Create a new account
   - Enter username and password
   - Password is automatically hashed and stored securely

2. **Login**: Authenticate with existing credentials
   - Enter username and password
   - Session is maintained until logout

3. **Search Trains (View Only)**: Browse available trains
   - Enter source and destination stations
   - View train details without booking
   - Login required to book tickets

4. **Exit**: Close the application

### Menu Options (Logged In)

1. **Logout**: End current session

2. **Fetch Bookings**: View all your booked tickets
   - Displays ticket ID, route, date, and train information

3. **Search Trains**: Find trains between stations
   - Enter source and destination
   - View detailed train information including:
     - Train ID and number
     - Full route with station times
     - Available seats count
   - Select a train for booking

4. **Book a Seat**: Reserve a seat on selected train
   - View seat layout (0 = available, 1 = booked)
   - Enter row and column numbers (1-indexed)
   - Ticket is generated with unique ID

5. **Cancel Booking**: Remove an existing booking
   - View your bookings
   - Enter ticket number (1-N) or ticket ID
   - Seat is freed and ticket removed

6. **Exit**: Close the application

### Example Workflow

```
1. Sign up with username "john" and password "password123"
2. Login with your credentials
3. Search trains from "bangalore" to "chennai"
4. Select a train from the results
5. Book a seat (e.g., Row 1, Column 1)
6. View your bookings to see the ticket
7. Cancel booking if needed
8. Logout
```

### Station Names
- Use lowercase station names for best results
- The system searches case-insensitively
- Example stations: bangalore, chennai, mumbai, delhi, hyderabad, etc.

## 🔒 Security Features

### Password Security
- **BCrypt Hashing**: All passwords are hashed using BCrypt before storage
- **No Plaintext Storage**: Passwords are never stored in plaintext
- **Secure Comparison**: Password verification uses constant-time comparison

### Input Validation
- Username and password cannot be empty
- Source and destination stations are validated
- Seat selection bounds checking
- Ticket ID validation for cancellations

### Data Integrity
- User authentication required for sensitive operations
- Session-based access control
- User can only access their own bookings

## 🐛 Troubleshooting

### Common Issues

1. **"Cannot find trains.json file"**
   - Ensure `app/src/main/java/ticket/booking/localDB/trains.json` exists
   - Check file permissions

2. **"Error: Unable to initialize the system"**
   - Verify JSON file format is valid
   - Check file paths are correct

3. **Build Failures**
   - Ensure Java 8+ is installed
   - Run `./gradlew clean build` to rebuild

4. **Gradle Wrapper Issues**
   - On Unix/Mac: `chmod +x gradlew`
   - Verify internet connection for first-time dependency download

## 📝 Notes

- The application uses a file-based JSON storage system (no database required)
- All data persists between application runs
- Train data is pre-populated in `trains.json`
- User data is created dynamically as users register
- Seat bookings are immediately reflected in the train data

## 🔮 Future Enhancements

Potential improvements for future versions:
- Database integration (MySQL, PostgreSQL)
- REST API for web/mobile clients
- Payment integration
- Email notifications
- Admin panel for train management
- Multi-threading for concurrent bookings
- Advanced search filters (date, time, train type)

## 👤 Author

AVINASH YADDLAPALLI

---

**Happy Booking! 🚂**
