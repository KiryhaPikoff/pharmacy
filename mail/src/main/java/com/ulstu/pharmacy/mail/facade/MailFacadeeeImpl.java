package com.ulstu.pharmacy.mail.facade;

import com.ulstu.pharmacy.mail.facade.model.MailMessage;
import lombok.SneakyThrows;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class MailFacadeeeImpl implements MailFacade {

    private Properties mailProperties;

    @Override
    @SneakyThrows
    public void sendMessage(Address address, MailMessage message) {
        Configuration mailConfig;
        if (Objects.isNull(mailProperties)) {
            mailConfig = new PropertiesConfiguration("mail.properties");
            mailProperties = new Properties();
            mailProperties.setProperty("mail.smtps.email",          mailConfig.getString("mail.smtps.email"));
            mailProperties.setProperty("mail.smtps.password",       mailConfig.getString("mail.smtps.password"));
            mailProperties.setProperty("mail.smtps.host",           mailConfig.getString("mail.smtps.host"));
            mailProperties.setProperty("mail.smtps.auth",           mailConfig.getString("mail.smtps.auth"));
            mailProperties.setProperty("mail.transport.protocol",   mailConfig.getString("mail.transport.protocol"));
        }

        Session mailSession = Session.getDefaultInstance(mailProperties);
        MimeMessage mimeMessage = new MimeMessage(mailSession);
        mimeMessage.setFrom(new InternetAddress(mailProperties.getProperty("mail.smtps.email")));
        mimeMessage.addRecipient(Message.RecipientType.TO, address);
        mimeMessage.setSubject(message.getTheme());
        Multipart mmp = new MimeMultipart();
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(message.getText(), "text/plain; charset=utf-8");
        mmp.addBodyPart(bodyPart);
        if (!message.getFiles().isEmpty()) {
            List<MimeBodyPart> attachments = message.getFiles().stream()
                    .map(this::createFileAttachment)
                    .collect(Collectors.toList());
            for (var attachment : attachments) {
                mmp.addBodyPart(attachment);
            }
        }
        mimeMessage.setContent(mmp);

        Transport transport = mailSession.getTransport();
        transport.connect(
                mailProperties.getProperty("mail.smtps.email"),
                mailProperties.getProperty("mail.smtps.password")
        );
        transport.sendMessage(mimeMessage, new Address[] {address} );
        transport.close();
    }

    @SneakyThrows
    private MimeBodyPart createFileAttachment(File file) {
            MimeBodyPart mbp = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(file);
            mbp.attachFile(file, fds.getContentType(), null);
            mbp.setDataHandler(new DataHandler(fds));
            mbp.setFileName(fds.getName());
            return mbp;
    }
}