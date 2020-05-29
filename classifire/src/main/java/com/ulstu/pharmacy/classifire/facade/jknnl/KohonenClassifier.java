package com.ulstu.pharmacy.classifire.facade.jknnl;

import com.ulstu.pharmacy.classifire.facade.ClassifierFacade;
import com.ulstu.pharmacy.classifire.facade.jknnl.data.LearningDataList;
import kohonen.DefaultNetworkModel;
import kohonen.LearningDataModel;
import kohonen.WTALearningFunction;
import learningFactorFunctional.ConstantFunctionalFactor;
import metrics.EuclidesMetric;
import topology.MatrixTopology;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KohonenClassifier implements ClassifierFacade {

    /**
     * Подсчитывает веса для классов.
     *
     * @param data       данные, которые необходимо классифицировать.
     * @param classCount количество классов, на которые надо поделить данные.
     * @return почитанные веса для каждого класса.
     */
    @Override
    public List<double[]> classify(List<double[]> data, Integer classCount) {
        StringBuilder errors = new StringBuilder();
        errors.append(this.checkInputData(data));
        errors.append(this.checkClassCount(classCount));
        errors.append(classCount >= data.size() ? "Class count must be < then data list  size; " : "");

        if (!errors.toString().isBlank()) {
            throw new RuntimeException(errors.toString());
        }

        MatrixTopology topology = new MatrixTopology(data.get(0).length, classCount);

        double[] maxWeights = this.getMaxWeights(data);

        DefaultNetworkModel network = new DefaultNetworkModel(data.get(0).length, maxWeights, topology);

        ConstantFunctionalFactor constantFactor = new ConstantFunctionalFactor(0.98);

        LearningDataModel learningData = new LearningDataList(data);

        WTALearningFunction learning = new WTALearningFunction(network,50000, new EuclidesMetric(), learningData, constantFactor);

        learning.learn();

        int neuronsCount = data.get(0).length * classCount;
        List<double[]> result = new ArrayList<>(neuronsCount);

        for (int neuronNum = 0; neuronNum < neuronsCount; neuronNum++) {
            result.add(
                    network.getNeuron(neuronNum).getWeight()
            );
        }

        return result;
    }

    /**
     * Высчитывает на основе входных данных максимальные веса для каждого фактора,
     * по которым будет классификация.
     * @param data
     * @return
     */
    private double[] getMaxWeights(List<double[]> data) {
        // Максимальные веса для каждого фактора, по которым будет классификация.
        double[] maxWeights = data.get(0);
        // Ищем максимальные значения для каждого фактора.
        for (int factorNum = 0; factorNum < maxWeights.length; factorNum++) {
            for (var vector : data) {
                if (vector[factorNum] > maxWeights[factorNum]) {
                    maxWeights[factorNum] = vector[factorNum];
                }
            }
        }
        return maxWeights;
    }

    private String checkInputData(List<double[]> data) {
        if (Objects.isNull(data)) {
            return "Input data list is null; ";
        }

        if (data.isEmpty()) {
            return "Input data list is empty; ";
        }

        int commonLength = data.get(0).length;
        for (var vector : data) {
            if (vector.length != commonLength) {
                return "Vectors have different lengths; ";
            }
        }

        return "";
    }

    private String checkClassCount(Integer classCount) {
        if (Objects.isNull(classCount)) {
            return "Class count is null; ";
        }
        if (classCount <= 1) {
            return "Class count must be > 1, but it is " + classCount;
        }

        return "";
    }
}