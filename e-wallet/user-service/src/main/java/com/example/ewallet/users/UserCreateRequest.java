package com.example.ewallet.users;


import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCreateRequest {

    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;

    private String mobile;

    @NotNull
    private String country;
    @NotNull
    private String kycId;

    private String password;

    public User to() {
        return User.builder()
                .country(country)
                .email(email)
                .name(name)
                .mobile(mobile)
                .kycId(kycId)
                .password(password)
                .build();

    }
}
