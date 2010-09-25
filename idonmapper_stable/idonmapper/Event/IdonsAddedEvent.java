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
