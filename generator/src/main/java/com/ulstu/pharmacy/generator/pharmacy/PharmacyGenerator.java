package com.ulstu.pharmacy.generator.pharmacy;

import com.ulstu.pharmacy.generator.random.RandomHelper;
import com.ulstu.pharmacy.pmmsl.pharmacy.binding.PharmacyBindingModel;
import com.ulstu.pharmacy.pmmsl.pharmacy.ejb.PharmacyEjbLocal;
import lombok.SneakyThrows;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class PharmacyGenerator {

    private PharmacyEjbLocal pharmacyEjb;

    private Integer pharmacyCountForGenerate;

    @Inject
    public void setPharmacyEjb(PharmacyEjbLocal pharmacyEjb) {
        this.pharmacyEjb = pharmacyEjb;
    }

    @PostConstruct
    public void init() {
        var pharmacys = pharmacyEjb.getAll();
        if (!pharmacys.isEmpty()) {
            return;
        }
        this.initProperties();
        for (int i = 0; i < pharmacyCountForGenerate; i++) {
            pharmacyEjb.create(
                    PharmacyBindingModel.builder()
                            .name(this.generateName())
                            .build());
        }
    }

    @SneakyThrows
    private void initProperties() {
        var config = new PropertiesConfiguration("generators.properties");
        this.pharmacyCountForGenerate = config.getInt("pharmacy.count");
    }

    private String generateName() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] name = new char[8];
        int rand;
        for (int i = 0; i< 8; i++) {
            rand = RandomHelper.randomInRange(0, chars.length - 1);
            name[i] = chars[rand];
        }
        return new String(name);
    }
}