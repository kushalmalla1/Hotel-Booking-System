package com.hotel.dao;

import com.hotel.exception.RecordNotFoundException;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public int addRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, type, rate, available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getType().name());
            ps.setDouble(3, room.getRate());
            ps.setBoolean(4, room.isAvailable());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public Room getRoomById(int id) throws SQLException, RecordNotFoundException {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        throw new RecordNotFoundException("Room with id " + id + " not found.");
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rooms.add(mapRow(rs));
        }
        return rooms;
    }

    public List<Room> searchByType(RoomType type) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE type = ? ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) rooms.add(mapRow(rs));
            }
        }
        return rooms;
    }

    public boolean updateRoom(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_number=?, type=?, rate=?, available=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getType().name());
            ps.setDouble(3, room.getRate());
            ps.setBoolean(4, room.isAvailable());
            ps.setInt(5, room.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setAvailability(int roomId, boolean available) throws SQLException {
        String sql = "UPDATE rooms SET available=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteRoom(int id) throws SQLException {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("id"),
                rs.getString("room_number"),
                RoomType.valueOf(rs.getString("type")),
                rs.getDouble("rate"),
                rs.getBoolean("available")
        );
    }
}
