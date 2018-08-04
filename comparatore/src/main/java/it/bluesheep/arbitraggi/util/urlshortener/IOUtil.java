package it.bluesheep.arbitraggi.util.urlshortener;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class IOUtil {
    /**
     * Utility hidding constructor. 
     */ 
    private IOUtil() {} 
 
    public static void close(Closeable stream) { 
        if (stream == null) { 
            return; 
        } 
        try { 
            stream.close(); 
        } catch (IOException e) { 
        	e.printStackTrace();
        } 
 
    } 
 
    public static String toString(InputStream in, String charset) throws IOException { 
        StringWriter out = new StringWriter(); 
        copy(new InputStreamReader(in, charset), out); 
        out.close(); 
        in.close(); 
        return out.toString(); 
    } 
 
 
    public static int copy(Reader input, Writer output) throws IOException { 
        char[] buffer = new char[1024]; 
        int count = 0; 
        int n = 0; 
        while (-1 != (n = input.read(buffer))) { 
            output.write(buffer, 0, n); 
            count += n; 
        } 
        return count; 
    } 
}
