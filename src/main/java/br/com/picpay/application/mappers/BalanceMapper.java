package br.com.picpay.application.mappers;

import br.com.picpay.application.dtos.deposit_balance_history.DepositBalanceHistoryResponse;
import br.com.picpay.domain.entities.user.DepositBalanceHistory;

import java.util.UUID;

public class BalanceMapper {

    public static DepositBalanceHistory toDepositBalanceHistory(UUID userId, UUID balanceId, double value) {
        return DepositBalanceHistory.builder()
                .userId(userId)
                .balanceId(balanceId)
                .value(value)
                .build();
    }

    public static DepositBalanceHistoryResponse toDepositBalanceHistoryResponse(DepositBalanceHistory depositBalanceHistory) {
        return DepositBalanceHistoryResponse.builder()
                .id(depositBalanceHistory.getId())
                .userId(depositBalanceHistory.getUserId())
                .balanceId(depositBalanceHistory.getBalanceId())
                .value(depositBalanceHistory.getValue())
                .depositDate(depositBalanceHistory.getDepositDate())
                .build();
    }
}

