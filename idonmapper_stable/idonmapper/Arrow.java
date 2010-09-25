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
 * An arrow that is drawn on the HexPanel by the user with an.
 */ 
public class Arrow implements HexPanelObject
{
    private int startX, startY, endX, endY;
    
    private boolean drawable = false;
    
    public Arrow(Point start)
    {
        startX = start.x;
        startY = start.y;
    }
    
    public void setEndPoint(Point p)
    {
        endX = p.x;
        endY = p.y;
        
        createTriangle();
    }
    
    private void createTriangle()
    {
        drawable = true;        
    }
    
    public void draw(Graphics2D g)
    {
        if(drawable)
        {
            g.drawLine(startX, startY, endX, endY);
        }
    }
    
    public double getXPos()
    {
        return startX;
    }
    public double getYPos()
    {
        return startY;
    }
    
    public void setPosition(double xPos, double yPos)
    {
        startX = (int)xPos;
        startY = (int)yPos;
        
        
    }
}
