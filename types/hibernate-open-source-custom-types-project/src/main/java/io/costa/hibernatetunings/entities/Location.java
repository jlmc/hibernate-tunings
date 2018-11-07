package io.costa.hibernatetunings.entities;

public class Location {

    private String country;
    private String city;

    public Location() {
    }

    private Location(final String country, final String city) {
        this.country = country;
        this.city = city;
    }

    public static Location of(final String country, final String city) {
        return new Location(country, city);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Location{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
