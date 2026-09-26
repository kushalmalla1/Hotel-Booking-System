# Hotel / Room Booking Management System

A terminal-based Java application for managing hotel rooms, guests, and
reservations, built with core Java, the Collections Framework, and JDBC
against a MySQL database.

## Features

- Add, update, list, and delete rooms (number, type, rate, availability)
- Register guests and list/search them
- Create bookings linking a guest to a room over a date range
- Prevents double-booking a room for overlapping dates
- Check-in / check-out workflow that updates room availability and calculates the bill
- Cancel a booking
- Occupancy report (currently checked-in guests)
- Upcoming reservations report
- Input validation and graceful handling of invalid input / SQL errors
- Custom exceptions: `RoomNotAvailableException`, `RecordNotFoundException`

## Technologies

- Java 11+ (core Java only, no frameworks)
- JDBC with `mysql-connector-java` 8.0.33
- MySQL 8
- Maven (build & dependency management)

## Project Structure

```
src/main/java/com/hotel/
├── model/       Person (abstract), Guest, StaffMember, Room, Booking, enums
├── dao/         RoomDAO, GuestDAO, BookingDAO - JDBC + PreparedStatement CRUD
├── service/     BookingService (business rules), ReportService
├── exception/   RoomNotAvailableException, RecordNotFoundException
├── util/        DBConnection
└── Main.java    Menu-driven console entry point
```

## Database Setup

1. Install MySQL and make sure it's running.
2. Run the schema script:
   ```
   mysql -u root -p < schema.sql
   ```
   This creates the `hotel_booking_db` database and the `rooms`, `guests`,
   `staff`, and `bookings` tables.
3. Copy the example config and fill in your own credentials:
   ```
   cp src/main/resources/db.properties.example src/main/resources/db.properties
   ```
   Edit `db.properties` with your MySQL username/password. This file is
   listed in `.gitignore` and is never committed.

   Alternatively, set environment variables `DB_URL`, `DB_USER`, `DB_PASSWORD`
   instead of using the properties file.

## Build & Run

Using Maven:
```
mvn clean package
java -jar target/hotel-booking-system-jar-with-dependencies.jar
```

## Screenshots

_(Add 2-3 screenshots here of the app running - menu, a booking being
created, and a report being printed.)_

## Known Limitations

- No billing/promo-code stretch goals implemented yet.
- No authentication / login mode - single-user console app.
