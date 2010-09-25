package idonmapper.Event;

/**
 * A Generic User Event which is extended to create events when
 * adding, removing and altering Idons on a HexPanel so that the user
 * can undo (Ctrl-Z) and redo (Ctrl-Y) them.
 */ 
public interface GenericUserEvent
{       
    /**
     * Empty undo method. Implement in sub-classes.
     */ 
    void undo();
    
    /**
     * Empty redo method. Implement in sub-classes.
     */ 
    void redo();
}

