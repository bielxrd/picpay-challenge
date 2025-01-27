package br.com.picpay.application.facade;

import br.com.picpay.application.dtos.BaseResponsePageable;
import br.com.picpay.application.dtos.transfer.TransferRequest;
import br.com.picpay.application.dtos.transfer.TransferResponse;
import br.com.picpay.application.dtos.transfer.TransfersListResponse;
import br.com.picpay.application.dtos.wallet.WalletUserResponse;
import br.com.picpay.application.services.balance.BalanceApplicationService;
import br.com.picpay.application.services.transfer.TransferApplicationService;
import br.com.picpay.application.services.wallet.WalletApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferFacade {
    private final WalletApplicationService walletApplicationService;
    private final TransferApplicationService transferApplicationService;
    private final BalanceApplicationService balanceApplicationService;

    @Transactional(rollbackFor = Exception.class)
    public TransferResponse transfer(TransferRequest transferRequest) {
        Map<UUID, WalletUserResponse> wallets = this.walletApplicationService.getWallets(transferRequest.getPayerId(), transferRequest.getReceiverId());

        this.balanceApplicationService.transferBalance(wallets.get(transferRequest.getPayerId()), wallets.get(transferRequest.getReceiverId()), transferRequest.getValue());

        return this.transferApplicationService.createTransfer(wallets.get(transferRequest.getPayerId()), wallets.get(transferRequest.getReceiverId()), transferRequest.getValue());
    }

    public BaseResponsePageable<List<TransfersListResponse>> getTransfersReceivedByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        return this.transferApplicationService.getTransfersReceivedByUserId(userId, pageNumber, pageSize);
    }

    public BaseResponsePageable<List<TransfersListResponse>> getTransfersPayedByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        return this.transferApplicationService.getTransfersPayedByUserId(userId, pageNumber, pageSize);
    }

    public BaseResponsePageable<List<TransfersListResponse>> getTransfersByUserIdAndPageable(UUID userId, int pageNumber, int pageSize) {
        return this.transferApplicationService.getTransfersGenericByUserId(userId, pageNumber, pageSize);
    }
}
