package br.com.picpay.application.services.auth;

import br.com.picpay.application.dtos.auth.AuthResponse;
import br.com.picpay.application.factory.AuthStrategyFactory;
import br.com.picpay.application.strategy.auth.interfaces.IAuthStrategy;
import br.com.picpay.infra.repositories.user.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {
    private final IUserRepository userRepository;
    private final AuthStrategyFactory authStrategyFactory;

    public AuthResponse auth(String email, String password) {
        final AuthResponse[] response = new AuthResponse[1];

        this.userRepository.findByEmail(email)
                .ifPresentOrElse(user -> {
                    IAuthStrategy authStrategy = this.authStrategyFactory.getStrategy(user.getRole().getRoleType());

                    if (authStrategy == null) {
                        throw new RuntimeException("Invalid role");
                    }

                    String token = authStrategy.auth(password, user.getPassword(), user.getRole().getRoleType(), user.getId());

                   response[0] = AuthResponse.builder()
                           .accessToken(token)
                           .build();
                }, () -> {
                    throw new RuntimeException("Invalid credentials");
                });

        return response[0];
    }
}
