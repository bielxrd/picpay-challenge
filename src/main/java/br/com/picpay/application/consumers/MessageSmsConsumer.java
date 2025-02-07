package br.com.picpay.application.consumers;

import br.com.picpay.application.enums.TransferType;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class MessageSmsConsumer {

    private final SnsClient snsClient;

    @SqsListener(queueNames = "${spring.cloud.aws.sqs.endpoint.sms}")
    public void receiveSmsMessage(@Payload String message, @Headers Map<String, Object> headers) {
        try {
            log.info("Starting to process: {}", message);

            String phoneNumber = formatPhoneNumber(message);
            String payerName = headers.get("payerName").toString();
            String receiverName = headers.get("receiverName").toString();
            double value = Double.parseDouble(headers.get("value").toString());
            String transferDate = headers.get("transferDate").toString();

            log.info("Received SMS message: {}", message);

            String content = buildMessage(TransferType.valueOf(headers.get("transferType").toString()),
                    value,
                    payerName,
                    receiverName);

            log.info("Sending SMS message: {}", content);

            PublishRequest request = PublishRequest.builder()
                    .phoneNumber(phoneNumber)
                    .message(content)
                    .messageAttributes(Map.of(
                            "AWS.SNS.SMS.SMSType", MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue("Transactional")
                                    .build()
                    ))
                    .build();

            PublishResponse response = snsClient.publish(request);
            log.info("SMS sent successfully. MessageId: {}", response.messageId());
        } catch (Exception e) {
            log.error("Error processing SMS message: {}", message, e);
        }
    }

    private String buildMessage(TransferType transferType, double value, String payerName, String receiverName) {
        return switch (transferType) {
            case RECEIPT -> String.format("You received a transfer of R$ %.2f from %s", value, payerName);
            case PAYMENT -> String.format("You made a transfer of R$ %.2f to %s", value, receiverName);
        };
    }

    private String formatPhoneNumber(String phoneNumber) {
        String cleanedNumber = phoneNumber.replaceAll("[^0-9]", "");

        if (!cleanedNumber.startsWith("55")) {
            cleanedNumber = "55" + cleanedNumber;
        }

        return "+" + cleanedNumber;
    }
}
