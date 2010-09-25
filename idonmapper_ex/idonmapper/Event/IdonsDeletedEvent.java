package idonmapper.Event;

import idonmapper.*;
import java.util.HashMap;
import java.awt.Color;

/**
 * Encapsulates deleted Idon data
 */ 
public class IdonsDeletedEvent extends GenericUserEvent
{
    HashMap<Coord, Idon> deleted;
    
    public IdonsDeletedEvent(HashMap<Coord, Idon> deleted)
    {
        this.deleted = deleted;    
    }

   /**
    * Defines the undo information for this
    * UserEvent. 
    */ 
    public void undo()
    {
        Controller.addMultipleIdons(deleted);
    }
    
   /**
    * Defines the redo information for this
    * UserEvent. 
    */ 
    public void redo()
    {
        Controller.removeIdonsFromHexPanel(deleted);
    }
    
    public String toString()
    {
        return "IdonsDeletedEvent";
    }
}
