package br.com.picpay.application.services.transfer;

import br.com.picpay.application.dtos.BaseResponsePageable;
import br.com.picpay.application.dtos.transfer.*;
import br.com.picpay.application.dtos.wallet.WalletUserResponse;
import br.com.picpay.application.enums.TransferType;
import br.com.picpay.domain.entities.transfer.Transfer;
import br.com.picpay.infra.repositories.transfer.ITransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferApplicationService {
    private final ITransferRepository transferRepository;

    public TransferResponse createTransfer(WalletUserResponse payer, WalletUserResponse receiver, double value) {
        Transfer transfer = Transfer.builder()
                .value(value)
                .payerId(payer.userId())
                .receiverId(receiver.userId())
                .transferDate(LocalDateTime.now())
                .build();

        Transfer entity = this.transferRepository.save(transfer);

        PayerResponse payerResponse = PayerResponse.builder()
                .walletId(payer.walletId())
                .name(payer.name())
                .document(payer.document())
                .email(payer.email())
                .build();

        ReceiverResponse receiverResponse = ReceiverResponse.builder()
                .walletId(receiver.walletId())
                .name(receiver.name())
                .document(receiver.document())
                .email(receiver.email())
                .build();

        return TransferResponse.builder()
                .id(entity.getId())
                .value(value)
                .transferDate(entity.getTransferDate())
                .payer(payerResponse)
                .receiver(receiverResponse)
                .build();
    }
    // domain layer -> modelos de negocio, entidades, regras de negocio
    // application layer -> casos de uso, servicos de aplicacao, orquestracao
    // infra layer -> banco de dados, servicos externos, aws, gcp, azure

    public  BaseResponsePageable<List<TransfersListResponse>> getTransfersReceivedByUserId(UUID userId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        var transfersEntities = this.transferRepository.findByReceiverId(userId, pageRequest);

        var data = transfersEntities.stream()
                .map(transfer -> TransfersListResponse.builder()
                        .transferId(transfer.getId())
                        .transferDate(transfer.getTransferDate())
                        .value(transfer.getValue())
                        .payer(PayerResponse.builder()
                                .walletId(transfer.getPayerId())
                                .name(transfer.getUserPayer().getName())
                                .email(transfer.getUserPayer().getEmail())
                                .document(transfer.getUserPayer().getDocument())
                                .build())
                        .transferType(TransferType.RECEIPT)
                        .build())
                .toList();

        return new BaseResponsePageable<List<TransfersListResponse>>(data, transfersEntities.getNumber(), transfersEntities.getSize(), transfersEntities.getTotalElements(), transfersEntities.getTotalPages());
    }

    public BaseResponsePageable<List<TransfersListResponse>> getTransfersPayedByUserId(UUID userId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        var transfersEntities = this.transferRepository.findByPayerId(userId, pageRequest);

        var data = transfersEntities.stream()
                .map(transfer -> TransfersListResponse.builder()
                        .transferId(transfer.getId())
                        .transferDate(transfer.getTransferDate())
                        .value(transfer.getValue())
                        .receiver(PayerResponse.builder()
                                .walletId(transfer.getPayerId())
                                .name(transfer.getUserPayer().getName())
                                .email(transfer.getUserPayer().getEmail())
                                .document(transfer.getUserPayer().getDocument())
                                .build())
                        .transferType(TransferType.PAYMENT)
                        .build()).toList();

        return new BaseResponsePageable<List<TransfersListResponse>>(data, transfersEntities.getNumber(), transfersEntities.getSize(), transfersEntities.getTotalElements(), transfersEntities.getTotalPages());
    }

    public BaseResponsePageable<List<TransfersListResponse>> getTransfersGenericByUserId(UUID userId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        var transfersEntities = this.transferRepository.findByPayerIdOrReceiverId(userId, userId, pageRequest);

        var data = transfersEntities.stream()
                .map(transfer -> {
                    boolean isPayer = isPayer(userId, transfer);

                    return TransfersListResponse.builder()
                            .transferId(transfer.getId())
                            .transferDate(transfer.getTransferDate())
                            .value(transfer.getValue())
                            .receiver(isPayer ? null : PayerResponse.builder()
                                    .walletId(transfer.getPayerId())
                                    .name(transfer.getUserPayer().getName())
                                    .email(transfer.getUserPayer().getEmail())
                                    .document(transfer.getUserPayer().getDocument())
                                    .build())
                            .payer(isPayer ? PayerResponse.builder()
                                    .walletId(transfer.getReceiverId())
                                    .name(transfer.getUserReceiver().getName())
                                    .email(transfer.getUserReceiver().getEmail())
                                    .document(transfer.getUserReceiver().getDocument())
                                    .build() : null)
                            .transferType(isPayer ? TransferType.PAYMENT : TransferType.RECEIPT)
                            .build();
                }).toList();

        return new BaseResponsePageable<List<TransfersListResponse>>(data, transfersEntities.getNumber(), transfersEntities.getSize(), transfersEntities.getTotalElements(), transfersEntities.getTotalPages());
    }

    private boolean isPayer(UUID userId,  Transfer transfer) {
        return userId.equals(transfer.getPayerId());
    }
}
