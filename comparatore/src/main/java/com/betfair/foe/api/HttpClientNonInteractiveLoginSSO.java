package com.betfair.foe.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
 
 
public class HttpClientNonInteractiveLoginSSO {
 
    private static int port = 443;
    private final static String CERTIFICATE_NAME = "BlueSheepCertificateJavaComparatore.p12";
 
    public List<String> login() throws Exception {
 
        HttpClient httpClient = new DefaultHttpClient();
        String responseString = null;
        String responseStringKA = null;
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream(new File("/Users/giorgio/" + CERTIFICATE_NAME)), "FDSAfdsa11");
            ctx.init(keyManagers, null, new SecureRandom());
            SSLSocketFactory factory = new SSLSocketFactory(ctx, new StrictHostnameVerifier());
 
            ClientConnectionManager manager = httpClient.getConnectionManager();
            manager.getSchemeRegistry().register(new Scheme("https", port, factory));
            HttpPost httpPost = new HttpPost("https://identitysso.betfair.it/api/certlogin");
            HttpPost httpPostKeepAlive = new HttpPost("https://identitysso.betfair.com/api/keepAlive");
            
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", "fabiodisante@hotmail.it"));
            nvps.add(new BasicNameValuePair("password", "FDSAfdsa11"));
            
            List<NameValuePair> nvpsKA = new ArrayList<NameValuePair>();
            nvpsKA.add(new BasicNameValuePair("username", "fabiodisante@hotmail.it"));
            nvpsKA.add(new BasicNameValuePair("password", "FDSAfdsa11"));
 
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
 
            httpPostKeepAlive.setEntity(new UrlEncodedFormEntity(nvpsKA));
 
            httpPost.setHeader("X-Application","txarSy4JZTpbX8OD");
            httpPost.setHeader("Accept","application/json");
            httpPost.setHeader("Connection", "keep-alive");
            
            httpPostKeepAlive.setHeader("X-Application","txarSy4JZTpbX8OD");
            httpPostKeepAlive.setHeader("Accept","application/json");
//            System.out.println("executing request" + httpPost.getRequestLine());
 
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
 
            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (entity != null) {
                responseString = EntityUtils.toString(entity);
                //extract the session token from responsestring
                System.out.println("Response String SessionKey acquisition:\n" + responseString);
            }
            
            String jsonSessionToken = (new JSONObject(responseString)).getString("sessionToken");
            
            httpPostKeepAlive.setHeader("X-Authentication",jsonSessionToken);
            
            HttpResponse responseKA = httpClient.execute(httpPostKeepAlive);
            HttpEntity entityKA = responseKA.getEntity();
            
            System.out.println("----------------------------------------");
            System.out.println(responseKA.getStatusLine());
            
            if(entityKA != null) {
            	responseStringKA = EntityUtils.toString(entityKA);
            	System.out.println("Response String Keep Alive:\n" + responseStringKA);
            }
        }catch(Exception e) {
            
        }finally {
            httpClient.getConnectionManager().shutdown();
        }
        
        return Arrays.asList(responseString,responseStringKA);
    }
 
 
 
    protected static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf.getKeyManagers();
    }
}

