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
