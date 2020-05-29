package com.ulstu.pharmacy.web.report;

import com.ulstu.pharmacy.mail.ejb.MailSenderEjbLocal;
import com.ulstu.pharmacy.mail.facade.model.MailMessage;
import com.ulstu.pharmacy.report.ReportEjbLocal;
import com.ulstu.pharmacy.report.model.MedicamentMovementReportViewModel;
import com.ulstu.pharmacy.report.template.FileModel;
import com.ulstu.pharmacy.web.helper.MessagesHelper;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ViewScoped
@ManagedBean
public class MedicamentMovementBean {

    private List<Date> fromToDates;

    private List<MedicamentMovementReportViewModel> data;

    private ReportEjbLocal reportEjb;

    private MailSenderEjbLocal mailSenderEjb;

    private String mailAddressTo;

    private FileModel reportFile;

    @PostConstruct
    public void init() {
        fromToDates = new LinkedList<>();
        data = new LinkedList<>();
    }

    @Inject
    public void setReportEjb(ReportEjbLocal reportEjb) {
        this.reportEjb = reportEjb;
    }

    @Inject
    public void setMailSenderEjb(MailSenderEjbLocal mailSenderEjb) {
        this.mailSenderEjb = mailSenderEjb;
    }

    public void createReportAndSendToMail() {
        try {
            this.createFile();
            if(Objects.isNull(fromToDates)) {
                throw new RuntimeException(new Throwable("Выберите период!"));
            }
            this.data = reportEjb.getMedicamentMovementFromToDate(
                    new Timestamp(fromToDates.get(0).getTime()),
                    new Timestamp(fromToDates.get(1).getTime()));
            this.sendReportOnMail();
        } catch (Exception ex) {
            MessagesHelper.errorMessage(ex);
        }
    }

    public StreamedContent downloadFile() {
        try {
            if(Objects.isNull(fromToDates)) {
                throw new RuntimeException(new Throwable("Выберите период!"));
            }
            this.createFile();
            return DefaultStreamedContent.builder()
                    .name(new Date() + ".pdf")
                    .contentType("file/pdf")
                    .stream(() -> {
                        try {
                            return new FileInputStream(this.reportFile.getFile());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).build();
        } catch (Exception ex) {
            MessagesHelper.errorMessage(ex);
        }
        return null;
    }

    public void sendReportOnMail() {
        try {
            this.mailSenderEjb.sendMessage(
                    new InternetAddress(this.mailAddressTo),
                    MailMessage.builder()
                            .theme("Отчет по движению медикаментов " + new Date())
                            .text("Движение медикаментов с " + fromToDates.get(0) + " по " + fromToDates.get(1))
                            .files(List.of(reportFile.getFile()))
                            .build()
        );
        } catch (Exception ex) {
            MessagesHelper.errorMessage(ex);
        }
    }

    private void createFile() {
        this.reportFile = reportEjb.createPdfMedicamentMovementReport(
                new Timestamp(fromToDates.get(0).getTime()),
                new Timestamp(fromToDates.get(1).getTime()));
    }
}