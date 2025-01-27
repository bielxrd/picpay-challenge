package br.com.picpay.infra.repositories.balance;

import br.com.picpay.domain.entities.shopkeeper.ShopkeeperBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IShopKeeperBalanceRepository extends JpaRepository<ShopkeeperBalance, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT u FROM tb_shopkeeper_balance u where u.id = :id", nativeQuery = false)
    Optional<ShopkeeperBalance> findByIdWithLock(@Param("id") UUID id);

    @Modifying
    @Query(value = "UPDATE tb_shopkeeper_balance SET balance = :balance WHERE id = :id", nativeQuery = false)
    void updateBalance(@Param("id") UUID id, @Param("balance") double balance);

    Optional<ShopkeeperBalance> findByUserShopkeeperId(UUID userId);
}
