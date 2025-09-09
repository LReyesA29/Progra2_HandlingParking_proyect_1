package Model;
import java.io.Serializable;
public class VehicleRate implements Serializable {
    private String typeVehicle;
    private double price;

    public VehicleRate(String typeVehicle, double price) {
        this.typeVehicle = typeVehicle;
        this.price = price;
    }

    public String getTypeVehicle() { 
        return typeVehicle; 
    }
    public double getPrice() { 
        return price; 
    }
    @Override public String toString(){ 
        return typeVehicle + "=" + price; 
    }
}
