/**
 * Copyright Sean Talbot 2010.
 * 
 * This file is part of idonmapper.
 * idonmapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * idonmapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with idonmapper.  If not, see <http://www.gnu.org/licenses/>. 
 */
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
