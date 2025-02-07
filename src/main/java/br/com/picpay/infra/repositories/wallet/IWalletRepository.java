package br.com.picpay.infra.repositories.wallet;

import br.com.picpay.application.dtos.wallet.WalletUserResponse;
import br.com.picpay.domain.entities.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IWalletRepository extends JpaRepository<Wallet, UUID> {
    public boolean existsByBalance(UUID id);

    @Query("""
    SELECT new br.com.picpay.application.dtos.wallet.WalletUserResponse(
        w.id, w.balance, 
        COALESCE(ub.user.id, ub_shop.userShopkeeper.id),
        w.type, 
        COALESCE(u.email, u_shop.email), 
        COALESCE(u.name, u_shop.name), 
        COALESCE(u.document, u_shop.document),
        COALESCE(u.phoneNumber, u_shop.phoneNumber)
    )
    FROM tb_wallet w
    LEFT JOIN tb_user_balance ub ON w.balance = ub.id AND ub.user IS NOT NULL
    LEFT JOIN tb_shopkeeper_balance ub_shop ON w.balance = ub_shop.id AND ub_shop.userShopkeeper IS NOT NULL
    LEFT JOIN tb_users u ON ub.user.id = u.id
    LEFT JOIN tb_users u_shop ON ub_shop.userShopkeeper.id = u_shop.id
    WHERE w.id = :walletId
""")
    Optional<WalletUserResponse> findWalletUserByWalletId(@Param("walletId") UUID walletId);
}
