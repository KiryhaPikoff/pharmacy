package com.ulstu.pharmacy.mail.ejb;

import com.ulstu.pharmacy.mail.facade.model.MailMessage;

import javax.ejb.Local;
import javax.mail.Address;

@Local
public interface MailSenderEjbLocal {

    public void sendMessage(Address address, MailMessage mailMessage);
}