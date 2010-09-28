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
