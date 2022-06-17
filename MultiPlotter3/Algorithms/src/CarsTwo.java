public class CarsTwo {
    private int car;
    private int price;
    private String make;
    private String sold;

    public CarsTwo(int car, int price, String make, String sold) {
        super();
        this.car = car;
        this.price = price;
        this.make = make;
        this.sold = sold;
    }

    @Override
    public String toString() {
        return "Car: " + car + " | Price: " + price + " | Make: " + make + " | Sold: " + sold;
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

    public String getMake() {
        return make;
    }

    public void setMake(String sold) {
        this.make = make;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }
}