package com.ulstu.pharmacy.web.report;

import com.ulstu.pharmacy.mail.ejb.MailSenderEjbLocal;
import com.ulstu.pharmacy.mail.facade.model.MailMessage;
import com.ulstu.pharmacy.pmmsl.medservice.ejb.MedicalServiceEjbLocal;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import com.ulstu.pharmacy.report.ReportEjbLocal;
import com.ulstu.pharmacy.web.helper.MessagesHelper;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ViewScoped
@ManagedBean
public class MedicalServiceReportBean {

    private ReportEjbLocal reportEjb;

    private MedicalServiceEjbLocal medicalServiceEjb;

    private MailSenderEjbLocal mailSenderEjb;

    private List<MedicalServiceViewModel> selectedMedicalServices;

    private List<MedicalServiceViewModel> allMedicalServices;

    private String mailAddressTo;

    @Inject
    public void setReportEjb(ReportEjbLocal reportEjb) {
        this.reportEjb = reportEjb;
    }

    @Inject
    public void setMedicalServiceEjb(MedicalServiceEjbLocal medicalServiceEjb) {
        this.medicalServiceEjb = medicalServiceEjb;
    }

    @Inject
    public void setMailSenderEjb(MailSenderEjbLocal mailSenderEjb) {
        this.mailSenderEjb = mailSenderEjb;
    }

    @PostConstruct
    public void init() {
        this.allMedicalServices = this.medicalServiceEjb.getAll();
        this.selectedMedicalServices = new LinkedList<>();
    }

    public void addToSelected(MedicalServiceViewModel medicalService) {
        selectedMedicalServices.add(medicalService);
        allMedicalServices.remove(medicalService);
    }


    public void removeFromSelected(MedicalServiceViewModel medicalService) {
        selectedMedicalServices.remove(medicalService);
        allMedicalServices.add(medicalService);
    }

    public void sendDocToMail() {
        try {
            this.checkSelectedListNotEmpty();
            this.sendReportToMail(MailMessage.builder()
                    .theme("Отчет по выбранным услугам " + new Date())
                    .text("Вы выбрали следующие услуги в формате .doc: ")
                    .files(
                            List.of(
                                    this.reportEjb.createDocMedicalServiceReport(
                                            this.getIdsOfSelectedMedServices()
                                    ).getFile()
                            )
                    )
                    .build());
        } catch (Exception ex) {
            MessagesHelper.errorMessage(ex);
        }
    }

    public void sendXlsToMail() {
        try {
            this.checkSelectedListNotEmpty();
            this.sendReportToMail(MailMessage.builder()
                    .theme("Отчет по выбранным услугам " + new Date())
                    .text("Вы выбрали следующие услуги в формате .xls: ")
                    .files(
                            List.of(
                                    this.reportEjb.createXlsMedicalServiceReport(
                                            this.getIdsOfSelectedMedServices()
                                    ).getFile()
                            )
                    )
                    .build());
        } catch (Exception ex) {
            MessagesHelper.errorMessage(ex);
        }
    }

    private void checkSelectedListNotEmpty() {
        if (this.selectedMedicalServices.isEmpty()) {
            throw new RuntimeException(new Throwable("Выберите услуги для формирования отчета!"));
        }
    }

    private Set<Long> getIdsOfSelectedMedServices() {
        return selectedMedicalServices.stream()
                .map(MedicalServiceViewModel::getId)
                .collect(Collectors.toSet());
    }

    private void sendReportToMail(MailMessage message) {
        try {
            this.mailSenderEjb.sendMessage(new InternetAddress(this.mailAddressTo), message);
        } catch (Exception ex) {
            MessagesHelper.errorMessage(ex);
        }
    }
}