package br.com.picpay.application.factory;

import br.com.picpay.application.strategy.user.interfaces.ICreateUserStrategy;
import br.com.picpay.domain.enums.ERole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserStrategyFactory {
    private final Map<ERole, ICreateUserStrategy> strategyMap;

    // Key: EROLE.USER, Value: CreateUserStrategy
    // Key: EROLE.SHOPKEEPER, Value: CreateShopkeeperStrategy

    public UserStrategyFactory(List<ICreateUserStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(ICreateUserStrategy::getRole, strategy -> strategy));
    }

    public ICreateUserStrategy getStrategy(ERole role) {
        return strategyMap.get(role);
    }
}
