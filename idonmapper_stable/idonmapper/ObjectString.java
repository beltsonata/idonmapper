package idonmapper;


/**
 * Conveniance class used for adding bare
 * Strings to the KeyboardInputPanel's 
 * 
 */ 
public class ObjectString extends Object
{
    public String s;
    
    /**
     * Creates a new ObjectString from an existing String.
     */ 
    public ObjectString(final String s)
    {
        this.s = s;
    }
    public String toString()
    {
        return s;
    }
}
