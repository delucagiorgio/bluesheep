package com.betfair.foe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class FoeDemo {

    private static Properties properties = new Properties();
    private static String applicationKey;
    private static String sessionToken;

    static {
        try {
            InputStream in = FoeDemo.class.getResourceAsStream("/foedemo.properties");
            properties.load(in);
            in.close();
        } catch (IOException exception) {
            System.out.println("Error loading the properties file: " + exception.toString());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {

        System.out.println("Welcome to FOE API example!\n");

        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(System.in));

        // getting the AppKey and the session token
        if (args.length >= 2) {
            applicationKey = args[0];
            sessionToken = args[1];
        } else {
            while (applicationKey == null || applicationKey.isEmpty()) {

                System.out.println("Please insert a valid App Key: ");
                System.out.print("> ");
                try {
                    applicationKey = inputStreamReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            while (sessionToken == null || sessionToken.isEmpty()) {
                System.out.println("Please insert a valid Session Token: ");
                System.out.print("> ");
                try {
                    sessionToken = inputStreamReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Choose an option: ");
        System.out.println("1. Specific call demo");
        System.out.println("2. Bulk loading demo");

        String option = null;
        while (option == null || (!option.equals("1") && !option.equals("2"))) {
            System.out.print("\nEnter 1 or 2: ");
            try {
                option = inputStreamReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FoeRescriptDemo demo = new FoeRescriptDemo(applicationKey, sessionToken);
        if (option.equals("1")) {
            demo.specificCallDemo();
        } else {
            demo.bulkLoadingDemo();
        }
    }

    public static Properties getProperties() {
        return properties;
    }

}
