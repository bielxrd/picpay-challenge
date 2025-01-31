package br.com.picpay.infra.services.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SqsService {

    private final SqsTemplate sqsTemplate;

    @Async
    public void sendEmailMessage(String queueName, String message, Map<String, Object> headers) {
        sqsTemplate.send(builder -> {
            builder.queue(queueName)
                    .payload(message)
                    .headers(headers);
        });
    }
}
