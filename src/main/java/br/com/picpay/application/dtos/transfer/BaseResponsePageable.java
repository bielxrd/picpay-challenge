package br.com.picpay.application.dtos.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseResponsePageable implements Serializable {
    private List<TransfersListResponse> data;
    private int pageNumber;
    private int pageSize;
    private long totalItems;
    private int totalPages;
}
