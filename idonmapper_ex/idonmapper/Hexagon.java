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
import java.util.*;

/**
 * Represents a Hexagon shape. 
 */
public class Hexagon implements HexPanelObject
{
    public static final int  HEX = 6,
                             HALF_HEX = 3;
    private GeneralPath path;
    private AffineTransform aff;
    private double sideLength, hexOffset, xPos, yPos, origXPos, origYPos;
    private Shape hexShape = null;
    private boolean shapeCreated = false;
    protected Coord coord;
    protected boolean initialPosition = true;
    
    /*
     * Hexagon
     */
    public Hexagon(double size, double xPos, double yPos, Coord coord)
    {
        checkArgs(size, xPos, yPos);
        
        sideLength = size;
        setPosition(xPos, yPos);
        this.coord = coord;
    }
    
    protected Coord getCoord()
    {
        return coord;
    }
    
    /*
     * Checks the size, xPos and yPos arguments
     * for non-positive values.
     */
    private void checkArgs(double... numbers)
    {
        if(containsNegativeOrZero(numbers))
        {
            throw new IllegalArgumentException(
                "Hexagon constructor received illegal number(s)");
        }
    }

    private boolean containsNegativeOrZero(double... numbers)
    {
        for(double n : numbers)
        {
            if(n < 0 || n == 0)
            {
                return true;
            }
        }
        return false;
    }

    /*
     * Sets the Hexagon's shape according to the arguments given
     * to the constructor.
     */
    private void setUp()
    {
        // Distance between 2 cells of this size
        hexOffset = (sideLength) * Math.sqrt(HALF_HEX);
        path = createPath();
        path.closePath();
        scaleRotateAndMove();
        createShape();
    }
    
    
    protected void changeSize(double sideLength)
    {
        this.sideLength = sideLength;
        setUp();
    }
    
    /*
     * Creates the Hexagon's GeneralPath object based on its given
     * coordinates.
     */
    private GeneralPath createPath()
    {
        GeneralPath path = new GeneralPath();
        path.setWindingRule(GeneralPath.WIND_EVEN_ODD);
        path.moveTo(1, 0);
        int n = 1;

        while(n < HEX)
        {
            path.lineTo(Math.cos(n * Math.PI/HALF_HEX),
                        Math.sin(n * Math.PI/HALF_HEX));
            ++n;
        }
        return path;
    }

    /**
     * Returns the distance between the centre of two Hexagons of 
     * this size.
     */ 
    public double getHexOffset()
    {
        return hexOffset;
    }

    /*
    public void setPosition(Position p)
    {
        setPosition(p.x, p.y);
    } */
    /*public Position getPosition()
    {
        return new Position(getXPos(), getYPos());
    }*/
    
    /*
     * Alter the position of the Hexagon, then create its
     * new geometry.
     */
    public void setPosition(double xPos, double yPos)
    {
        /*
         * Store the very first x
         * and y position so that
         * they can be reverted to 
         * easily
         */ 
        if(initialPosition)
        {
            origXPos = xPos;
            origYPos = yPos;
            
            initialPosition = false;
        }
        this.xPos = xPos;
        this.yPos = yPos;
        setUp();
    }
    
    
    public double getOriginalXPos()
    {
        return origXPos;
    }
    
    
    public double getOriginalYPos()
    {
        return origYPos;
    }

	/*
	 * Returns the Hexagons horizontal hex panel coordinate 
	 */
    public double getXPos()
    {
        return xPos;
    }
    
    /*
	 * Returns the Hexagons vertical hex panel coordinate 
	 */
    public double getYPos()
    {
        return yPos;
    }

    /*
     * Scales up the path to the desired demension and rotates
     * to the 'pointing up' orientation
     */
    private void scaleRotateAndMove()
    {
        aff = new AffineTransform();
        aff.translate(xPos, yPos);
        aff.rotate(Math.toRadians(30));
        aff.scale(sideLength, sideLength);
    }
    /**
	 * Returns the Hexagon's GeneralPath object.
	 */ 
    public GeneralPath getPath()
    {
		if(path == null)
		{
			throw new NullPointerException("Hexagon.getPath() GeneralPath "
				+ "object not been created ");
			
		}
        return path;
    }
    
    /*
     * Creates the Shape of the
     * Hexagon.
     */ 
    private void createShape()
    {
        hexShape = aff.createTransformedShape(path);
        shapeCreated = true;
    }

	/**
	 * Returns the Hexagon's side (i.e. vertex) length.
	 */ 
    public double getSideLength()
    {
        return sideLength;
    }

    /**
     * Returns the Hexagon's Shape object. createShape  
     */
    protected Shape getShape()
    {
        if(shapeCreated == false)
        {
            createShape();
        }
        return hexShape;
    }

    /*
     * Draws the Hexagon (i.e. onto a HexPanel).
     */
    public void draw(Graphics2D g)
    {
        g.draw(getShape());
        
    }
    
    /*
     * Return true if point @p is inside
     * the hexagon's Shape
     */ 
    protected boolean containsPoint(Point p)
    {
        if(this.getShape().contains(p))
        {
            return true;
        }
        return false;
    }

    public String toString()
    {
        return "Hexagon";
    }

    /**
     * Returns true if the Hexagon's 
     * hexShape overlaps with @rectangle.
     */
    protected boolean intersects(Rectangle rectangle)
    {
        if(rectangle == null)
        {
            throw new NullPointerException("rectangle sent to intersect"
                    + " is null");   
        }
        
        if(this.getShape()/*.getBounds2D()*/.intersects(rectangle))
        {
            return true;
        }
        return false;

    }

    
    /**
     * Return true if @this hexagon is completely
     * enclosed by rectangle @visible
     */ 
    public boolean isEnclosedBy(Rectangle2D visible)
    {
        Area area = new Area(visible);   
        if(area.contains(this.getShape().getBounds2D()))
        {
            return true;
        }
        return false;
    }
}



