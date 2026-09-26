package com.hotel.service;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.GuestDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.exception.RecordNotFoundException;
import com.hotel.model.Booking;
import com.hotel.model.Guest;
import com.hotel.model.Room;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds human-readable console reports for occupancy and upcoming reservations.
 * Demonstrates Collections Framework usage: sorting via Comparator, filtering, iteration.
 */
public class ReportService {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final GuestDAO guestDAO = new GuestDAO();

    public String occupancyReport() throws SQLException {
        List<Booking> checkedIn = bookingDAO.getCurrentlyCheckedIn();
        StringBuilder sb = new StringBuilder("=== Current Occupancy ===\n");

        if (checkedIn.isEmpty()) {
            sb.append("No rooms currently occupied.\n");
            return sb.toString();
        }

        // Sort by room id for a stable, readable report.
        List<Booking> sorted = checkedIn.stream()
                .sorted(Comparator.comparingInt(Booking::getRoomId))
                .collect(Collectors.toList());

        for (Booking b : sorted) {
            try {
                Room room = roomDAO.getRoomById(b.getRoomId());
                Guest guest = guestDAO.getGuestById(b.getGuestId());
                sb.append(String.format("Room %s (%s) - %s - until %s%n",
                        room.getRoomNumber(), room.getType(), guest.getName(), b.getCheckOutDate()));
            } catch (RecordNotFoundException e) {
                sb.append("Booking #").append(b.getId()).append(": ").append(e.getMessage()).append("\n");
            }
        }
        return sb.toString();
    }

    public String upcomingReservationsReport() throws SQLException {
        List<Booking> upcoming = bookingDAO.getUpcomingBookings();
        StringBuilder sb = new StringBuilder("=== Upcoming Reservations ===\n");

        if (upcoming.isEmpty()) {
            sb.append("No upcoming reservations.\n");
            return sb.toString();
        }

        for (Booking b : upcoming) {
            try {
                Room room = roomDAO.getRoomById(b.getRoomId());
                Guest guest = guestDAO.getGuestById(b.getGuestId());
                sb.append(String.format("%s: Room %s - %s -> %s%n",
                        guest.getName(), room.getRoomNumber(), b.getCheckInDate(), b.getCheckOutDate()));
            } catch (RecordNotFoundException e) {
                sb.append("Booking #").append(b.getId()).append(": ").append(e.getMessage()).append("\n");
            }
        }
        return sb.toString();
    }
}
