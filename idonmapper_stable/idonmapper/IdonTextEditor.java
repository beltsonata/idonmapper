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
    private Idon idon;
    
    private boolean editing = false;
    
    public IdonTextEditor()
    {
        super();
    }
    
    public void editIdon(final Idon i)
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
    public void keyPressed(final KeyEvent e)
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
        System.out.println("stopEditing");
        
        setVisible(false);
        final String oldString = idon.getIdea();
        final String newString = getText();
        
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
    private boolean isEnterKey(final KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            return true;
        }
        return false;
    }
    /**
     * Unused boiler plate code.
     */ 
    public void keyReleased(final KeyEvent e){}
    
    /**
     * Unused boiler plate code.
     */ 
    public void keyTyped(final KeyEvent e){}
}

