package idonmapper;
import java.util.*;
import java.awt.Rectangle;

/**
 * Represents the saved state information an idonmapper map. 
 */ 
public class IdonMap 
{
    private final String mapName;
    private final double cellSize;
    private final List<Idon> idons;
    private final Set<String> suggestions;
    private final Rectangle viewRect;
    
    public IdonMap(final String mapName, final double cellSize, 
                   final Collection<Idon> idons, 
                   final Collection<String> suggestions, 
                   final Rectangle viewRect)
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
    
    public Set<String> getSuggestions()
    {
        return suggestions;
    }
    
    public List<Idon> getIdons()
    {
        return idons;
    }
}
