package br.com.picpay.application.controllers;

import br.com.picpay.application.dtos.transfer.BaseResponsePageable;
import br.com.picpay.application.dtos.transfer.TransferRequest;
import br.com.picpay.application.dtos.transfer.TransfersAmountListResponse;
import br.com.picpay.application.facade.TransferFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferFacade transferFacade;


    @PostMapping("/transfer")
    public ResponseEntity<Object> transfer(@RequestBody TransferRequest transferRequest) {
        return ResponseEntity.ok(this.transferFacade.transfer(transferRequest));
    }


    @GetMapping("/payed")
    public ResponseEntity<BaseResponsePageable> getTransfersPayedByUserIdAndPageable(@RequestHeader UUID userId,
                                                                                                                  @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                                                                                  @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        return ResponseEntity.ok(this.transferFacade.getTransfersPayedByUserIdAndPageable(userId, pageNumber, pageSize));
    }

    @GetMapping("/received")
    public ResponseEntity<BaseResponsePageable> getTransfersReceived(@RequestHeader UUID userId,
                                                                                                  @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                                                                  @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        return ResponseEntity.ok(this.transferFacade.getTransfersReceivedByUserIdAndPageable(userId, pageNumber, pageSize));
    }

    @GetMapping("/")
    public ResponseEntity<BaseResponsePageable> getTransfers(@RequestHeader UUID userId,
                                                                                          @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                                                          @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        return ResponseEntity.ok(this.transferFacade.getTransfersByUserIdAndPageable(userId, pageNumber, pageSize));
    }

    @GetMapping("/amount")
    public ResponseEntity<TransfersAmountListResponse> getAmountTransferredFilteredByDateRange(@RequestHeader UUID userId,
                                                                                               @RequestParam LocalDateTime startDate,
                                                                                               @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(this.transferFacade.getAmountTransferredFilteredByDateRange(userId, startDate, endDate));
    }
}
