package com.hotel.exception;

/**
 * Thrown when a requested Room, Guest, or Booking record does not exist.
 */
public class RecordNotFoundException extends Exception {
    public RecordNotFoundException(String message) {
        super(message);
    }
}
