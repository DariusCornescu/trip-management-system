# 🏖️ Trip Booking System

A comprehensive travel booking application built with React (frontend) and Spring Boot (backend), allowing users to browse and book trips while providing administrators with full system management capabilities.
- ![image](https://github.com/user-attachments/assets/777fe34e-b3a8-482e-a661-30ef06603f8e)
- ![image](https://github.com/user-attachments/assets/d37e798c-5805-4b56-8426-b6740b1851e6)
- ![image](https://github.com/user-attachments/assets/6b88ab63-4615-45f2-8d5d-eba2369ef4f6)

## 📋 Table of Contents
- [Features](#-features)
- [Technologies Used](#-technologies-used)
- [System Architecture](#-system-architecture)
- [Installation & Setup](#-installation--setup)
- [Usage](#-usage)
- [API Documentation](#-api-documentation)
- [Troubleshooting](#-troubleshooting)

## 🚀 Features

### For Regular Users
- **Secure Authentication** - Login with username and password
- **Trip Search & Browse** - Filter trips by destination and time
- **Online Reservations** - Book tickets with automatic validation
- **Intuitive Interface** - Responsive and user-friendly design
- **Real-time Updates** - Live seat availability and pricing

### For Administrators
- **Trip Management** - Full CRUD operations for trips (Create, Read, Update, Delete)
- **User Management** - Complete user account administration
- **Admin Dashboard** - Centralized system overview
- **Reservation Monitoring** - Track all bookings and cancellations
- **Search & Filter** - Advanced search capabilities

### Technical Features
- **Real-time Updates** - WebSocket integration for live synchronization
- **Data Validation** - Comprehensive validation on both frontend and backend
- **Error Handling** - Clear error messages and graceful failure handling
- **Responsive Design** - Compatible with all devices and screen sizes
- **RESTful API** - Clean and well-documented API endpoints

## 🛠️ Technologies Used

### Frontend
- **React 18** - Main framework with functional components and hooks
- **Tailwind CSS** - Utility-first CSS framework for styling
- **JavaScript ES6+** - Modern JavaScript features
- **WebSocket** - Real-time communication
- **Fetch API** - HTTP requests and data fetching
- **React Context** - State management for authentication

### Backend
- **Spring Boot 2.7+** - Main Java framework
- **Spring MVC** - REST API development
- **Spring Security** - Authentication and authorization
- **Hibernate/JPA** - Object-Relational Mapping
- **SQLite** - Lightweight database (easily configurable)
- **Maven** - Dependency management and build tool
- **SLF4J + Logback** - Logging framework

### Additional Technologies
- **WebSocket** - Bidirectional communication
- **CORS** - Cross-origin resource sharing
- **BCrypt** - Password hashing
- **Git** - Version control

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENT (React)                         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │   Login     │ │ Trip List   │ │   Admin Panel       │   │
│  │ Component   │ │ Component   │ │   Component         │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │ Reservation │ │   Context   │ │    WebSocket        │   │
│  │   Modal     │ │   Provider  │ │    Client           │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP/WebSocket
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   SERVER (Spring Boot)                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │    User     │ │    Trip     │ │    Reservation      │   │
│  │ Controller  │ │ Controller  │ │    Controller       │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │   Service   │ │ Repository  │ │    WebSocket        │   │
│  │   Layer     │ │   Layer     │ │    Handler          │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    DATABASE (SQLite)                       │
│     ┌─────────┐ ┌─────────┐ ┌─────────────┐ ┌─────────┐    │
│     │  Users  │ │  Trips  │ │Reservations │ │Customers│    │
│     │  Table  │ │  Table  │ │   Table     │ │  Table  │    │
│     └─────────┘ └─────────┘ └─────────────┘ └─────────┘    │
└─────────────────────────────────────────────────────────────┘
```

## 🔧 Installation & Setup

### Prerequisites
- **Java 11+** installed
- **Node.js 16+** and npm installed
- **Maven 3.6+** installed
- **Git** for cloning the repository

### 1. Clone the Repository
```bash
git clone https://github.com/DariusCornescu/trip-management-system.git
cd trip-booking-system
```

### 2. Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will run on `http://localhost:8080`

### 3. Frontend Setup

```bash
cd frontend
npm install
npm start
```

The frontend will run on `http://localhost:3000`

### 4. Verify Installation
- Navigate to `http://localhost:3000`
- Login with admin credentials
- Verify you can create trips and make reservations

## 📱 Usage

### For Regular Users

1. **Authentication**
   - Access the application
   - Enter username and password
   - Click "Sign In"

2. **Browsing Trips**
   - View available trips on the main dashboard
   - Use the search bar to filter by destination
   - Check prices and available seats

3. **Making a Reservation**
   - Click "Book Now" on desired trip
   - Fill in personal information
   - Select number of tickets
   - Confirm the reservation

### For Administrators

1. **Trip Management**
   - Access "Trip Management" tab
   - Click "➕ Create New Trip" for new trips
   - Edit existing trips with "Edit" button
   - Delete trips with "Delete" button

2. **User Management**
   - Access "User Management" tab
   - Add new users
   - Edit user information
   - Delete user accounts (except Admin)

### Advanced Features

- **Advanced Search**: Filter by destination and time range
- **Live Updates**: Changes reflect instantly across all sessions
- **Auto Validation**: System prevents overbooking automatically
- **Responsive Design**: Works seamlessly on all devices

## 📚 API Documentation

### Authentication Endpoints

#### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "your_password"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "userId": 1,
  "username": "admin",
  "isAdmin": true
}
```

### Trip Management Endpoints

#### Get All Trips
```bash
GET /api/trips

Response:
[
  {
    "id": 1,
    "attractionName": "Paris",
    "transportCompany": "AirFrance",
    "departureTime": "10:00 - 2025-07-15",
    "price": 299.99,
    "availableSeats": 48
  }
]
```

#### Create New Trip
```bash
POST /api/trips/{id}
Content-Type: application/json

{
  "attractionName": "Paris",
  "transportCompany": "AirFrance", 
  "departureTime": "10:00 - 2025-07-15",
  "price": 299.99,
  "availableSeats": 50
}
```

#### Update Trip
```bash
PUT /api/trips/{id}
Content-Type: application/json

{
  "attractionName": "Paris - Updated",
  "transportCompany": "AirFrance",
  "departureTime": "11:00 - 2025-07-15", 
  "price": 349.99,
  "availableSeats": 45
}
```

#### Delete Trip
```bash
DELETE /api/trips/{id}

Response:
{
  "message": "Trip deleted successfully with id: 1"
}
```

#### Search Trips
```bash
# Search by attraction
GET /api/trips/search/attraction?attraction=Paris

# Search by attraction and time
GET /api/trips/search/time?attraction=Paris&startTime=09:00&endTime=12:00
```

### Reservation Endpoints

#### Create Reservation
```bash
POST /api/reservations
Content-Type: application/json

{
  "tripId": 1,
  "customerName": "John Doe",
  "customerPhone": "+1234567890",
  "tickets": 2
}

Response:
{
  "success": true,
  "message": "Reservation created successfully",
  "reservation": {
    "id": 123,
    "tripId": 1,
    "customerId": 456,
    "tickets": 2,
    "customerName": "John Doe",
    "tripName": "Paris"
  }
}
```

#### Get Reservations by Trip
```bash
GET /api/reservations/trip/{tripId}

Response:
[
  {
    "id": 123,
    "tripId": 1,
    "customerId": 456,
    "tickets": 2,
    "customerName": "John Doe",
    "tripName": "Paris"
  }
]
```

#### Cancel Reservation
```bash
DELETE /api/reservations/{id}

Response:
{
  "message": "Reservation cancelled successfully"
}
```

### User Management Endpoints (Admin Only)

#### Get All Users
```bash
GET /api/auth/users

Response:
[
  {
    "id": 1,
    "username": "admin",
    "password": "$2a$10$..."
  },
  {
    "id": 2,
    "username": "user1",
    "password": "$2a$10$..."
  }
]
```

#### Create User
```bash
POST /api/auth/users
Content-Type: application/json

{
  "id": 123,
  "username": "newuser",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "User created successfully",
  "user": {
    "id": 123,
    "username": "newuser"
  }
}
```

#### Update User
```bash
PUT /api/auth/users/{id}
Content-Type: application/json

{
  "username": "updateduser",
  "password": "newpassword123"
}
```

#### Delete User
```bash
DELETE /api/auth/users/{id}

Response:
{
  "message": "User deleted successfully"
}
```

#### Search Users
```bash
GET /api/auth/users/search?username=john
```

### Response Format

#### Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... }
}
```

#### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "error": "Technical details"
}
```

## 🐛 Troubleshooting

### Common Issues and Solutions

#### Backend Won't Start
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Clean and reinstall dependencies
mvn clean install

# Check if port 8080 is available
netstat -an | grep 8080
```

#### Frontend Won't Start
```bash
# Check Node.js version
node -version

# Clear npm cache
npm cache clean --force

# Remove node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### Database Connection Issues
- Verify database file permissions
- Check path in `config.properties`
- Ensure SQLite is properly configured
- Recreate database if corrupted

#### CORS Errors
- Verify CORS configuration in `TripApiServer.java`
- Check allowed origins in configuration
- Ensure frontend runs on expected port (3000)

#### Authentication Problems
- Verify credentials in environment variables
- Check if admin user exists in database
- Clear browser cache and cookies
- Verify backend authentication endpoint

#### WebSocket Connection Issues
- Check if WebSocket endpoint is configured
- Verify port availability
- Check firewall settings
- Ensure proper WebSocket handshake

### Performance Issues
- Check database indexes
- Monitor memory usage
- Review SQL query performance
- Enable database query logging

### Debug Mode
Enable debug logging by adding to `application.properties`:
```properties
logging.level.com.darius.project=DEBUG
logging.level.org.springframework.web=DEBUG
```

## 🤝 Contributing

### How to Contribute

1. **Fork the Repository**
   ```bash
   git clone https://github.com/DariusCornescu/trip-management-system.git
   ```

2. **Create a Feature Branch**
   ```bash
   git checkout -b feature/amazing-new-feature
   ```

3. **Make Your Changes**
   - Follow coding standards
   - Add tests for new features
   - Update documentation

4. **Test Thoroughly**
   - Test all CRUD operations
   - Verify responsive design
   - Check error handling
   - Validate security measures

5. **Commit Changes**
   ```bash
   git commit -m "Add: Amazing new feature description"
   ```

6. **Push to Your Branch**
   ```bash
   git push origin feature/amazing-new-feature
   ```

7. **Create Pull Request**
   - Provide clear description
   - Include screenshots if applicable
   - Reference related issues

### Coding Standards

#### Java (Backend)
- Use camelCase for variables and methods
- Use PascalCase for classes
- Document public methods with JavaDoc
- Handle exceptions appropriately
- Follow SOLID principles
- Use meaningful variable names

Example:
```java
/**
 * Creates a new trip reservation
 * @param request The reservation request details
 * @return ResponseEntity with reservation result
 */
@PostMapping
public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
    // Implementation
}
```

#### JavaScript (Frontend)
- Use const/let instead of var
- Prefer functional components with hooks
- Use destructuring for props
- Follow descriptive naming conventions
- Use arrow functions consistently

Example:
```javascript
const TripCard = ({ trip, onEdit, onDelete, onMakeReservation, isAdmin }) => {
    const handleBooking = () => {
        onMakeReservation(trip);
    };

    return (
        <div className="trip-card">
            {/* Component content */}
        </div>
    );
};
```

### Testing Guidelines
- Write unit tests for new features
- Test API endpoints with Postman
- Verify cross-browser compatibility
- Test responsive design on multiple devices
- Validate accessibility standards

### Code Review Process
- All PRs require at least one review
- Ensure tests pass before merging
- Verify security implications
- Check performance impact
- Update documentation as needed

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

** Cornescu Darius** - *Lead Developer*

## 🙏 Acknowledgments

- Spring Boot Community for the excellent framework
- React Team for the powerful React library
- Tailwind CSS for the utility-first CSS framework
- SQLite for the lightweight database solution
- All contributors who helped improve this project


