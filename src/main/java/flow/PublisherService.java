package flow;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PublisherService {
    private static String dropProp;
    private static String buffProp;

    public static void main(String[] args) throws InterruptedException {
        FileInputStream fileInputStream;
        Properties properties = new Properties();
        try {
            fileInputStream = new FileInputStream("src/main/java/flow/props/config.properties");
            properties.load(fileInputStream);
            dropProp = properties.getProperty("drop.strategy");
            buffProp = properties.getProperty("buffer.strategy");
        } catch (IOException e) {
            System.out.println("Error, property file doesn't exist");
        }
        if (dropProp.equals("true")) {
            Strategy strategy = new DropStrategy();
            strategy.publish();
        } else if (buffProp.equals("true")) {
            Strategy strategy = new BufferStrategy();
            strategy.publish();
        } else {
            System.out.println("none of the strategies selected");
        }
    }
}
