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
