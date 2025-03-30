package br.com.picpay.application.facade;

import br.com.picpay.application.dtos.transfer.BaseResponsePageable;
import br.com.picpay.application.dtos.transfer.TransferRequest;
import br.com.picpay.application.dtos.transfer.TransferResponse;
import br.com.picpay.application.dtos.transfer.TransfersAmountListResponse;
import br.com.picpay.application.proxy.TransferProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransferFacade {
    private final TransferProxy transferProxy;

    public TransferResponse transfer(TransferRequest transferRequest) {
        return this.transferProxy.executeTransfer(transferRequest);
    }

    public BaseResponsePageable getTransfersReceivedByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
       return transferProxy.getTransfersReceivedByUserIdAndPageable(userId, pageNumber, pageSize);
    }

    public BaseResponsePageable getTransfersPayedByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        return transferProxy.getTransfersPayedByUserIdAndPageable(userId, pageNumber, pageSize);
    }

    public BaseResponsePageable getTransfersByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        return transferProxy.getTransfersByUserIdAndPageable(userId, pageNumber, pageSize);
    }

    public TransfersAmountListResponse getAmountTransferredFilteredByDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return transferProxy.getAmountTransferredFilteredByDateRange(userId, startDate, endDate);
    }
}
