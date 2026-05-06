package com.example.handler;

import com.example.calendar.MeetingCalendar;
import com.example.model.MeetingRequest;

/**
 * Handler 4: Verifica se o organizador ja tem uma reuniao no mesmo horario.
 *
 * Regra: o mesmo organizador nao pode ter duas reunioes simultaneas.
 */
public class ConflictHandler extends MeetingHandler {

    private final MeetingCalendar calendar;

    public ConflictHandler(MeetingCalendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public boolean handle(MeetingRequest request) {
        boolean temConflito = calendar.organizadorTemConflito(request);

        if (temConflito) {
            request.reject(
                "Conflito de agenda: o organizador '" + request.getOrganizer().getName() +
                "' ja possui uma reuniao em " + request.getDate() +
                " das " + request.getStartTime() + " as " + request.getEndTime() + "."
            );
            return false;
        }

        System.out.println("[ConflictHandler] Sem conflitos para: " + request.getOrganizer().getName());
        return handleNext(request);
    }
}
