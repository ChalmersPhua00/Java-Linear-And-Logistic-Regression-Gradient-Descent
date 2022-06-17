import java.util.ArrayList;

class DataItem {
    ArrayList<Double> xVectors;
    double y;

    public DataItem(ArrayList<Double> xVectors, double y) {
        this.xVectors = xVectors;
        this.y = y;
    }
}