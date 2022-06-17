import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

// Plots multiple (x,y) data lists
// Four Classes:
//     MultiPlotterData3 - to hold data to be plotted (lists of XYData items with names)
//     MultiPlotter3 - to plot the data
//     XYPair - to hold a single (x,y) data pair
//     MultiPlotter3Test - program to test MultiPlotter3
public class MultiPlotter3 extends JFrame {
    // A MultiPlotter has common x values, stored in ArrayList<Double> xValues
    // The y values for each curve are stored in HashMap<String,ArrayList<Double>> yValues
    // Each element of yValues is one series of y data:
    //      - the Key is the name of the series,
    //      - the Value is an ArrayList<Double> containing the series' y data values
    // Example of use
    // - Generate the *data*
    //      ArrayList<Double> xValues = the x data
    //      ArrayList<Double> yValues = the y data
    // - Create an instance of a MultiPlotterData3 object
    //      var multiPlotterData = new MultiPlotterData3();
    // - Add the (X,Y) data sequences
    //      multiPlotterData.AddYData("Parabola", xArrayList, yArrayList); // data is in x and y ArrayList<Double>
    //      multiPlotterData.AddYData("Sine", xyArraylist); // data is in ArrayList<XYPairs>
    // - In the main()
    //   Run I/O asynchronously on Swing's event dispatch thread
    //      SwingUtilities.invokeLater(new Runnable() {
    //         @Override
    //         public void run() {
    //            final var plotter = new MultiPlotter3("Plot Name", "X-Axis Name", "Y-Axis Name",
    //                                          multiPlotterData, // the data
    //                                          MultiPlotter3.PlotType.LinePlot); // the type of plot
    //            plotter.setVisible(true);
    //         }
    //     } );
    // ---------------------------------------------------------------------------------------------------------------

    // The type of plot to generate
    enum PlotType  {ScatterPlot, LinePlot, LinePlotWithSymbols;}

    public MultiPlotter3(String plotTitle, String xAxisTitle, String yAxisTitle, MultiPlotterData3 plotData, PlotType plotType) {
        super(plotTitle);

        // Create the dataset
        XYSeriesCollection dataset = new XYSeriesCollection();

        // Create each xy-value series, in turn, and add it to the dataset
        for (Map.Entry<String, ArrayList<XYPair>> xyGroup : plotData.xyGroupMap.entrySet()) {
            var dataName = xyGroup.getKey();
            var series = new XYSeries(dataName);

            // Add each x and y value pair to the series
            for (XYPair xyValue : xyGroup.getValue()) {
                series.add(xyValue.x, xyValue.y);
            }
            dataset.addSeries(series);
        }

        // Create the Chart
        JFreeChart chart = null;
        switch (plotType) {
            case ScatterPlot:
                chart = ChartFactory.createScatterPlot(plotTitle, xAxisTitle, yAxisTitle, dataset);
                break;
            case LinePlot:
            case LinePlotWithSymbols:
                chart = ChartFactory.createXYLineChart(plotTitle, xAxisTitle, yAxisTitle, dataset);
                break;
        }
/*
        // Supported at language level 12 and above
        JFreeChart chart =
                switch (plotType) {
                    case ScatterPlot ->
                        ChartFactory.createScatterPlot(plotTitle, xAxisTitle, yAxisTitle, dataset);
                    case LinePlot, LinePlotWithSymbols ->
                        ChartFactory.createXYLineChart(plotTitle, xAxisTitle, yAxisTitle, dataset);
                }
*/
        // Get plot components
        var plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        // Add symbols if necessary
        if (plotType == PlotType.LinePlotWithSymbols)
            renderer.setDefaultShapesVisible(true);

        /*
        Unsuccessful Attempts to suppress including zero in the y axis
        final var rangeAxis = chart.getXYPlot().getRangeAxis();
        rangeAxis.setAutoRange(true);
        // Auto range
        final ValueAxis axis = plot.getRangeAxis(                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           );
        axis.setAutoRangeIncludesZero(false);
        // renderer.setSeriesShapesVisible(1,true); // show symbols for one series
        */

        // Create the Swing Panel with the chart inside
        JPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);

        // Adjust display characteristics
        // Default window size
        final int DefaultWindowHeight = 700;
        final int DefaultWindowWidth = 1000;
        setSize(DefaultWindowWidth, DefaultWindowHeight); // This can be set outside instead
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit the app when the plot window closes
        setLocationRelativeTo(null); // Place the plot in the center of the screen
    }
}