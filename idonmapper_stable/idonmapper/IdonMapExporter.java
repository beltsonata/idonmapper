/*package idonmapper;

import java.io.*;
import org.apache.batik.svggen.*;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;


/**

 */ /*
public class IdonMapExporter
{
    private static Document doc;
    private static DOMImplementation DOMimp;
    public final static String svgURL = "http://www.w3.org/2000/svg";
    
    private static SVGGraphics2D svgGen;
    private static boolean setUp = false;
    private static Writer writer;
    
    /**
     * Attempts to export an SVG representation of the state of a
     * HexPanel to a file.
     * 
     * @return true if successful.
     */ 
    /*
     * Not working properly - at least, the SVGs produced are not
     * visible.
     *
    public static boolean exportSVG(HexPanel hp, File file)
    {
        if(!setUp)
        {
            setUp();
        }
        doc = DOMimp.createDocument(svgURL, "svg", null);
        svgGen = new SVGGraphics2D(doc);
        hp.paintComponent(svgGen);     
        setUpFileStream(file);
        streamSVGToFile();        
        return true;
    }
    
    private static boolean streamSVGToFile()
    {
        try
        {
            svgGen.stream(writer, true);
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
    
    private static void setUp()
    {
        DOMimp = GenericDOMImplementation.getDOMImplementation();
        setUp = true;
    }
}
*/
