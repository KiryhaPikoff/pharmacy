package com.ulstu.pharmacy.web.login;

import com.ulstu.pharmacy.login.LoginEjbLocal;
import com.ulstu.pharmacy.login.model.Authentication;
import com.ulstu.pharmacy.web.helper.MessagesHelper;
import com.ulstu.pharmacy.web.helper.SessionHelper;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

@Getter
@Setter
@ViewScoped
@ManagedBean
public class LoginBean {

    private LoginEjbLocal loginEjb;

    private String login;

    private char[] password;

    @Inject
    public void setLoginEjb(LoginEjbLocal loginEjb) {
        this.loginEjb = loginEjb;
    }

    public String validate() {
        boolean valid = loginEjb.validate(
                Authentication.builder()
                        .login(this.login)
                        .password(this.password)
                .build());
        if (valid) {
            HttpSession session = SessionHelper.getSession();
            session.setAttribute("isAuthorized", true);
            return "success";
        } else {
            MessagesHelper.errorMessage("Неверный логин или пароль");
            return "fail";
        }
    }

    public String logout() {
        HttpSession session = SessionHelper.getSession();
        session.invalidate();
        return "logout";
    }
}