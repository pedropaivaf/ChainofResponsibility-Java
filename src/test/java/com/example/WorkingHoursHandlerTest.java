package com.example;

import com.example.handler.MeetingHandler;
import com.example.handler.WorkingHoursHandler;
import com.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WorkingHoursHandler - Validacao de Horario Comercial")
class WorkingHoursHandlerTest {

    private MeetingHandler handler;
    private User organizador;
    private Room sala;
    private LocalDate data;

    @BeforeEach
    void setUp() {
        handler = new WorkingHoursHandler();
        organizador = new User("Pedro", "pedro@empresa.com");
        sala = new Room("Sala A", 5);
        data = LocalDate.of(2025, 6, 10);
    }

    @Test
    @DisplayName("Deve aprovar reuniao no horario comercial (08:00 - 18:00)")
    void deveAprovarHorarioComercial() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(), organizador, sala
        );
        // Handler sem proximo: se passar aqui, retorna true
        boolean resultado = handler.handle(req);
        assertTrue(resultado, "Deveria aprovar horario dentro do comercial");
        assertFalse(req.getMessage().contains("invalido"), "Nao deveria ter mensagem de erro");
    }

    @Test
    @DisplayName("Deve aprovar reuniao exatamente no limite (08:00 - 18:00)")
    void deveAprovarNoLimiteExato() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(8, 0), LocalTime.of(18, 0),
            List.of(), organizador, sala
        );
        boolean resultado = handler.handle(req);
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve rejeitar reuniao que comeca antes das 08:00")
    void deveRejeitarAntesDoInicio() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(7, 0), LocalTime.of(9, 0),
            List.of(), organizador, sala
        );
        boolean resultado = handler.handle(req);
        assertFalse(resultado, "Deveria rejeitar inicio antes das 08:00");
        assertFalse(req.isApproved());
        assertTrue(req.getMessage().contains("invalido") || req.getMessage().contains("Horario"));
    }

    @Test
    @DisplayName("Deve rejeitar reuniao que termina apos 18:00")
    void deveRejeitarAposOFim() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(17, 0), LocalTime.of(19, 0),
            List.of(), organizador, sala
        );
        boolean resultado = handler.handle(req);
        assertFalse(resultado, "Deveria rejeitar termino apos 18:00");
        assertFalse(req.isApproved());
    }

    @Test
    @DisplayName("Deve rejeitar reuniao completamente fora do horario (noturna)")
    void deveRejeitarHorarioNoturno() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(20, 0), LocalTime.of(21, 0),
            List.of(), organizador, sala
        );
        boolean resultado = handler.handle(req);
        assertFalse(resultado);
        assertFalse(req.isApproved());
        assertNotNull(req.getMessage());
        assertFalse(req.getMessage().isBlank());
    }
}
