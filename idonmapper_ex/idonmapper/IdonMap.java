package idonmapper;
import java.util.*;
import java.awt.Rectangle;

/**
 * Represents the saved state detauks of an idonmapper map. 
 */ 
public class IdonMap 
{
    String mapName;
    double cellSize;
    ArrayList<Idon> idons;
    HashSet<String> suggestions;
    Rectangle viewRect;
    
    public IdonMap(String mapName, double cellSize, Collection<Idon> idons, 
                   Collection<String> suggestions, Rectangle viewRect)
    {
        this.mapName = mapName;
        this.cellSize = cellSize;
        this.idons = new ArrayList<Idon>(idons);
        this.suggestions = new HashSet<String>(suggestions);
        this.viewRect = viewRect;
    }
    
    public String getMapName()
    {
        return mapName;
    }
    
    public double getCellSize()
    {
        return cellSize;
    }
    
    public Rectangle getViewRect()
    {
        return viewRect;
    }
    
    public HashSet<String> getSuggestions()
    {
        return suggestions;
    }
    
    public ArrayList<Idon> getIdons()
    {
        return idons;
    }
}
