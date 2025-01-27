package br.com.picpay.domain.entities.shopkeeper;

import br.com.picpay.domain.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_shopkeeper_balance")
public class ShopkeeperBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private double balance;
    @OneToOne
    @JoinColumn(name = "shopkeeper_id", foreignKey = @ForeignKey(name = "fk_shopkeeper_balance_shopkeeper"))
    private User userShopkeeper;
}
