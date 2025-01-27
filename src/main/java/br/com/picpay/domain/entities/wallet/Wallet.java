package br.com.picpay.domain.entities.wallet;

import br.com.picpay.domain.enums.WalletType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@Entity(name = "tb_wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;
    private UUID balance;
    @Enumerated(EnumType.STRING)
    private WalletType type;
}
