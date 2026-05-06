package com.example;

import com.example.calendar.MeetingCalendar;
import com.example.handler.*;
import com.example.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Classe principal que demonstra o padrao Chain of Responsibility
 * aplicado a um sistema de agendamento de reunioes.
 *
 * A cadeia de validacao e:
 *   WorkingHoursHandler
 *       -> MaxParticipantsHandler
 *           -> RoomAvailabilityHandler
 *               -> ConflictHandler
 *                   -> SchedulingHandler
 */
public class Main {

    public static void main(String[] args) {
        // --- Configuracao inicial ---
        MeetingCalendar calendario = new MeetingCalendar();

        // Usuarios
        User pedro   = new User("Pedro",   "pedro@empresa.com");
        User ana     = new User("Ana",     "ana@empresa.com");
        User carlos  = new User("Carlos",  "carlos@empresa.com");
        User lucia   = new User("Lucia",   "lucia@empresa.com");

        // Salas
        Room salaPequena = new Room("Sala A", 3);
        Room salaGrande  = new Room("Sala B", 10);

        // --- Montagem da cadeia de handlers ---
        MeetingHandler cadeia = buildChain(calendario);

        System.out.println("=".repeat(60));
        System.out.println("SISTEMA DE AGENDAMENTO DE REUNIOES");
        System.out.println("Padrao: Chain of Responsibility");
        System.out.println("=".repeat(60));

        // --- Caso 1: Agendamento valido ---
        System.out.println("\n[CASO 1] Reuniao valida - deve ser aprovada");
        System.out.println("-".repeat(60));
        MeetingRequest req1 = new MeetingRequest(
            LocalDate.of(2025, 6, 10),
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            List.of(ana, carlos),
            pedro,
            salaPequena
        );
        cadeia.handle(req1);
        printResult(req1);

        // --- Caso 2: Horario fora do comercial ---
        System.out.println("\n[CASO 2] Horario invalido (19:00 - 20:00) - deve ser rejeitado");
        System.out.println("-".repeat(60));
        MeetingRequest req2 = new MeetingRequest(
            LocalDate.of(2025, 6, 10),
            LocalTime.of(19, 0),
            LocalTime.of(20, 0),
            List.of(ana),
            pedro,
            salaGrande
        );
        cadeia.handle(req2);
        printResult(req2);

        // --- Caso 3: Sala sem capacidade ---
        System.out.println("\n[CASO 3] Sala cheia (Sala A cap. 3, 4 pessoas) - deve ser rejeitado");
        System.out.println("-".repeat(60));
        MeetingRequest req3 = new MeetingRequest(
            LocalDate.of(2025, 6, 11),
            LocalTime.of(14, 0),
            LocalTime.of(15, 0),
            List.of(ana, carlos, lucia), // + organizador = 4 pessoas
            pedro,
            salaPequena
        );
        cadeia.handle(req3);
        printResult(req3);

        // --- Caso 4: Sala indisponivel (conflito de sala) ---
        System.out.println("\n[CASO 4] Sala ja ocupada - deve ser rejeitado");
        System.out.println("-".repeat(60));
        MeetingRequest req4 = new MeetingRequest(
            LocalDate.of(2025, 6, 10), // mesmo dia do caso 1
            LocalTime.of(9, 30),       // mesmo horario (conflita)
            LocalTime.of(10, 30),
            List.of(lucia),
            carlos,
            salaPequena               // mesma sala do caso 1
        );
        cadeia.handle(req4);
        printResult(req4);

        // --- Caso 5: Conflito de agenda do organizador ---
        System.out.println("\n[CASO 5] Organizador com conflito de agenda - deve ser rejeitado");
        System.out.println("-".repeat(60));
        MeetingRequest req5 = new MeetingRequest(
            LocalDate.of(2025, 6, 10), // mesmo dia do caso 1
            LocalTime.of(9, 0),        // horario que conflita com reuniao de pedro
            LocalTime.of(10, 0),
            List.of(lucia),
            pedro,                    // pedro ja tem reuniao nesse horario (caso 1)
            salaGrande
        );
        cadeia.handle(req5);
        printResult(req5);

        // --- Resumo ---
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Total de reunioes agendadas: " + calendario.getTotalMeetings());
        System.out.println("=".repeat(60));
    }

    /**
     * Constroi e retorna o primeiro handler da cadeia.
     * A cadeia e: WorkingHours -> MaxParticipants -> RoomAvailability -> Conflict -> Scheduling
     */
    public static MeetingHandler buildChain(MeetingCalendar calendario) {
        MeetingHandler workingHours   = new WorkingHoursHandler();
        MeetingHandler maxParticipants = new MaxParticipantsHandler();
        MeetingHandler roomAvailability = new RoomAvailabilityHandler(calendario);
        MeetingHandler conflict        = new ConflictHandler(calendario);
        MeetingHandler scheduling      = new SchedulingHandler(calendario);

        workingHours
            .setNext(maxParticipants)
            .setNext(roomAvailability)
            .setNext(conflict)
            .setNext(scheduling);

        return workingHours;
    }

    private static void printResult(MeetingRequest request) {
        String status = request.isApproved() ? "APROVADO" : "REJEITADO";
        System.out.println("Resultado: " + status);
        System.out.println("Mensagem:  " + request.getMessage());
    }
}
