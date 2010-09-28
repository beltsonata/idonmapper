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
