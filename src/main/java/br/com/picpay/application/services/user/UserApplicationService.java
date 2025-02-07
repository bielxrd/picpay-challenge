package br.com.picpay.application.services.user;

import br.com.picpay.application.dtos.user.UserProfileDto;
import br.com.picpay.application.dtos.user.create.UserRequest;
import br.com.picpay.domain.entities.user.User;
import br.com.picpay.domain.services.UserDomainService;
import br.com.picpay.infra.repositories.user.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserDomainService userDomainService;
    private final IUserRepository userRepository;

    public User createUser(UserRequest userRequest) {
        userDomainService.validateUser(userRequest);

        if (userRepository.existsByEmailOrDocument(userRequest.getEmail(), userRequest.getDocument())) {
            throw new IllegalArgumentException("User already exists");
        }

        return this.userRepository.save(userRequest.toEntity());
    }

    public UserProfileDto getUserProfile(UUID id) {
        return this.userRepository.findByIdCustom(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
