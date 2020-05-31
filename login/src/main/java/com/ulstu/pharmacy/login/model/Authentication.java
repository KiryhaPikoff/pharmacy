package com.ulstu.pharmacy.login.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Authentication {

    private final String login;

    private final char[] password;
}