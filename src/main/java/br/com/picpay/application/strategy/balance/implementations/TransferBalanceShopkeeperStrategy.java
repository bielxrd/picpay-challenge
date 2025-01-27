package br.com.picpay.application.strategy.balance.implementations;

import br.com.picpay.application.strategy.balance.interfaces.ITransferBalanceStrategy;
import br.com.picpay.domain.entities.shopkeeper.ShopkeeperBalance;
import br.com.picpay.domain.entities.user.UserBalance;
import br.com.picpay.domain.enums.WalletType;
import br.com.picpay.domain.services.balance.BalanceDomainService;
import br.com.picpay.infra.repositories.balance.IShopKeeperBalanceRepository;
import br.com.picpay.infra.repositories.balance.IUserBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferBalanceShopkeeperStrategy implements ITransferBalanceStrategy {
    private final IShopKeeperBalanceRepository shopKeeperBalanceRepository;
    private final IUserBalanceRepository userBalanceRepository;
    private final BalanceDomainService balanceDomainService;

    @Override
    public void transfer(UserBalance balancePayer, UUID balanceReceiverId, double value, UUID walletPayerId, UUID walletReceiverId) {
        ShopkeeperBalance balancePayee = shopKeeperBalanceRepository.findByIdWithLock(balanceReceiverId)
                .orElseThrow(() -> new IllegalArgumentException("Shopkeeper balance not found"));

        this.balanceDomainService.validate(balancePayer.getBalance(), balancePayee.getBalance(), value, walletPayerId, walletReceiverId);

        double newBalancePayer = this.balanceDomainService.decreaseBalance(balancePayer.getBalance(), value);
        double newBalancePayee = this.balanceDomainService.incrementBalance(balancePayee.getBalance(), value);

        balancePayer.setBalance(newBalancePayer);
        balancePayee.setBalance(newBalancePayee);

        this.userBalanceRepository.updateBalance(balancePayer.getId(), newBalancePayer);
        this.shopKeeperBalanceRepository.updateBalance(balancePayee.getId(), newBalancePayee);
    }

    @Override
    public WalletType getWalletType() {
        return WalletType.SHOPKEEPER_BALANCE;
    }
}
