package com.betfair.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import it.bluesheep.BlueSheepComparatoreMain;
import it.bluesheep.entities.util.ComparatoreConstants;
import it.bluesheep.util.BlueSheepLogger;
 
 
public class HttpClientNonInteractiveLoginSSO {
 
    private static int port = 443; 
    private static Logger logger;
    
    public HttpClientNonInteractiveLoginSSO() {
    	logger = (new BlueSheepLogger(HttpClientNonInteractiveLoginSSO.class)).getLogger();
    }
    
    public String login() throws Exception {
 
        HttpClient httpClient = new DefaultHttpClient();
        String responseString = null;
        String jsonSessionToken = null;
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream(new File(BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.BETFAIR_CERTIFICATE_PATH))), BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.BETFAIR_PASSWORD));
            ctx.init(keyManagers, null, new SecureRandom());
            SSLSocketFactory factory = new SSLSocketFactory(ctx, new StrictHostnameVerifier());
 
            ClientConnectionManager manager = httpClient.getConnectionManager();
            manager.getSchemeRegistry().register(new Scheme("https", port, factory));
            HttpPost httpPost = new HttpPost("https://identitysso.betfair.it/api/certlogin");
            
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.BETFAIR_USERNAME)));
            nvps.add(new BasicNameValuePair("password", BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.BETFAIR_PASSWORD)));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
 
            httpPost.setHeader("X-Application", BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.BETFAIR_APPKEY));
            httpPost.setHeader("Accept", BlueSheepComparatoreMain.getProperties().getProperty(ComparatoreConstants.BETFAIR_APPLICATION_JSON));
            httpPost.setHeader("Connection", "keep-alive");
            
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
 
            if (entity != null) {
                responseString = EntityUtils.toString(entity);
            }
            
            jsonSessionToken = (new JSONObject(responseString)).getString(ComparatoreConstants.BETFAIR_SESSION_TOKEN_STRING);
        }catch(Exception e) {
        	logger.log(Level.SEVERE, "Error occurred during login on Betfair.it non interactive login: error is " + e.getMessage(), e);
        }finally {
            httpClient.getConnectionManager().shutdown();
        }
        
        return jsonSessionToken;
    }
 
 
 
    protected static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf.getKeyManagers();
    }
}

