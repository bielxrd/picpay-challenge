package br.com.picpay.application.services.wallet;

import br.com.picpay.application.dtos.wallet.WalletUserResponse;
import br.com.picpay.domain.entities.wallet.Wallet;
import br.com.picpay.infra.repositories.wallet.IWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletApplicationService {

    private final IWalletRepository walletRepository;

    public Wallet create(Wallet walletRequest) {
        if (walletRepository.existsByBalance(walletRequest.getBalance())) {
            throw new IllegalArgumentException("Wallet already exists");
        }

        return this.walletRepository.save(walletRequest);
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<UUID, WalletUserResponse> getWallets(UUID payerId, UUID receiverId) {
        WalletUserResponse walletPayer = this.walletRepository.findWalletUserByWalletId(payerId)
                .orElseThrow(() -> new IllegalArgumentException("Payer not found"));

        WalletUserResponse walletReceiver = this.walletRepository.findWalletUserByWalletId(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        return Map.of(walletPayer.walletId(), walletPayer, walletReceiver.walletId(), walletReceiver);
    }

}
