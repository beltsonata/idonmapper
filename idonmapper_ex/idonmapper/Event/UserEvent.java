package idonmapper.Event;


/**
 * User Events - the events that the idonmapper
 * application records in the Controller.userEventStack.
 * 
 * Enables the Controller to perform Undo/Redo
 * functions. 
 * 
 * Note that an incomplete GenericUserEvent template
 * is below for the most used of tasks.
 */
public interface UserEvent 
{        
    void undo();
    void redo();
}
