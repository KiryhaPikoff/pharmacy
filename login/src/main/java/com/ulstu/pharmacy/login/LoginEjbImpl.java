package com.ulstu.pharmacy.login;

import com.ulstu.pharmacy.login.model.Authentication;
import lombok.SneakyThrows;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.ejb.Stateless;
import java.util.Arrays;
import java.util.Objects;

@Stateless
public class LoginEjbImpl implements LoginEjbLocal {

    private Configuration configuration;

    @Override
    public boolean validate(Authentication auth) {
        if (Objects.isNull(configuration)) {
            this.loadConfiguration();
        }
        return checkLogin(auth.getLogin()) &&
               checkPassword(auth.getPassword());
    }

    private boolean checkLogin(String login) {
        return login.equals(
                configuration.getString("auth.admin.login")
        );
    }

    private boolean checkPassword(char[] password) {
        return Arrays.equals(
                password,
                configuration.getString("auth.admin.password")
                        .toCharArray()
        );
    }

    @SneakyThrows
    private void loadConfiguration() {
       this.configuration = new PropertiesConfiguration("auth.properties");
    }
}