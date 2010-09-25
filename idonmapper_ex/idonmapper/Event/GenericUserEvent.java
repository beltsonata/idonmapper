package idonmapper.Event;
/**
 * General, all-purpose user
 * event class.
 */ 
public abstract class GenericUserEvent extends Object implements UserEvent
{    
    public GenericUserEvent()
    {}   
    
    /**
     * Empty undo and redo methods. 
     * Implementations exist
     * in the sub classes.
     */ 
    public void undo()
    {   
    }
    public void redo()
    {   
    }
}

