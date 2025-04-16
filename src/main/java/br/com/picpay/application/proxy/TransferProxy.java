package br.com.picpay.application.proxy;

import br.com.picpay.application.dtos.transfer.BaseResponsePageable;
import br.com.picpay.application.dtos.transfer.TransferRequest;
import br.com.picpay.application.dtos.transfer.TransferResponse;
import br.com.picpay.application.dtos.transfer.TransfersAmountListResponse;
import br.com.picpay.application.dtos.wallet.WalletUserResponse;
import br.com.picpay.application.services.balance.BalanceApplicationService;
import br.com.picpay.application.services.transfer.TransferApplicationService;
import br.com.picpay.application.services.wallet.WalletApplicationService;
import br.com.picpay.infra.services.redis.RedisService;
import br.com.picpay.common.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class TransferProxy {
    private final RedisService redisService;
    private final WalletApplicationService walletApplicationService;
    private final TransferApplicationService transferApplicationService;
    private final BalanceApplicationService balanceApplicationService;

    @Value("${spring.cloud.aws.sqs.endpoint}")
    private String queueName;

    @Value("${spring.cloud.aws.sqs.endpoint.sms}")
    private String queueSmsName;

    public TransferResponse executeTransfer(TransferRequest transferRequest) {
        Map<UUID, WalletUserResponse> wallets = this.walletApplicationService.getWallets(transferRequest.getPayerId(), transferRequest.getReceiverId());

        this.balanceApplicationService.transferBalance(wallets.get(transferRequest.getPayerId()), wallets.get(transferRequest.getReceiverId()), transferRequest.getValue());

         var transferResponse = this.transferApplicationService.createTransfer(wallets.get(transferRequest.getPayerId()), wallets.get(transferRequest.getReceiverId()), transferRequest.getValue());

        this.transferApplicationService.notifyEmailTransfer(queueName, transferResponse, transferRequest.getValue());

        this.transferApplicationService.notifySmsTransfer(queueSmsName, transferResponse, transferRequest.getValue());

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

        this.redisService.evictCaches(cacheEntries);
        return transferResponse;
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
            return redisService.get(CacheUtils.buildKey("transfers", userId.toString(), String.valueOf(pageNumber), String.valueOf(pageSize)), BaseResponsePageable.class);

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
