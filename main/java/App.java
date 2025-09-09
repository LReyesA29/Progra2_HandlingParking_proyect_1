package java;
import View.Interface;
import Persistence.HandlingPersistence;

/**
 * Main starter - launches console Interface.
 * Explicitly initializes persistence so files are loaded before UI starts.
 */
public class App {
    public static void main(String[] args) {
        // Initialize persistence (loads users, rates, vehicles, records)
        HandlingPersistence.getInstance();
        Interface view = new Interface();
        view.showInfo();
    }
}
