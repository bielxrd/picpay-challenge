package br.com.picpay.common.utils;

import software.amazon.awssdk.services.ses.model.*;

public class MessageUtils {
    public static SendEmailRequest buildEmailRequestReceiver(String emailFrom, String emailTo, String receiverName, String payerName, double value, String transferDate) {
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .source(emailFrom)
                .destination(Destination.builder()
                        .toAddresses(emailTo)
                        .build())
                .message(Message.builder()
                        .subject(Content.builder()
                                .data("Transferência recebida")
                                .charset("UTF-8")
                                .build())
                        .body(Body.builder()
                                .text(Content.builder()
                                        .data(String.format(
                                                "Olá %s,\n\nVocê recebeu uma transferência de %s no valor de %s em %s.",
                                                receiverName,
                                                payerName,
                                                value,
                                                transferDate
                                        ))
                                        .charset("UTF-8")
                                        .build())
                                .build())
                        .build())
                .build();

        return emailRequest;
    }

    public static SendEmailRequest buildEmailRequestPayer(String emailFrom, String emailTo, String receiverName, String payerName, double value, String transferDate) {
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .source(emailFrom)
                .destination(Destination.builder()
                        .toAddresses(emailTo)
                        .build())
                .message(Message.builder()
                        .subject(Content.builder()
                                .data("Transferência realizada")
                                .charset("UTF-8")
                                .build())
                        .body(Body.builder()
                                .text(Content.builder()
                                        .data(String.format(
                                                "Olá %s,\n\nVocê realizou uma transferência para %s no valor de %s em %s.",
                                                payerName,
                                                receiverName,
                                                value,
                                                transferDate
                                        ))
                                        .charset("UTF-8")
                                        .build())
                                .build())
                        .build())
                .build();

        return emailRequest;
    }
}
