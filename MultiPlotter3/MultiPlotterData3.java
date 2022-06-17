import java.util.ArrayList;
import java.util.LinkedHashMap;

// MultiPlotterData holds the data for MultiPlotter3
// Each XY data sequence is stored in a map indexed by the data's name, stored in insertion order
// Each XY data sequence is of type ArrayList<XYPair>
class MultiPlotterData3 {
    // Store the XY sequences indexed by their name: in insertion order
    public LinkedHashMap<String, ArrayList<XYPair>> xyGroupMap = new LinkedHashMap<>();

    public MultiPlotterData3() {
    }

    // Adds data from ArrayList<XYPair>
    public void AddXYData(String yDataName, ArrayList<XYPair> xyData) {
        xyGroupMap.put(yDataName, xyData);
    }

    // Adds data from separate x and y ArrayList<Double> lists
    public void AddXYData(String yDataName, ArrayList<Double> xData, ArrayList<Double> yData) {
        AddXYData(yDataName, XYValues(xData,yData));
    }

    // Creates an array list of XYPair elements from their x and y components
    private ArrayList<XYPair> XYValues(ArrayList<Double> xValues, ArrayList<Double> yValues) {
        ArrayList<XYPair> output = new ArrayList<>();
        int length = Math.min(xValues.size(),yValues.size());
        for (int i = 0; i < length; i++) {
            output.add(new XYPair(xValues.get(i), yValues.get(i)));
        }
        return output;
    }
}