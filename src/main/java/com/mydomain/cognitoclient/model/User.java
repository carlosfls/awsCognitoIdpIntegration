package com.mydomain.cognitoclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String username;

    private String email;

    private String password;

    private String nuevaPassword;
}
