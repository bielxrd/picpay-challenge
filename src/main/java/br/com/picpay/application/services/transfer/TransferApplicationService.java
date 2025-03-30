package br.com.picpay.application.services.transfer;

import br.com.picpay.application.dtos.transfer.BaseResponsePageable;
import br.com.picpay.application.dtos.transfer.*;
import br.com.picpay.application.dtos.wallet.WalletUserResponse;
import br.com.picpay.application.enums.TransferType;
import br.com.picpay.domain.entities.transfer.Transfer;
import br.com.picpay.infra.repositories.transfer.ITransferRepository;
import br.com.picpay.infra.services.sqs.SqsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransferApplicationService {
    private final ITransferRepository transferRepository;
    private final SqsService sqsService;

    @Transactional(rollbackFor = Exception.class)
    public TransferResponse createTransfer(WalletUserResponse payer, WalletUserResponse receiver, double value) {
        Transfer transfer = Transfer.builder()
                .value(value)
                .payerId(payer.userId())
                .receiverId(receiver.userId())
                .transferDate(LocalDateTime.now())
                .build();

        Transfer entity = this.transferRepository.save(transfer);

        PayerResponse payerResponse = PayerResponse.builder()
                .id(payer.userId())
                .walletId(payer.walletId())
                .name(payer.name())
                .document(payer.document())
                .email(payer.email())
                .phoneNumber(payer.phoneNumber())
                .build();

        ReceiverResponse receiverResponse = ReceiverResponse.builder()
                .id(receiver.userId())
                .walletId(receiver.walletId())
                .name(receiver.name())
                .document(receiver.document())
                .email(receiver.email())
                .phoneNumber(receiver.phoneNumber())
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

    public  BaseResponsePageable getTransfersReceivedByUserId(UUID userId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        var transfersEntities = this.transferRepository.findByReceiverId(userId, pageRequest);

        var data = transfersEntities.stream()
                .map(transfer -> TransfersListResponse.builder()
                        .transferId(transfer.getId())
                        .transferDate(transfer.getTransferDate())
                        .value(transfer.getValue())
                        .payer(PayerResponse.builder()
                                .id(transfer.getUserPayer().getId())
                                .walletId(transfer.getPayerId())
                                .name(transfer.getUserPayer().getName())
                                .email(transfer.getUserPayer().getEmail())
                                .document(transfer.getUserPayer().getDocument())
                                .build())
                        .transferType(TransferType.RECEIPT)
                        .build())
                .toList();

        return new BaseResponsePageable(data, transfersEntities.getNumber(), transfersEntities.getSize(), transfersEntities.getTotalElements(), transfersEntities.getTotalPages());
    }

    public BaseResponsePageable getTransfersPayedByUserId(UUID userId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        var transfersEntities = this.transferRepository.findByPayerId(userId, pageRequest);

        var data = transfersEntities.stream()
                .map(transfer -> TransfersListResponse.builder()
                        .transferId(transfer.getId())
                        .transferDate(transfer.getTransferDate())
                        .value(transfer.getValue())
                        .receiver(PayerResponse.builder()
                                .id(transfer.getUserReceiver().getId())
                                .walletId(transfer.getPayerId())
                                .name(transfer.getUserPayer().getName())
                                .email(transfer.getUserPayer().getEmail())
                                .document(transfer.getUserPayer().getDocument())
                                .build())
                        .transferType(TransferType.PAYMENT)
                        .build()).toList();

        return new BaseResponsePageable(data, transfersEntities.getNumber(), transfersEntities.getSize(), transfersEntities.getTotalElements(), transfersEntities.getTotalPages());
    }

    public BaseResponsePageable getTransfersGenericByUserId(UUID userId, int pageNumber, int pageSize) {
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
                                    .id(transfer.getUserPayer().getId())
                                    .walletId(transfer.getPayerId())
                                    .name(transfer.getUserPayer().getName())
                                    .email(transfer.getUserPayer().getEmail())
                                    .document(transfer.getUserPayer().getDocument())
                                    .build())
                            .payer(isPayer ? PayerResponse.builder()
                                    .id(transfer.getUserReceiver().getId())
                                    .walletId(transfer.getReceiverId())
                                    .name(transfer.getUserReceiver().getName())
                                    .email(transfer.getUserReceiver().getEmail())
                                    .document(transfer.getUserReceiver().getDocument())
                                    .build() : null)
                            .transferType(isPayer ? TransferType.PAYMENT : TransferType.RECEIPT)
                            .build();
                }).toList();

        return new BaseResponsePageable(data, transfersEntities.getNumber(), transfersEntities.getSize(), transfersEntities.getTotalElements(), transfersEntities.getTotalPages());
    }

    public TransfersAmountListResponse getTransfersAmount(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        var transfersList = this.transferRepository.findByReceiverIdAndTransferDateBetween(userId, startDate, endDate);

        double total = transfersList.stream()
                .mapToDouble(Transfer::getValue)
                .sum();

        BigDecimal totalBigDecimal = BigDecimal.valueOf(total);

        var transfersListResponseDto = transfersList.stream()
                .map(transfer -> TransfersListResponse.builder()
                        .transferId(transfer.getId())
                        .transferDate(transfer.getTransferDate())
                        .value(transfer.getValue())
                        .payer(PayerResponse.builder()
                                .id(transfer.getUserPayer().getId())
                                .walletId(transfer.getPayerId())
                                .name(transfer.getUserPayer().getName())
                                .email(transfer.getUserPayer().getEmail())
                                .document(transfer.getUserPayer().getDocument())
                                .build())
                        .transferType(TransferType.RECEIPT)
                        .build()).toList();

        return TransfersAmountListResponse.builder()
                .amount(totalBigDecimal)
                .transfers(transfersListResponseDto)
                .build();
    }

    @Async
    public void notifyEmailTransfer(String queueName, TransferResponse transferResponse, double value) {
        try {
            Map<String, Object> messageAttributesReceiver = Map.of("receiver", transferResponse.getReceiver().getEmail(),
                    "value", value,
                    "payerName", transferResponse.getPayer().getName(),
                    "receiverName", transferResponse.getReceiver().getName(),
                    "transferDate", transferResponse.getTransferDate(),
                    "transferType", TransferType.RECEIPT);

            this.sqsService.sendMessage(queueName, transferResponse.getReceiver().getEmail(), messageAttributesReceiver);

            Map<String, Object> messageAttributesPayer = Map.of("payer", transferResponse.getPayer().getEmail(),
                    "value", value,
                    "receiverName", transferResponse.getReceiver().getName(),
                    "payerName", transferResponse.getPayer().getName(),
                    "transferDate", transferResponse.getTransferDate(),
                    "transferType", TransferType.PAYMENT);

            this.sqsService.sendMessage(queueName, transferResponse.getPayer().getEmail(), messageAttributesPayer);
        } catch (Exception e) {
            log.error("Error to send email message", e);
        }
    }

    @Async
    public void notifySmsTransfer(String queueSmsName, TransferResponse transferResponse, double value) {
        try {
            Map<String, Object> messageAttributesReceiver = Map.of(
                    "value", value,
                    "payerName", transferResponse.getPayer().getName(),
                    "receiverName", transferResponse.getReceiver().getName(),
                    "transferDate", transferResponse.getTransferDate(),
                    "transferType", TransferType.RECEIPT);

            this.sqsService.sendMessage(queueSmsName, String.valueOf(transferResponse.getReceiver().getPhoneNumber()), messageAttributesReceiver);

            Map<String, Object> messageAttributesPayer = Map.of("value", value,
                    "receiverName", transferResponse.getReceiver().getName(),
                    "payerName", transferResponse.getPayer().getName(),
                    "transferDate", transferResponse.getTransferDate(),
                    "transferType", TransferType.PAYMENT);

            this.sqsService.sendMessage(queueSmsName, String.valueOf(transferResponse.getPayer().getPhoneNumber()), messageAttributesPayer);
        } catch (Exception e) {
            log.error("Error to send sms message", e);
        }
    }

    private boolean isPayer(UUID userId,  Transfer transfer) {
        return userId.equals(transfer.getPayerId());
    }
}
