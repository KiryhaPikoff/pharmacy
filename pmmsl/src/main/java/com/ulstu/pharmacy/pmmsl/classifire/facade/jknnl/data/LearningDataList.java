package com.ulstu.pharmacy.pmmsl.classifire.facade.jknnl.data;

import kohonen.LearningDataModel;

import java.util.List;

public class LearningDataList implements LearningDataModel {

    private List<double[]> dataList;

    public LearningDataList(List<double[]> dataList) {
        this.dataList = dataList;
    }

    @Override
    public double[] getData(int index) {
        return dataList.get(index);
    }

    @Override
    public int getDataSize() {
        return dataList.size();
    }
}