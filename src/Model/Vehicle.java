package Model;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private String licensePlate;
    private String typeVehicle;
    private String owner;
    private String model;
    private String color;
    private double priceHour;

    public Vehicle() {}

    public Vehicle(String licensePlate, String typeVehicle, String owner, String model, String color, double priceHour) {
        this.licensePlate = licensePlate;
        this.typeVehicle = typeVehicle;
        this.owner = owner;
        this.model = model;
        this.color = color;
        this.priceHour = priceHour;
    }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getTypeVehicle() { return typeVehicle; }
    public void setTypeVehicle(String typeVehicle) { this.typeVehicle = typeVehicle; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getPriceHour() { return priceHour; }
    public void setPriceHour(double priceHour) { this.priceHour = priceHour; }
}
