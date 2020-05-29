package com.ulstu.pharmacy.classifire.medicament.ejb;

import com.ulstu.pharmacy.classifire.facade.ClassifierFacade;
import com.ulstu.pharmacy.classifire.medicament.category.PriceCategory;
import com.ulstu.pharmacy.pmmsl.medicament.ejb.MedicamentEjbLocal;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class MedicamentPriceClassifierEjb implements MedicamentPriceClassifierEjbLocal {

    private final MedicamentEjbLocal medicamentEjb;

    private final ClassifierFacade classifier;

    private final Integer priceClassCount = 4;

    @Inject
    public MedicamentPriceClassifierEjb(MedicamentEjbLocal medicamentEjb,
                                        ClassifierFacade classifier) {
        this.medicamentEjb = medicamentEjb;
        this.classifier = classifier;
    }

    @Override
    public Map<PriceCategory, List<MedicamentViewModel>> classify() {
        var medicaments = medicamentEjb.getAll();

        List<double[]> classWeights = classifier.classify(
                this.createDataClassifier(medicaments),
                priceClassCount
        );

        return this.distributeToClassesWeights(medicaments, classWeights);
    }

    private Map<PriceCategory, List<MedicamentViewModel>> distributeToClassesWeights(
            List<MedicamentViewModel> medicaments,
            List<double[]> classWeights) {
        List<Double> sortedWeights = classWeights.stream()
                .map(doubles -> doubles[0])
                .sorted()
                .collect(Collectors.toList());

        Map<PriceCategory, List<MedicamentViewModel>> result = new LinkedHashMap<>(medicaments.size());

        double currentWeight = -1;
        double nextWeight = -1;
        for (int counter = 0; counter < sortedWeights.size() - 1; counter++) {
            List<MedicamentViewModel> medicamentsInClass = new LinkedList<>();
            currentWeight = sortedWeights.get(counter);
            nextWeight    = sortedWeights.get(counter + 1);
            for (var medicament : medicaments) {
                double medicamentPrice = medicament.getPrice().doubleValue();
                if(Math.abs(medicamentPrice - currentWeight) <=
                   Math.abs(medicamentPrice - nextWeight)) {
                    medicamentsInClass.add(medicament);
                }
            }
            medicaments.removeAll(medicamentsInClass);
            result.put(
                    PriceCategory.builder().
                            threshold((int) currentWeight)
                            .build(),
                    medicamentsInClass
            );
        }

        result.put(
                PriceCategory.builder().
                        threshold((int) nextWeight)
                        .build(),
                medicaments
        );

        return result;
    }

    private List<double[]> createDataClassifier(List<MedicamentViewModel> medicaments) {
        List<double[]> data = new LinkedList<>();

        for (var medicament : medicaments) {
            data.add( new double[] { medicament.getPrice().doubleValue() });
        }

        return data;
    }
}