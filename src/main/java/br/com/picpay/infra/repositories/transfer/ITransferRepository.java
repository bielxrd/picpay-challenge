package br.com.picpay.infra.repositories.transfer;

import br.com.picpay.domain.entities.transfer.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ITransferRepository extends JpaRepository<Transfer, UUID> {
    Page<Transfer> findByReceiverId(UUID userId, Pageable pageable);
    Page<Transfer> findByPayerId(UUID userId, Pageable pageable);
    Page<Transfer> findByPayerIdOrReceiverId(UUID payerId, UUID receiverId, Pageable pageable);
}
