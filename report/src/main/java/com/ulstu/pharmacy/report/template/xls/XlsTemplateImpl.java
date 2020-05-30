package com.ulstu.pharmacy.report.template.xls;

import com.ulstu.pharmacy.report.facade.xls.XlsFacade;
import com.ulstu.pharmacy.report.facade.xls.helper.CellProperty;
import com.ulstu.pharmacy.report.model.MedicalServiceReportViewModel;
import com.ulstu.pharmacy.report.template.FileExtension;
import com.ulstu.pharmacy.report.template.FileModel;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

public class XlsTemplateImpl implements XlsTemplate {

    private final XlsFacade xlsFacade;

    @Inject
    public XlsTemplateImpl(XlsFacade xlsFacade) {
        this.xlsFacade = xlsFacade;
    }

    @Override
    public FileModel getMedicalServiceWithMedicaments(List<MedicalServiceReportViewModel> medicalServiceModels) {
        xlsFacade.createNew();
        xlsFacade.write("Дата списания услуги", new CellProperty(0, 0));
        xlsFacade.write("Список использованных медикаментов", new CellProperty(0, 1));
        writeMedicalServices(medicalServiceModels, new CellProperty(1, 0));

        return toFileModel(
            xlsFacade.createFile()
        );
    }

    private void writeMedicalServices(List<MedicalServiceReportViewModel> medicalServiceModels,
                                      CellProperty leftTopCell) {
        CellProperty currentCellProperty = leftTopCell;
        for (var medicalServiceModel : medicalServiceModels) {
            writeMedicalService(medicalServiceModel, currentCellProperty);
            int medicamentListSize = medicalServiceModel.getMedicamentsCount().size();
            currentCellProperty = new CellProperty(
                    currentCellProperty.getRow() + medicamentListSize,
                    currentCellProperty.getColumn()
            );
        }
    }

    private void writeMedicalService(MedicalServiceReportViewModel medicalServiceModel, CellProperty leftTopCell) {
        int currentRow = leftTopCell.getRow();
        xlsFacade.write(
                medicalServiceModel.getDate().toString(),
                new CellProperty(currentRow, leftTopCell.getColumn())
        );
        for (var medicamentCountModel : medicalServiceModel.getMedicamentsCount()) {
            xlsFacade.write(
                    medicamentCountModel.getMedicamentName(),
                    new CellProperty(currentRow, leftTopCell.getColumn() + 1)
            );
            currentRow++;
        }
    }

    private FileModel toFileModel(File file) {
        return FileModel.builder()
                .title(file.getName())
                .fileExtension(FileExtension.XLS)
                .file(file)
                .build();
    }
}