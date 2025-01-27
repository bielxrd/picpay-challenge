package br.com.picpay.domain.entities.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tb_user_balance")
public class UserBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private double balance;
    @OneToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_balance_user"))
    private User user;
}
