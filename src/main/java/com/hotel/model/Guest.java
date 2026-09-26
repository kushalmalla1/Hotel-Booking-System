package com.hotel.model;

public class Guest extends Person {
    private String address;

    public Guest(int id, String name, String phone, String email, String address) {
        super(id, name, phone, email);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getRole() {
        return "Guest";
    }
}
