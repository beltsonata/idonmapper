package idonmapper;

import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;
import java.io.*;
import java.awt.*;

/**
 * Allows the user to export a PostScript file based upon the current
 * contents of the HexPanel.
 */ 
public class PostscriptExporter
{
    private EPSDocumentGraphics2D g2d;
    private Dimension requiredSize;
    
    public PostscriptExporter(){}
    
    /**
     * Sets up the exporter with a File
     */ 
    /*
     * This isn't working properly. Currently, drawing the entire panel
     * results in huge postscript images with the user's map in the middle,
     * very small. Have tried specifying the Dimension of the map to 
     * export (HexPanel.getMapDimension()); this doesn't work as it doesn't 
     * allow one to state where this dimension should begin from on the
     * HexPanel!
     */ 
    public void setUp(final File f)
    {
        try
        {
            final FileOutputStream fos = new FileOutputStream(f);
            Dimension wholePanel = Controller.getHexPanel()
                                              .calculateMinimumSize();
            //Dimension requiredSize = 
                          // Controller.getHexPanel().getMapDimension();
            
            
            g2d = new EPSDocumentGraphics2D(false);
            g2d.setGraphicContext(new org.apache.xmlgraphics.java2d
                                                     .GraphicContext());
            
            g2d.setupDocument(fos, wholePanel.width, wholePanel.height);

        }
        catch(final IOException e)
        {
            e.printStackTrace();
            //System.out.println(e.getMessage());
        }
    }
    
    
    /*
     * Attempts to export the Idon Map to the file, returning true on 
     * success. 
     */ 
    public boolean save()
    {
        try
        {            
            Controller.getHexPanel().paintToPostScript(g2d);
            g2d.finish();
            return true;
        }
        catch(final IOException e)
        {
            e.printStackTrace();
            //System.out.println(e.getMessage());
        }
        return false;
    }
}
