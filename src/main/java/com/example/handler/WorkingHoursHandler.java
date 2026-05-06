package com.example.handler;

import com.example.model.MeetingRequest;

import java.time.LocalTime;

/**
 * Handler 1: Valida se a reuniao esta dentro do horario comercial.
 *
 * Regra: reunioes so podem ser agendadas entre 08:00 e 18:00.
 * Se o horario estiver fora desse intervalo, a solicitacao e rejeitada
 * e a cadeia e interrompida.
 */
public class WorkingHoursHandler extends MeetingHandler {

    private static final LocalTime HORA_INICIO = LocalTime.of(8, 0);
    private static final LocalTime HORA_FIM = LocalTime.of(18, 0);

    @Override
    public boolean handle(MeetingRequest request) {
        LocalTime inicio = request.getStartTime();
        LocalTime fim = request.getEndTime();

        boolean dentroDoHorario = !inicio.isBefore(HORA_INICIO) && !fim.isAfter(HORA_FIM);

        if (!dentroDoHorario) {
            request.reject(
                "Horario invalido: reunioes devem ocorrer entre " +
                HORA_INICIO + " e " + HORA_FIM +
                ". Solicitado: " + inicio + " - " + fim
            );
            return false;
        }

        System.out.println("[WorkingHoursHandler] Horario OK: " + inicio + " - " + fim);
        return handleNext(request);
    }
}
