package com.example;

import com.example.calendar.MeetingCalendar;
import com.example.handler.MeetingHandler;
import com.example.handler.RoomAvailabilityHandler;
import com.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoomAvailabilityHandler - Verificacao de Disponibilidade da Sala")
class RoomAvailabilityHandlerTest {

    private MeetingCalendar calendario;
    private MeetingHandler handler;
    private User organizador;
    private Room sala;
    private LocalDate data;

    @BeforeEach
    void setUp() {
        calendario = new MeetingCalendar();
        handler = new RoomAvailabilityHandler(calendario);
        organizador = new User("Pedro", "pedro@empresa.com");
        sala = new Room("Sala A", 10);
        data = LocalDate.of(2025, 6, 10);

        // Pre-agendamento: sala ocupada das 10:00 as 11:00
        Meeting existente = new Meeting(
            data, LocalTime.of(10, 0), LocalTime.of(11, 0),
            List.of(), new User("Carlos", "carlos@empresa.com"), sala
        );
        calendario.addMeeting(existente);
    }

    @Test
    @DisplayName("Deve aprovar quando sala esta livre (horario diferente)")
    void deveAprovarSalaLivre() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(11, 0), LocalTime.of(12, 0), // apos o existente
            List.of(), organizador, sala
        );
        boolean resultado = handler.handle(req);
        assertTrue(resultado, "Sala deve estar disponivel depois das 11:00");
    }

    @Test
    @DisplayName("Deve aprovar em data diferente mesmo no mesmo horario")
    void deveAprovarEmDataDiferente() {
        MeetingRequest req = new MeetingRequest(
            LocalDate.of(2025, 6, 11), // dia diferente
            LocalTime.of(10, 0), LocalTime.of(11, 0),
            List.of(), organizador, sala
        );
        boolean resultado = handler.handle(req);
        assertTrue(resultado, "Sala deve estar disponivel em data diferente");
    }

    @Test
    @DisplayName("Deve rejeitar quando sala esta ocupada no mesmo horario exato")
    void deveRejeitarSalaOcupadaMesmoHorario() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(10, 0), LocalTime.of(11, 0),
            List.of(), organizador, sala
        );
        boolean resultado = handler.handle(req);
        assertFalse(resultado, "Sala deve estar ocupada no mesmo horario");
        assertFalse(req.isApproved());
        assertTrue(req.getMessage().contains("indisponivel") || req.getMessage().contains("reservada"));
    }

    @Test
    @DisplayName("Deve rejeitar quando horario se sobrepe parcialmente (inicio dentro)")
    void deveRejeitarSobreposicaoParcialInicio() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(10, 30), LocalTime.of(11, 30), // comeca no meio
            List.of(), organizador, sala
        );
        boolean resultado = handler.handle(req);
        assertFalse(resultado, "Sobreposicao parcial deve ser rejeitada");
    }

    @Test
    @DisplayName("Deve rejeitar quando horario se sobrepe parcialmente (fim dentro)")
    void deveRejeitarSobreposicaoParcialFim() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 30), LocalTime.of(10, 30), // termina no meio
            List.of(), organizador, sala
        );
        boolean resultado = handler.handle(req);
        assertFalse(resultado, "Sobreposicao parcial deve ser rejeitada");
    }

    @Test
    @DisplayName("Deve aprovar em sala diferente mesmo no mesmo horario")
    void deveAprovarEmSalaDiferente() {
        Room outraSala = new Room("Sala B", 10);
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(10, 0), LocalTime.of(11, 0),
            List.of(), organizador, outraSala
        );
        boolean resultado = handler.handle(req);
        assertTrue(resultado, "Sala B deve estar disponivel");
    }
}
