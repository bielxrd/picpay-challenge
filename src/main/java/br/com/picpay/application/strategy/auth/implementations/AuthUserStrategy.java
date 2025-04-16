package br.com.picpay.application.strategy.auth.implementations;

import br.com.picpay.application.strategy.auth.interfaces.IAuthStrategy;
import br.com.picpay.domain.enums.ERole;
import br.com.picpay.common.utils.AuthUtils;
import br.com.picpay.common.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthUserStrategy implements IAuthStrategy {

    @Override
    public String auth(String rawPassword, String encodedPassword, ERole role, UUID userId) {
        boolean matches = AuthUtils.isPasswordMatch(rawPassword, encodedPassword);

        if (!matches) {
            throw new RuntimeException("Invalid credentials");
        }

        return JwtUtils.generateToken(userId, role);
    }

    @Override
    public ERole getRoleType() {
        return ERole.USER;
    }
}
