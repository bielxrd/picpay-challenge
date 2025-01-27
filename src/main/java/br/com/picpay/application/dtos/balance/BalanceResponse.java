package br.com.picpay.application.dtos.balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {
    private UUID balanceId;
    private double balance;
}
