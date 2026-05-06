package com.example.handler;

import com.example.model.MeetingRequest;

/**
 * Handler abstrato da cadeia de responsabilidade.
 *
 * Define a estrutura basica de cada no da cadeia:
 * - Referencia ao proximo handler
 * - Metodo handle() a ser implementado por cada handler concreto
 * - Metodo handleNext() que delega ao proximo handler
 *
 * Padrao: Chain of Responsibility
 */
public abstract class MeetingHandler {

    private MeetingHandler next;

    /**
     * Define o proximo handler na cadeia e o retorna (permite encadeamento fluente).
     *
     * Exemplo:
     *   handlerA.setNext(handlerB).setNext(handlerC);
     */
    public MeetingHandler setNext(MeetingHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Processa a solicitacao de reuniao.
     * Cada handler concreto decide se processa ou rejeita o pedido.
     *
     * @param request solicitacao de agendamento
     * @return true se aprovada, false se rejeitada
     */
    public abstract boolean handle(MeetingRequest request);

    /**
     * Passa a solicitacao ao proximo handler da cadeia.
     * Se nao houver proximo, aprova por padrao.
     */
    protected boolean handleNext(MeetingRequest request) {
        if (next == null) {
            return true;
        }
        return next.handle(request);
    }
}
