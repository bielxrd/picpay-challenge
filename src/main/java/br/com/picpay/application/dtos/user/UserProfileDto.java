package br.com.picpay.application.dtos.user;

import br.com.picpay.domain.entities.role.Role;
import br.com.picpay.domain.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private UUID id;
    private String name;
    private String email;
    private String document;
    private long phoneNumber;
    private double balance;
    private UUID balanceId;
    private UUID walletId;
    private ERole role;
}
