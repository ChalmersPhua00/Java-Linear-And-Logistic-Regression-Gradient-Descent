import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LinearRegression {
    public static void main(String[] args) {
        List<DataItem> data = getDataFromCSV("/Users/chalmersphua/Documents/College Honors Project/honorsData/yahooStock.csv");
        for (int i = 0; i < data.get(0).xVectors.size(); i++) {
            if (featureMinAndMax(data, i).get(1) - featureMinAndMax(data, i).get(0) > 2) {
                double xMin = featureMinAndMax(data, i).get(0);
                double xMax = featureMinAndMax(data, i).get(1);
                for (int j = 0; j < data.size(); j++) {
                    data.get(j).xVectors.set(i, scaleX(data.get(j).xVectors.get(i), xMin, xMax));
                }
            }
        }

        List<DataItem> tempData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            tempData.add(data.get(i));
        }
        List<DataItem> trainingData = new ArrayList<>();
        int trainingDataSize = tempData.size()*4/5;
        Random r = new Random();
        for (int i = 0; i < trainingDataSize; i++) {
            int randomIndex = r.nextInt(tempData.size());
            trainingData.add(tempData.get(randomIndex));
            tempData.remove(randomIndex);
        }
        List<DataItem> validationData = tempData;

        ArrayList<Double> thetas = new ArrayList<>();
        for (int i = 0; i < data.get(0).xVectors.size(); i++) {
            thetas.add(0.0);
        }
        System.out.println("Cost: " + cost(validationData, gD(trainingData, thetas, 0.3, 500)));

        for (int i = 0; i < validationData.size(); i++) {
            System.out.println("Actual: " + validationData.get(i).y + " | Predicted: " + yPredictor(validationData.get(i).xVectors, thetas));
        }
////// MULTIPLOTTER3 //////////////////////////////////////////////////////////////////////////////////////////////
        MultiPlotterData3 dataPlotOne = generateData(trainingData);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final var plotter3 = new MultiPlotter3("Cost/Epoch graph", "Epoch", "Cost", dataPlotOne, MultiPlotter3.PlotType.LinePlot);
                plotter3.setVisible(true);
            }
        });

        MultiPlotterData3 dataPlotTwo = generateScatterPlotData(trainingData);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final var plotter3 = new MultiPlotter3("Y/X-Vectors ScatterPlot", "X-Vectors (Without X0)", "Y", dataPlotTwo, MultiPlotter3.PlotType.ScatterPlot);
                plotter3.setVisible(true);
            }
        });
    }

    public static MultiPlotterData3 generateScatterPlotData(List<DataItem> data) {
        MultiPlotterData3 output = new MultiPlotterData3();
        for (int i = 1; i < data.get(0).xVectors.size(); i++) {
            ArrayList<XYPair> tempData = new ArrayList<>();
            for (int j = 0; j < data.size(); j++) {
                tempData.add(new XYPair(data.get(j).xVectors.get(i), data.get(j).y));
            }
            output.AddXYData("X" + (i), tempData);
        }
        return output;
    }

    public static MultiPlotterData3 generateData(List<DataItem> data) {
        MultiPlotterData3 multiPlotterData = new MultiPlotterData3();
        int maxEpoch = 500;
        ArrayList<Double> alphas = new ArrayList<>();
        alphas.add(0.05);
        alphas.add(0.1);
        alphas.add(0.2);
        alphas.add(0.3);
        ArrayList<Double> xValues = new ArrayList<>();
        for (int i = 1; i <= maxEpoch; i++) {
            double xValue = i;
            xValues.add(xValue);
        }
        ArrayList<Double> thetas = new ArrayList<>();
        for (int l = 0; l < alphas.size(); l++) {
            thetas.clear();
            for (int i = 0; i < data.get(0).xVectors.size(); i++) {
                thetas.add(0.0);
            }
            multiPlotterData.AddXYData(alphas.get(l).toString(), xValues, gDAndCostByEpoch(data, thetas, alphas.get(l), maxEpoch).get(1));
        }
        return multiPlotterData;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Dataset scaling //////////////////////////////////////////////////////////////////////////////////////////////
    // Find the min and max value of a specific feature (ex. x1, x2, x3, ...),
    // amongst all instances of the dataset.
    // featureMindAndMax(data, x1).get(0) == min of x1 at data
    // featureMindAndMax(data, x1).get(1) == max of x1 at data
    public static ArrayList<Double> featureMinAndMax(List<DataItem> data, int feature) {
        ArrayList<Double> result = new ArrayList<>();
        ArrayList<Double> allXOfFeature = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            allXOfFeature.add(data.get(i).xVectors.get(feature));
        }
        double min = data.get(0).xVectors.get(feature);
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i).xVectors.get(feature) < min) {
                min = data.get(i).xVectors.get(feature);
            }
        }
        double max = data.get(0).xVectors.get(feature);
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i).xVectors.get(feature) > max) {
                max = data.get(i).xVectors.get(feature);
            }
        }
        result.add(min);
        result.add(max);
        return result;
    }

    // xMin ==> original min value | xMax ==> original max value
    // [minVal, maxVal] ==> scale range
    // x ==> original value to be scaled
    public static double scaleX(double x, double xMin, double xMax) {
        final double minVal = 0.0;
        final double maxVal = 2.0;
        return ((x - xMin)/(xMax - xMin)) * (maxVal - minVal) + minVal;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // xVectors = all dimensions (features)
    // thetas = all thetas
    // Predict the value of y given xVectors and thetas
    public static double yPredictor(ArrayList<Double> xVectors, ArrayList<Double> thetas) {
        double sum = 0;
        for (int i = 0; i < xVectors.size(); i++) {
            sum += xVectors.get(i) * thetas.get(i);
        }
        return sum;
    }

    // For each instance...
    // --Locate the actual y value from the data
    // --Predict y using yPredictor given x values from the data and thetas
    // --Square the difference between the actual and predicted y
    // Do this for every instance and sum them altogether
    // Lastly, divide it by the number of instances and multiply 1/2 for simplification
    public static double cost(List<DataItem> data, ArrayList<Double> thetas) {
        double totalSquaredCost = 0.0;
        for (int instance = 0; instance < data.size(); instance++) {
            double actualY = data.get(instance).y;
            double predictedY = yPredictor(data.get(instance).xVectors, thetas);
            double squaredCost = (actualY - predictedY) * (actualY - predictedY);
            totalSquaredCost += squaredCost;
        }
        // 0.5 * totalSquaredCost/data.size() == 1/2 * ∑(θ0 + θ1xi - yi)^2/m == 1/2m * ∑(θ0 + θ1xi - yi)^2
        return 0.5 * totalSquaredCost/data.size();
    }

    // Partial Derivative of Cost by Theta[thetaIndex]
    // Partial Derivative of cost()
    public static double partialDerivative(List<DataItem> data, ArrayList<Double> thetas, int thetaIndex) {
        double sum = 0;
        // ∑(θ0 + θ1xi - yi)xi
        for (int d = 0; d < data.size(); d++) {
            sum +=  (yPredictor(data.get(d).xVectors, thetas) - data.get(d).y) * data.get(d).xVectors.get(thetaIndex);
        }
        // sum/data.size() == sum * 1/data.size() == ∑(θ0 + θ1xi - yi)xi * 1/m
        return sum/data.size();
    }

    public static ArrayList<Double> gD(List<DataItem> data, ArrayList<Double> thetas, double alpha, int maxEpochs) {
        // For a number of times (maxEpochs)
        // Update all the thetas using gradient descent each time (epoch)
        ArrayList<Double> tempTheta = new ArrayList<>();
        for (int i = 1; i <= maxEpochs; i++) {
            for (int j = 0; j < thetas.size(); j++) {
                tempTheta.add(thetas.get(j) - alpha * partialDerivative(data, thetas, j));
            }
            // Assign tempTheta to theta
            for (int j = 0; j < thetas.size(); j++) {
                thetas.set(j, tempTheta.get(j));
            }
            tempTheta.clear();
        }
        return thetas;
    }

    // Returns an ArrayList<ArrayList<Double>> containing:
    // -- ArrayList<Double> of final thetas (gDAndCostByEpoch(...).get(0))
    // -- ArrayList<Double> of costs at each epoch (gDAndCostByEpoch(...).get(1))
    public static ArrayList<ArrayList<Double>> gDAndCostByEpoch(List<DataItem> data, ArrayList<Double> thetas, double alpha, int maxEpochs) {
        ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> costByEpoch = new ArrayList<>();
        ArrayList<Double> tempTheta = new ArrayList<>();
        // For a number of times (maxEpochs)
        // Update all the thetas using gradient descent each time (epoch)
        for (int i = 1; i <= maxEpochs; i++) {
            for (int j = 0; j < thetas.size(); j++) {
                tempTheta.add(thetas.get(j) - alpha * partialDerivative(data, thetas, j));
            }
            // Assign tempTheta to theta
            for (int j = 0; j < thetas.size(); j++) {
                thetas.set(j, tempTheta.get(j));
            }
            // Record the costs at each epoch before the theta update for the next epoch
            costByEpoch.add(cost(data, thetas));
            tempTheta.clear();
        }
        result.add(thetas);
        result.add(costByEpoch);
        return result;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// CSV dataset to JAVA dataset converter ////////////////////////////////////////////////////////////////////////
    public static List<DataItem> getDataFromCSV(String file) {
        List<DataItem> data = new ArrayList<>();
        Path pathToFile = Paths.get(file);
        try(BufferedReader br = Files.newBufferedReader(pathToFile)){
            String row = br.readLine();
            while (row != null) {
                String [] attributes = row.split(",");
                DataItem dataItem = createDataItem(attributes);
                data.add(dataItem);
                row = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static DataItem createDataItem(String[] attributes) {
        ArrayList<Double> xVectors = new ArrayList<>();
        xVectors.add(1.0);
        for (int i = 0; i < attributes.length-1; i++) {
            try {
                double x = Double.parseDouble(attributes[i]);
                xVectors.add(x);
            }
            catch (Exception e) {}
        }
        double y;
        try {
            y = Double.parseDouble(attributes[attributes.length-1]);
        }
        catch (Exception e) { y = 0; }
        DataItem dataItem = new DataItem(xVectors, y);
        return dataItem;
    }
}