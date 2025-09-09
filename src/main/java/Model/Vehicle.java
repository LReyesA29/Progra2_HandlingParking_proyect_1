package Model;
import java.io.Serializable;

public class Vehicle implements Serializable {
    private String licensePlate;
    private String typeVehicle;
    private String owner;
    private String model;
    private String color;
    private double priceHour;

    public Vehicle(String licensePlate,String typeVehicle,String owner,String model,String color,double pricePerHour){
        
        setLicensePlate(licensePlate);
        setTypeVehicle(typeVehicle);
        setOwner(owner);
        setModel(model);
        setColor(color);
        setPriceHour(priceHour);
        
    }

    public String getLicensePlate(){
        return licensePlate; 
    }
    public String getTypeVehicle(){ 
        return typeVehicle; 
    }
    public String getOwner(){
         return owner; 
        }
    public String getModel(){
         return model; 
    }
    public String getColor(){
        return color; 
    }
    public double getPriceHour(){
        return priceHour; 
    }

    public void setOwner(String owner){
        this.owner = owner; 
    }
    public void setModel(String model){ 
        this.model = model; 
    }
    public void setColor(String color){ 
        this.color = color; 
    }
    public void setPriceHour(double pricePerHour){ 
        this.priceHour = pricePerHour; 
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public void setTypeVehicle(String typeVehicle) {
        this.typeVehicle = typeVehicle;
    }

    @Override public String toString(){
        return "Vehiculo:\n" +"Placa:"+ licensePlate + "tipo: " + typeVehicle + "Dueño:" + owner ;
    }
}
