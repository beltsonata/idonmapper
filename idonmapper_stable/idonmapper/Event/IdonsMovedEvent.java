package idonmapper.Event;
import idonmapper.Controller;
import idonmapper.Coord;
import idonmapper.Idon;
import java.util.*;

/**
 * Defines an event where an Idon has
 * been moved from one Coord
 * to a new Coord on the Hex Panel
 */ 
public class IdonsMovedEvent implements GenericUserEvent
{
    /*
     * The Idons effected and their original coordinates
     */ 
    Map<Idon, Coord> oldPositions;
    
    /*
     * The Idon's old positions
     */ 
    Map<Idon, Coord> newPositions;
         
    public IdonsMovedEvent(final Map<Idon, Coord> oldPositions, 
                           final Map<Idon, Coord> newPositions)
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
    
    private void doIt(final Map<Idon, Coord> here, 
                      final Map<Idon, Coord> there)
    {
        Controller.moveMultipleIdons(here, there);
        Controller.getHexPanel().repaint();
    }
    
    public String toString()
    {
        return "IdonsMovedEvent";
    }
}
