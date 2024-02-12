package Models;

public class Shoe {

    private int id;
    private String model;
    private Brand brand;
    private Color color;
    private int Price;
    private Size size;
    private int balance;

    public Shoe(int id, String model, Brand brand, Color color, int price, Size size, int balance) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.color = color;
        Price = price;
        this.size = size;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
