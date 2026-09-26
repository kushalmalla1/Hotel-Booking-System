package com.hotel.service;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.exception.RecordNotFoundException;
import com.hotel.exception.RoomNotAvailableException;
import com.hotel.model.Booking;
import com.hotel.model.BookingStatus;
import com.hotel.model.Room;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Holds the booking business rules on top of the DAO layer:
 * overlap prevention, check-in/check-out workflow, billing.
 */
public class BookingService {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    public Booking createBooking(int guestId, int roomId, LocalDate checkIn, LocalDate checkOut)
            throws SQLException, RoomNotAvailableException, RecordNotFoundException {

        if (!checkOut.isAfter(checkIn)) {
            throw new RoomNotAvailableException("Check-out date must be after check-in date.");
        }

        // Confirm the room exists.
        Room room = roomDAO.getRoomById(roomId);

        if (bookingDAO.hasOverlap(roomId, checkIn, checkOut)) {
            throw new RoomNotAvailableException(
                    "Room " + room.getRoomNumber() + " is already booked for an overlapping date range.");
        }

        Booking booking = new Booking(0, guestId, roomId, checkIn, checkOut, BookingStatus.BOOKED);
        int id = bookingDAO.addBooking(booking);
        booking.setId(id);
        return booking;
    }

    public void checkIn(int bookingId) throws SQLException, RecordNotFoundException {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new IllegalStateException("Only BOOKED reservations can be checked in.");
        }
        bookingDAO.updateStatus(bookingId, BookingStatus.CHECKED_IN);
        roomDAO.setAvailability(booking.getRoomId(), false);
    }

    public double checkOut(int bookingId) throws SQLException, RecordNotFoundException {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Only CHECKED_IN reservations can be checked out.");
        }
        bookingDAO.updateStatus(bookingId, BookingStatus.CHECKED_OUT);
        roomDAO.setAvailability(booking.getRoomId(), true);

        Room room = roomDAO.getRoomById(booking.getRoomId());
        return booking.getNights() * room.getRate();
    }

    public void cancelBooking(int bookingId) throws SQLException, RecordNotFoundException {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new IllegalStateException("Cannot cancel a completed stay.");
        }
        bookingDAO.updateStatus(bookingId, BookingStatus.CANCELLED);
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            roomDAO.setAvailability(booking.getRoomId(), true);
        }
    }

    public List<Booking> getUpcomingBookings() throws SQLException {
        return bookingDAO.getUpcomingBookings();
    }

    public List<Booking> getCurrentOccupancy() throws SQLException {
        return bookingDAO.getCurrentlyCheckedIn();
    }

    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.getAllBookings();
    }
}
