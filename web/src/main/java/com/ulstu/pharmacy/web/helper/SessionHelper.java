package com.ulstu.pharmacy.web.helper;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class SessionHelper {

    public static HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
    }
}