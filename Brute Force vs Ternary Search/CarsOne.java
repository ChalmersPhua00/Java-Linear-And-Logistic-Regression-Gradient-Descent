public class CarsOne {
    private int car;
    private int price;
    private int sold;

    public CarsOne(int car, int price, int sold) {
        super();
        this.car = car;
        this.price = price;
        this.sold = sold;
    }

    @Override
    public String toString() {
        return "Car: " + car + " | Price: " + price + " | Sold: " + sold;
    }

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }
}