package br.com.picpay.application.facade;

import br.com.picpay.application.dtos.auth.AuthResponse;
import br.com.picpay.application.dtos.user.UserProfileDto;
import br.com.picpay.application.dtos.user.create.UserRequest;
import br.com.picpay.application.dtos.user.create.UserResponse;
import br.com.picpay.application.factory.UserStrategyFactory;
import br.com.picpay.application.services.auth.AuthApplicationService;
import br.com.picpay.application.services.user.UserApplicationService;
import br.com.picpay.infra.services.redis.RedisService;
import br.com.picpay.shared.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final AuthApplicationService authApplicationService;
    private final UserApplicationService userApplicationService;
    private final UserStrategyFactory userStrategyFactory;
    private final RedisService redisService;

    public UserResponse createUser(UserRequest userRequest) {
        var strategy = userStrategyFactory.getStrategy(userRequest.getRole());

        if (strategy == null) {
            throw new IllegalArgumentException("Invalid role");
        }

        return strategy.createUser(userRequest);
    }

    public AuthResponse auth(String email, String password) {
        return this.authApplicationService.auth(email, password);
    }

    public UserProfileDto getUserProfile(UUID id) {
        if (redisService.existsByKey(CacheUtils.buildKey("user-profile", id.toString()))) {
            return redisService.get(CacheUtils.buildKey("user-profile", id.toString()), UserProfileDto.class);
        }

        var profile = this.userApplicationService.getUserProfile(id);

        redisService.save(CacheUtils.buildKey("user-profile", id.toString()), profile, Duration.ofMinutes(25));

        return profile;
    }
}
