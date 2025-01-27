package br.com.picpay.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseResponsePageable<T>{
    private T data;
    private int pageNumber;
    private int pageSize;
    private long totalItems;
    private int totalPages;
}
