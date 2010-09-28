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
import idonmapper.Idon;
import java.util.*;
import java.awt.Color;

/**
 * Defines the contents of a UserEvent in which Idons are added
 * to the HexPanel. 
 */ 
public class IdonsAddedEvent implements GenericUserEvent
{
    final private Map<Coord, Idon> added;
    
    public IdonsAddedEvent(final Map<Coord, Idon> added)
    {
        for(final Map.Entry<Coord, Idon> e : added.entrySet())
        {
            final Coord c = e.getKey();
            final Idon i = e.getValue();
            //System.out.println(c.x + ", " + c.y + " | " + i.getIdea());
        }
        
        this.added = added;    
    }
       
   /**
    * Defines the undo information for this
    * UserEvent. 
    */ 
    public void undo()
    {
        Controller.removeIdonsFromHexPanel(added);
    }
    
   /**
    * Defines the redo information for this
    * UserEvent. 
    */ 
    public void redo()
    {
        Controller.addMultipleIdons(added);
    }
    
    public String toString()
    {
        return "IdonsAddedEvent: ";
    }
}
