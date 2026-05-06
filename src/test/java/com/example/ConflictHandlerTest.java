package com.example;

import com.example.calendar.MeetingCalendar;
import com.example.handler.ConflictHandler;
import com.example.handler.MeetingHandler;
import com.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConflictHandler - Verificacao de Conflito de Agenda do Organizador")
class ConflictHandlerTest {

    private MeetingCalendar calendario;
    private MeetingHandler handler;
    private User pedro;
    private Room salaA;
    private Room salaB;
    private LocalDate data;

    @BeforeEach
    void setUp() {
        calendario = new MeetingCalendar();
        handler = new ConflictHandler(calendario);
        pedro = new User("Pedro", "pedro@empresa.com");
        salaA = new Room("Sala A", 10);
        salaB = new Room("Sala B", 10);
        data = LocalDate.of(2025, 6, 10);

        // Pedro ja tem reuniao das 09:00 as 10:00
        Meeting reuniaoExistente = new Meeting(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(), pedro, salaA
        );
        calendario.addMeeting(reuniaoExistente);
    }

    @Test
    @DisplayName("Deve aprovar quando organizador nao tem conflito (horario livre)")
    void deveAprovarSemConflito() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(10, 0), LocalTime.of(11, 0), // apos a reuniao existente
            List.of(), pedro, salaB
        );
        boolean resultado = handler.handle(req);
        assertTrue(resultado, "Pedro nao tem conflito depois das 10:00");
    }

    @Test
    @DisplayName("Deve aprovar quando organizador diferente no mesmo horario")
    void deveAprovarOrganizadorDiferente() {
        User ana = new User("Ana", "ana@empresa.com");
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(), ana, salaB // Ana nao tem reuniao, so Pedro tem
        );
        boolean resultado = handler.handle(req);
        assertTrue(resultado, "Ana nao tem conflito no horario");
    }

    @Test
    @DisplayName("Deve rejeitar quando organizador ja tem reuniao no mesmo horario")
    void deveRejeitarComConflito() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(), pedro, salaB // Pedro ja tem reuniao nesse horario
        );
        boolean resultado = handler.handle(req);
        assertFalse(resultado, "Pedro ja tem reuniao das 09:00 as 10:00");
        assertFalse(req.isApproved());
        assertTrue(req.getMessage().contains("Pedro") || req.getMessage().contains("conflito")
            || req.getMessage().contains("Conflito"));
    }

    @Test
    @DisplayName("Deve rejeitar quando horario do organizador se sobrepe parcialmente")
    void deveRejeitarSobreposicaoParcial() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 30), LocalTime.of(10, 30),
            List.of(), pedro, salaB
        );
        boolean resultado = handler.handle(req);
        assertFalse(resultado, "Sobreposicao parcial deve ser rejeitada");
    }

    @Test
    @DisplayName("Deve aprovar quando organizador tem reuniao em data diferente")
    void deveAprovarEmDataDiferente() {
        MeetingRequest req = new MeetingRequest(
            LocalDate.of(2025, 6, 11), // outro dia
            LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(), pedro, salaA
        );
        boolean resultado = handler.handle(req);
        assertTrue(resultado, "Pedro nao tem conflito em outra data");
    }
}
