package com.example.handler;

import com.example.model.MeetingRequest;

/**
 * Handler 2: Valida se o numero de participantes nao excede a capacidade da sala.
 *
 * Regra: a quantidade de participantes (incluindo o organizador) deve
 * caber na capacidade maxima da sala solicitada.
 */
public class MaxParticipantsHandler extends MeetingHandler {

    @Override
    public boolean handle(MeetingRequest request) {
        int totalPessoas = request.getParticipants().size() + 1; // +1 = organizador
        int capacidade = request.getRoom().getCapacity();

        if (totalPessoas > capacidade) {
            request.reject(
                "Capacidade excedida: a sala '" + request.getRoom().getName() +
                "' suporta " + capacidade + " pessoa(s), mas foram solicitadas " +
                totalPessoas + " (incluindo o organizador)."
            );
            return false;
        }

        System.out.println("[MaxParticipantsHandler] Capacidade OK: " +
            totalPessoas + "/" + capacidade + " na sala " + request.getRoom().getName());
        return handleNext(request);
    }
}
