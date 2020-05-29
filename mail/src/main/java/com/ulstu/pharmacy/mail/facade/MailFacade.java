package com.ulstu.pharmacy.mail.facade;

import com.ulstu.pharmacy.mail.facade.model.MailMessage;

import javax.mail.Address;

public interface MailFacade {

    void sendMessage(Address address, MailMessage message);
}