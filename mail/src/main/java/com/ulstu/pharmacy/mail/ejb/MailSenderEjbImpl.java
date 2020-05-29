package com.ulstu.pharmacy.mail.ejb;

import com.ulstu.pharmacy.mail.facade.MailFacade;
import com.ulstu.pharmacy.mail.facade.model.MailMessage;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Address;

@Stateless
public class MailSenderEjbImpl implements MailSenderEjbLocal {

    private MailFacade mailFacade;

    @Inject
    public MailSenderEjbImpl(MailFacade mailFacade) {
        this.mailFacade = mailFacade;
    }

    @Override
    @Asynchronous
    public void sendMessage(Address address, MailMessage mailMessage) {
        mailFacade.sendMessage(address, mailMessage);
    }
}