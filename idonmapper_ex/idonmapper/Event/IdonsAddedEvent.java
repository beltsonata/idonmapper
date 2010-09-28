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

import idonmapper.*;
import java.util.*;
import java.awt.Color;

/**
 *
 */ 
public class IdonsAddedEvent extends GenericUserEvent
{
    HashMap<Coord, Idon> added;
    
    public IdonsAddedEvent(HashMap<Coord, Idon> added)
    {
              
        for(Map.Entry<Coord, Idon> e : added.entrySet())
        {
            Coord c = e.getKey();
            Idon i = e.getValue();
            System.out.println(c.x + ", " + c.y + " | " + i.getIdea());
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
