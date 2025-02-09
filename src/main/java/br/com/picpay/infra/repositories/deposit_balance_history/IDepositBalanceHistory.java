package br.com.picpay.infra.repositories.deposit_balance_history;

import br.com.picpay.domain.entities.user.DepositBalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IDepositBalanceHistory extends JpaRepository<DepositBalanceHistory, UUID> {
    List<DepositBalanceHistory> findAllByUserId(UUID userId);
}
