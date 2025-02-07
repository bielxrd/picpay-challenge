package br.com.picpay.infra.repositories.user;

import br.com.picpay.application.dtos.user.UserProfileDto;
import br.com.picpay.domain.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailOrDocument(String email, String document);
    Optional<User> findByEmail(String email);

    @Query("""
    SELECT new br.com.picpay.application.dtos.user.UserProfileDto(
    u.id,
    u.name,
    u.email,
    u.document,
    u.phoneNumber,
    ub.balance,
    ub.id,
    w.id,
    u.role.roleType
    ) FROM tb_users u
    INNER JOIN tb_user_balance ub on u.id = ub.user.id
    INNER JOIN tb_wallet w on w.balance = ub.id
    WHERE u.id = :id
""")
    Optional<UserProfileDto> findByIdCustom(@Param("id") UUID id);
}
