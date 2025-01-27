package br.com.picpay.application.dtos.user.create;

import br.com.picpay.domain.entities.role.Role;
import br.com.picpay.domain.entities.user.User;
import br.com.picpay.domain.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String name;
    private String document;
    private String email;
    private String password;
    private double balance = 0.0;
    private ERole role;

    public User toEntity() {
       Role roleEntity = Role.builder().roleType(role).build();
        User user = new User(null, name, document, email, password, roleEntity);
        roleEntity.setUser(user);
        return user;
    }
}
