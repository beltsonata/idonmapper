package idonmapper.Event;

import idonmapper.Controller;
import idonmapper.Idon;

/**
 * Defines an event when an Idon's idea has changed
 */ 
public class IdonStringChangeEvent extends GenericUserEvent
{
    Idon i;
    String oldStr, newStr;
    
    public IdonStringChangeEvent(Idon i, String oldStr, String newStr)
    {
        this.i = i;
        this.oldStr = oldStr;
        this.newStr = newStr;
    }
    public void undo()
    {   
        Controller.setIdonIdea(i, oldStr);
    }
    public void redo()
    {   
        Controller.setIdonIdea(i, newStr);
    }
}
