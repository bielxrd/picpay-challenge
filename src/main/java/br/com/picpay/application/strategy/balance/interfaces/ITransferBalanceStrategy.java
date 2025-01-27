package br.com.picpay.application.strategy.balance.interfaces;

import br.com.picpay.domain.entities.user.UserBalance;
import br.com.picpay.domain.enums.WalletType;

import java.util.UUID;

public interface ITransferBalanceStrategy {
    void transfer(UserBalance balancePayer, UUID balanceReceiverId, double value, UUID walletPayerId, UUID walletReceiverId);
    WalletType getWalletType();
}
