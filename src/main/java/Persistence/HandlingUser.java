package Persistence;

import Model.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import Enums.ETypeFile;
import Interfaces.IActionsFile;

public class HandlingUser implements IActionsFile {

    private List<User> users;

    private static final String FILE_PATH = "src/main/resources/data/loginUsers.ser";

    public HandlingUser() {
        this.users = new ArrayList<>();
    }

    @Override
    public void loadFile(ETypeFile eTypeFile) {
        if (eTypeFile.equals(ETypeFile.SER)) {
            loadFileSerializate();
        }
    }

    @Override
    public void dumpFile(ETypeFile eTypeFile) {
        if (eTypeFile.equals(ETypeFile.SER)) {
            dumpFileSerializate();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFileSerializate() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            users = (List<User>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de usuarios no encontrado.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void dumpFileSerializate() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public User findUser(String userName, String password) {
        for (User u : users) {
            if (u.getUserName().equals(userName) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
        dumpFile(ETypeFile.SER); 
    }
}
