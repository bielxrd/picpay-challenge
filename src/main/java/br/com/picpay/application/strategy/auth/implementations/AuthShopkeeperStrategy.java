package br.com.picpay.application.strategy.auth.implementations;

import br.com.picpay.application.strategy.auth.interfaces.IAuthStrategy;
import br.com.picpay.domain.enums.ERole;
import br.com.picpay.shared.utils.AuthUtils;
import br.com.picpay.shared.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthShopkeeperStrategy implements IAuthStrategy  {

    private final AuthUtils authUtils;
    private final JwtUtils jwtUtils;

    @Override
    public String auth(String email, String password, ERole role, UUID userId) {
        boolean matches = this.authUtils.isPasswordMatch(email, password);

        if (!matches) {
            throw new RuntimeException("Invalid credentials");
        }

        return this.jwtUtils.generateToken(userId, role);
    }

    @Override
    public ERole getRoleType() {
        return ERole.SHOPKEEPER;
    }
}
