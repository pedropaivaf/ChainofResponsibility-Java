package com.example;

import com.example.calendar.MeetingCalendar;
import com.example.handler.MeetingHandler;
import com.example.handler.SchedulingHandler;
import com.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SchedulingHandler - Efetivacao do Agendamento")
class SchedulingHandlerTest {

    private MeetingCalendar calendario;
    private MeetingHandler handler;
    private User organizador;
    private Room sala;
    private LocalDate data;

    @BeforeEach
    void setUp() {
        calendario = new MeetingCalendar();
        handler = new SchedulingHandler(calendario);
        organizador = new User("Pedro", "pedro@empresa.com");
        sala = new Room("Sala A", 10);
        data = LocalDate.of(2025, 6, 10);
    }

    @Test
    @DisplayName("Deve salvar a reuniao no calendario")
    void deveSalvarReuniaoNoCalendario() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(), organizador, sala
        );

        handler.handle(req);

        assertEquals(1, calendario.getTotalMeetings(), "Deveria ter 1 reuniao no calendario");
    }

    @Test
    @DisplayName("Deve marcar a solicitacao como aprovada")
    void deveMacarComoAprovada() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(), organizador, sala
        );

        boolean resultado = handler.handle(req);

        assertTrue(resultado);
        assertTrue(req.isApproved(), "Solicitacao deve ser marcada como aprovada");
    }

    @Test
    @DisplayName("Deve incluir data e horario na mensagem de aprovacao")
    void mensagemDeveConterDetalhes() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(14, 0), LocalTime.of(15, 0),
            List.of(), organizador, sala
        );

        handler.handle(req);

        String msg = req.getMessage();
        assertTrue(msg.contains("14:00") || msg.contains("sucesso"),
            "Mensagem deve conter horario ou confirmacao de sucesso");
    }

    @Test
    @DisplayName("Deve acumular multiplas reunioes no calendario")
    void deveAcumularMultiplasReunioes() {
        MeetingRequest req1 = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), organizador, sala
        );
        MeetingRequest req2 = new MeetingRequest(
            data, LocalTime.of(11, 0), LocalTime.of(12, 0), List.of(), organizador, sala
        );

        handler.handle(req1);
        handler.handle(req2);

        assertEquals(2, calendario.getTotalMeetings(), "Deveria ter 2 reunioes agendadas");
    }
}
