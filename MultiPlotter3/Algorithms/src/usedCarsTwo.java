import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/*
usedCarsTwo walk-through:
// Part 1 CSV to Java
1. The program begins by making the CSV dataset readable in Java
// Part 2 Data Preparation
1. splitData is an identical copy of data
2. The program randomly selects and then remove 4/5 (80%) of data from splitData to trainingData
3. The rest of splitData becomes validationData
// Part 3 Training
1. usedCarsTwoTrainingAlgorithm() == usedCarsOneTrainingAlgorithm(), I'll explain why:
-------------------------------------------------------------------------------------------------------------------
usedCarsOne: trainingData goes straight to usedCarsOneTrainingAlgorithm()
Reason: Training algorithm in usedCarsOne need only to find one price threshold
-------------------------------------------------------------------------------------------------------------------
usedCarsTwo: trainingData splits into two other ArrayLists each containing data from a single car make
             Instead of trainingData, the two Arraylists with different car make enter usedCarsTwoTrainingAlgorithm() accordingly
Reason: usedCarsTwo need two price threshold, one for Chev one for Cadil
        The helper method usedCarsTwoSortMake() filters the data of the same make together
-------------------------------------------------------------------------------------------------------------------
However, on usedCarsTwoTrainingAlgorithm()...Whenever I call usedCarsTwoEvaluation()...
The Chev and Cadil price thresholds are the same [ex. usedCarsTwoEvaluation(trainingData, i, i)]
Reason: Because the data in the dataset for usedCarsTwoEvaluation() will all be the same make as mentioned
        So one of the car make won't matter
        so I might as well make them (data, i, i) or (data, i, -1) when targeting Chev or (data, -1, i) when targeting Cadil
// Part 4 Helper Methods
1. usedCarsTwoEvaluation(List<CarsTwo> data, int thresholdChev, int thresholdCadil)
   -- Same as usedCarsOne
2. usedCarsTwoPredictor(CarsTwo car, int thresholdChev, int thresholdCadil)
   -- Firstly, determine the car make is Chev or Cadil
   -- Then, compare car.getPrice with the threshold of its make to predict whether the car can be sold or not
3. usedCarsTwoSortMake(List<CarsTwo> data, String carMake)
   -- From the "data" dataset, create an ArrayList of CarsTwo with all the same carMake
// Part 5 Validation
1. To validate, in the main method,
   print usedCarsTwoEvaluation(validationData, usedCarsTwoTrainingAlgorithm(usedCarsTwoSortMake(trainingData, "Chev")), usedCarsTwoTrainingAlgorithm(usedCarsTwoSortMake(trainingData, "Cadil")))
   -- First parameter: Same as usedCarsOne
   -- Second parameter: The best price threshold for Chev found through running all the Chev of trainingData in usedCarsTwoTrainingAlgorithm()
   -- Third parameter: The best price threshold for Cadil found through running all the Cadil of trainingData in usedCarsTwoTrainingAlgorithm()
// Part 6 MultiPlotter3
1. generateData() generates the entire usedCarsTwo data directly from the CSV file for MultiPlotter3 within an adjusted price threshold range
*/
public class usedCarsTwo {
    public static void main(String[] args) {
        // ex: /*FILE DIRECTORY*/ = /Users/johndoe/Desktop/Datasets/usedCarsTwoData.csv
        List<CarsTwo> data = getCarTwoFromCSV("/*FILE DIRECTORY*/");
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
        // Find the right price threshold that can get the highest accuracy out of trainingData
        double maxAccuracy = usedCarsTwoEvaluation(trainingData, minPrice, minPrice);
        int bestThreshold = minPrice;
        for (int i = minPrice+1; i <= maxPrice; i++) {
            if (usedCarsTwoEvaluation(trainingData, i, i) > maxAccuracy) {
                maxAccuracy = usedCarsTwoEvaluation(trainingData, i, i);
                bestThreshold = i;
            }
        }
        return bestThreshold;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static List<CarsTwo> getCarTwoFromCSV(String file) {
        List<CarsTwo> data = new ArrayList<>();
        Path pathToFile = Paths.get(file);
        try(BufferedReader br = Files.newBufferedReader(pathToFile)){
            String row = br.readLine();
            while (row != null) {
                String [] attributes = row.split(",");
                CarsTwo cars = createCarTwo(attributes);
                data.add(cars);
                row = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static CarsTwo createCarTwo(String[] attributes) {
        int car;
        int price;
        String make;
        String sold;
        try {
            car = Integer.parseInt(attributes[0]);
        }
        catch (Exception e) { car = 0; }
        try {
            price = Integer.parseInt(attributes[1]);
        }
        catch (Exception e) { price = 0; }
        try {
            make = attributes[2];
        }
        catch (Exception e) { make = null; }
        try {
            sold = attributes[3];
        }
        catch (Exception e) { sold = null; }
        CarsTwo cars = new CarsTwo(car, price, make, sold);
        return cars;
    }
}