# Agendador de Reuniões

Projeto de estudo do padrão de projeto **Chain of Responsibility** aplicado a um sistema de agendamento de reuniões em Java.

---

## Padrão usado

**Chain of Responsibility** — cada validação é um elo na corrente. Se o elo atual aprova, passa para o próximo. Se rejeita, a cadeia para ali.

Ordem dos handlers:

1. `WorkingHoursHandler` — a reunião está dentro do horário comercial (08h–18h)?
2. `MaxParticipantsHandler` — cabe todo mundo na sala?
3. `RoomAvailabilityHandler` — a sala está livre nesse horário?
4. `ConflictHandler` — o organizador já tem outra reunião no mesmo horário?
5. `SchedulingHandler` — tudo certo, agenda!

---

## Estrutura das classes

```
src/main/java/com/example/
├── model/
│   ├── User.java            → representa um usuário (nome, e-mail)
│   ├── Room.java            → sala de reunião (nome, capacidade)
│   ├── Meeting.java         → reunião já agendada
│   └── MeetingRequest.java  → solicitação de agendamento (aprovada ou rejeitada)
├── handler/
│   ├── MeetingHandler.java          → classe abstrata base dos handlers
│   ├── WorkingHoursHandler.java     → valida horário comercial
│   ├── MaxParticipantsHandler.java  → valida capacidade da sala
│   ├── RoomAvailabilityHandler.java → verifica disponibilidade da sala
│   ├── ConflictHandler.java         → verifica conflito do organizador
│   └── SchedulingHandler.java       → efetua o agendamento
├── calendar/
│   └── MeetingCalendar.java → repositório das reuniões agendadas
└── Main.java                → monta a cadeia e executa exemplos
```

---

## Como rodar

```bash
mvn test
```

40 testes cobrindo cada handler individualmente e a cadeia completa.
