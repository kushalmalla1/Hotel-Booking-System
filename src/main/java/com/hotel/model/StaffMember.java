package com.hotel.model;

public class StaffMember extends Person {
    private String position;

    public StaffMember(int id, String name, String phone, String email, String position) {
        super(id, name, phone, email);
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String getRole() {
        return "Staff - " + position;
    }
}
