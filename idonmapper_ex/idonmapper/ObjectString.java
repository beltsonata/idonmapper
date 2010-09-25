package idonmapper;


/**
 * Conveniance class used for adding bare
 * Strings to the KeyboardInputPanel's 
 * 
 */ 
public class ObjectString extends Object
{
    String s;
    public ObjectString(String s)
    {
        this.s = s;
    }
    public String toString()
    {
        return s;
    }
}
