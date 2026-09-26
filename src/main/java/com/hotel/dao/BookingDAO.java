package com.hotel.dao;

import com.hotel.exception.RecordNotFoundException;
import com.hotel.model.Booking;
import com.hotel.model.BookingStatus;
import com.hotel.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public int addBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (guest_id, room_id, check_in, check_out, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getGuestId());
            ps.setInt(2, booking.getRoomId());
            ps.setDate(3, Date.valueOf(booking.getCheckInDate()));
            ps.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            ps.setString(5, booking.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public Booking getBookingById(int id) throws SQLException, RecordNotFoundException {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        throw new RecordNotFoundException("Booking with id " + id + " not found.");
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) bookings.add(mapRow(rs));
        }
        return bookings;
    }

    /**
     * Checks whether the given room has any active (BOOKED or CHECKED_IN)
     * booking whose date range overlaps [checkIn, checkOut).
     * Used by the service layer to prevent double-booking.
     */
    public boolean hasOverlap(int roomId, LocalDate checkIn, LocalDate checkOut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings " +
                "WHERE room_id = ? AND status IN ('BOOKED','CHECKED_IN') " +
                "AND check_in < ? AND check_out > ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, Date.valueOf(checkOut));
            ps.setDate(3, Date.valueOf(checkIn));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Booking> getUpcomingBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = 'BOOKED' AND check_in >= CURDATE() ORDER BY check_in";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) bookings.add(mapRow(rs));
        }
        return bookings;
    }

    public List<Booking> getCurrentlyCheckedIn() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = 'CHECKED_IN' ORDER BY check_in";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) bookings.add(mapRow(rs));
        }
        return bookings;
    }

    public boolean updateStatus(int bookingId, BookingStatus status) throws SQLException {
        String sql = "UPDATE bookings SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET guest_id=?, room_id=?, check_in=?, check_out=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, booking.getGuestId());
            ps.setInt(2, booking.getRoomId());
            ps.setDate(3, Date.valueOf(booking.getCheckInDate()));
            ps.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            ps.setString(5, booking.getStatus().name());
            ps.setInt(6, booking.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteBooking(int id) throws SQLException {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("id"),
                rs.getInt("guest_id"),
                rs.getInt("room_id"),
                rs.getDate("check_in").toLocalDate(),
                rs.getDate("check_out").toLocalDate(),
                BookingStatus.valueOf(rs.getString("status"))
        );
    }
}
