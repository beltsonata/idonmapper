package idonmapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Renderer used by the KeyboardInputPanel that keeps a record of the
 * currently highlighted (focused) Object in its JComboBox
 */ 
public class SuggestionRenderer extends JLabel implements ListCellRenderer 
{
    /*
     * Keep a record of which
     * cell in the list index 
     * is currently in focus
     * and which is selected
     */ 
    private int focusedCellIndex = -1;   
    private int selectedCellIndex = -1;   
    
    JComboBox combo;
    JList currentList = null;
    
    /**
     * Creates a new renderer and associates it with a JComboBox.
     * 
     * @param combo The JComboBox that will use this custom renderer. 
     */ 
    public SuggestionRenderer(JComboBox combo)
    {
        super();
        setOpaque(true);
	    setVerticalAlignment(CENTER);
        combo.setRenderer(this);
        this.combo = combo;
    }
    
    /**
     * Returns this object.
     * 
     * This should not be used except internally
     * by the KeyboardInputPanel
     */ 
    public Component getListCellRendererComponent(JList list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) 
    {
        /*
         * Check to see if a different list has been sent.
         */ 
        if(list != currentList)
        {
            /*
             * If so, update our current list.
             */ 
            currentList = list;
            
            /*
             * Associate a mouse listener with the
             * List to read user selections. 
             */ 
            list.addMouseListener(new MouseAdapter()
            {
                public void mouseReleased(MouseEvent e)
                { 
                    Controller.getInputPanel().mouseReleasedOnSuggestion(e);
                }
            });
        }        
        if(cellHasFocus)
        {
            focusedCellIndex = index;
            //System.out.println("focused -> " + value.toString());
        }
	    if(isSelected)
        {
            selectedCellIndex = index;
            //System.out.println("selected -> " + value.toString());
	    	setBackground(list.getSelectionBackground());
	    	setForeground(list.getSelectionForeground());
        }
        else 
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setText(value.toString());
		return this;
	}
    
    /**
     * Used to find the currently
     * focused (highlighted) cell in 
     * a JComboBox.
     * 
     * @return The index in the List to 
     * the focused Object.
     */ 
    public int getFocusedIndex()
    {
        return focusedCellIndex;
    }
    
    /**
     * Getter for the currently selected
     * Object's position in the list
     * 
     * @return The index in the List to 
     * the selected Object
     */ 
    public int getSelectedIndex()
    {
        return selectedCellIndex;
    }
    
    /**
     * Getter method for the Object's
     * JList. Used by the KIP to read
     * mouse events fired on the popup
     * menu.
     * 
     * @return The current JList.
     */ 
    protected JList getList()
    {
        return currentList;
    }
}
