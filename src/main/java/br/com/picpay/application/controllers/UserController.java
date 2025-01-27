package br.com.picpay.application.controllers;

import br.com.picpay.application.dtos.user.create.UserRequest;
import br.com.picpay.application.dtos.user.create.UserResponse;
import br.com.picpay.application.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest userRequest) {
        UserResponse user = this.userFacade.createUser(userRequest);
        return ResponseEntity.ok(user);
    }

}
