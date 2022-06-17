import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// ---------------------------------------------------------
// Program to test MultiPlotter3, the multiple sequence plotter
// ---------------------------------------------------------
public class MultiPlotter3Test {
    // Generates multiple curves and plots them
    // When the line curve runs from (0,0) to (DATA_LENGTH-1,DATA_LENGTH-1), you can use the line curve to verify scaling
    public static void main(String[] args) {
        // Generate the data
        // MultiPlotterData3 theData = MultiPlotter3Test.generateData();
        MultiPlotterData3 theData = MultiPlotter3Test.generateRandomData(5, 200);

        // Run I/O asynchronously on Swing's event dispatch thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final var plotter3 = new MultiPlotter3("Plotting Test", "Index", "Value", theData, MultiPlotter3.PlotType.ScatterPlot);
                plotter3.setVisible(true);
            }
        });
    }

    // Generates random (x,y) data sequences each with nPointsPerSeq
    // Number of sequences = nSeqs
    public static MultiPlotterData3 generateRandomData(int nSeqs, int nPointsPerSeq) {
        MultiPlotterData3 output = new MultiPlotterData3();
        Random rand = new Random();
        // Offset the sequences, generate the offsets
        final ArrayList<XYPair> seqOffsets = new ArrayList<>(Arrays.asList(new XYPair(5, 6), new XYPair(7, 4)));
        // Add additional offsets as necessary
        for (int iSeq = 2; iSeq < nSeqs; iSeq++) {
            seqOffsets.add(new XYPair(rand.nextInt(10) - 5, rand.nextInt(10) - 5));
        }

        // Generate the sequences
        for (int iDataSeq = 0; iDataSeq < nSeqs; iDataSeq++) {
            ArrayList<XYPair> data = new ArrayList<>();
            for (int i = 0; i < nPointsPerSeq; i++) {
                data.add(new XYPair(rand.nextGaussian() + seqOffsets.get(iDataSeq).x, rand.nextGaussian() + seqOffsets.get(iDataSeq).y));
            }
            output.AddXYData("Seq " + (iDataSeq + 1), data);
        }
        return output;
    }

    // Generates the data to plot as a MultiPlotterData object
    // Returns lists of a linear, parabola, and sine function
    public static MultiPlotterData3 generateData() {
        // Individual curves
        ArrayList<XYPair> line = new ArrayList<>();
        ArrayList<XYPair> parabola = new ArrayList<>();
        ArrayList<XYPair> sine = new ArrayList<>();

        // Generate the data
        final int DATA_MAX = 100;
        final double SINE_AMP = DATA_MAX / 2; // integer division
        final double SINE_PERIOD = 25;
        double PARABOLA_OFFSET = Math.floor(DATA_MAX / 2.0);
        final var maxX = DATA_MAX - PARABOLA_OFFSET;
        final double PARABOLA_FACTOR = 1.0 * 2 * SINE_AMP / (maxX * maxX);

        final Random rand = new Random();
        // Create line, sine, and parabola data against a common x value
        final double xOffset = 0;
        final double yOffset = 3;
        for (int i = 0; i <= DATA_MAX; i++) {
            double xValue = i + xOffset; // x-values are double
            // xValue = rand.nextDouble()*DATA_MAX; // Generate random x values
            line.add(new XYPair(xValue, xValue + yOffset)); // y-values are double
            var sineY = SINE_AMP * Math.sin((2 * Math.PI / SINE_PERIOD) * xValue) + SINE_AMP;
            sine.add(new XYPair(xValue, sineY + yOffset));
            var parabolaY = PARABOLA_FACTOR * (xValue-xOffset - PARABOLA_OFFSET) * (xValue-xOffset - PARABOLA_OFFSET);
            parabola.add(new XYPair(xValue, parabolaY + yOffset));
        }

        MultiPlotterData3 multiPlotterData3 = new MultiPlotterData3();
        multiPlotterData3.AddXYData("Line", line);
        multiPlotterData3.AddXYData("Sine", sine);
        multiPlotterData3.AddXYData("Parabola", parabola);

        return multiPlotterData3;
    }
}