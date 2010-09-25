package idonmapper;

import idonmapper.Event.*;
import java.io.*;
import net.miginfocom.swing.MigLayout;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.MouseScrollableUI;
import org.jdesktop.swingx.*;


/**
 * Used to draw arrows on the HexPanel.
 */ 
public class ArrowPainter 
{
    private Arrow arrow;
    private boolean painting = false;
    
    /**
     * Creates a new ArrowPainter.
     */ 
    public ArrowPainter(){}
    
    /**
     * Creates a new ArrowPainter with a new Arrow beginning at the 
     * specified Point.
     */ 
    public ArrowPainter(Point start)
    {
        setStartPoint(start);
        
    }
    
    public boolean isPainting()
    {
        return painting;
    }
    
    /**
     * Sets the end point for the Arrow.
     */
    public void setStartPoint(Point start)
    {
        System.out.println("setting arrow start " + start);
        arrow = new Arrow(start);   
        painting = true;
    }
    
    
    /**
     * Updates the arrow with the current mouse position
     */ 
    public void setTempEnd(Point p)
    {
        //System.out.println("updating arrow");
        arrow.setEndPoint(p);
    }
    
    /**
     * Sets the end point for the Arrow.
     */ 
    public void setEndPoint(Point p)
    {
        arrow.setEndPoint(p);
        painting = false;
    }
}
