import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DemoA {
    public static void main(String[] args) {
        List<DataItem> perthRainData = getDataFromCSV("perthRain.csv");
        List<DataItem> melbourneRainData = getDataFromCSV("melbourneRain.csv");
        for (int i = 0; i < perthRainData.get(0).xVectors.size(); i++) {
            if (featureMinAndMax(perthRainData, i).get(1) - featureMinAndMax(perthRainData, i).get(0) > 2) {
                double xMin = featureMinAndMax(perthRainData, i).get(0);
                double xMax = featureMinAndMax(perthRainData, i).get(1);
                for (int j = 0; j < perthRainData.size(); j++) {
                    perthRainData.get(j).xVectors.set(i, scaleX(perthRainData.get(j).xVectors.get(i), xMin, xMax));
                }
            }
        }
        for (int i = 0; i < melbourneRainData.get(0).xVectors.size(); i++) {
            if (featureMinAndMax(melbourneRainData, i).get(1) - featureMinAndMax(melbourneRainData, i).get(0) > 2) {
                double xMin = featureMinAndMax(melbourneRainData, i).get(0);
                double xMax = featureMinAndMax(melbourneRainData, i).get(1);
                for (int j = 0; j < melbourneRainData.size(); j++) {
                    melbourneRainData.get(j).xVectors.set(i, scaleX(melbourneRainData.get(j).xVectors.get(i), xMin, xMax));
                }
            }
        }

        List<DataItem> tempPerthRainData = new ArrayList<>();
        List<DataItem> tempMelbourneRainData = new ArrayList<>();
        for (int i = 0; i < perthRainData.size(); i++) {
            tempPerthRainData.add(perthRainData.get(i));
            tempMelbourneRainData.add(melbourneRainData.get(i));
        }
        List<DataItem> trainingDataPerth = new ArrayList<>();
        List<DataItem> trainingDataMelbourne = new ArrayList<>();
        int trainingDataSize = perthRainData.size()*4/5;
        Random r = new Random();
        for (int i = 0; i < trainingDataSize; i++) {
            int randomIndex = r.nextInt(tempPerthRainData.size());
            trainingDataPerth.add(tempPerthRainData.get(randomIndex));
            tempPerthRainData.remove(randomIndex);
            trainingDataMelbourne.add(tempMelbourneRainData.get(randomIndex));
            tempMelbourneRainData.remove(randomIndex);
        }
        List<DataItem> validationDataPerth = tempPerthRainData;
        List<DataItem> validationDataMelbourne = tempMelbourneRainData;

        ArrayList<Double> thetasPerth = new ArrayList<>();
        ArrayList<Double> thetasMelbourne = new ArrayList<>();
        for (int i = 0; i < perthRainData.get(0).xVectors.size(); i++) {
            thetasPerth.add(0.0);
            thetasMelbourne.add(0.0);
        }
        ArrayList<Double> thetaVectorPerth = gD(trainingDataPerth, thetasPerth, 0.3, 5000);
        ArrayList<Double> thetaVectorMelbourne = gD(trainingDataMelbourne, thetasMelbourne, 0.3, 5000);

        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;
        for (int i = 0; i < validationDataPerth.size(); i++) {
            if (domainPredictor(validationDataPerth.get(i).xVectors, thetaVectorPerth) == validationDataPerth.get(i).y) {
                count1++;
            }
            if (domainPredictor(validationDataMelbourne.get(i).xVectors, thetaVectorMelbourne) == validationDataMelbourne.get(i).y) {
                count2++;
            }
            if (domainPredictor(validationDataMelbourne.get(i).xVectors, thetaVectorPerth) == validationDataMelbourne.get(i).y) {
                count3++;
            }
            if (domainPredictor(validationDataPerth.get(i).xVectors, thetaVectorMelbourne) == validationDataPerth.get(i).y) {
                count4++;
            }
        }
        System.out.println("Total number of correct predictions/Total number of instances of validationData...");
        System.out.println("Perth Rain Predictor Accuracy: " + count1 + "/" + validationDataPerth.size());
        System.out.println("Melbourne Rain Predictor Accuracy: " + count2 + "/" + validationDataPerth.size());
        System.out.println("Perth Rain Predictor Accuracy (Predicting Melbourne): " + count3 + "/" + validationDataPerth.size());
        System.out.println("Melbourne Rain Predictor Accuracy (Predicting Perth): " + count4 + "/" + validationDataPerth.size());

        MultiPlotterData3 dataPlotOne = generateData(trainingDataPerth);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final var plotter3 = new MultiPlotter3("Perth Rain Prediction Cost/Epoch graph", "Epoch", "Cost", dataPlotOne, MultiPlotter3.PlotType.LinePlot);
                plotter3.setVisible(true);
            }
        });

        MultiPlotterData3 dataPlotTwo = generateData(trainingDataMelbourne);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final var plotter3 = new MultiPlotter3("Melbourne Rain Prediction Cost/Epoch graph", "Epoch", "Cost", dataPlotTwo, MultiPlotter3.PlotType.LinePlot);
                plotter3.setVisible(true);
            }
        });
    }

    public static MultiPlotterData3 generateData(List<DataItem> data) {
        MultiPlotterData3 multiPlotterData = new MultiPlotterData3();
        int maxEpoch = 5000;
        ArrayList<Double> alphas = new ArrayList<>();
        alphas.add(0.3);
        alphas.add(0.2);
        alphas.add(0.01);
        alphas.add(0.05);
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

    public static double yPredictor(ArrayList<Double> xVectors, ArrayList<Double> thetas) {
        double sum = 0;
        for (int i = 0; i < xVectors.size(); i++) {
            sum += xVectors.get(i) * thetas.get(i);
        }
        return 1/(1 + Math.exp(- sum));
    }

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