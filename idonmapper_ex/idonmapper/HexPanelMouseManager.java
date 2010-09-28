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
