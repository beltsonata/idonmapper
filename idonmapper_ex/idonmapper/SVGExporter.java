/**
 * Copyright Sean Talbot 2010.
 * 
 * This file is part of idonmapper.
 * idonmapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * idonmapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with idonmapper.  If not, see <http://www.gnu.org/licenses/>. 
 */
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
