package br.com.picpay.domain.entities.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tb_deposits_balance_history")
public class DepositBalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "value")
    private double value;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "balance_id")
    private UUID balanceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserBalance balance;

    @CreationTimestamp
    private LocalDateTime depositDate;

}
