package com.ulstu.pharmacy.web.helper;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessagesHelper {

    public static void infoMessage(String text) {
        FacesContext.getCurrentInstance()
                .addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Информация! " + text, "")
                );
    }

    public static void errorMessage(String text) {
        FacesContext.getCurrentInstance()
                .addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка! " +  text, "")
                );
    }

    public static void errorMessage(Exception ex) {
        FacesContext.getCurrentInstance()
                .addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка! " +  ex.getCause().getMessage(), "")
                );
    }
}