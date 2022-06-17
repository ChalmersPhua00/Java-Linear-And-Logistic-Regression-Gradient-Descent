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
usedCarsOne walk-through:
// Part 1 CSV to Java
1. The program begins by making the CSV dataset readable in Java
// Part 2 Data Preparation
1. splitData is an identical copy of data
2. The program randomly selects and then remove 4/5 (80%) of data from splitData to trainingData
3. The rest of splitData becomes validationData
// Part 3 Training
1. trainingData goes to usedCarsOneTrainingAlgorithm() whenever called
2. To minimize looping range later, get the smallest and largest possible price threshold from trainingData
3. From the smallest to the largest possible price, use usedCarsOneEvaluation() to find the maxAccuracy amongst them
4. The price with the maxAccuracy becomes the bestThreshold price for validation later
// Part 4 Helper Methods
1. usedCarsOneEvaluation(List<CarsOne> data, int priceThreshold)
   -- Determine the accuracy of a priceThreshold on data, using usedCarsOnePredictor() to predict the output of each data
2. usedCarsOnePredictor(CarsOne car, int priceThreshold)
   -- Compare car.getPrice with priceThreshold to predict whether the car can be sold or not
// Part 5 Validation
1. To validate, in the main method,
   print usedCarsOneEvaluation(validationData, usedCarsOneTrainingAlgorithm(trainingData)),
   -- First parameter: The other 20% of splitData not selected for trainingData
   -- Second parameter: The best price threshold found through running trainingData in usedCarsOneTrainingAlgorithm()
2. Note: The printed accuracy varies every time the program runs,
         because trainingData and validationData gets filled with random data from splitData everytime it runs
// Part 6 MultiPlotter3
1. generateData() generates the entire usedCarsOne data directly from the CSV file for MultiPlotter3 within an adjusted price threshold range
*/
public class usedCarsOne {
    public static void main(String[] args) {
        // ex: /*FILE DIRECTORY*/ = /Users/johndoe/Desktop/Datasets/usedCarsOneData.csv
        List<CarsOne> data = getCarOneFromCSV("/*FILE DIRECTORY*/");
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
        // Find the right price threshold that can get the highest accuracy out of trainingData
        double maxAccuracy = usedCarsOneEvaluation(trainingData, minPrice);
        int bestThreshold = minPrice;
        for (int i = minPrice+1; i <= maxPrice; i++) {
            if (usedCarsOneEvaluation(trainingData, i) > maxAccuracy) {
                maxAccuracy = usedCarsOneEvaluation(trainingData, i);
                bestThreshold = i;
            }
        }
        return bestThreshold;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static List<CarsOne> getCarOneFromCSV(String file) {
        List<CarsOne> data = new ArrayList<>();
        Path pathToFile = Paths.get(file);
        try(BufferedReader br = Files.newBufferedReader(pathToFile)){
            String row = br.readLine();
            while (row != null) {
                String [] attributes = row.split(",");
                CarsOne cars = createCarOne(attributes);
                data.add(cars);
                row = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static CarsOne createCarOne(String[] attributes) {
        int car, price, sold;
        try {
            car = Integer.parseInt(attributes[0]);
        }
        catch (Exception e) { car = 0; }
        try {
            price = Integer.parseInt(attributes[1]);
        }
        catch (Exception e) { price = 0; }
        try {
            sold = Integer.parseInt(attributes[2]);
        }
        catch (Exception e) { sold = 1010; }
        CarsOne cars = new CarsOne(car, price, sold);
        return cars;
    }
}