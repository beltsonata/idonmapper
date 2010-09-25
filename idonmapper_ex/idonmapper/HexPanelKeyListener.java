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
