package idonmapper.Event;

import idonmapper.Controller;
import idonmapper.Idon;
import java.util.*;

/**
 * Encapsulates information about one or more Idons having their sizes
 * altered. [Note that this is not used in the stable version].
 */ 
public class IdonSizeChangeEvent implements GenericUserEvent
{
    final private List<Idon> idons;
    final private int change;
    
    public IdonSizeChangeEvent(final List<Idon> idons, final int change)
    {
        this.idons = idons;
        this.change = change;
    }
    public void undo()
    {   
        int revert;
        if(change == Controller.getHexPanel().INCREASE)
        {
            revert = Controller.getHexPanel().DECREASE;
        }
        else if(change == Controller.getHexPanel().DECREASE)
        {
            revert = Controller.getHexPanel().INCREASE;
        }
        else
        {
            throw new IllegalStateException();
        }
        Controller.getHexPanel().alterIdonSize(idons, revert);
    }
    public void redo()
    {   
        Controller.getHexPanel().alterIdonSize(idons, change);
    }
    

    
    public String toString()
    {
        return "IdonSizeChangeEvent";
    }
}
