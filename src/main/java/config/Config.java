package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static final String FILE = "resources/config/appconfig.properties";
    private static Properties props = new Properties();
    private static Config INSTANCE;

    static {
        try (FileInputStream fis = new FileInputStream(FILE)) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("No se pudo cargar el archivo de configuracion " + FILE + e.getMessage());
        }
    }

    private Config(){

    }

    public static Config getInstance(){
        if (INSTANCE == null) INSTANCE = new Config();
        return INSTANCE;
    }

    public static String getPathFiles() {
        return props.getProperty("app.config.path.files");
    }

    public static String getNameFileTXT() {
        return props.getProperty("app.config.path.file.name.txt", "data.txt"); 
    }
    public static String getNameFileCSV() { 
        return props.getProperty("app.config.path.file.name.csv", "vehicleRate.csv"); 
    }
    public static String getNameFileJson() { 
        return props.getProperty("app.config.path.file.name.json", "recordParking.json"); 
    }
    public static String getNameFileXML() { 
        return props.getProperty("app.config.path.file.name.xml", "dataVehicle.xml"); 
    }
    public static String getNameFileSer() { 
        return props.getProperty("app.config.path.file.name.ser", "loginUsers.ser"); 
    }

    public static int getSizeParking() {
        String override = System.getProperty("app.config.sizeParking");
        if (override != null && !override.trim().isEmpty()) {
            try { 
                return Integer.parseInt(override); 
            } catch(Exception e) 
            {

            }
        }
        try {
            return Integer.parseInt(props.getProperty("app.config.sizeParking", "0"));
        } catch (Exception e) {
            return 0;
        }
    }
}
