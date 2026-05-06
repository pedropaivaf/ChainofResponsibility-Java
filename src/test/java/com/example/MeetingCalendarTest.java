package com.example;

import com.example.calendar.MeetingCalendar;
import com.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MeetingCalendar - Gerenciamento de Reunioes")
class MeetingCalendarTest {

    private MeetingCalendar calendario;
    private User pedro;
    private User ana;
    private Room salaA;
    private Room salaB;
    private LocalDate data;

    @BeforeEach
    void setUp() {
        calendario = new MeetingCalendar();
        pedro = new User("Pedro", "pedro@empresa.com");
        ana = new User("Ana", "ana@empresa.com");
        salaA = new Room("Sala A", 5);
        salaB = new Room("Sala B", 10);
        data = LocalDate.of(2025, 6, 10);
    }

    @Test
    @DisplayName("Calendario deve iniciar vazio")
    void calendarioDeveIniciarVazio() {
        assertEquals(0, calendario.getTotalMeetings());
        assertTrue(calendario.getMeetings().isEmpty());
    }

    @Test
    @DisplayName("Deve adicionar uma reuniao ao calendario")
    void deveAdicionarReuniao() {
        Meeting reuniao = new Meeting(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0),
            List.of(ana), pedro, salaA
        );
        calendario.addMeeting(reuniao);
        assertEquals(1, calendario.getTotalMeetings());
    }

    @Test
    @DisplayName("Deve retornar reunioes de uma data especifica")
    void deveRetornarReunioesPorData() {
        LocalDate outraData = LocalDate.of(2025, 6, 11);

        calendario.addMeeting(new Meeting(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), pedro, salaA
        ));
        calendario.addMeeting(new Meeting(
            outraData, LocalTime.of(14, 0), LocalTime.of(15, 0), List.of(), ana, salaB
        ));

        List<Meeting> reunioesDoDia = calendario.getMeetingsByDate(data);
        assertEquals(1, reunioesDoDia.size(), "So deve ter 1 reuniao no dia 10/06");

        List<Meeting> reunioesDaOutraData = calendario.getMeetingsByDate(outraData);
        assertEquals(1, reunioesDaOutraData.size(), "So deve ter 1 reuniao no dia 11/06");
    }

    @Test
    @DisplayName("Sala disponivel quando calendario esta vazio")
    void salaDisponivelComCalendarioVazio() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), pedro, salaA
        );
        assertTrue(calendario.isSalaDisponivel(req), "Sala deve estar disponivel em calendario vazio");
    }

    @Test
    @DisplayName("Sala indisponivel quando ja existe reuniao no mesmo horario")
    void salaIndisponivelComConflito() {
        calendario.addMeeting(new Meeting(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), pedro, salaA
        ));

        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), ana, salaA
        );
        assertFalse(calendario.isSalaDisponivel(req), "Sala deve estar ocupada");
    }

    @Test
    @DisplayName("Sala disponivel para outra sala no mesmo horario")
    void salaDisponivelParaOutraSala() {
        calendario.addMeeting(new Meeting(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), pedro, salaA
        ));

        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), ana, salaB
        );
        assertTrue(calendario.isSalaDisponivel(req), "Sala B deve estar disponivel");
    }

    @Test
    @DisplayName("Organizador sem conflito quando calendario vazio")
    void semConflitoComCalendarioVazio() {
        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), pedro, salaA
        );
        assertFalse(calendario.organizadorTemConflito(req),
            "Nao deve haver conflito em calendario vazio");
    }

    @Test
    @DisplayName("Organizador com conflito quando ja tem reuniao no horario")
    void organizadorComConflito() {
        calendario.addMeeting(new Meeting(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), pedro, salaA
        ));

        MeetingRequest req = new MeetingRequest(
            data, LocalTime.of(9, 30), LocalTime.of(10, 30), List.of(), pedro, salaB
        );
        assertTrue(calendario.organizadorTemConflito(req),
            "Pedro ja tem reuniao neste horario");
    }

    @Test
    @DisplayName("Deve limpar o calendario com clear()")
    void deveLimparCalendario() {
        calendario.addMeeting(new Meeting(
            data, LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), pedro, salaA
        ));
        assertEquals(1, calendario.getTotalMeetings());

        calendario.clear();
        assertEquals(0, calendario.getTotalMeetings());
    }
}
