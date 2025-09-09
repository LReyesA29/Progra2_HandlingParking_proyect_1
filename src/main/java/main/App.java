package main;
import View.Interface;
import Persistence.HandlingPersistence;


public class App {
    public static void main(String[] args) {
        
        HandlingPersistence.getInstance();
        Interface view = new Interface();
        view.showInfo();
    }
}
