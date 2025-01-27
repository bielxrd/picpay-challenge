package br.com.picpay.application.dtos.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    public UUID payerId;
    public UUID receiverId;
    public double value;
}
