package br.com.picpay.domain.services.balance;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BalanceDomainService {

    private void validateBalance(double oldBalance, double value) {
        if (oldBalance < value) {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    private void validateValue(double value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Value must be greater than 0");
        }
    }

    public double incrementBalance(double oldBalancePayee, double value) {
        return oldBalancePayee += value;
    }

    public double decreaseBalance(double oldBalancePayer, double value) {
        return oldBalancePayer -= value;
    }

    private void isSameUser(UUID payerId, UUID payeeId) {
        if (payerId.equals(payeeId)) {
            throw new IllegalArgumentException("Payer and payee must be different");
        }
    }

    public void validate(double oldBalancePayer, double oldBalancePayee, double value, UUID payerId, UUID payeeId) {
        validateBalance(oldBalancePayer, value);
        validateValue(value);
        isSameUser(payerId, payeeId);
    }
}
