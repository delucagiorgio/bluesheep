package arbs.imagegeneration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xhtmlrenderer.util.FSImageWriter;

import it.bluesheep.util.BlueSheepLogger;

/**
 * Converte Xhtml in png, ma in maniera pessima. Da trovare altra soluzione
 * @author Fabio
 *
 */
public class XHTML2PngConverter {
	
	private final static int IMAGE_WIDTH = 1024;
	private static Logger logger;
	
	public XHTML2PngConverter() {
		logger = (new BlueSheepLogger(XHTML2PngConverter.class)).getLogger();
	}
	
	public void convert (String inputFile, String outputFileName) {
		
		File f = new File(inputFile);

        // constructing does not render; not until getImage() is called
		Java2DRenderer renderer = null;
		
		try {
			renderer = new Java2DRenderer(f, IMAGE_WIDTH);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		// this renders and returns the image, which is stored in the J2R; will not
		// be re-rendered, calls to getImage() return the same instance
		BufferedImage img = renderer.getImage();  
		  
		  
		// write it out, full size, PNG
		// FSImageWriter instance can be reused for different images,
		// defaults to PNG
		FSImageWriter imageWriter = new FSImageWriter();
		try {
			imageWriter.write(img, outputFileName);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}