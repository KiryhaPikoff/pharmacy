package com.ulstu.pharmacy.classifire.medicament.ejb;

import com.ulstu.pharmacy.classifire.medicament.category.PriceCategory;
import com.ulstu.pharmacy.pmmsl.medicament.view.MedicamentViewModel;

import javax.ejb.Local;
import java.util.List;
import java.util.Map;

@Local
public interface MedicamentPriceClassifierEjbLocal {

    Map<PriceCategory, List<MedicamentViewModel>> classify(Integer priceClassCount);
}