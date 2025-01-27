package br.com.picpay.domain.entities.transfer;

import br.com.picpay.domain.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "tb_transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private double value;
    @Column(name = "transfer_date")
    private LocalDateTime transferDate;

    @Column(name = "payer_id")
    private UUID payerId;

    @Column(name = "receiver_id")
    private UUID receiverId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payer_id", insertable = false, updatable = false)
    private User userPayer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    private User userReceiver;
}
