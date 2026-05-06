package com.example;

import com.example.calendar.MeetingCalendar;
import com.example.handler.MeetingHandler;
import com.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integracao da cadeia completa de handlers.
 * Valida o comportamento end-to-end do padrao Chain of Responsibility.
 */
@DisplayName("ChainIntegration - Cadeia Completa de Handlers")
class ChainIntegrationTest {

    private MeetingCalendar calendario;
    private MeetingHandler cadeia;
    private User pedro;
    private User ana;
    private User carlos;
    private Room salaA;  // capacidade 3
    private Room salaB;  // capacidade 10
    private LocalDate data;

    @BeforeEach
    void setUp() {
        calendario = new MeetingCalendar();
        cadeia = Main.buildChain(calendario);
        pedro  = new User("Pedro",  "pedro@empresa.com");
        ana    = new User("Ana",    "ana@empresa.com");
        carlos = new User("Carlos", "carlos@empresa.com");
        salaA  = new Room("Sala A", 3);
        salaB  = new Room("Sala B", 10);
        data   = LocalDate.of(2025, 6, 10);
    }

    @Test
    @DisplayName("Cadeia completa: deve aprovar solicitacao valida")
    void deveAprovarSolicitacaoValida() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(ana), pedro, salaA
        );

        boolean resultado = cadeia.handle(req);

        assertTrue(resultado);
        assertTrue(req.isApproved());
        assertEquals(1, calendario.getTotalMeetings());
    }

    @Test
    @DisplayName("Cadeia: deve parar no WorkingHoursHandler para horario invalido")
    void deveBloquearNoHorarioInvalido() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(19, 0), LocalTime.of(20, 0),
            List.of(ana), pedro, salaA
        );

        boolean resultado = cadeia.handle(req);

        assertFalse(resultado);
        assertFalse(req.isApproved());
        assertEquals(0, calendario.getTotalMeetings(), "Nenhuma reuniao deve ser agendada");
        assertTrue(req.getMessage().contains("invalido") || req.getMessage().contains("Horario"));
    }

    @Test
    @DisplayName("Cadeia: deve parar no MaxParticipantsHandler para sala cheia")
    void deveBloquearNaCapacidade() {
        // Sala A tem capacidade 3, mas sao 4 pessoas (3 participantes + 1 organizador)
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(ana, carlos, new User("Lucia", "lucia@empresa.com")),
            pedro, salaA
        );

        boolean resultado = cadeia.handle(req);

        assertFalse(resultado);
        assertFalse(req.isApproved());
        assertEquals(0, calendario.getTotalMeetings());
    }

    @Test
    @DisplayName("Cadeia: deve parar no RoomAvailabilityHandler para sala ocupada")
    void deveBloquearNaSalaOcupada() {
        // Primeiro agendamento (valido)
        MeetingRequest req1 = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(ana), pedro, salaA
        );
        cadeia.handle(req1);
        assertTrue(req1.isApproved());

        // Segundo agendamento na mesma sala e horario
        MeetingRequest req2 = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(ana), carlos, salaA
        );
        boolean resultado = cadeia.handle(req2);

        assertFalse(resultado);
        assertFalse(req2.isApproved());
        assertEquals(1, calendario.getTotalMeetings(), "Apenas 1 reuniao deve estar agendada");
        assertTrue(req2.getMessage().contains("indisponivel") || req2.getMessage().contains("reservada"));
    }

    @Test
    @DisplayName("Cadeia: deve parar no ConflictHandler para conflito do organizador")
    void deveBloquearNoConflitoDaOrganizador() {
        // Pedro agenda reuniao das 09:00 as 10:00 na Sala A
        MeetingRequest req1 = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(ana), pedro, salaA
        );
        cadeia.handle(req1);
        assertTrue(req1.isApproved());

        // Pedro tenta agendar outra reuniao no mesmo horario (sala diferente)
        MeetingRequest req2 = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(carlos), pedro, salaB
        );
        boolean resultado = cadeia.handle(req2);

        assertFalse(resultado);
        assertFalse(req2.isApproved());
        assertEquals(1, calendario.getTotalMeetings());
        assertTrue(req2.getMessage().contains("Pedro") || req2.getMessage().contains("conflito")
            || req2.getMessage().contains("Conflito"));
    }

    @Test
    @DisplayName("Cadeia: deve aceitar duas reunioes validas em sequencia")
    void deveAceitarDuasReuniaoValidas() {
        MeetingRequest req1 = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(ana), pedro, salaA
        );
        MeetingRequest req2 = new MeetingRequest(
            data, LocalTime.of(10, 0), LocalTime.of(11, 0),
            List.of(ana), pedro, salaA
        );

        cadeia.handle(req1);
        cadeia.handle(req2);

        assertTrue(req1.isApproved());
        assertTrue(req2.isApproved());
        assertEquals(2, calendario.getTotalMeetings());
    }
}
