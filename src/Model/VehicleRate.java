package Model;

import java.io.Serializable;

public class VehicleRate implements Serializable {
    private String typeVehicle;
    private double price;

    public VehicleRate() {}

    public VehicleRate(String typeVehicle, double price) {
        this.typeVehicle = typeVehicle;
        this.price = price;
    }

    public String getTypeVehicle() { return typeVehicle; }
    public void setTypeVehicle(String typeVehicle) { this.typeVehicle = typeVehicle; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
