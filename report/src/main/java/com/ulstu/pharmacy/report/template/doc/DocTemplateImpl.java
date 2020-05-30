package com.ulstu.pharmacy.report.template.doc;

import com.ulstu.pharmacy.report.facade.doc.DocFacade;
import com.ulstu.pharmacy.report.facade.util.Style;
import com.ulstu.pharmacy.report.model.MedicalServiceReportViewModel;
import com.ulstu.pharmacy.report.template.FileExtension;
import com.ulstu.pharmacy.report.template.FileModel;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

public class DocTemplateImpl implements DocTemplate {

    private final DocFacade docFacade;

    @Inject
    public DocTemplateImpl(DocFacade docFacade) {
        this.docFacade = docFacade;
    }

    @Override
    public FileModel getMedicalServiceWithMedicaments(List<MedicalServiceReportViewModel> medicalServiceModels) {
        Style headerStyle = Style.builder()
                .fontSize(16)
                .isBold(true)
                .isInCenter(true)
                .build();
        Style dateStyle = Style.builder()
                .fontSize(14)
                .isBold(true)
                .isInCenter(false)
                .build();

        docFacade.createNew();
        docFacade.append("Список услуг с использованными медикаментами", headerStyle);
        docFacade.newLine();
        for (var medicalServiceModel : medicalServiceModels) {
            docFacade.append(medicalServiceModel.getDate().toString(), dateStyle);
            docFacade.newLine();
            medicalServiceModel.getMedicamentsCount().forEach(medicamentCountModel -> {
                docFacade.append("\t" + medicamentCountModel.getMedicamentName());
                docFacade.newLine();
            });
            docFacade.newLine();
        }

        return this.toFileModel(
                docFacade.createFile()
        );
    }

    private FileModel toFileModel(File file) {
        return FileModel.builder()
                .title(file.getName())
                .fileExtension(FileExtension.DOC)
                .file(file)
                .build();
    }
}