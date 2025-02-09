package br.com.picpay.application.dtos.deposit_balance_history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositBalanceHistoryResponse {
    private UUID id;
    private double value;
    private UUID userId;
    private UUID balanceId;
    private LocalDateTime depositDate;
}
