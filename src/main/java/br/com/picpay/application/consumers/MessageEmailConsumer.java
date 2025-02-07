package br.com.picpay.application.consumers;

import br.com.picpay.application.enums.TransferType;
import br.com.picpay.shared.utils.MessageUtils;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;

import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class MessageEmailConsumer {

    @Value("${spring.cloud.aws.ses.email}")
    private String emailFrom;

    private final SesClient sesClient;

    @SqsListener(queueNames = "${spring.cloud.aws.sqs.endpoint}")
    public void receiveMessageEmail(@Payload String messageBody, @Headers Map<String, Object> headers) {
        try {
            SendEmailRequest sendEmailRequest = null;

            switch (TransferType.valueOf(headers.get("transferType").toString())) {
                case RECEIPT -> sendEmailRequest = MessageUtils.buildEmailRequestReceiver(emailFrom,
                        headers.get("receiver").toString(),
                        headers.get("receiverName").toString(),
                        headers.get("payerName").toString(),
                        Double.parseDouble(headers.get("value").toString()),
                        headers.get("transferDate").toString());
                case PAYMENT ->
                        sendEmailRequest = MessageUtils.buildEmailRequestPayer(emailFrom,
                                headers.get("payer").toString(),
                                headers.get("receiverName").toString(),
                                headers.get("payerName").toString(),
                                Double.parseDouble(headers.get("value").toString()),
                                headers.get("transferDate").toString());
            }

            sesClient.sendEmail(sendEmailRequest);
            log.info("Email message sent successfully for {}", headers.get("transferType"));
        } catch (SesException e) {
            log.error("Error to send email message", e);
        }
    }
}
