package idonmapper.Event;

import idonmapper.Controller;
import idonmapper.Coord;
import idonmapper.Idon;
import java.util.*;
import java.awt.Color;

/**
 * Encapsulates deleted Idon data
 */ 
public class IdonsDeletedEvent implements GenericUserEvent
{
    final private Map<Coord, Idon> deleted;
    
    public IdonsDeletedEvent(final Map<Coord, Idon> deleted)
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
