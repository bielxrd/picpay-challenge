package br.com.picpay.domain.services;

import br.com.picpay.application.dtos.user.create.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainService {
    private final PasswordEncoder passwordEncoder;

    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public boolean isValidCPF(String cpf) {
        return cpf != null && cpf.matches("\\d{11}");
    }

    public boolean isPasswordStrong(String password) {
        return password != null && password.length() >= 8;
    }

    public boolean isValidName(String name) {
        return name != null && name.length() >= 3;
    }

    public void validateUser(UserRequest user) {
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!isValidCPF(user.getDocument())) {
            throw new IllegalArgumentException("Invalid CPF format");
        }

        if (!isPasswordStrong(user.getPassword())) {
            throw new IllegalArgumentException("Password is not strong enough");
        }

        if (!isValidName(user.getName())) {
            throw new IllegalArgumentException("Name is not valid");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
}
