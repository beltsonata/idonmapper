package idonmapper;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;
/**
 * Document filter used by the IdonTextEditor
 * that converts all chars to uppercase
 */ 
// Unused.
public class UppercaseDocFilter extends DocumentFilter
{
    public UppercaseDocFilter()
    {     
    } 
    
    public void insertString(final DocumentFilter.FilterBypass fb, 
                                final int off,final String s, 
                                    final AttributeSet attr)
                                         throws BadLocationException
    {
        super.insertString(fb, off, s.toUpperCase(Controller.LOCALE), 
                            attr);        
    }
 
    public void replace(final DocumentFilter.FilterBypass fb, 
                        final int off, final int l, final String s, 
                        final AttributeSet attr) 
                                            throws BadLocationException
    {
         super.replace(fb, off, l, s.toUpperCase(Controller.LOCALE), 
                            attr);
    }
}
