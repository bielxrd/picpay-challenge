package br.com.picpay.application.consumers;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageEmailConsumer {

    @Value("${spring.cloud.aws.sqs.endpoint}")
    private String queueName;

    @SqsListener(queueNames = "${spring.cloud.aws.sqs.endpoint}")
    public void receiveMessageEmail(@Payload String messageBody, @Headers Map<String, Object> headers) {
        System.out.println("Message: " + messageBody);
        System.out.println("Headers: " + headers);
    }
}
