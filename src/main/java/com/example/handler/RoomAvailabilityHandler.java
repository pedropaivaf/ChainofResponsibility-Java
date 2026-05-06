package com.example.handler;

import com.example.calendar.MeetingCalendar;
import com.example.model.MeetingRequest;

/**
 * Handler 3: Verifica se a sala esta disponivel no horario solicitado.
 *
 * Consulta o calendario para ver se ha outra reuniao ocupando
 * a mesma sala no mesmo intervalo de tempo.
 */
public class RoomAvailabilityHandler extends MeetingHandler {

    private final MeetingCalendar calendar;

    public RoomAvailabilityHandler(MeetingCalendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public boolean handle(MeetingRequest request) {
        boolean disponivel = calendar.isSalaDisponivel(request);

        if (!disponivel) {
            request.reject(
                "Sala indisponivel: a sala '" + request.getRoom().getName() +
                "' ja esta reservada em " + request.getDate() +
                " das " + request.getStartTime() + " as " + request.getEndTime() + "."
            );
            return false;
        }

        System.out.println("[RoomAvailabilityHandler] Sala disponivel: " + request.getRoom().getName());
        return handleNext(request);
    }
}
