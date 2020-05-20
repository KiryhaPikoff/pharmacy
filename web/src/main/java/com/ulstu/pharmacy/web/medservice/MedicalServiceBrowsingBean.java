package com.ulstu.pharmacy.web.medservice;

import com.ulstu.pharmacy.pmmsl.medservice.ejb.MedicalServiceEjbLocal;
import com.ulstu.pharmacy.pmmsl.medservice.view.MedicalServiceViewModel;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.List;

@Getter
@Setter
@ViewScoped
@ManagedBean
public class MedicalServiceBrowsingBean {

    private MedicalServiceEjbLocal medicalServiceEjb;

    private List<MedicalServiceViewModel> medicalServices;

    @PostConstruct
    public void initValues() {
        this.medicalServices = medicalServiceEjb.getAll();
    }

    @Inject
    public void setMedicalServiceEjb(MedicalServiceEjbLocal medicalServiceEjb) {
        this.medicalServiceEjb = medicalServiceEjb;
    }
}