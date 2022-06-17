import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LogisticRegression {
    public static void main(String[] args) {
        List<DataItem> data = getDataFromCSV("/Users/chalmersphua/Documents/College Honors Project/honorsData/heartFailureData.csv");
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
        System.out.println("Cost: " + cost(validationData, gD(trainingData, thetas, 0.5, 800)));

        int count = 0;
        for (int i = 0; i < validationData.size(); i++) {
            if (domainPredictor(validationData.get(i).xVectors, thetas) == validationData.get(i).y) {
                count++;
            }
        }
        System.out.println("Total number of correct predictions/Total number of instances of validationData: " + count + "/" + validationData.size());
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

        MultiPlotterData3 dataPlotThree = generateFeatureVsFeatureScatterPlotData(trainingData, 1, 3);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final var plotter3 = new MultiPlotter3("X1/X3 ScatterPlot", "X1", "X3", dataPlotThree, MultiPlotter3.PlotType.ScatterPlot);
                plotter3.setVisible(true);
            }
        });
    }

    public static MultiPlotterData3 generateFeatureVsFeatureScatterPlotData(List<DataItem> data, int xAxisFeature, int yAxisFeature) {
        MultiPlotterData3 output = new MultiPlotterData3();
        ArrayList<XYPair> tempData1 = new ArrayList<>();
        ArrayList<XYPair> tempData2 = new ArrayList<>();
        for (int j = 0; j < data.size(); j++) {
            if (data.get(j).y == 0) {
                tempData1.add(new XYPair(data.get(j).xVectors.get(xAxisFeature), data.get(j).xVectors.get(yAxisFeature)));
            } else if (data.get(j).y == 1) {
                tempData2.add(new XYPair(data.get(j).xVectors.get(xAxisFeature), data.get(j).xVectors.get(yAxisFeature)));
            }
        }
        output.AddXYData("0", tempData1);
        output.AddXYData("1", tempData2);
        return output;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
        int maxEpoch = 800;
        ArrayList<Double> alphas = new ArrayList<>();
        alphas.add(0.5);
        alphas.add(0.4);
        alphas.add(0.3);
        alphas.add(0.2);
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

    public static double scaleX(double x, double xMin, double xMax) {
        final double minVal = 0.0;
        final double maxVal = 2.0;
        return ((x - xMin)/(xMax - xMin)) * (maxVal - minVal) + minVal;
    }

    // Completely similar to yPredictor of linear regression,
    // except that this returns 1/(1 + Math.exp(- sum)) instead of sum.
    // In logistic regression, sigmoid function is needed to find the theta vector.
    // 1/(1 + Math.exp(- sum)) == 1/1 + e^-x == Sigmoid Function
    public static double yPredictor(ArrayList<Double> xVectors, ArrayList<Double> thetas) {
        double sum = 0;
        for (int i = 0; i < xVectors.size(); i++) {
            sum += xVectors.get(i) * thetas.get(i);
        }
        return 1/(1 + Math.exp(- sum));
    }

    // domainPredictor is a modification of yPredictor and is called when getting the final prediction,
    // because yPredictor is used to find the theta vector and its a double,
    // while the final prediction of logistic regression must either be 0 or 1.
    // So domainPredictor takes the value of yPredictor and turn it into an integer.
    // Example: (int) 0.5 == 0 | (int) 0.9 == 0 | (int) 0.3 == 0
    // Because of the problem on the example above, while we want everything above 0.5 to be 1 and below to be 0,
    // we add + 0.5 so that...
    // Example: (int) 0.5 + 0.5 == 1 | (int) 0.9 + 0.5 == 1.4 == 1 | (int) 0.3 + 0.5 == 0.8 == 0
    public static int domainPredictor(ArrayList<Double> xVectors, ArrayList<Double> thetas) {
        return (int) (yPredictor(xVectors, thetas) + 0.5);
    }

    public static double cost(List<DataItem> data, ArrayList<Double> thetas) {
        double totalSquaredCost = 0.0;
        for (int instance = 0; instance < data.size(); instance++) {
            double actualY = data.get(instance).y;
            double predictedY = yPredictor(data.get(instance).xVectors, thetas);
            double squaredCost = (actualY - predictedY) * (actualY - predictedY);
            totalSquaredCost += squaredCost;
        }
        return 0.5 * totalSquaredCost/data.size();
    }

    public static double partialDerivative(List<DataItem> data, ArrayList<Double> thetas, int thetaIndex) {
        double sum = 0;
        for (int d = 0; d < data.size(); d++) {
            sum +=  (yPredictor(data.get(d).xVectors, thetas) - data.get(d).y) * data.get(d).xVectors.get(thetaIndex);
        }
        return sum/data.size();
    }

    public static ArrayList<Double> gD(List<DataItem> data, ArrayList<Double> thetas, double alpha, int maxEpochs) {
        ArrayList<Double> tempTheta = new ArrayList<>();
        for (int i = 1; i <= maxEpochs; i++) {
            for (int j = 0; j < thetas.size(); j++) {
                tempTheta.add(thetas.get(j) - alpha * partialDerivative(data, thetas, j));
            }
            for (int j = 0; j < thetas.size(); j++) {
                thetas.set(j, tempTheta.get(j));
            }
            tempTheta.clear();
        }
        return thetas;
    }

    public static ArrayList<ArrayList<Double>> gDAndCostByEpoch(List<DataItem> data, ArrayList<Double> thetas, double alpha, int maxEpochs) {
        ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> costByEpoch = new ArrayList<>();
        ArrayList<Double> tempTheta = new ArrayList<>();
        for (int i = 1; i <= maxEpochs; i++) {
            for (int j = 0; j < thetas.size(); j++) {
                tempTheta.add(thetas.get(j) - alpha * partialDerivative(data, thetas, j));
            }
            for (int j = 0; j < thetas.size(); j++) {
                thetas.set(j, tempTheta.get(j));
            }
            costByEpoch.add(cost(data, thetas));
            tempTheta.clear();
        }
        result.add(thetas);
        result.add(costByEpoch);
        return result;
    }

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