package br.com.picpay.application.facade;

import br.com.picpay.application.dtos.auth.AuthResponse;
import br.com.picpay.application.dtos.user.UserProfileDto;
import br.com.picpay.application.dtos.user.create.UserRequest;
import br.com.picpay.application.dtos.user.create.UserResponse;
import br.com.picpay.application.factory.UserStrategyFactory;
import br.com.picpay.application.services.auth.AuthApplicationService;
import br.com.picpay.application.services.user.UserApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final AuthApplicationService authApplicationService;
    private final UserApplicationService userApplicationService;
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

    public UserProfileDto getUserProfile(UUID id) {
        return this.userApplicationService.getUserProfile(id);
    }
}
