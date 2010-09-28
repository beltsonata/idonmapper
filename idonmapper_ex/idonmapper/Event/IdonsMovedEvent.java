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
import java.util.HashMap;

/**
 * Defines an event where an Idon has
 * been moved from one Coord
 * to a new Coord on the Hex Panel
 */ 
public class IdonsMovedEvent extends GenericUserEvent
{
    /*
     * The Idons effected and their original coordinates
     */ 
    HashMap<Idon, Coord> oldPositions;
    
    /*
     * The Idon's old positions
     */ 
    HashMap<Idon, Coord> newPositions;
         
    public IdonsMovedEvent(HashMap<Idon, Coord> oldPositions, 
                           HashMap<Idon, Coord> newPositions)
    {
        this.oldPositions = oldPositions;
        this.newPositions = newPositions;
    }
    

   /**
    * Defines the undo implementation for this
    * UserEvent. 
    */ 
    public void undo()
    {
        doIt(newPositions, oldPositions);
    }
    
   /**
    * Defines the redo implementation for this
    * UserEvent. 
    */ 
    public void redo()
    {
        doIt(oldPositions, newPositions);
    }
    
    private void doIt(HashMap<Idon, Coord> here, 
                      HashMap<Idon, Coord> there)
    {
        Controller.moveMultipleIdons(here, there);
        Controller.getHexPanel().repaint();
    }
    
    public String toString()
    {
        return "IdonsMovedEvent";
    }
}
