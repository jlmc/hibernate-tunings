package io.costa.hibernatetunings.entities;

public class Tiket {

    private String registationCode;
    private double price;

    public Tiket() {
    }

    private Tiket(final String registationCode, final double price) {
        this.registationCode = registationCode;
        this.price = price;
    }

    public static Tiket of(final String registationCode, final double price) {
        return new Tiket(registationCode, price);
    }

    public String getRegistationCode() {
        return registationCode;
    }

    public void setRegistationCode(final String registationCode) {
        this.registationCode = registationCode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Tiket{" +
                "registationCode='" + registationCode + '\'' +
                ", price=" + price +
                '}';
    }
}
