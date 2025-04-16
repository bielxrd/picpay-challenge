package br.com.picpay.application.controllers;

import br.com.picpay.application.dtos.deposit_balance_history.DepositBalanceHistoryResponse;
import br.com.picpay.application.facade.BalanceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<Void> depositBalance(@RequestHeader UUID userId, @PathVariable double value) {
        this.balanceFacade.depositBalance(userId, value);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping("/deposit-history")
    public ResponseEntity<List<DepositBalanceHistoryResponse>> getDepositsBalanceHistory(@RequestHeader UUID userId,
                                                                                         @RequestParam(required = false, defaultValue = "false") boolean isDateOrdering,
                                                                                         @RequestParam(required = false, defaultValue = "false") boolean isValueOrdering) {
        return ResponseEntity.ok(this.balanceFacade.getDepositsBalanceHistoryByUserId(userId, isDateOrdering, isValueOrdering));
    }
}
