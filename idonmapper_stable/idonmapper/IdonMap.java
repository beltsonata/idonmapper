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
import java.util.*;
import java.awt.Point;

/**
 * Represents the saved state information an idonmapper map.
 */ 
public class IdonMap 
{
    private final String mapName;
    private final double cellSize;
    private final List<Idon> idons;
    private final Set<String> suggestions;
    //private final Rectangle viewRect;
    private final Point viewPoint;
    
    
    public IdonMap(final String mapName, final double cellSize, 
                   final Collection<Idon> idons, 
                   final Collection<String> suggestions, 
                   final Point viewPoint
                   /*final Rectangle viewRect*/)
    {
        this.mapName = mapName;
        this.cellSize = cellSize;
        this.idons = new ArrayList<Idon>(idons);
        this.suggestions = new HashSet<String>(suggestions);
        //this.viewRect = viewRect;
        this.viewPoint = viewPoint;
    }
    
    public String getMapName()
    {
        return mapName;
    }
    
    public double getCellSize()
    {
        return cellSize;
    }
    
    /*public Rectangle getViewRect()
    {
        return viewRect;
    }*/
    public Point getViewPoint()
    {
        return viewPoint;
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
