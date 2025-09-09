package Model;
import java.io.Serializable;
public class VehicleRate implements Serializable {
    private String typeVehicle;
    private double price;

    public VehicleRate(String typeVehicle, double price) {
        setTypeVehicle(typeVehicle);
        setPrice(price);
    }

    public String getTypeVehicle() { 
        return typeVehicle; 
    }
    public void setTypeVehicle(String typeVehicle) {
        this.typeVehicle = typeVehicle;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() { 
        return price; 
    }
    @Override public String toString(){ 
        return typeVehicle + "=precio por hora: " + price; 
    }
}
