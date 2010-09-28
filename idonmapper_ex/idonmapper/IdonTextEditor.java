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

import idonmapper.Event.IdonStringChangeEvent;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;

/**
 * A pop-up box triggered by a user double-clicking on 
 * an Idonthat allows one to edit the text it contains. 
 */ 
public class IdonTextEditor extends JTextPane implements KeyListener
{
    Idon idon;
    
    boolean editing = false;
    
    public IdonTextEditor()
    {
        super();
    }
    
    public void editIdon(Idon i)
    {
        this.idon = i;
        setUp();
    }
    
    private void setUp()
    {
        setText(idon.getIdea());

        /*
         * Overlay text editing area on
         * the current Idon's text area
         */ 
        setBounds(idon.getTextRect());
        setFont(idon.getFont());
        setVisible(true);
        
   
        addKeyListener(this);
        requestFocusInWindow();
        
        /*
         * Select all of the text so the
         * user can overwrite it immediately
         */ 
        selectAll();
        editing = true;
        setEnabled(true);
    }
    
    public boolean isEditingIdon()
    {
        return editing;
    }
    
    /**
     * Deal with Key Pressed events
     */ 
    public void keyPressed(KeyEvent e)
    {
        if(isEnterKey(e))
        {
            stopEditing();   
        }  
    } 
    
    /**
     * Method called when the user clicks outside / hits the enter key
     * whilst editing an Idon.
     */ 
    protected String stopEditing()
    {
       //System.out.println("stopEditing");
        
        setVisible(false);
        String oldString = idon.getIdea();
        String newString = getText();
        
        //setText(textPane.getText());
        
        if(!oldString.equals(newString))
        {
            Controller.setIdonIdea(idon, newString);
            
            //Controller.setEditedIdon(null);
            Controller.pushToUndoStack(new IdonStringChangeEvent(idon, 
                                                 oldString, newString));
            Controller.getSetResultsFromString(newString);
        }
        
        /*
         * Give focus back to the HexPanel
         */ 
        Controller.getHexPanel().requestFocusInWindow();
        //Controller.getHexPanel().repaint();
        editing = false;
        setEnabled(false);
        
        return newString;
    }
    
    /*
     * Conveniance class to discern if @e
     * is the Enter key
     */ 
    private boolean isEnterKey(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            return true;
        }
        return false;
    }
    
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
}

