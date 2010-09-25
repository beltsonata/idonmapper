package idonmapper;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;

/**
 * Defines the procedures for handling MouseEvents on 
 * the HexPanel. 
 */
public class HexPanelMouseManager extends MouseInputAdapter  
{
    public HexPanelMouseManager()
    {
        /*
         * Set this object as the listener
         * for the hex panel for
         * *both* types of Mouse Listener
         */ 
        Controller.getHexPanel().addMouseMotionListener(this);
        Controller.getHexPanel().addMouseListener(this);
    }
    
    /**
     * Defines the procedure for pressing
     * a mouse button on the HexPanel
     */ 
    public void mousePressed(MouseEvent e)
    {        
        Controller.hexPanelMousePressed(e);
    }
    
    /**
     * Defines the procedure for dragging
     * a mouse button across the HexPanel
     */ 
    public void mouseDragged(MouseEvent e)
    {   
        Controller.hexPanelMouseDragged(e);
    }
    
    /**
     * Deal with mouseReleased (mouse up) events on the HexPanel.
     */
    public void mouseReleased(MouseEvent e)
    {     
        Controller.hexPanelMouseReleased(e);      
    }
    
    /**
     * Deal with mouse clicke events on the HexPanel.
     */
    public void	mouseClicked(MouseEvent e) 
    {   
        //Controller.hexPanelMouseClicked(e);
    }
    
    public void mouseMoved(MouseEvent e){}
}
