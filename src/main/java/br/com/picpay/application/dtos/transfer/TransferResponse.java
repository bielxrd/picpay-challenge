package br.com.picpay.application.dtos.transfer;

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
public class TransferResponse {
    private UUID id;
    private double value;
    private PayerResponse payer;
    private ReceiverResponse receiver;
    private LocalDateTime transferDate;
}
