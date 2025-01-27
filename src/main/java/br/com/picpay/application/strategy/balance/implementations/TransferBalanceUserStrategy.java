package br.com.picpay.application.strategy.balance.implementations;

import br.com.picpay.application.strategy.balance.interfaces.ITransferBalanceStrategy;
import br.com.picpay.domain.entities.user.UserBalance;
import br.com.picpay.domain.enums.WalletType;
import br.com.picpay.domain.services.balance.BalanceDomainService;
import br.com.picpay.infra.repositories.balance.IUserBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferBalanceUserStrategy implements ITransferBalanceStrategy {
    private final IUserBalanceRepository userBalanceRepository;
    private final BalanceDomainService balanceDomainService;

    @Override
    public void transfer(UserBalance balancePayer, UUID balanceReceiverId, double value, UUID walletPayerId, UUID walletReceiverId) {
        UserBalance balancePayee = userBalanceRepository.findByIdWithLock(balanceReceiverId)
                .orElseThrow(() -> new IllegalArgumentException("User balance not found"));

        this.balanceDomainService.validate(balancePayer.getBalance(), balancePayee.getBalance(), value, walletPayerId, walletReceiverId);

        double newBalancePayer = this.balanceDomainService.decreaseBalance(balancePayer.getBalance(), value);
        double newBalancePayee = this.balanceDomainService.incrementBalance(balancePayee.getBalance(), value);

        balancePayer.setBalance(newBalancePayer);
        balancePayee.setBalance(newBalancePayee);

        this.userBalanceRepository.updateBalance(balancePayer.getId(), newBalancePayer);
        this.userBalanceRepository.updateBalance(balancePayee.getId(), newBalancePayee);
    }

    @Override
    public WalletType getWalletType() {
        return WalletType.USER_BALANCE;
    }
}
