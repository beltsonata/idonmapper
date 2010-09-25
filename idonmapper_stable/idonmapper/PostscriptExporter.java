package idonmapper;

import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;
import java.io.*;
import java.awt.*;


public class PostscriptExporter
{
    private EPSDocumentGraphics2D g2d;
    
    public PostscriptExporter(){}
    
    /**
     * Sets up the exporter with a File
     */ 
    public void setUp(final File f)
    {
        try
        {
            final FileOutputStream fos = new FileOutputStream(f);
            final Dimension d = Controller.getHexPanel().getPreferredSize();
            g2d = new EPSDocumentGraphics2D(false);
            g2d.setGraphicContext(new org.apache.xmlgraphics.java2d
                                            .GraphicContext());
            g2d.setupDocument(fos, d.width, d.height);
            save();
        }
        catch(final IOException e)
        {
            e.printStackTrace();
            //System.out.println(e.getMessage());
        }
    }
    
    
    /**
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
