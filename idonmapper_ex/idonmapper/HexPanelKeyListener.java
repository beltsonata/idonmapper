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
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Deals with key-presses sent to
 * hex panel, when it has focus 
 */ 
public class HexPanelKeyListener implements KeyListener
{        
    public HexPanelKeyListener()
    {
        Controller.getHexPanel().addKeyListener(this);
    }
    
    /**
     * Defines the actions taken from
     * key press events on the Hex 
     * panel
     */ 
    public void keyPressed(KeyEvent e) 
    {
        Controller.hexPanelKeyPressed(e);        
    }
 
    public void keyTyped(KeyEvent e) {}
    /**
     * Deals with Key released events
     * on the Hex Panel
     */ 
    public void keyReleased(KeyEvent e) 
    {       
        Controller.hexPanelKeyReleased(e);
    }
    
    
}
