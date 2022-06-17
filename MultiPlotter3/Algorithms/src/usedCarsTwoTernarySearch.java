import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/*
usedCarsTwoTernarySearch walk-through:
This program is a duplication of usedCarsTwo, I replaced the brute-force part with ternary search exactly the same as usedCarsOneTernarySearch
!!!! The problem with optimizeByThirds() !!!!
The problem with optimizeByThirds() is different here, the data is much easier to handle
--The minPrice and maxPrice of both Chev and Cadil tend to be around 14040 to 58900
--In this case, sector1 and sector2 tend to be around 28993 and 43947 respectively
--Fortunately, for both Chev and Cadil, the accuracies at sector1 and sector2 have quite a big difference
--Unlike usedCarsOneTernarySearch, I only used < or > here instead of <= or >= (Because I need no to be biased by adding = to either side)
--There is hardly any point (maybe impossible) where sector1 and sector2 can have the same accuracy
*/
public class usedCarsTwoTernarySearch {
    public static void main(String[] args) {
        // ex: /*FILE DIRECTORY*/ = /Users/johndoe/Desktop/Datasets/usedCarsTwoData.csv
        List<CarsTwo> data = usedCarsTwo.getCarTwoFromCSV("/*FILE DIRECTORY*/");
        // Randomly split 4/5 of the data for training and 1/5 for validation
        List<CarsTwo> splitData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            splitData.add(data.get(i));
        }
        List<CarsTwo> trainingData = new ArrayList<>();
        int numberOfDataForTraining = splitData.size()*4/5;
        Random r = new Random();
        for (int i = 0; i < numberOfDataForTraining; i++) {
            int randomIndex = r.nextInt(splitData.size());
            trainingData.add(splitData.get(randomIndex));
            splitData.remove(randomIndex);
        }
        List<CarsTwo> validationData = splitData;
////// VALIDATION /////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("Validation accuracy: " + usedCarsTwoEvaluation(validationData, usedCarsTwoTrainingAlgorithm(usedCarsTwoSortMake(trainingData, "Chev")), usedCarsTwoTrainingAlgorithm(usedCarsTwoSortMake(trainingData, "Cadil"))));
////// MULTIPLOTTER3 //////////////////////////////////////////////////////////////////////////////////////////////
        // Maximum accuracy for data found using MultiPlotter3
        System.out.println("usedCarsTwo max accuracy: " + usedCarsTwoEvaluation(data, 27180, 42335));
        // Find the ultimate most accurate price threshold value using MultiPlotter3
        final MultiPlotterData3 theData = generateData(data);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final var plotter2 = new MultiPlotter3("usedCarsTwo Accuracy/Price Threshold graph", "Price Threshold", "Accuracy", theData, MultiPlotter3.PlotType.LinePlot);
                plotter2.setVisible(true);
            }
        });
    }

    public static MultiPlotterData3 generateData(List<CarsTwo> data) {
        ArrayList<Double> accuracyDataChev = new ArrayList<>();
        ArrayList<Double> accuracyDataCadil = new ArrayList<>();
        ArrayList<Double> xValues = new ArrayList<>();
        final int thresholdStartRange = 20000;
        final int thresholdEndRange = 50000;
        for (int i = thresholdStartRange; i <= thresholdEndRange; i++) {
            double  xValue = i;
            xValues.add(xValue);
            accuracyDataChev.add(usedCarsTwoEvaluation(usedCarsTwoSortMake(data, "Chev"), i, -1));
            accuracyDataCadil.add(usedCarsTwoEvaluation(usedCarsTwoSortMake(data, "Cadil"), -1, i));
        }
        var multiPlotterData = new MultiPlotterData3();
        multiPlotterData.AddXYData("Chev", xValues, accuracyDataChev);
        multiPlotterData.AddXYData("Cadil", xValues, accuracyDataCadil);
        return multiPlotterData;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double usedCarsTwoEvaluation(List<CarsTwo> data, int thresholdChev, int thresholdCadil) {
        double numberCorrectPredictions = 0;
        for (int i = 0; i < data.size(); i++) {
            if (usedCarsTwoPredictor(data.get(i), thresholdChev, thresholdCadil).equals(data.get(i).getSold())) {
                numberCorrectPredictions++;
            }
        }
        return numberCorrectPredictions/data.size();
    }

    public static String usedCarsTwoPredictor(CarsTwo car, int thresholdChev, int thresholdCadil) {
        if (car.getMake().equals("Chev")) {
            if (car.getPrice() >= thresholdChev) {
                return "no";
            }
        } else if (car.getMake().equals("Cadil")) {
            if (car.getPrice() >= thresholdCadil) {
                return "no";
            }
        }
        return "yes";
    }

    public static ArrayList<CarsTwo> usedCarsTwoSortMake(List<CarsTwo> data, String carMake) {
        ArrayList<CarsTwo> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getMake().equals(carMake)) {
                result.add(data.get(i));
            }
        }
        return result;
    }
////// TRAINING ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static int usedCarsTwoTrainingAlgorithm(List<CarsTwo> trainingData) {
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

    public static int optimizeByThirds(List<CarsTwo> data, int sectorZero, int sectorThree) {
        while (sectorThree - sectorZero > 2) {
            int sectorOne = sectorZero + (sectorThree - sectorZero) / 3;
            int sectorTwo = sectorThree - (sectorThree - sectorZero) / 3;
            if (usedCarsTwoEvaluation(data, sectorOne, sectorOne) < usedCarsTwoEvaluation(data, sectorTwo, sectorTwo)) {
                sectorZero = sectorOne;
            } else {
                sectorThree = sectorTwo;
            }
        }
        return (sectorZero+sectorThree)/2;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
