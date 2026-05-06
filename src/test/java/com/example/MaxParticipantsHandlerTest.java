package com.example;

import com.example.handler.MaxParticipantsHandler;
import com.example.handler.MeetingHandler;
import com.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MaxParticipantsHandler - Validacao de Capacidade da Sala")
class MaxParticipantsHandlerTest {

    private MeetingHandler handler;
    private User organizador;
    private LocalDate data;
    private LocalTime inicio;
    private LocalTime fim;

    @BeforeEach
    void setUp() {
        handler = new MaxParticipantsHandler();
        organizador = new User("Pedro", "pedro@empresa.com");
        data = LocalDate.of(2025, 6, 10);
        inicio = LocalTime.of(10, 0);
        fim = LocalTime.of(11, 0);
    }

    @Test
    @DisplayName("Deve aprovar quando participantes + organizador cabem na sala")
    void deveAprovarDentroCapacidade() {
        Room sala = new Room("Sala A", 5);
        List<User> participantes = List.of(
            new User("Ana", "ana@empresa.com"),
            new User("Carlos", "carlos@empresa.com")
        ); // 2 + 1 organizador = 3, sala tem 5

        MeetingRequest req = new MeetingRequest(data, inicio, fim, participantes, organizador, sala);
        boolean resultado = handler.handle(req);
        assertTrue(resultado, "Deveria aprovar: 3 pessoas em sala de 5");
    }

    @Test
    @DisplayName("Deve aprovar quando ocupa exatamente a capacidade maxima")
    void deveAprovarCapacidadeExata() {
        Room sala = new Room("Sala B", 3);
        List<User> participantes = List.of(
            new User("Ana", "ana@empresa.com"),
            new User("Carlos", "carlos@empresa.com")
        ); // 2 + 1 organizador = 3 = capacidade

        MeetingRequest req = new MeetingRequest(data, inicio, fim, participantes, organizador, sala);
        boolean resultado = handler.handle(req);
        assertTrue(resultado, "Deveria aprovar na capacidade exata");
    }

    @Test
    @DisplayName("Deve rejeitar quando participantes + organizador excedem a capacidade")
    void deveRejeitarAcimaCapacidade() {
        Room sala = new Room("Sala A", 2);
        List<User> participantes = List.of(
            new User("Ana", "ana@empresa.com"),
            new User("Carlos", "carlos@empresa.com")
        ); // 2 + 1 organizador = 3, sala tem 2

        MeetingRequest req = new MeetingRequest(data, inicio, fim, participantes, organizador, sala);
        boolean resultado = handler.handle(req);
        assertFalse(resultado, "Deveria rejeitar: 3 pessoas em sala de 2");
        assertFalse(req.isApproved());
        assertTrue(req.getMessage().contains("Capacidade") || req.getMessage().contains("capacidade"));
    }

    @Test
    @DisplayName("Deve aprovar com apenas o organizador (sem participantes)")
    void deveAprovarSemParticipantes() {
        Room sala = new Room("Sala A", 1);
        MeetingRequest req = new MeetingRequest(data, inicio, fim, List.of(), organizador, sala);
        boolean resultado = handler.handle(req);
        assertTrue(resultado, "Deveria aprovar so o organizador em sala de 1");
    }

    @Test
    @DisplayName("Deve incluir nome da sala na mensagem de rejeicao")
    void mensagemDeveConterNomeDaSala() {
        Room sala = new Room("Sala Pequena", 1);
        List<User> participantes = List.of(new User("Ana", "ana@empresa.com"));
        MeetingRequest req = new MeetingRequest(data, inicio, fim, participantes, organizador, sala);

        handler.handle(req);
        assertTrue(req.getMessage().contains("Sala Pequena"),
            "A mensagem de rejeicao deve conter o nome da sala");
    }
}
