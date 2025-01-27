package br.com.picpay.application.facade;

import br.com.picpay.application.dtos.auth.AuthResponse;
import br.com.picpay.application.dtos.user.create.UserRequest;
import br.com.picpay.application.dtos.user.create.UserResponse;
import br.com.picpay.application.factory.UserStrategyFactory;
import br.com.picpay.application.services.auth.AuthApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final AuthApplicationService authApplicationService;
    private final UserStrategyFactory userStrategyFactory;

    public UserResponse createUser(UserRequest userRequest) {
        var strategy = userStrategyFactory.getStrategy(userRequest.getRole());

        if (strategy == null) {
            throw new IllegalArgumentException("Invalid role");
        }

        return strategy.createUser(userRequest);
    }

    public AuthResponse auth(String email, String password) {
        return this.authApplicationService.auth(email, password);
    }
}
