package Model;
import java.io.Serializable;

public class Vehicle implements Serializable {
    private String licensePlate;
    private String typeVehicle;
    private String owner;
    private String model;
    private String color;
    private double pricePerHour;

    public Vehicle(String licensePlate, String typeVehicle, String owner, String model, String color, double pricePerHour) {
        this.licensePlate = licensePlate;
        this.typeVehicle = typeVehicle;
        this.owner = owner;
        this.model = model;
        this.color = color;
        this.pricePerHour = pricePerHour;
    }

    public String getLicensePlate() { return licensePlate; }
    public String getTypeVehicle() { return typeVehicle; }
    public String getOwner() { return owner; }
    public String getModel() { return model; }
    public String getColor() { return color; }
    public double getPricePerHour() { return pricePerHour; }

    public void setOwner(String owner) { this.owner = owner; }
    public void setModel(String model) { this.model = model; }
    public void setColor(String color) { this.color = color; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }
}
