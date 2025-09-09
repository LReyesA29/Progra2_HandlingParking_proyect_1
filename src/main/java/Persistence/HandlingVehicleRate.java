package Persistence;
import Model.VehicleRate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import Enums.ETypeFile;
import Interfaces.IActionsFile;

public class HandlingVehicleRate extends FilePlain implements IActionsFile {
    private List<VehicleRate> rates;
    private static final String FILE_PATH = "src/main/resources/data/vehicleRate.csv";

    public HandlingVehicleRate() {
        this.rates = new ArrayList<>();
    }

    @Override
    public void loadFile(ETypeFile eTypeFile) {
        if (eTypeFile.equals(ETypeFile.CSV)) {
            loadFileCSV();
        }
    }

    @Override
    public void dumpFile(ETypeFile eTypeFile) { }

    private void loadFileCSV() {
        rates.clear();
        List<String> lines = this.reader(FILE_PATH);
        for (String row : lines) {
            StringTokenizer st = new StringTokenizer(row, ";");
            if (st.countTokens() == 2) {
                String type = st.nextToken();
                double price = Double.parseDouble(st.nextToken());
                rates.add(new VehicleRate(type, price));
            }
        }
    }

    public VehicleRate findRateByType(String type) {
        return rates.stream()
                .filter(r -> r.getTypeVehicle().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }

    public List<VehicleRate> getRates() {
        return rates;
    }
}
