package helper;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessagesHelper {

    public static void errorMessage(Exception ex) {
        FacesContext.getCurrentInstance()
                .addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка!", ex.getCause().getMessage())
                );
    }
}