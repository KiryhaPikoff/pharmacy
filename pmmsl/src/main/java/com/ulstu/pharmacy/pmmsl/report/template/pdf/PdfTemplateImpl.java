package com.ulstu.pharmacy.pmmsl.report.template.pdf;

import com.ulstu.pharmacy.pmmsl.report.facade.pdf.PdfFacade;
import com.ulstu.pharmacy.pmmsl.report.model.MedicamentMovementReportViewModel;
import com.ulstu.pharmacy.pmmsl.report.template.FileExtension;
import com.ulstu.pharmacy.pmmsl.report.template.FileModel;

import javax.inject.Inject;
import java.io.File;
import java.sql.Date;
import java.util.List;

public class PdfTemplateImpl implements PdfTemplate {

    private final PdfFacade pdfFacade;

    @Inject
    public PdfTemplateImpl(PdfFacade pdfFacade) {
        this.pdfFacade = pdfFacade;
    }

    @Override
    public FileModel getMedicamentMovement(List<MedicamentMovementReportViewModel> medicamentMovementModels) {
        pdfFacade.createNew();

        for (var medicamentMovementModel : medicamentMovementModels) {
            pdfFacade.append(
                    this.getMedicamentMovementString(medicamentMovementModel)
            );
            pdfFacade.newLine();
        }

        return this.toFileModel(
                pdfFacade.createFile()
        );
    }

    private String getMedicamentMovementString(MedicamentMovementReportViewModel medicamentMovementModel) {
        StringBuilder result = new StringBuilder();

        result.append(new Date(medicamentMovementModel.getDate().getTime()));
        result.append(" - ");
        result.append(medicamentMovementModel.getMovementType());
        result.append(" - ");
        result.append(medicamentMovementModel.getMedicamentName());
        result.append(" - ");
        result.append(medicamentMovementModel.getCount());

        return result.toString();
    }

    private FileModel toFileModel(File file) {
        return FileModel.builder()
                .title(file.getName())
                .extension(FileExtension.PDF)
                .file(file)
                .build();
    }
}