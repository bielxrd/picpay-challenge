package br.com.picpay.application.factory;

import br.com.picpay.application.strategy.auth.interfaces.IAuthStrategy;
import br.com.picpay.domain.enums.ERole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthStrategyFactory {

    private final Map<ERole, IAuthStrategy> strategyMap;

    public AuthStrategyFactory(List<IAuthStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(IAuthStrategy::getRoleType, strategy -> strategy));
    }

    public IAuthStrategy getStrategy(ERole role) {
        return strategyMap.get(role);
    }

}
