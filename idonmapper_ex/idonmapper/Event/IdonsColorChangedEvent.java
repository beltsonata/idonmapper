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
package idonmapper.Event;

import idonmapper.Controller;
import idonmapper.Coord;
import java.util.HashMap;
import java.awt.Color;

/**
 * Defines a user event in which one or
 * more the Idons on the HexPanel change
 * color.
 */ 
public class IdonsColorChangedEvent extends GenericUserEvent
{
    HashMap<Coord, Color> oldColorMap, newColorMap;
     
    public IdonsColorChangedEvent(HashMap<Coord, Color> oldColorMap,
                                  HashMap<Coord, Color> newColorMap)
    {
        this.oldColorMap = oldColorMap;
        this.newColorMap = newColorMap;
    }

   /**
    * Defines the undo implementation for this
    * UserEvent. 
    */ 
    public void undo()
    {
        Controller.setIdonColors(oldColorMap);
    }
    
   /**
    * Defines the redo implementation for this
    * UserEvent. 
    */ 
    public void redo()
    {
        Controller.setIdonColors(newColorMap);
    }
    
    public String toString()
    {
        return "IdonsColorChangedEvent";
    }
}
