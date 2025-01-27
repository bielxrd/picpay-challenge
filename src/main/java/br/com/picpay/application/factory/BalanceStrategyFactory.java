package br.com.picpay.application.factory;

import br.com.picpay.application.strategy.balance.interfaces.ITransferBalanceStrategy;
import br.com.picpay.domain.enums.WalletType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BalanceStrategyFactory {
    private final Map<WalletType, ITransferBalanceStrategy> strategyMap;

    public BalanceStrategyFactory(List<ITransferBalanceStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(ITransferBalanceStrategy::getWalletType, strategy -> strategy));
    }

    public ITransferBalanceStrategy getStrategy(WalletType walletType) {
        return strategyMap.get(walletType);
    }
}
