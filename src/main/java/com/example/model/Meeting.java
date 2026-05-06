package com.example.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Representa uma reuniao ja agendada no sistema.
 */
public class Meeting {

    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final List<User> participants;
    private final User organizer;
    private final Room room;

    public Meeting(LocalDate date, LocalTime startTime, LocalTime endTime,
                   List<User> participants, User organizer, Room room) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = List.copyOf(participants);
        this.organizer = organizer;
        this.room = room;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public User getOrganizer() {
        return organizer;
    }

    public Room getRoom() {
        return room;
    }

    /**
     * Verifica se o horario informado conflita com esta reuniao.
     */
    public boolean conflictsWith(LocalDate date, LocalTime start, LocalTime end) {
        if (!this.date.equals(date)) {
            return false;
        }
        return start.isBefore(this.endTime) && end.isAfter(this.startTime);
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "date=" + date +
                ", start=" + startTime +
                ", end=" + endTime +
                ", room=" + room.getName() +
                ", organizer=" + organizer.getName() +
                ", participants=" + participants.size() +
                "}";
    }
}
