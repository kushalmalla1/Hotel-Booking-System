package com.hotel.dao;

import com.hotel.exception.RecordNotFoundException;
import com.hotel.model.Guest;
import com.hotel.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public int addGuest(Guest guest) throws SQLException {
        String sql = "INSERT INTO guests (name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, guest.getName());
            ps.setString(2, guest.getPhone());
            ps.setString(3, guest.getEmail());
            ps.setString(4, guest.getAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public Guest getGuestById(int id) throws SQLException, RecordNotFoundException {
        String sql = "SELECT * FROM guests WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        throw new RecordNotFoundException("Guest with id " + id + " not found.");
    }

    public List<Guest> getAllGuests() throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) guests.add(mapRow(rs));
        }
        return guests;
    }

    public List<Guest> searchByName(String name) throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests WHERE name LIKE ? ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) guests.add(mapRow(rs));
            }
        }
        return guests;
    }

    public boolean updateGuest(Guest guest) throws SQLException {
        String sql = "UPDATE guests SET name=?, phone=?, email=?, address=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guest.getName());
            ps.setString(2, guest.getPhone());
            ps.setString(3, guest.getEmail());
            ps.setString(4, guest.getAddress());
            ps.setInt(5, guest.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteGuest(int id) throws SQLException {
        String sql = "DELETE FROM guests WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        return new Guest(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address")
        );
    }
}
