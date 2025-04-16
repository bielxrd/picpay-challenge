package br.com.picpay.application.facade;

import br.com.picpay.application.dtos.deposit_balance_history.DepositBalanceHistoryResponse;
import br.com.picpay.application.mappers.BalanceMapper;
import br.com.picpay.application.services.balance.BalanceApplicationService;
import br.com.picpay.application.services.deposit_balance_history.DepositBalanceHistoryApplicationService;
import br.com.picpay.common.utils.OrderingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class BalanceFacade {
    private final BalanceApplicationService balanceApplicationService;
    private final DepositBalanceHistoryApplicationService depositBalanceHistoryApplicationService;

    @CacheEvict(value = "deposit-balance-history", key="#userId")
    public void depositBalance(UUID userId, double value) {
        var balanceId = balanceApplicationService.depositBalance(userId, value);

        if (balanceId == null) {
            throw new IllegalArgumentException("Error depositing balance");
        }

        var depositBalanceHistory = BalanceMapper.toDepositBalanceHistory(userId, balanceId, value);

        var success = depositBalanceHistoryApplicationService.createDepositBalanceHistory(depositBalanceHistory);

        if (!success) {
            throw new IllegalArgumentException("Error creating deposit balance history");
        }
    }

    @Cacheable(value = "deposit-balance-history", key="#userId" )
    public List<DepositBalanceHistoryResponse> getDepositsBalanceHistoryByUserId(UUID userId, boolean isDateOrdering, boolean isValueOrdering) {
        var depositsBalanceHistory  = depositBalanceHistoryApplicationService.getDepositsBalanceHistoryByUserId(userId);

        if (depositsBalanceHistory == null  || depositsBalanceHistory.isEmpty()) {
            throw new IllegalArgumentException("No deposit balance history found");
        }

        var depositBalanceHistoryDto = depositsBalanceHistory.stream()
                .map(BalanceMapper::toDepositBalanceHistoryResponse)
                .toList();

        return OrderingUtils.applyOrdering(depositBalanceHistoryDto, isDateOrdering, isValueOrdering);
    }
}

