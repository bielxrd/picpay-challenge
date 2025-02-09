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
import br.com.picpay.infra.services.sqs.SqsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransferFacade {
    private final WalletApplicationService walletApplicationService;
    private final TransferApplicationService transferApplicationService;
    private final BalanceApplicationService balanceApplicationService;
    private final SqsService sqsService;
    private final RedisTemplate redisTemplate;

    @Value("${spring.cloud.aws.sqs.endpoint}")
    private String queueName;

    @Value("${spring.cloud.aws.sqs.endpoint.sms}")
    private String queueSmsName;


    @Caching(evict = {
            @CacheEvict(value = {"transfers-amount-received"},
                    key = "#transferRequest.payerId"),
            @CacheEvict(value = {"transfers-amount-received"},
                    key = "#transferRequest.receiverId"),
            @CacheEvict(value = {"user-profile"},
            key = "#transferRequest.receiverId"),
            @CacheEvict(value = {"user-profile"},
                    key = "#transferRequest.payerId")
    })
    public TransferResponse transfer(TransferRequest transferRequest) {
        TransferResponse transferResponse = executeTransfer(transferRequest);
        notifyEmailTransfer(transferResponse, transferRequest.getValue());
        notifySmsTransfer(transferResponse, transferRequest.getValue());
        evictUserTransfersCache(transferResponse.getPayer().getId(), "transfers-payed");
        return transferResponse;
    }


    @Transactional(rollbackFor = Exception.class)
    protected TransferResponse executeTransfer(TransferRequest transferRequest) {
        Map<UUID, WalletUserResponse> wallets = this.walletApplicationService.getWallets(transferRequest.getPayerId(), transferRequest.getReceiverId());

        this.balanceApplicationService.transferBalance(wallets.get(transferRequest.getPayerId()), wallets.get(transferRequest.getReceiverId()), transferRequest.getValue());

        return this.transferApplicationService.createTransfer(wallets.get(transferRequest.getPayerId()), wallets.get(transferRequest.getReceiverId()), transferRequest.getValue());
    }

    private void notifyEmailTransfer(TransferResponse transferResponse, double value) {
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

    private void notifySmsTransfer(TransferResponse transferResponse, double value) {
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

    public void evictUserTransfersCache(UUID userId, String cacheName) {
        String keyPattern = cacheName + "::" + userId.toString() + "*";
        Set<String> keys = redisTemplate.keys(keyPattern);

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Removed {} cache entries for user {} with pattern {}",
                    keys.size(), userId, keyPattern);

            if (log.isDebugEnabled()) {
                keys.forEach(key -> log.debug("Removed cache key: {}", key));
            }
        } else {
            log.debug("No cache entries found for pattern: {}", keyPattern);
        }
    }


    @Cacheable(value = "transfers-payed", key = "#userId + '-' + #pageNumber + '-' + #pageSize")
    public BaseResponsePageable getTransfersReceivedByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        return this.transferApplicationService.getTransfersReceivedByUserId(userId, pageNumber, pageSize);
    }

    @Cacheable(value = "transfers-payed", key = "#userId + '-' + #pageNumber + '-' + #pageSize")
    public BaseResponsePageable getTransfersPayedByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        return this.transferApplicationService.getTransfersPayedByUserId(userId, pageNumber, pageSize);
    }

    @Cacheable(value = "transfers-payed", key = "#userId + '-' + #pageNumber + '-' + #pageSize")
    public BaseResponsePageable getTransfersByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        return this.transferApplicationService.getTransfersGenericByUserId(userId, pageNumber, pageSize);
    }

    @Cacheable(value = "transfers-received-amount", key = "#userId")
    public TransfersAmountListResponse getAmountTransferredFilteredByDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return this.transferApplicationService.getTransfersAmount(userId, startDate, endDate);
    }
}
