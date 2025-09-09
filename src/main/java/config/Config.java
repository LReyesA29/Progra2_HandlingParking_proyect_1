package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static final String FILE = "src/main/resources/config/appconfig.properties";
    private static Properties props = new Properties();
    private static Config INSTANCE;

    static {
        try (FileInputStream fis = new FileInputStream(FILE)) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("Warning: could not load config file: " + FILE + " -> " + e.getMessage());
        }
    }

    private Config(){

    }

    public static Config getInstance(){
        if (INSTANCE == null) INSTANCE = new Config();
        return INSTANCE;
    }

    public static String getPathFiles() {
        String override = System.getProperty("app.config.path.files");
        String v = (override != null && !override.trim().isEmpty()) ? override : props.getProperty("app.config.path.files", "resources/data/");
        if (v.startsWith("src/") || v.startsWith("./") ) return v;
        return "src/main/" + v;
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
