package br.com.picpay.application.controllers;

import br.com.picpay.application.dtos.deposit_balance_history.DepositBalanceHistoryResponse;
import br.com.picpay.application.facade.BalanceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/balance")
public class BalanceController {

    private final BalanceFacade balanceFacade;

    @PutMapping("/deposit/{value}")
    public ResponseEntity<Void> depositBalance(Principal principal, @PathVariable double value) {
        this.balanceFacade.depositBalance(UUID.fromString(principal.getName()), value);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping("/deposit-history")
    public ResponseEntity<List<DepositBalanceHistoryResponse>> getDepositsBalanceHistory(Principal principal,
                                                                                         @RequestParam(required = false, defaultValue = "false") boolean isDateOrdering,
                                                                                         @RequestParam(required = false, defaultValue = "false") boolean isValueOrdering) {
        return ResponseEntity.ok(this.balanceFacade.getDepositsBalanceHistoryByUserId(UUID.fromString(principal.getName()), isDateOrdering, isValueOrdering));
    }
}
