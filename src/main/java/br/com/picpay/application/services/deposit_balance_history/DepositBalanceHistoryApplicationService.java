package br.com.picpay.application.services.deposit_balance_history;

import br.com.picpay.domain.entities.user.DepositBalanceHistory;
import br.com.picpay.infra.repositories.deposit_balance_history.IDepositBalanceHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class DepositBalanceHistoryApplicationService {

    private final IDepositBalanceHistory depositBalanceHistory;

    public boolean createDepositBalanceHistory(DepositBalanceHistory depositBalanceHistory) {
        try {
            var depositBalanceHistoryEntity = this.depositBalanceHistory.save(depositBalanceHistory);
            return true;
        } catch (Exception e) {
            log.error("Error creating deposit balance history", e);
            return false;
        }
    }

    public List<DepositBalanceHistory> getDepositsBalanceHistoryByUserId(UUID userId) {
        return this.depositBalanceHistory.findAllByUserId(userId);
    }
}
