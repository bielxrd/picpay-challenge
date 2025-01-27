package br.com.picpay.infra.repositories.balance;

import br.com.picpay.domain.entities.user.User;
import br.com.picpay.domain.entities.user.UserBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IUserBalanceRepository extends JpaRepository<UserBalance, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT u FROM tb_user_balance u where u.id = :id", nativeQuery = false)
    Optional<UserBalance> findByIdWithLock(@Param("id") UUID id);
    Optional<UserBalance> findByUserId(UUID userId);

    @Modifying
    @Query(value = "UPDATE tb_user_balance SET balance = :balance WHERE id = :id", nativeQuery = false)
    void updateBalance(@Param("id") UUID id, @Param("balance") double balance);
}
