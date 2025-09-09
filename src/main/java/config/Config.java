package config;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class Config {
   
    private static Properties props = new Properties();

    private static Config config;	
    
	private String pathFiles;	
	private String nameFileTXT;	
	private String nameFileCSV;	
	private String nameFileJson;
	private String nameFileXML;	
	private String nameFileSer;	
	private Properties properties;

	private Config() {
		this.properties= new Properties();
		try (FileInputStream entrada 
				= new FileInputStream("resources/config/appconfig.properties")) {
			properties.load(entrada);
            this.pathFiles = properties.getProperty("app.config.path.files");
            this.nameFileTXT =  properties.getProperty("app.config.path.file.name.txt");
            this.nameFileCSV = properties.getProperty("app.config.path.file.name.csv");
            this.nameFileJson = properties.getProperty("app.config.path.file.name.json");
            this.nameFileXML = properties.getProperty("app.config.path.file.name.xml");
            this.nameFileSer = properties.getProperty("app.config.path.file.name.ser");


        } catch (IOException ex) {
            System.err.println("Error al cargar el archivo properties de configuracion: " + ex.getMessage());
        }
	}
	
	public static Config getInstance() {
		if(Objects.isNull(config)) {
			config = new Config();
		}
		return config;
	}

	public String getPathFiles() {
		return pathFiles;
	}

	public void setPathFiles(String pathFiles) {
		this.pathFiles = pathFiles;
	}

	public String getNameFileTXT() {
		return nameFileTXT;
	}

	public void setNameFileTXT(String nameFileTXT) {
		this.nameFileTXT = nameFileTXT;
	}

	public String getNameFileCSV() {
		return nameFileCSV;
	}

	public void setNameFileCSV(String nameFileCSV) {
		this.nameFileCSV = nameFileCSV;
	}

	public String getNameFileJson() {
		return nameFileJson;
	}

	public void setNameFileJson(String nameFileJson) {
		this.nameFileJson = nameFileJson;
	}

	public String getNameFileXML() {
		return nameFileXML;
	}

	public void setNameFileXML(String nameFileXML) {
		this.nameFileXML = nameFileXML;
	}

	public String getNameFileSer() {
		return nameFileSer;
	}

	public void setNameFileSer(String nameFileSer) {
		this.nameFileSer = nameFileSer;
	}

    public static int getSizeParking() {
        return Integer.parseInt(props.getProperty("app.config.sizeParking", "0"));
    }
}
