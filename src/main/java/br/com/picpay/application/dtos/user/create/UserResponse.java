package br.com.picpay.application.dtos.user.create;

import br.com.picpay.application.dtos.balance.BalanceResponse;
import br.com.picpay.application.dtos.wallet.WalletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private BalanceResponse balance;
    private WalletResponse wallet;
}
