package com.ulstu.pharmacy.web.medicament;

import com.ulstu.pharmacy.pmmsl.classifire.medicament.category.PriceCategory;
import com.ulstu.pharmacy.pmmsl.classifire.medicament.ejb.MedicamentPriceClassifierEjbLocal;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ViewScoped
@ManagedBean
public class MedicamentClassifierBean {

    private MedicamentPriceClassifierEjbLocal medicamentClassifierEjb;

    private Map<PriceCategory, List<MedicamentViewModel>> classifiedMedicaments;

    private LineChartModel lineModel;

    @PostConstruct
    public void init() {
        this.classifiedMedicaments = this.medicamentClassifierEjb.classify();
        this.createLineModels();
    }

    @Inject
    public void setMedicamentClassifierEjb(MedicamentPriceClassifierEjbLocal medicamentClassifierEjb) {
        this.medicamentClassifierEjb = medicamentClassifierEjb;
    }

    private void createLineModels() {
        lineModel = initLinearModel();
        lineModel.setTitle("Цены медикаментов по категориям");
        lineModel.setExtender("removeLegend");
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(this.classifiedMedicaments.entrySet().stream()
                .flatMap(priceCategoryListEntry -> priceCategoryListEntry.getValue().stream())
                .max(Comparator.comparingInt(o -> o.getPrice().intValue())
        ).get().getPrice());

        Axis xAxis = lineModel.getAxis(AxisType.X);
        xAxis.setMin(1);
        xAxis.setMax(this.classifiedMedicaments.values().stream()
                .mapToLong(List::size)
                .sum());
        xAxis.setTickInterval("1");
    }

    private LineChartModel initLinearModel() {
        LineChartModel model = new LineChartModel();

        int pointer = 1;

        for (var priceClass : classifiedMedicaments.keySet()) {
            LineChartSeries classSeries = new LineChartSeries();
            classSeries.setFill(true);
            for (var medicament : classifiedMedicaments.get(priceClass)) {
                classSeries.set(pointer, medicament.getPrice());
                pointer++;
            }
            model.addSeries(classSeries);
        }

        return model;
    }
}