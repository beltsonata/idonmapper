package idonmapper;

import java.io.*;
import java.awt.*;
/*import org.apache.batik.svggen.*;
import org.apache.batik.dom.GenericDOMImplementation;*/
import org.w3c.dom.*;

/**
 * Not working. SVG files created by this don't display anything
 * but a white background!
 */ 
public class SVGExporter
{
  /*  private static Document doc;
    private static DOMImplementation DOMimp;
    public final static String svgURL = "http://www.w3.org/2000/svg";
    
    private static SVGGraphics2D svgGen;
    private static boolean setUp = false;
    private static Writer writer;
    
    
    public static boolean exportSVG(HexPanel hp, File file)
    {
        DOMimp = GenericDOMImplementation.getDOMImplementation();
        doc = DOMimp.createDocument(svgURL, "svg", null);
        svgGen = new SVGGraphics2D(doc);
        
        Rectangle vis = Controller.getHexPanel().visibleArea();
        
        svgGen.setSVGCanvasSize(new Dimension((int)vis.getWidth(), 
                                              (int)vis.getHeight()));
           
        hp.paintComponent(svgGen);
        
        if(!setUpFileStream(file))
        {
            return false;
        }
        return streamSVGToFile(); 
    }
    
    private static boolean streamSVGToFile()
    {
        try
        {
            svgGen.stream(writer, false);
        }
        catch(SVGGraphics2DIOException e)
        {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    
    private static boolean setUpFileStream(File file)
    {
        try
        {
            writer = new OutputStreamWriter(new FileOutputStream(file), 
                                                "UTF-8");
        }
        catch(FileNotFoundException e)
        {
            System.out.println(e.getMessage());
            return false;
        }
        catch(UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());        
            return false;
        }
        return true;
    }
    */
}
