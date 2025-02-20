package br.com.picpay.application.facade;

import br.com.picpay.application.dtos.transfer.BaseResponsePageable;
import br.com.picpay.application.dtos.transfer.TransferRequest;
import br.com.picpay.application.dtos.transfer.TransferResponse;
import br.com.picpay.application.dtos.transfer.TransfersAmountListResponse;
import br.com.picpay.application.dtos.wallet.WalletUserResponse;
import br.com.picpay.application.enums.TransferType;
import br.com.picpay.application.services.balance.BalanceApplicationService;
import br.com.picpay.application.services.transfer.TransferApplicationService;
import br.com.picpay.application.services.wallet.WalletApplicationService;
import br.com.picpay.infra.services.redis.RedisService;
import br.com.picpay.infra.services.sqs.SqsService;
import br.com.picpay.shared.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransferFacade {
    private final WalletApplicationService walletApplicationService;
    private final TransferApplicationService transferApplicationService;
    private final BalanceApplicationService balanceApplicationService;
    private final SqsService sqsService;
    private final RedisService redisService;

    @Value("${spring.cloud.aws.sqs.endpoint}")
    private String queueName;

    @Value("${spring.cloud.aws.sqs.endpoint.sms}")
    private String queueSmsName;


    public TransferResponse transfer(TransferRequest transferRequest) {
        TransferResponse transferResponse = executeTransfer(transferRequest);
        notifyEmailTransfer(transferResponse, transferRequest.getValue());
        notifySmsTransfer(transferResponse, transferRequest.getValue());
        List<Map.Entry<String, String>> cacheEntries = Arrays.asList(
                Map.entry("transfers-amount-received", transferResponse.getPayer().getId().toString()),
                Map.entry("transfers-amount-received", transferResponse.getReceiver().getId().toString()),
                Map.entry("user-profile", transferResponse.getPayer().getId().toString()),
                Map.entry("user-profile", transferResponse.getReceiver().getId().toString()),
                Map.entry("transfers-received", transferResponse.getReceiver().getId().toString()),
                Map.entry("transfers-payed", transferResponse.getPayer().getId().toString()),
                Map.entry("transfers", transferResponse.getPayer().getId().toString()),
                Map.entry("transfers", transferResponse.getReceiver().getId().toString())
        );

        evictCaches(cacheEntries);
        return transferResponse;
    }

    protected TransferResponse executeTransfer(TransferRequest transferRequest) {
        Map<UUID, WalletUserResponse> wallets = this.walletApplicationService.getWallets(transferRequest.getPayerId(), transferRequest.getReceiverId());

        this.balanceApplicationService.transferBalance(wallets.get(transferRequest.getPayerId()), wallets.get(transferRequest.getReceiverId()), transferRequest.getValue());

        return this.transferApplicationService.createTransfer(wallets.get(transferRequest.getPayerId()), wallets.get(transferRequest.getReceiverId()), transferRequest.getValue());
    }

    @Async
    protected void notifyEmailTransfer(TransferResponse transferResponse, double value) {
        try {
            Map<String, Object> messageAttributesReceiver = Map.of("receiver", transferResponse.getReceiver().getEmail(),
                    "value", value,
                    "payerName", transferResponse.getPayer().getName(),
                    "receiverName", transferResponse.getReceiver().getName(),
                    "transferDate", transferResponse.getTransferDate(),
                    "transferType", TransferType.RECEIPT);

            this.sqsService.sendMessage(queueName, transferResponse.getReceiver().getEmail(), messageAttributesReceiver);

            Map<String, Object> messageAttributesPayer = Map.of("payer", transferResponse.getPayer().getEmail(),
                    "value", value,
                    "receiverName", transferResponse.getReceiver().getName(),
                    "payerName", transferResponse.getPayer().getName(),
                    "transferDate", transferResponse.getTransferDate(),
                    "transferType", TransferType.PAYMENT);

            this.sqsService.sendMessage(queueName, transferResponse.getPayer().getEmail(), messageAttributesPayer);
        } catch (Exception e) {
            log.error("Error to send email message", e);
        }
    }

    @Async
    protected void notifySmsTransfer(TransferResponse transferResponse, double value) {
        try {
            Map<String, Object> messageAttributesReceiver = Map.of(
                    "value", value,
                    "payerName", transferResponse.getPayer().getName(),
                    "receiverName", transferResponse.getReceiver().getName(),
                    "transferDate", transferResponse.getTransferDate(),
                    "transferType", TransferType.RECEIPT);

            this.sqsService.sendMessage(queueSmsName, String.valueOf(transferResponse.getReceiver().getPhoneNumber()), messageAttributesReceiver);

            Map<String, Object> messageAttributesPayer = Map.of("value", value,
                    "receiverName", transferResponse.getReceiver().getName(),
                    "payerName", transferResponse.getPayer().getName(),
                    "transferDate", transferResponse.getTransferDate(),
                    "transferType", TransferType.PAYMENT);

            this.sqsService.sendMessage(queueSmsName, String.valueOf(transferResponse.getPayer().getPhoneNumber()), messageAttributesPayer);
        } catch (Exception e) {
            log.error("Error to send sms message", e);
        }
    }

    @Async
    protected void evictCaches(List<Map.Entry<String, String>> entries) {
        Set<String> cacheKeys = new HashSet<>();

        entries.forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.contains("transfers") && !key.contains("amount")) {
                cacheKeys.add(CacheUtils.buildKeyFilter(key, value));
            } else {
                cacheKeys.add(CacheUtils.buildKey(key, value));
            }
        });

        cacheKeys.forEach((key) -> {
            if (key.contains("*")) {
                log.info("Deleting keys by filter: {}", key);
                redisService.deleteKeysByFilter(key);
            } else {
                log.info("Deleting key with no filter: {}", key);
                redisService.delete(key);
            }
        });

    }

    public BaseResponsePageable getTransfersReceivedByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        if (redisService.existsByKey(CacheUtils.buildKey("transfers-received", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize))))
            return redisService.get(CacheUtils.buildKey("transfers-received", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize)), BaseResponsePageable.class);

        var transfersReceived = this.transferApplicationService.getTransfersReceivedByUserId(userId, pageNumber, pageSize);

        redisService.save(CacheUtils.buildKey("transfers-received", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize)), transfersReceived, Duration.ofMinutes(5));

        return transfersReceived;
    }

    public BaseResponsePageable getTransfersPayedByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        if (redisService.existsByKey(CacheUtils.buildKey("transfers-payed", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize)))) {
            log.info("Cache key: {}", CacheUtils.buildKey("transfers-payed", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize)));
            return redisService.get(CacheUtils.buildKey("transfers-payed", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize)), BaseResponsePageable.class);
        }

        var transfersPayed = this.transferApplicationService.getTransfersPayedByUserId(userId, pageNumber, pageSize);

        redisService.save(CacheUtils.buildKey("transfers-payed", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize)), transfersPayed, Duration.ofMinutes(5));

        return transfersPayed;
    }

    public BaseResponsePageable getTransfersByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        if (redisService.existsByKey(CacheUtils.buildKey("transfers", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize))))
            return redisService.get(CacheUtils.buildKey("transfers-payed", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize)), BaseResponsePageable.class);

        var transfers = this.transferApplicationService.getTransfersGenericByUserId(userId, pageNumber, pageSize);

        redisService.save(CacheUtils.buildKey("transfers", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize)), transfers, Duration.ofMinutes(5));

        return transfers;
    }

    public TransfersAmountListResponse getAmountTransferredFilteredByDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        if (redisService.existsByKey(CacheUtils.buildKey("transfers-amount-received", userId.toString())))
            return redisService.get(CacheUtils.buildKey("transfers-amount-received", userId.toString()), TransfersAmountListResponse.class);

        var transfers = this.transferApplicationService.getTransfersAmount(userId, startDate, endDate);

        redisService.save(CacheUtils.buildKey("transfers-amount-received", userId.toString()), transfers, Duration.ofMinutes(5));

        return transfers;
    }
}
