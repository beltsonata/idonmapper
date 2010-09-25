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
import java.awt.Rectangle;


public class IdonMapFileHandler implements ErrorHandler
{
    private DocumentBuilderFactory fact;
    private DocumentBuilder builder;
    private Document xmlDoc = null;
    private DOMSource source;
    private Transformer trans;
    private File dtdFile;
    private boolean containsFatalError = false;
    
    /*
     * Local reference to the dtd file for parsing
     * idonmapper xml files (assuming that the dtd file is
     * in the idonmapper base directory):
     * 
     * BASE/idonmapper/NAME_OF_DTD_FILE
     */ 
    public final String DTD_FILE_PATH = "idonmapper/idon-map.dtd";
    
    /*
     * The *absolute* path of the above dtd file path,
     * which is necessary when exporting xml files
     * so that the dtd can be found when 
     */ 
    private String absoluteDTDPath; 
    
    private int errors = 0;
    private int warnings = 0;
        
    public IdonMapFileHandler()
    {
        loadDTD();    
    }
    
    private void loadDTD()
    {
        dtdFile = new File(DTD_FILE_PATH);
        
        if(dtdFile == null)
        {
            throw new NullPointerException("Failed to load dtd file");
        } 
        absoluteDTDPath = dtdFile.getAbsolutePath();
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
    protected IdonMap importFile(File file)
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
        
        DocumentType dt = xmlDoc.getDoctype();
        
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
        System.out.println("retrieveIdonMapData");
        String mapName = "";
        int cellSize = -1;
        ArrayList<Idon> idons;
        ArrayList<String> suggestions;
        
        /*
         * Get every Node from the document in one traversable list
         */ 
        Element elem = xmlDoc.getDocumentElement();
        NodeList allNodes = elem.getElementsByTagName("*"); 
        mapName = retrieveMapName(allNodes);
        cellSize = (int)retrieveNumberFromTextNode(allNodes, "cell-size");
        idons = retrieveIdons(allNodes, cellSize);
        Rectangle view = retrieveViewRect(allNodes);
        suggestions = retrieveSuggestions(allNodes);
        return new IdonMap(mapName, cellSize, idons, suggestions, view);
    }
    
    /*
     * Retrieves the suggestion information from a
     * NodeList and returns a Rectangle.
     */
    private ArrayList<String> retrieveSuggestions(NodeList allNodes)
    {
        ArrayList<String> suggestions = new ArrayList<String>();
        
        NodeList suggestionsList = getChildNodesFromString(allNodes, 
                                                     "suggestions");
         for(int i = 0; i < suggestionsList.getLength(); i++)
         {
             Node currentNode = suggestionsList.item(i);
            
             if(currentNode.getNodeType() == Node.ELEMENT_NODE)
             {
                 if(currentNode.getNodeName().equals("suggestion"))
                 {
                     NodeList children = currentNode.getChildNodes();
                     String currString = getFirstTextNodeValue(children);
                    
                     suggestions.add(currString);
                 }
             }
         }
         return suggestions;
    }

    /*
     * Retrieves the view port information from a
     * NodeList and returns a Rectangle.
     */ 
    private Rectangle retrieveViewRect(NodeList allNodes)
    {
        //NodeList children = allNodes.getChildNodes();
        NodeList rectNodes = getChildNodesFromString(allNodes, 
                                                     "view-position");
        int height = (int)retrieveNumberFromTextNode(rectNodes, "height");
        int width = (int)retrieveNumberFromTextNode(rectNodes, "width");
        int xPos = (int)retrieveNumberFromTextNode(rectNodes, "x-pos");
        int yPos = (int)retrieveNumberFromTextNode(rectNodes, "y-pos");                 
        return new Rectangle(xPos, yPos, width, height);
    }
    
    /*
     * Retrieves Idon information from the beginning of the
     * 'idons' section of an xml Document. Returns them
     * in an ArrayList.
     */ 
    ArrayList<Idon> retrieveIdons(NodeList allNodes, int cellSize)
    {
        ArrayList<Idon> idons = new ArrayList<Idon>();
        int xCoord, yCoord;        
        NodeList idonsStart = getChildNodesFromString(allNodes, "idons");
 
        for(int i = 0; i < idonsStart.getLength(); i++)
        {
            Node currentNode = idonsStart.item(i);
            
            if(currentNode.getNodeType() == Node.ELEMENT_NODE)
            {
                if(currentNode.getNodeName().equals("idon"))
                {
                    getIdonInfoFromNode(currentNode, idons);
                }
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
    protected boolean exportFile(IdonMap map, File file)
    {
        String path = file.getPath();
        String name = file.getName();
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
        Element idonMapEl = xmlDoc.createElement("idon-map");
        Element mapNameEl = createElementWithText("map-name", 
                                                   map.getMapName());
        Element cellSizeEl = createElementWithText("cell-size", 
                                  Double.toString(map.getCellSize()));
        Element idonsEl = xmlDoc.createElement("idons");
        createIdonElements(map, idonsEl);
        
        Element suggestionsEl = createSuggestionsElement(map);
        Element viewEl = createViewElement(map);
        
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
    
    private boolean documentToFile(File file)
    {
        try
        {
            TransformerFactory tfact = TransformerFactory.newInstance();
            trans = tfact.newTransformer();
        }
        catch(TransformerConfigurationException e)
        {
            e.printStackTrace();
            return false;
        }
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        
        trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, absoluteDTDPath);
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        StreamResult res = new StreamResult(file);
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
    private Element createSuggestionsElement(IdonMap map)
    {
        Element suggestionsEl = xmlDoc.createElement("suggestions");
        
        for(String s : map.getSuggestions())
        {
            Element sugg = createElementWithText("suggestion", 
                                                 s);
            suggestionsEl.appendChild(sugg);
        }
        
        return suggestionsEl;   
    }
    
    private Element createViewElement(IdonMap map)
    {
        Element viewEl = xmlDoc.createElement("view-position");
        Element heightEl = createElementWithText("height", 
                            Integer.toString(map.getViewRect().height));
        Element widthEl = createElementWithText("width", 
                            Integer.toString(map.getViewRect().width));
        Element xPosEl = createElementWithText("x-pos",
                            Integer.toString(map.getViewRect().x));
        Element yPosEl = createElementWithText("y-pos",
                            Integer.toString(map.getViewRect().y));
        viewEl.appendChild(heightEl);
        viewEl.appendChild(widthEl);
        viewEl.appendChild(xPosEl);
        viewEl.appendChild(yPosEl);
        return viewEl;
    }
    /*
     * Creates the "idon" element for each Idon in the 
     * map, adding each one as a child to Element parent.
     */ 
    private void createIdonElements(IdonMap map, Element parent)
    {
        for(Idon idon : map.getIdons())
        {
            /*
             * For each Idon, create new
             * child elements for idea,
             * color and coord
             */ 
            Element idonEl = xmlDoc.createElement("idon");
            
            Element coordEl = xmlDoc.createElement("coord");
            Element xCoordEl = createElementWithText("x-coord", 
                                   Integer.toString(idon.getCoord().x));
            Element yCoordEl = createElementWithText("y-coord", 
                                   Integer.toString(idon.getCoord().y));
            coordEl.appendChild(xCoordEl);
            coordEl.appendChild(yCoordEl);
            idonEl.appendChild(coordEl);
            
            Element ideaEl = createElementWithText("idea", idon.getIdea());
            idonEl.appendChild(ideaEl);
            
            Element colEl = createColorElement(idon.getColor());
            idonEl.appendChild(colEl);
            
            String size = Integer.toString(idon.getSize());
            
            Element sizeEl = createElementWithText("size", size);
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
    private Element createColorElement(Color col)
    {
        Element colEl = xmlDoc.createElement("color");
        
        Element redEl = createElementWithText("red", 
                                Integer.toString(col.getRed()));
 
        Element greenEl = createElementWithText("green", 
                                Integer.toString(col.getGreen()));
            
        Element blueEl = createElementWithText("blue", 
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
    private Element createElementWithText(String name, String str)
    {
        Element el = xmlDoc.createElement(name);
        Text text = xmlDoc.createTextNode(str);
        el.appendChild(text);
        return el;
    }
    
    /*
     * For each idon element Node, 
     * create a new Idon with
     * the specified coords, idea and color
     */ 
    private void getIdonInfoFromNode(Node currentNode, 
                                     ArrayList<Idon> idons)
    {
        NodeList idonChildren = currentNode.getChildNodes();
        NodeList coordNodes = getChildNodesFromString(
                                     idonChildren, "coord");   
        int xcoord = (int)retrieveNumberFromTextNode(coordNodes, 
                                                "x-coord");
        int ycoord = (int)retrieveNumberFromTextNode(coordNodes, 
                                                "y-coord");
        Coord coord = new Coord(xcoord, ycoord);
        
        if(coord == null)
        {
            System.out.println("coord is null");
        }
        
        Hexagon hex = Controller.getHexPanel()
                                .getHexagonFromCoord(coord);
        if(hex == null)
        {
            throw new NullPointerException("fileHandler.getIdonInfoFromNode"
                    + " could not get a Hexagon at " + coord);
        }
                                
        NodeList ideaNodes = getChildNodesFromString(
                                     idonChildren, "idea");
        String idea = getFirstTextNodeValue(ideaNodes);
        
        NodeList colorNodes = getChildNodesFromString(idonChildren, 
                                                      "color"); 
        int r = getColorValue(colorNodes, "red");
        int g = getColorValue(colorNodes, "green");
        int b = getColorValue(colorNodes, "blue");
        Color color = new Color(r, g, b);
        
        int idonSize = (int)retrieveNumberFromTextNode(idonChildren, "size");
        System.out.println("got size from xml: " + idonSize);
        
        if(idons == null)
        {
            idons = new ArrayList<Idon>(); 
        }
        double idonSideLength = 
            Controller.getHexPanel().calculateIdonSize(
                                        hex.getSideLength(), idonSize);

        idons.add(new Idon(idonSideLength, hex.getXPos(), hex.getYPos(), 
                        coord, idea, color, idonSize));
    }
                                    
    
    /*
     * Utility method to retrieve one of the RGB
     * values from the 'color' Element from
     * a NodeList.
     */ 
    private int getColorValue(NodeList nl, String rgb)
    {
        return (int)retrieveNumberFromTextNode(nl, rgb);   
    }
    
    
    /*
     * Retrieve the cell size data from a NodeList.
     */ 
    private double retrieveNumberFromTextNode(NodeList allNodes, String childListName)
    {
        NodeList childNodes = getChildNodesFromString(allNodes, childListName);
        
        if(childNodes == null)
        {
            System.out.println(childListName + " not found");
            return -1;
        }
        
        String cellString = getFirstTextNodeValue(childNodes);
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
    private String retrieveMapName(NodeList allNodes)
    {
        NodeList mapNameNodes = getChildNodesFromString(allNodes, "map-name");
        return getFirstTextNodeValue(mapNameNodes);
    }
    
    /*
     * Utility method to extract a named element NodeList from 
     * inside another NodeList.
     */ 
    private NodeList getChildNodesFromString(NodeList nl, String name)
    {
        for(int i = 0; i < nl.getLength(); i++)
        {
            Node curNode = nl.item(i);
            if(curNode.getNodeType() == Node.ELEMENT_NODE)
            {
                if(curNode.getNodeName().equals(name))
                {
                    return curNode.getChildNodes();
                }
            }
        }
        return null;
    }
    
    /*
     * Returns the first value inside the first text node
     * in a NodeList.
     */ 
    private String getFirstTextNodeValue(NodeList nl)
    {
        if(nl == null)
        {
            return null;
        }
        for(int i = 0; i < nl.getLength(); i++)
        {
            Node n = nl.item(i);
            
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
    private void getDocFromFile(File file)
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
        catch(ParserConfigurationException e)
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
        catch(FileNotFoundException e)
        {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
        catch(IOException e) 
        {
            e.printStackTrace();
        }      
        catch(SAXException e)
        {
            System.out.println("xml parsing failed - check xml file");
        } 
        catch(IllegalArgumentException e)
        {
            System.out.println("parsing failed - illegal argument\n");
        } 
   }

    
    /**
     * Public error handling methods. Not to be used other than 
     * internally.
     */ 
    public void fatalError(SAXParseException e) throws SAXException 
    {
        System.out.println(e.getLineNumber() + ":" + e.getColumnNumber()
                    + ": " + e.getPublicId() + ".\n" + e.getMessage());
        System.out.println("Fatal error!");
        containsFatalError = true;    
        
    }

    public void error(SAXParseException e) throws SAXParseException
    {
        System.out.println(e.getLineNumber() + ":" + e.getColumnNumber()
                    + ": " + e.getPublicId() + ".\n" + e.getMessage());
        System.out.println("Error!");
        errors++;
    }

    public void warning(SAXParseException e) throws SAXParseException
    {
        System.out.println(e.getLineNumber() + ":" + e.getColumnNumber()
                    + ": " + e.getPublicId() + ".\n" + e.getMessage());
        warnings++;
    }
}
