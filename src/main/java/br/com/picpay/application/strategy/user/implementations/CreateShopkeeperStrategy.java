package br.com.picpay.application.strategy.user.implementations;

import br.com.picpay.application.dtos.balance.BalanceResponse;
import br.com.picpay.application.dtos.user.create.UserRequest;
import br.com.picpay.application.dtos.user.create.UserResponse;
import br.com.picpay.application.dtos.wallet.WalletResponse;
import br.com.picpay.application.services.balance.BalanceApplicationService;
import br.com.picpay.application.services.user.UserApplicationService;
import br.com.picpay.application.services.wallet.WalletApplicationService;
import br.com.picpay.application.strategy.user.interfaces.ICreateUserStrategy;
import br.com.picpay.domain.entities.wallet.Wallet;
import br.com.picpay.domain.enums.ERole;
import br.com.picpay.domain.enums.WalletType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateShopkeeperStrategy implements ICreateUserStrategy {

    private final UserApplicationService userApplicationService;
    private final BalanceApplicationService balanceApplicationService;
    private final WalletApplicationService walletApplicationService;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        var userEntity = this.userApplicationService.createUser(userRequest);

        BalanceResponse balanceResponse = this.balanceApplicationService.createBalanceForShopkeeper(userRequest.getBalance(), userEntity);

        Wallet walletRequest = Wallet.builder()
                .balance(balanceResponse.getBalanceId())
                .type(WalletType.SHOPKEEPER_BALANCE)
                .build();

        Wallet wallet = this.walletApplicationService.create(walletRequest);

        return UserResponse.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .balance(new BalanceResponse(balanceResponse.getBalanceId(), balanceResponse.getBalance()))
                .wallet(new WalletResponse(wallet.getId()))
                .build();
    }

    @Override
    public ERole getRole() {
        return ERole.SHOPKEEPER;
    }
}
