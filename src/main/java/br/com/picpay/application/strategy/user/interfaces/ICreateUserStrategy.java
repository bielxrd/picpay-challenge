package br.com.picpay.application.strategy.user.interfaces;

import br.com.picpay.application.dtos.user.create.UserRequest;
import br.com.picpay.application.dtos.user.create.UserResponse;
import br.com.picpay.domain.enums.ERole;

public interface ICreateUserStrategy {
    UserResponse createUser(UserRequest userRequest);
    ERole getRole();
}
