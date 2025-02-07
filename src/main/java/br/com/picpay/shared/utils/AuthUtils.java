package br.com.picpay.shared.utils;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AuthUtils {

    public static boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        var passwordEncoder = new Argon2PasswordEncoder(16,32, 1, 65536, 4);
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
