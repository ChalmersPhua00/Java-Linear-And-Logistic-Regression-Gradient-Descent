import java.util.Objects;

/*
Captures an (x,y) pair
Used to hold data for MultiPlotter3
*/
public class XYPair {
    final double x;
    final double y;

    public XYPair(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        XYPair that = (XYPair) other;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
