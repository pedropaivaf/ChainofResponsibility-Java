package com.example.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Representa a solicitacao de agendamento de uma reuniao.
 * Este objeto percorre toda a cadeia de handlers (Chain of Responsibility).
 */
public class MeetingRequest {

    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final List<User> participants;
    private final User organizer;
    private final Room room;

    private boolean approved;
    private String message;

    public MeetingRequest(LocalDate date, LocalTime startTime, LocalTime endTime,
                          List<User> participants, User organizer, Room room) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = List.copyOf(participants);
        this.organizer = organizer;
        this.room = room;
        this.approved = false;
        this.message = "";
    }

    // --- Acoes da solicitacao ---

    public void approve(String message) {
        this.approved = true;
        this.message = message;
    }

    public void reject(String reason) {
        this.approved = false;
        this.message = reason;
    }

    // --- Getters ---

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

    public boolean isApproved() {
        return approved;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "MeetingRequest{" +
                "date=" + date +
                ", start=" + startTime +
                ", end=" + endTime +
                ", room=" + room.getName() +
                ", organizer=" + organizer.getName() +
                ", participants=" + participants.size() +
                ", approved=" + approved +
                ", message='" + message + "'" +
                "}";
    }
}
