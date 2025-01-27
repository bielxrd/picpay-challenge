package br.com.picpay.application.controllers;

import br.com.picpay.application.dtos.BaseResponsePageable;
import br.com.picpay.application.dtos.transfer.TransferRequest;
import br.com.picpay.application.dtos.transfer.TransfersListResponse;
import br.com.picpay.application.facade.TransferFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferFacade transferFacade;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public ResponseEntity<Object> transfer(@RequestBody TransferRequest transferRequest) {
        return ResponseEntity.ok(this.transferFacade.transfer(transferRequest));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/payed")
    public ResponseEntity<BaseResponsePageable<List<TransfersListResponse>>> getTransfersPayedByUserIdAndPageable(Principal principal,
                                                                                                                  @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                                                                                  @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        return ResponseEntity.ok(this.transferFacade.getTransfersPayedByUserIdAndPageable(UUID.fromString(principal.getName()), pageNumber, pageSize));
    }

    @GetMapping("/received")
    public ResponseEntity<BaseResponsePageable<List<TransfersListResponse>>> getTransfersReceived(Principal principal,
                                                                                                  @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                                                                  @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        return ResponseEntity.ok(this.transferFacade.getTransfersReceivedByUserIdAndPageable(UUID.fromString(principal.getName()), pageNumber, pageSize));
    }

    @GetMapping("/")
    public ResponseEntity<BaseResponsePageable<List<TransfersListResponse>>> getTransfers(Principal principal,
                                                                                          @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                                                                          @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        return ResponseEntity.ok(this.transferFacade.getTransfersByUserIdAndPageable(UUID.fromString(principal.getName()), pageNumber, pageSize));
    }
}
