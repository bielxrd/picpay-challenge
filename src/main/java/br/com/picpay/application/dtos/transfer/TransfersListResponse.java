package br.com.picpay.application.dtos.transfer;

import br.com.picpay.application.enums.TransferType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransfersListResponse implements Serializable {
    private UUID transferId;
    private LocalDateTime transferDate;
    private double value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PayerResponse payer;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PayerResponse receiver;
    private TransferType transferType;
}
