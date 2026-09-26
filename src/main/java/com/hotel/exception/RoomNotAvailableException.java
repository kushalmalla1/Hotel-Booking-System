package com.hotel.exception;

/**
 * Thrown when a booking is attempted for a room that is already
 * booked for overlapping dates, or otherwise unavailable.
 */
public class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}
