package util;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import Model.User;

public class MakeUsers {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        users.add(new User("admin", "1234"));
        users.add(new User("user1", "pwd"));
        users.add(new User("guest", "123"));
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("src/main/resources/data/loginUsers.ser"))) {
            oos.writeObject(users);
            System.out.println("loginUsers.ser creado con usuarios por defecto.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
