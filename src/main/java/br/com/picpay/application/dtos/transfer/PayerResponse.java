package br.com.picpay.application.dtos.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayerResponse implements Serializable {
    private UUID walletId;
    private String name;
    private String email;
    private String document;
    private long phoneNumber;
}
