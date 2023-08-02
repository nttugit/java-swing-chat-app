package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.User;

public class ObjectParser {
    public static String objectToString(Object obj) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        return byteArrayOutputStream.toString("ISO-8859-1"); // Convert byte array to string
    }

    
    public static Object stringToObject(String str) throws Exception {
        byte[] bytes = str.getBytes("ISO-8859-1"); // Convert string to byte array
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        return obj;
    }

    
    public static void main(String[] args) throws Exception {
        // Create an example object
        Object user = new User("nttu","pass1");
        
        // Convert the object to a string
        String objectString = objectToString(user);
        
        System.out.println("Object as String: " + objectString);
    }
}


