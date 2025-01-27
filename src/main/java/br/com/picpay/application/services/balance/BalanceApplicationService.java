package br.com.picpay.application.services.balance;

import br.com.picpay.application.dtos.balance.BalanceResponse;
import br.com.picpay.application.dtos.wallet.WalletUserResponse;
import br.com.picpay.application.factory.BalanceStrategyFactory;
import br.com.picpay.application.strategy.balance.interfaces.ITransferBalanceStrategy;
import br.com.picpay.domain.entities.shopkeeper.ShopkeeperBalance;
import br.com.picpay.domain.entities.user.User;
import br.com.picpay.domain.entities.user.UserBalance;
import br.com.picpay.domain.entities.wallet.Wallet;
import br.com.picpay.domain.services.balance.BalanceDomainService;
import br.com.picpay.infra.repositories.balance.IShopKeeperBalanceRepository;
import br.com.picpay.infra.repositories.balance.IUserBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceApplicationService {

    private final IUserBalanceRepository userBalanceRepository;
    private final BalanceStrategyFactory balanceStrategyFactory;
    private final IShopKeeperBalanceRepository shopKeeperBalanceRepository;

    public BalanceResponse createBalanceForUser(double balance, User user) {
        userBalanceRepository.findByUserId(user.getId())
                .ifPresent(x -> {
                    throw new IllegalArgumentException("User balance already exists");
                });

        UserBalance entity = this.userBalanceRepository.save(UserBalance.builder().balance(balance).user(user).build());

        return BalanceResponse.builder()
                .balanceId(entity.getId())
                .balance(entity.getBalance())
                .build();
    }

    public BalanceResponse createBalanceForShopkeeper(double balance, User user) {
        Optional<ShopkeeperBalance> shopkeeperBalance = this.shopKeeperBalanceRepository.findByUserShopkeeperId(user.getId());

        if (shopkeeperBalance.isPresent()) {
            throw new IllegalArgumentException("Shopkeeper balance already exists");
        }

        ShopkeeperBalance entity = this.shopKeeperBalanceRepository.save(ShopkeeperBalance.builder().balance(balance).userShopkeeper(user).build());

        return BalanceResponse.builder()
                .balanceId(entity.getId())
                .balance(entity.getBalance())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferBalance(WalletUserResponse walletPayer, WalletUserResponse walletReceiver, double value) {
        UserBalance balancePayer = this.userBalanceRepository.findByIdWithLock(walletPayer.balance())
                .orElseThrow(() -> new IllegalArgumentException("User balance payer not found"));

        ITransferBalanceStrategy transferBalanceStrategy = this.balanceStrategyFactory.getStrategy(walletReceiver.type());

        if (transferBalanceStrategy == null) {
            throw new IllegalArgumentException("Invalid wallet type");
        }

        transferBalanceStrategy.transfer(balancePayer, walletReceiver.balance(), value, walletPayer.walletId(), walletReceiver.walletId());
    }

}
