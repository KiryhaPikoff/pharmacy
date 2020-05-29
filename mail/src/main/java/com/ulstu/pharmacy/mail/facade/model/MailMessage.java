package com.ulstu.pharmacy.mail.facade.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;


@Getter
@Setter
@Builder
public class MailMessage {

    private String theme;

    private String text;

    private List<File> files;
}