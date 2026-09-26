package com.hotel.model;

/**
 * Abstract base class representing any person in the system.
 * Extended by Guest and StaffMember (demonstrates abstraction + inheritance).
 */
public abstract class Person {
    private int id;
    private String name;
    private String phone;
    private String email;

    public Person(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Every subtype must describe its own role.
     * Demonstrates polymorphism when called through a Person reference.
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("#%d %s (%s) - %s | %s", id, name, getRole(), phone, email);
    }
}
