package com.ulstu.pharmacy.web.converter;

import com.ulstu.pharmacy.web.helper.MessagesHelper;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("CharArrayConverter")
public class CharArrayConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value == null) {
            return null;
        }
        return value.toCharArray();
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null) {
            return null;
        }
        char[] inputValue = new char[0];
        try {
            inputValue = (char[]) value;
        } catch (Exception ex) {
            MessagesHelper.errorMessage(
                    new RuntimeException(
                            new Throwable("Произошла ошибка при чтении данных, обратитесь к разработчикам.")
                    )
            );
        }
        return new String(inputValue);
    }
}
