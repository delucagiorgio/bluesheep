package arbs.util.urlshortener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class NetUtil {
	public static final int TIMEOUT = 10000; 
    /**
     * Utility hidding constructor. 
     */ 
    private NetUtil() {} 
 
    public static String doPost(String url, Map<String, String> params) throws IOException { 
        return doPost(url, params, null, "utf8"); 
    } 
 
    public static String doPost(String url, Map<String, String> params, Map<String, String> reqProperties, String charset) throws IOException { 
        String paramsStr = paramsToString(params); 
        byte[] postData = paramsStr.getBytes("utf8"); 
 
        URL u = new URL(url); 
 
        HttpURLConnection conn = (HttpURLConnection) u.openConnection(); 
        conn.setReadTimeout(TIMEOUT); 
        conn.setConnectTimeout(TIMEOUT); 
        conn.setDoOutput(true); 
        conn.setUseCaches(false); 
        conn.setRequestMethod("POST"); 
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length)); 
        if (reqProperties != null) { 
            for (Map.Entry<String, String> rp : reqProperties.entrySet()) { 
                conn.setRequestProperty(rp.getKey(), rp.getValue()); 
            } 
        } 
 
        OutputStream out = conn.getOutputStream(); 
        try { 
            out.write(postData); 
        } finally { 
            IOUtil.close(out); 
        } 
 
        InputStream in = conn.getInputStream(); 
        try { 
            return IOUtil.toString(in, charset); 
        } finally { 
            IOUtil.close(in); 
        } 
    } 
 
    public static String paramsToString(Map<String, String> params) throws IOException { 
        StringBuffer paramsStr = new StringBuffer(); 
        Iterator<Map.Entry<String,String>> paramIter = params.entrySet().iterator(); 
        if (paramIter.hasNext()) { 
            Map.Entry<String, String> param = paramIter.next(); 
            String paramName = param.getKey(); 
            paramsStr.append(paramName); 
            paramsStr.append("="); 
            paramsStr.append(URLEncoder.encode(param.getValue(), "utf8")); 
        } 
        while (paramIter.hasNext()) { 
            Map.Entry<String, String> param = paramIter.next(); 
            paramsStr.append("&"); 
            paramsStr.append(param.getKey()); 
            paramsStr.append("="); 
            paramsStr.append(URLEncoder.encode(param.getValue(), "utf8")); 
        } 
        return paramsStr.toString(); 
    } 
}
