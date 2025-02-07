package br.com.picpay.application.dtos.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransfersAmountListResponse {
    private BigDecimal amount;
    private List<TransfersListResponse> transfers;
}
