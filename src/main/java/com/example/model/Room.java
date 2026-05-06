package com.example.model;

/**
 * Representa uma sala (local) onde a reuniao sera realizada.
 */
public class Room {

    private final String name;
    private final int capacity;

    public Room(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "Room{name='" + name + "', capacity=" + capacity + "}";
    }
}
