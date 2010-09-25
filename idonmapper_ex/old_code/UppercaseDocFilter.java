package idonmapper;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;
/**
 * Document filter used by the IdonTextEditor
 * that converts all chars to uppercase
 */ 
public class UppercaseDocFilter extends DocumentFilter
{
    public UppercaseDocFilter()
    {     
    } 
    
    public void insertString(DocumentFilter.FilterBypass fb, int off,
                             String s, AttributeSet attr)
                             throws BadLocationException
    {
        super.insertString(fb, off, s.toUpperCase(), attr);        
    }
 
    public void replace(DocumentFilter.FilterBypass fb, int off, int l,
                        String s, AttributeSet attr) 
                        throws BadLocationException
    {
         super.replace(fb, off, l, s.toUpperCase(), attr);
    }
}
