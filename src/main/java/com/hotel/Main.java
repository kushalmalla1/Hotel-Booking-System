package com.hotel;

import com.hotel.dao.GuestDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.exception.RecordNotFoundException;
import com.hotel.exception.RoomNotAvailableException;
import com.hotel.model.*;
import com.hotel.service.BookingService;
import com.hotel.service.ReportService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final RoomDAO roomDAO = new RoomDAO();
    private static final GuestDAO guestDAO = new GuestDAO();
    private static final BookingService bookingService = new BookingService();
    private static final ReportService reportService = new ReportService();

    // In-memory cache of rooms keyed by id - demonstrates Map usage alongside DB persistence.
    private static final Map<Integer, Room> roomCache = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("=== Hotel / Room Booking Management System ===");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1 -> addRoom();
                    case 2 -> listRooms();
                    case 3 -> addGuest();
                    case 4 -> listGuests();
                    case 5 -> createBooking();
                    case 6 -> checkIn();
                    case 7 -> checkOut();
                    case 8 -> cancelBooking();
                    case 9 -> System.out.print(reportService.occupancyReport());
                    case 10 -> System.out.print(reportService.upcomingReservationsReport());
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option, try again.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (RecordNotFoundException | RoomNotAvailableException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Not allowed: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("""
                \n----- MENU -----
                1. Add Room
                2. List Rooms
                3. Add Guest
                4. List Guests
                5. Create Booking
                6. Check-In
                7. Check-Out
                8. Cancel Booking
                9. Occupancy Report
                10. Upcoming Reservations Report
                0. Exit
                """);
    }

    private static void addRoom() throws SQLException {
        String number = readLine("Room number: ");
        RoomType type = readRoomType();
        double rate = readDouble("Rate per night: ");
        Room room = new Room(0, number, type, rate, true);
        int id = roomDAO.addRoom(room);
        room.setId(id);
        roomCache.put(id, room);
        System.out.println("Room added: " + room);
    }

    private static void listRooms() throws SQLException {
        List<Room> rooms = roomDAO.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms yet.");
            return;
        }
        for (Room r : rooms) {
            roomCache.put(r.getId(), r);
            System.out.println(r);
        }
    }

    private static void addGuest() throws SQLException {
        String name = readLine("Name: ");
        String phone = readLine("Phone: ");
        String email = readLine("Email: ");
        String address = readLine("Address: ");
        Guest guest = new Guest(0, name, phone, email, address);
        int id = guestDAO.addGuest(guest);
        guest.setId(id);
        System.out.println("Guest added: " + guest);
    }

    private static void listGuests() throws SQLException {
        List<Guest> guests = guestDAO.getAllGuests();
        if (guests.isEmpty()) {
            System.out.println("No guests yet.");
            return;
        }
        guests.forEach(System.out::println);
    }

    private static void createBooking() throws SQLException, RoomNotAvailableException, RecordNotFoundException {
        int guestId = readInt("Guest ID: ");
        int roomId = readInt("Room ID: ");
        LocalDate checkIn = readDate("Check-in date (yyyy-mm-dd): ");
        LocalDate checkOut = readDate("Check-out date (yyyy-mm-dd): ");
        Booking booking = bookingService.createBooking(guestId, roomId, checkIn, checkOut);
        System.out.println("Booking created: " + booking);
    }

    private static void checkIn() throws SQLException, RecordNotFoundException {
        int bookingId = readInt("Booking ID: ");
        bookingService.checkIn(bookingId);
        System.out.println("Checked in successfully.");
    }

    private static void checkOut() throws SQLException, RecordNotFoundException {
        int bookingId = readInt("Booking ID: ");
        double total = bookingService.checkOut(bookingId);
        System.out.printf("Checked out. Total bill: Rs.%.2f%n", total);
    }

    private static void cancelBooking() throws SQLException, RecordNotFoundException {
        int bookingId = readInt("Booking ID: ");
        bookingService.cancelBooking(bookingId);
        System.out.println("Booking cancelled.");
    }

    // ---------- Input helpers with validation ----------

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Please use format yyyy-mm-dd.");
            }
        }
    }

    private static RoomType readRoomType() {
        while (true) {
            System.out.print("Room type (SINGLE/DOUBLE/SUITE): ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return RoomType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter SINGLE, DOUBLE, or SUITE.");
            }
        }
    }
}
