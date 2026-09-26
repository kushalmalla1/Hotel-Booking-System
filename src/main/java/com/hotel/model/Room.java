package com.hotel.model;

public class Room {
    private int id;
    private String roomNumber;
    private RoomType type;
    private double rate;
    private boolean available;

    public Room(int id, String roomNumber, RoomType type, double rate, boolean available) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.rate = rate;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return String.format("Room #%d [%s] %s - Rs.%.2f/night - %s",
                id, roomNumber, type, rate, available ? "Available" : "Occupied");
    }
}
