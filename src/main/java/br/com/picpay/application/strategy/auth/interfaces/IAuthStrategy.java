package br.com.picpay.application.strategy.auth.interfaces;


import br.com.picpay.domain.enums.ERole;

import java.util.UUID;

public interface IAuthStrategy {
    String auth(String email, String password, ERole role, UUID userId);
    ERole getRoleType();
}
