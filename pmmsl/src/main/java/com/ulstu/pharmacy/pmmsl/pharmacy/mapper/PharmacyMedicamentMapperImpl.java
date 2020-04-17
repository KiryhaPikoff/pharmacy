package com.ulstu.pharmacy.pmmsl.pharmacy.mapper;

import com.ulstu.pharmacy.pmmsl.medicament.mapper.MedicamentMapper;
import com.ulstu.pharmacy.pmmsl.pharmacy.entity.PharmacyMedicament;
import com.ulstu.pharmacy.pmmsl.pharmacy.view.PharmacyMedicamentViewModel;

import javax.inject.Inject;
import java.util.Objects;

public class PharmacyMedicamentMapperImpl implements PharmacyMedicamentMapper {

    private final MedicamentMapper medicamentMapper;

    @Inject
    public PharmacyMedicamentMapperImpl(MedicamentMapper medicamentMapper) {
        this.medicamentMapper = medicamentMapper;
    }

    @Override
    public PharmacyMedicament toEntity(PharmacyMedicamentViewModel pharmacyMedicamentViewModel) {
        PharmacyMedicament pharmacyMedicament = null;
        if (Objects.nonNull(pharmacyMedicamentViewModel)) {
            pharmacyMedicament = PharmacyMedicament.builder()
                    .medicament(
                            medicamentMapper.toEntity(
                                    pharmacyMedicamentViewModel.getMedicament()
                            )
                    )
                    .count(pharmacyMedicamentViewModel.getCount())
                    .receiptDate(pharmacyMedicamentViewModel.getReceiptDate())
                    .build();
            pharmacyMedicament.setId(pharmacyMedicamentViewModel.getId());
        }
        return pharmacyMedicament;
    }

    @Override
    public PharmacyMedicamentViewModel toViewModel(PharmacyMedicament pharmacyMedicament) {
        return Objects.isNull(pharmacyMedicament) ? null :
                PharmacyMedicamentViewModel.builder()
                        .id(pharmacyMedicament.getId())
                        .count(pharmacyMedicament.getCount())
                        .medicament(
                                medicamentMapper.toViewModel(
                                        pharmacyMedicament.getMedicament()
                                )
                        )
                        .receiptDate(pharmacyMedicament.getReceiptDate())
                        .build();
    }
}