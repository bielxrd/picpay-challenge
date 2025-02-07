package br.com.picpay.application.controllers;

import br.com.picpay.application.dtos.user.UserProfileDto;
import br.com.picpay.application.dtos.user.create.UserRequest;
import br.com.picpay.application.dtos.user.create.UserResponse;
import br.com.picpay.application.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

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

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(Principal principal) {
        UUID id = UUID.fromString(principal.getName());
        return ResponseEntity.ok(userFacade.getUserProfile(id));
    }

}
