package br.com.picpay.domain.enums;

public enum ERole {
    USER,
    SHOPKEEPER;

    public String getDescription() {
        return "ROLE_" + this.name();
    }
}
