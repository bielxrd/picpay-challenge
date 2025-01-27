package br.com.picpay.application.dtos.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiverResponse {
    private UUID walletId;
    private String name;
    private String email;
    private String document;
}
