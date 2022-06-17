import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/*
usedCarsOneTernarySearch walk-through:
The program of usedCarsOneTernarySearch is similar to usedCarsOne
The exact differences between usedCarsOneTernarySearch and usedCarsOne:
-- usedCarsOneTernarySearch uses ternary search while usedCarsOne uses brute-force
-- Bottom part of usedCarsOneTrainingAlgorithm() has changed to a single line calling and returning a new helper method optimizeByThirds() instead of a brute-force for loop
-- optimizeByThirds() --> A ternary search method returning the best threshold value
!!!! The problem with optimizeByThirds() !!!!
I noticed that for optimizeByThirds(), I got to know the estimated appearance of the accuracy/threshold graph to determine a particular code
Example to explain what I mean:
--Assume that optimizeByThirds(data, minPrice, maxPrice)
--sector1 = minPrice = 0 | sector3 = maxPrice = 3000
--So, at the beginning sector1 should be 1000 and sector2 should be 2000
--Now the program suppose to compare the accuracy at sector1 = 1000 and sector2 = 2000
--HOWEVER, for certain data like usedCarsOne, the accuracy from 0 to 2000 is a flat line, while the peak only starts to rise between 2000 to 3000
The accuracy now at sector1 == sector2, so which side to eliminate?
In this case I should keep sector2, so at optimizeByThirds, I...
------------------------------------------------------------------------------------------------------------------
if (usedCarsOneEvaluation(data, sectorOne) <= usedCarsOneEvaluation(data, sectorTwo)) {
   sectorZero = sectorOne;
} else {
   sectorThree = sectorTwo;
}
------------------------------------------------------------------------------------------------------------------
In an opposite scenario, where a flat line from 1000 to 3000 instead, I should...
if (usedCarsOneEvaluation(data, sectorOne) >= usedCarsOneEvaluation(data, sectorTwo)) {
   sectorThree = sectorTwo;
} else {
   sectorZero = sectorOne;
}
------------------------------------------------------------------------------------------------------------------
*/
public class usedCarsOneTernarySearch {
    public static void main(String[] args) {
        // ex: /*FILE DIRECTORY*/ = /Users/johndoe/Desktop/Datasets/usedCarsOneData.csv
        List<CarsOne> data = usedCarsOne.getCarOneFromCSV("/*FILE DIRECTORY*/");
        // Randomly split 4/5 of the data for training and 1/5 for validation
        List<CarsOne> splitData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            splitData.add(data.get(i));
        }
        List<CarsOne> trainingData = new ArrayList<>();
        int numberOfDataForTraining = splitData.size()*4/5;
        Random r = new Random();
        for (int i = 0; i < numberOfDataForTraining; i++) {
            int randomIndex = r.nextInt(splitData.size());
            trainingData.add(splitData.get(randomIndex));
            splitData.remove(randomIndex);
        }
        List<CarsOne> validationData = splitData;
////// VALIDATION /////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("Validation accuracy: " + usedCarsOneEvaluation(validationData, usedCarsOneTrainingAlgorithm(trainingData)));
////// MULTIPLOTTER3 //////////////////////////////////////////////////////////////////////////////////////////////
        // Maximum accuracy for data found using MultiPlotter3
        System.out.println("usedCarsOne max accuracy: " + usedCarsOneEvaluation(data, 26464));
        // Find the ultimate most accurate price threshold value using MultiPlotter3
        final MultiPlotterData3 theData = generateData(data);
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                final var plotter2 = new MultiPlotter3("usedCarsOne Accuracy/Price threshold graph", "Price Threshold", "Accuracy", theData, MultiPlotter3.PlotType.LinePlot);
                plotter2.setVisible(true);
            }
        });
    }

    public static MultiPlotterData3 generateData(List<CarsOne> data) {
        ArrayList<Double> accuracyData = new ArrayList<>();
        ArrayList<Double> xValues = new ArrayList<>();
        final int thresholdStartRange = 20000;
        final int thresholdEndRange = 30000;
        for (int i = thresholdStartRange; i <= thresholdEndRange; i++) {
            double  xValue = i;
            xValues.add(xValue);
            accuracyData.add(usedCarsOneEvaluation(data, i));
        }
        var multiPlotterData = new MultiPlotterData3();
        multiPlotterData.AddXYData("usedCarsOne", xValues, accuracyData);
        return multiPlotterData;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double usedCarsOneEvaluation(List<CarsOne> data, int priceThreshold) {
        double numberCorrectPredictions = 0;
        for (int i = 0; i < data.size(); i++) {
            if (usedCarsOnePredictor(data.get(i), priceThreshold) == data.get(i).getSold()) {
                numberCorrectPredictions++;
            }
        }
        return numberCorrectPredictions/data.size();
    }

    public static int usedCarsOnePredictor(CarsOne car, int priceThreshold) {
        if (car.getPrice() >= priceThreshold) {
            return 0;
        }
        return 1;
    }
////// TRAINING ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static int usedCarsOneTrainingAlgorithm(List<CarsOne> trainingData) {
        // Find the largest and the smallest possible price threshold from trainingData
        int maxPrice = trainingData.get(0).getPrice();
        int minPrice = trainingData.get(0).getPrice();
        for (int i = 1; i < trainingData.size(); i++) {
            if (trainingData.get(i).getPrice() > maxPrice) {
                maxPrice = trainingData.get(i).getPrice();
            }
            if (trainingData.get(i).getPrice() < minPrice) {
                minPrice = trainingData.get(i).getPrice();
            }
        }
        // Find the right price threshold using ternary search
        return optimizeByThirds(trainingData, minPrice, maxPrice);
    }

    public static int optimizeByThirds(List<CarsOne> data, int sectorZero, int sectorThree) {
        while (sectorThree - sectorZero > 2) {
            int sectorOne = sectorZero + (sectorThree - sectorZero) / 3;
            int sectorTwo = sectorThree - (sectorThree - sectorZero) / 3;
            if (usedCarsOneEvaluation(data, sectorOne) <= usedCarsOneEvaluation(data, sectorTwo)) {
                sectorZero = sectorOne;
            } else {
                sectorThree = sectorTwo;
            }
        }
        return (sectorZero+sectorThree)/2;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}