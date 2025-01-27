package br.com.picpay.application.dtos.wallet;

import br.com.picpay.domain.enums.WalletType;

import java.util.UUID;

public record WalletUserResponse(UUID walletId, UUID balance, UUID userId, WalletType type, String email, String name, String document) {}
