package com.betfair.foe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.betfair.foe.api.HttpClientNonInteractiveLoginSSO;

import it.bluesheep.entities.util.sport.Sport;

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

        applicationKey = "txarSy4JZTpbX8OD";
        
        HttpClientNonInteractiveLoginSSO loginHttpHelper = new HttpClientNonInteractiveLoginSSO();
        String product = null;
        try {
        	JSONArray credentialJSONArray = new JSONArray(loginHttpHelper.login());
			sessionToken = new JSONObject(credentialJSONArray.getString(0)).getString("sessionToken");
			product = new JSONObject(credentialJSONArray.getString(1)).getString("product");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        
        System.out.println(sessionToken);
        
        while (sessionToken == null || sessionToken.isEmpty()) {
            System.out.println("Please insert a valid Session Token: ");
            System.out.print("> ");
        }

        ApiNGJsonRpcDemo demo = new ApiNGJsonRpcDemo();
        demo.start(applicationKey, sessionToken, Sport.CALCIO);

    }

    public static Properties getProperties() {
        return properties;
    }

}
