package br.com.picpay.infra.repositories.user;

import br.com.picpay.domain.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailOrDocument(String email, String document);
    Optional<User> findByEmail(String email);
}
