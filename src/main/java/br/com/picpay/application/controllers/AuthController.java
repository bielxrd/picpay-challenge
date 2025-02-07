package br.com.picpay.application.controllers;

import br.com.picpay.application.dtos.auth.AuthRequest;
import br.com.picpay.application.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserFacade userFacade;

    @PostMapping
    public ResponseEntity<Object> auth(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(this.userFacade.auth(authRequest.getEmail(), authRequest.getPassword()));
    }
}
