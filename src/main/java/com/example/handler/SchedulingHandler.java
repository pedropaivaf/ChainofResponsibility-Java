package com.example.handler;

import com.example.calendar.MeetingCalendar;
import com.example.model.Meeting;
import com.example.model.MeetingRequest;

/**
 * Handler 5 (final): Efetiva o agendamento da reuniao no calendario.
 *
 * Este e o ultimo handler da cadeia. Se a solicitacao chegou ate aqui,
 * passou por todas as validacoes e pode ser agendada.
 */
public class SchedulingHandler extends MeetingHandler {

    private final MeetingCalendar calendar;

    public SchedulingHandler(MeetingCalendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public boolean handle(MeetingRequest request) {
        Meeting reuniao = new Meeting(
            request.getDate(),
            request.getStartTime(),
            request.getEndTime(),
            request.getParticipants(),
            request.getOrganizer(),
            request.getRoom()
        );

        calendar.addMeeting(reuniao);

        request.approve(
            "Reuniao agendada com sucesso! " +
            request.getDate() + " das " + request.getStartTime() +
            " as " + request.getEndTime() +
            " na sala '" + request.getRoom().getName() + "'."
        );

        System.out.println("[SchedulingHandler] Reuniao agendada: " + reuniao);
        return true;
    }
}
