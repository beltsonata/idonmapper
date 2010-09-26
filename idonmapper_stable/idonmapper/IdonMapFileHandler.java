package idonmapper;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.*;

import java.io.*;
import java.awt.Color;
import java.awt.Point;

/**
 * Allows the saving and retrieving of IdonMaps into the application.
 */ 
public class IdonMapFileHandler implements ErrorHandler
{
    private DocumentBuilderFactory fact;
    private DocumentBuilder builder;
    private Document xmlDoc = null;
    private DOMSource source;
    private Transformer trans;
    private boolean containsFatalError = false;
    public final static String DTD_STRING = 
        "<!ELEMENT idon-map (map-name, cell-size, idons, suggestions,"
        + "view-position)>\n<!ELEMENT map-name (#PCDATA)>\n"
        + "<!ELEMENT cell-size (#PCDATA)>\n<!ELEMENT idons (idon*)>\n"
        + "<!ELEMENT idon (coord,idea,color,size)>\n"
        + "<!ELEMENT coord (x-coord,y-coord)>\n"
        + "<!ELEMENT x-coord (#PCDATA)>\n<!ELEMENT y-coord (#PCDATA)>\n"
        + "<!ELEMENT idea (#PCDATA)>\n<!ELEMENT color (red,green,blue)>\n"
        + "<!ELEMENT red (#PCDATA)>\n<!ELEMENT green (#PCDATA)>\n"
        + "<!ELEMENT blue (#PCDATA)>\n<!ELEMENT size (#PCDATA)>\n"
        + "<!ELEMENT suggestions (suggestion*)>\n<!ELEMENT suggestion "
        + "(#PCDATA)>\n<!ELEMENT view-position (x-pos,y-pos)>\n"
        + "<!ELEMENT x-pos (#PCDATA)>\n<!ELEMENT y-pos (#PCDATA)>";
        
    /*
     * Local reference to the dtd file for parsing
     * idonmapper xml files (assuming that the dtd file is
     * in the idonmapper base directory):
     * 
     * 
     * BASE/dtd/NAME_OF_DTD_FILE
     * 
     * If it does exist it is created from the DTD_STRING above.
     */ 
    public static String DTD_FILE_PATH = "idon-map.dtd";
    
    private String absoluteDTDPath;
    private int errors = 0;
    private int warnings = 0;
    private File dtdFile;
    
    
    public IdonMapFileHandler()
    {
        loadDTD();       
    }
    
    private void createDTD()
    {
        System.out.println("creating DTD file");
        try
        {
            dtdFile.createNewFile();
            FileWriter wr = new FileWriter(dtdFile);
            BufferedWriter bw = new BufferedWriter(wr);
            bw.write(DTD_STRING, 0, DTD_STRING.length());
            System.out.println("created DTD at " + dtdFile.getCanonicalPath());
            bw.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void loadDTD()
    {
        String path = System.getProperty("user.home");
        path = path.concat(System.getProperty("file.separator"));
        path = path.concat(DTD_FILE_PATH);
        
        System.out.println("load dtd path: " + path);
        
        dtdFile = new File(path);
        
        if(!dtdFile.exists())
        {
            createDTD();
        } 
    }
    
    /**
     * Attempts to parse an XML file according to
     * the idon-map.dtd file, then extract 
     * IdonMap data.
     * 
     * <p>
     * 
     * @param file The xml file to parse for data.
     * 
     * @return An IdonMap object containing valid 
     * data extracted from the file, or null.
     */ 
    protected IdonMap importFile(final File file)
    {   
        getDocFromFile(file);
        
        if(containsFatalError || errors > 0)
        {
            System.out.println("Failed to parse input file.");
            return null;
        }
        
        if(xmlDoc == null)
        {
            return null;   
        }
        
        final DocumentType dt = xmlDoc.getDoctype();
        
        /*
         * Check the xml file's doctype
         */ 
        if(!dt.getName().toString().equals("idon-map"))
        {
            System.out.println("importFile - file has wrong dtd: "
                                                      + dt.getName());
            return null;
        }
        return retrieveIdonMapData();
    }
    
    /*
     * Retrieves the IdonMap data from the Document
     * returned from the parsing process, or null 
     * on failure.
     */ 
    private IdonMap retrieveIdonMapData()
    {
        //System.out.println("retrieveIdonMapData");
        String mapName = "";
        int cellSize = -1;
        final List<Idon> idons;
        final List<String> suggestions;
        
        /*
         * Get every Node from the document in one traversable list
         */ 
        final Element elem = xmlDoc.getDocumentElement();
        final NodeList allNodes = elem.getElementsByTagName("*"); 
        mapName = retrieveMapName(allNodes);
        cellSize = (int)retrieveNumberFromTextNode(allNodes, "cell-size");
        idons = retrieveIdons(allNodes);
        //final Rectangle view = retrieveViewRect(allNodes);
        final Point view = retrieveViewPoint(allNodes);
        
        suggestions = retrieveSuggestions(allNodes);
        return new IdonMap(mapName, cellSize, idons, suggestions, view);
    }
    
    /*
     * Retrieves the suggestion information from a
     * NodeList and returns a Rectangle.
     */
    private List<String> retrieveSuggestions(final NodeList allNodes)
    {
        final List<String> suggestions = new ArrayList<String>();
        
        final NodeList suggestionsList = getChildNodesFromString(allNodes, 
                                                     "suggestions");
         for(int i = 0; i < suggestionsList.getLength(); i++)
         {
             final Node currentNode = suggestionsList.item(i);
             
             if(currentNode.getNodeType() == Node.ELEMENT_NODE
                &&
               currentNode.getNodeName().equals("suggestion"))
             {
                 final NodeList children = currentNode.getChildNodes();
                 final String currString = getFirstTextNodeValue(children);
                 suggestions.add(currString);
             }
         }
         return suggestions;
    }

    /*
     * Retrieves the view port information from a
     * NodeList and returns a Rectangle.
     */ 
    private Point retrieveViewPoint(final NodeList allNodes)
    {
        //NodeList children = allNodes.getChildNodes();
        final NodeList rectNodes = getChildNodesFromString(allNodes, 
                                                     "view-position");
        //final int height = (int)retrieveNumberFromTextNode(rectNodes, "height");
        //final int width = (int)retrieveNumberFromTextNode(rectNodes, "width");
        final int xPos = (int)retrieveNumberFromTextNode(rectNodes, "x-pos");
        final int yPos = (int)retrieveNumberFromTextNode(rectNodes, "y-pos");                 
        //return new Rectangle(xPos, yPos, width, height);
        return new Point(xPos, yPos);
    }
    
    /*
     * Retrieves Idon information from the beginning of the
     * 'idons' section of an xml Document. Returns them
     * in an ArrayList.
     */ 
    private List<Idon> retrieveIdons(final NodeList allNodes)
    {
        final List<Idon> idons = new ArrayList<Idon>();
        
        final NodeList idonsStart = getChildNodesFromString(allNodes, "idons");
 
        for(int i = 0; i < idonsStart.getLength(); i++)
        {
            final Node currentNode = idonsStart.item(i);
            
            if(currentNode.getNodeType() == Node.ELEMENT_NODE
                &&
               currentNode.getNodeName().equals("idon"))
            {
                getIdonInfoFromNode(currentNode, idons);
            }
        }
        return idons;
    }
    
    /**
     * Exports an IdonMap object to a file.
     * 
     * @param idonMap The map to export.
     * 
     * @param file The file to write to. This will
     * be overwritten, if it exists.
     */ 
    protected boolean exportFile(final IdonMap map, final File file)
    {
        final String path = file.getPath();
        final String name = file.getName();
        System.out.println("file: " + path + ", " + name);
        
        try
        {
            fact = DocumentBuilderFactory.newInstance();
            builder = fact.newDocumentBuilder();
        }
        catch(ParserConfigurationException e)
        {
            e.printStackTrace();
            // pop up an error dialog
            return false;
        } 
        
        xmlDoc = builder.newDocument();
        
        /*
         * Create each element individually
         */  
        final Element idonMapEl = xmlDoc.createElement("idon-map");
        final Element mapNameEl = createElementWithText("map-name", 
                                                   map.getMapName());
        final Element cellSizeEl = createElementWithText("cell-size", 
                                   Double.toString(map.getCellSize()));
        final Element idonsEl = xmlDoc.createElement("idons");
        createIdonElements(map, idonsEl);
        
        final Element suggestionsEl = createSuggestionsElement(map);
        final Element viewEl = createViewElement(map);
        
        /*
         * Add the elements to the Document
         */ 
        xmlDoc.appendChild(idonMapEl);
        
        idonMapEl.appendChild(mapNameEl);
        idonMapEl.appendChild(cellSizeEl);
        idonMapEl.appendChild(idonsEl);
        idonMapEl.appendChild(suggestionsEl);
        idonMapEl.appendChild(viewEl);
        
        return documentToFile(file);
    }
    
    private boolean documentToFile(final File file)
    {
        try
        {
            final TransformerFactory tfact = TransformerFactory.newInstance();
            trans = tfact.newTransformer();
        }
        catch(final TransformerConfigurationException e)
        {
            e.printStackTrace();
            return false;
        }
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        
        //trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, absoluteDTDPath);
        trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD_FILE_PATH);
        
        trans.setOutputProperty(OutputKeys.STANDALONE, "no");
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        final StreamResult res = new StreamResult(file);
        source = new DOMSource(xmlDoc);
        
        try
        {
            trans.transform(source, res);
        }
        catch(TransformerException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /*
     * Creates the Element containing all user set
     * data in the the IdonMap
     */ 
    private Element createSuggestionsElement(final IdonMap map)
    {
        final Element suggestionsEl = xmlDoc.createElement("suggestions");
        
        for(final String s : map.getSuggestions())
        {
            final Element sugg = createElementWithText("suggestion", 
                                                 s);
            suggestionsEl.appendChild(sugg);
        }
        
        return suggestionsEl;   
    }
    
    private Element createViewElement(final IdonMap map)
    {
        final Element viewEl = xmlDoc.createElement("view-position");
  
        final Element xPosEl = createElementWithText("x-pos",
                            Integer.toString(map.getViewPoint().x));
        final Element yPosEl = createElementWithText("y-pos",
                            Integer.toString(map.getViewPoint().y));
                            
        viewEl.appendChild(xPosEl);
        viewEl.appendChild(yPosEl);
        return viewEl;
    }
    /*
     * Creates the "idon" element for each Idon in the 
     * map, adding each one as a child to Element parent.
     */ 
    private void createIdonElements(final IdonMap map, 
                                    final Element parent)
    {
        for(final Idon idon : map.getIdons())
        {
            /*
             * For each Idon, create new
             * child elements for idea,
             * color and coord
             */ 
            final Element idonEl = xmlDoc.createElement("idon");
            
            final Element coordEl = xmlDoc.createElement("coord");
            final Element xCoordEl = createElementWithText("x-coord", 
                                   Integer.toString(idon.getCoord().x));
            final Element yCoordEl = createElementWithText("y-coord", 
                                   Integer.toString(idon.getCoord().y));
            coordEl.appendChild(xCoordEl);
            coordEl.appendChild(yCoordEl);
            idonEl.appendChild(coordEl);
            
            final Element ideaEl 
                        = createElementWithText("idea", idon.getIdea());
            idonEl.appendChild(ideaEl);
            
            final Element colEl = createColorElement(idon.getColor());
            idonEl.appendChild(colEl);
            
            final String size = Integer.toString(idon.getSize());
            
            final Element sizeEl = createElementWithText("size", size);
            idonEl.appendChild(sizeEl); 
            
            /*
             * Append the idonElement to the parent.
             */ 
            parent.appendChild(idonEl);
        }
    }
    
    /*
     * Extracts the RGB information from 
     * a Color object and returns it as 
     * a Document Element.
     */ 
    private Element createColorElement(final Color col)
    {
        final Element colEl = xmlDoc.createElement("color");
        
        final Element redEl = createElementWithText("red", 
                                Integer.toString(col.getRed()));
 
        final Element greenEl = createElementWithText("green", 
                                Integer.toString(col.getGreen()));
            
        final Element blueEl = createElementWithText("blue", 
                                Integer.toString(col.getBlue())); 
        colEl.appendChild(redEl);
        colEl.appendChild(greenEl);
        colEl.appendChild(blueEl);
        return colEl;
    }
    
    /*
     * Utility method to create a new Element
     * with a single Text element inside.
     */ 
    private Element createElementWithText(final String name, 
                                          final String str)
    {
        final Element el = xmlDoc.createElement(name);
        final Text text = xmlDoc.createTextNode(str);
        el.appendChild(text);
        return el;
    }
    
    /*
     * For each idon element Node, create a new Idon with
     * the specified coords, idea and color and add to the list of Idons.
     */ 
    private void getIdonInfoFromNode(final Node currentNode, 
                                     List<Idon> idons)
    {
        final NodeList idonChildren = currentNode.getChildNodes();
        final NodeList coordNodes = getChildNodesFromString(
                                     idonChildren, "coord");   
        final int xcoord = (int)retrieveNumberFromTextNode(coordNodes, 
                                                "x-coord");
        final int ycoord = (int)retrieveNumberFromTextNode(coordNodes, 
                                                "y-coord");
        final Coord coord = new Coord(xcoord, ycoord);
        
        if(coord == null)
        {
            System.out.println("coord is null");
        }
        
        final Hexagon hex = Controller.getHexPanel()
                                      .getHexagonFromCoord(coord);
        if(hex == null)
        {
            throw new NullPointerException("fileHandler.getIdonInfoFromNode"
                    + " could not get a Hexagon at " + coord);
        }
                                
        final NodeList ideaNodes = getChildNodesFromString(
                                     idonChildren, "idea");
        final String idea = getFirstTextNodeValue(ideaNodes);
        
        final NodeList colorNodes = getChildNodesFromString(idonChildren, 
                                                      "color"); 
        final int r = getColorValue(colorNodes, "red");
        final int g = getColorValue(colorNodes, "green");
        final int b = getColorValue(colorNodes, "blue");
        final Color color = new Color(r, g, b);
        
        final int idonSize 
                = (int)retrieveNumberFromTextNode(idonChildren, "size");
        System.out.println("got size from xml: " + idonSize);
        
        if(idons == null)
        {
            idons = new ArrayList<Idon>(); 
        }
        
        idons.add(new Idon(hex, idea, color, idonSize));  
    }
                                    
    
    /*
     * Utility method to retrieve one of the RGB
     * values from the 'color' Element from
     * a NodeList.
     */ 
    private int getColorValue(final NodeList nl, final String rgb)
    {
        return (int)retrieveNumberFromTextNode(nl, rgb);   
    }
    
    
    /*
     * Retrieve the cell size data from a NodeList.
     */ 
    private double retrieveNumberFromTextNode(final NodeList allNodes, 
                                              final String childListName)
    {
        final NodeList childNodes 
                    = getChildNodesFromString(allNodes, childListName);
        
        if(childNodes == null)
        {
            System.out.println(childListName + " not found");
            return -1;
        }
        
        final String cellString = getFirstTextNodeValue(childNodes);
        
        try
        {
            return Double.parseDouble(cellString);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return -1;
    }
    
    
    
    /*
     * Retrieve the IdonMap's Name from a NodeList.
     */ 
    private String retrieveMapName(final NodeList allNodes)
    {
        final NodeList mapNameNodes 
                        = getChildNodesFromString(allNodes, "map-name");
        return getFirstTextNodeValue(mapNameNodes);
    }
    
    /*
     * Utility method to extract a named element NodeList from 
     * inside another NodeList.
     */ 
    private NodeList getChildNodesFromString(final NodeList nl, 
                                             final String name)
    {
        for(int i = 0; i < nl.getLength(); i++)
        {
            final Node curNode = nl.item(i);
            
            if(curNode.getNodeType() == Node.ELEMENT_NODE
                &&
               curNode.getNodeName().equals(name))
            {
                return curNode.getChildNodes();
            }
        }
        return null;
    }
    
    /*
     * Returns the first value inside the first text node
     * in a NodeList.
     */ 
    private String getFirstTextNodeValue(final NodeList nl)
    {
        if(nl == null)
        {
            return null;
        }
        for(int i = 0; i < nl.getLength(); i++)
        {
            final Node n = nl.item(i);
            
            if(n.getNodeType() == Node.TEXT_NODE)
            {
                return n.getNodeValue().trim();
            }
        }   
        return null;
    }
    
    /*
     * Utility method to extract a w3 Document
     * object from a valid IdonMap into xmlDoc
     */ 
    private void getDocFromFile(final File file)
    {
        fact = DocumentBuilderFactory.newInstance();
        fact.setValidating(true);
        fact.setIgnoringComments(true);
        fact.setNamespaceAware(true);

        try
        {
            builder = fact.newDocumentBuilder();
            builder.setErrorHandler(this);
        }
        catch(final ParserConfigurationException e)
        {
            e.printStackTrace();
            return;
        }
        try 
        {
            /*
             * Try and parse the file
             * and retrieve a Document
             */ 
            xmlDoc = builder.parse(file);
        }
        catch(final FileNotFoundException e)
        {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
        catch(final IOException e) 
        {
            e.printStackTrace();
        }      
        catch(final SAXException e)
        {
            System.out.println("xml parsing failed - check xml file");
        } 
        catch(final IllegalArgumentException e)
        {
            System.out.println("parsing failed - illegal argument\n");
        } 
   }

    
    /**
     * Public error handling methods. Not to be used other than 
     * internally.
     */ 
    public void fatalError(final SAXParseException e) throws SAXException 
    {
        System.out.println(e.getLineNumber() + ":" + e.getColumnNumber()
                    + ": " + e.getPublicId() + ".\n" + e.getMessage());
        System.out.println("Fatal error!");
        containsFatalError = true;    
    }

    public void error(final SAXParseException e) throws SAXParseException
    {
        System.out.println(e.getLineNumber() + ":" + e.getColumnNumber()
                    + ": " + e.getPublicId() + ".\n" + e.getMessage());
        System.out.println("Error!");
        errors++;
    }

    public void warning(final SAXParseException e) throws SAXParseException
    {
        System.out.println(e.getLineNumber() + ":" + e.getColumnNumber()
                    + ": " + e.getPublicId() + ".\n" + e.getMessage());
        warnings++;
    }
}
