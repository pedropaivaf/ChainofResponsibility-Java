package com.example.calendar;

import com.example.model.Meeting;
import com.example.model.MeetingRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calendario de reunioes agendadas.
 *
 * Armazena todas as reunioes confirmadas e fornece metodos
 * de consulta usados pelos handlers da cadeia.
 */
public class MeetingCalendar {

    private final List<Meeting> meetings = new ArrayList<>();

    /**
     * Adiciona uma reuniao ao calendario.
     */
    public void addMeeting(Meeting meeting) {
        meetings.add(meeting);
    }

    /**
     * Retorna todas as reunioes agendadas.
     */
    public List<Meeting> getMeetings() {
        return List.copyOf(meetings);
    }

    /**
     * Retorna as reunioes de uma data especifica.
     */
    public List<Meeting> getMeetingsByDate(LocalDate date) {
        return meetings.stream()
                .filter(m -> m.getDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Verifica se a sala esta disponivel para o horario solicitado.
     * Uma sala esta ocupada se ja existe outra reuniao nela que conflita.
     */
    public boolean isSalaDisponivel(MeetingRequest request) {
        return meetings.stream()
                .filter(m -> m.getRoom().getName().equals(request.getRoom().getName()))
                .noneMatch(m -> m.conflictsWith(
                        request.getDate(),
                        request.getStartTime(),
                        request.getEndTime()
                ));
    }

    /**
     * Verifica se o organizador ja tem outra reuniao no mesmo horario.
     */
    public boolean organizadorTemConflito(MeetingRequest request) {
        return meetings.stream()
                .filter(m -> m.getOrganizer().getEmail().equals(request.getOrganizer().getEmail()))
                .anyMatch(m -> m.conflictsWith(
                        request.getDate(),
                        request.getStartTime(),
                        request.getEndTime()
                ));
    }

    /**
     * Remove todas as reunioes (util para testes).
     */
    public void clear() {
        meetings.clear();
    }

    /**
     * Retorna o numero de reunioes agendadas.
     */
    public int getTotalMeetings() {
        return meetings.size();
    }
}
