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

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import java.awt.geom.*;
import java.awt.font.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class Idon extends Hexagon
{
    private String idea;
    
    // store the points of the hexagon shap in here 
    //private HashMap<String, Point2D.Double> hexPointMap;
    
    private Font font; 
    /*
     * Idon size variables.
     */ 
    public static final int SIZE_SMALL = 0, SIZE_NORMAL = 1, 
                            SIZE_LARGE = 2, SMALL_MULT = 3,
                            NORMAL_MULT = 6, LARGE_MULT = 12;
    // ints signifying where to find each point on the hex shape
    // in the PathIterator below    
    private static final int Southern = 0, BottomRight = 1, 
                             BottomLeft = 2, TopLeft = 3, Northern = 4,
                             TopRight = 5;
                             
    //private double smallSize, normalSize, largeSize;
    private int size, paraStart, paraEnd;
    
    /* 
     * Whether or not the Idon has been
     * selected on the hex panel
     */ 
    private boolean isSelected = false;
    
    /*
     * Two different stroke widths for the
     * Idon's outline, depending on the
     * state of @isSelected
     */ 
    private static final float normalWidth = 1.2f, selectedWidth = 4.2f;
                               
    private BasicStroke normalStroke = null, selectedStroke = null;
    
    private boolean hasTextRect = false;
    private boolean isBeingDragged = false;
    
    /*
     * Default colours 
     */
    protected Color fillColor = Color.yellow, 
                    textColor = Color.black;
                    
    protected Rectangle textAreaRect;
    private FontRenderContext frc;
    private LineBreakMeasurer breakMeasurer;
    
    private ArrayList<TextLayout> layouts = null;
            
    private Point2D.Double textRectTopLeft;
    
    //Font smallFont, normalFont, largeFont;
    
    /**
     * Main Idon constructor. 
     */
    public Idon(double hexSize, double xPos, double yPos, Coord c, 
                String idea, Color col, int size)
    {
        super(hexSize, xPos, yPos, c);
        this.size = size;
        //setFontSizes();
        createStrokeStyles();
        createTextAreaBounds();
        setIdea(idea);
        setFont();
        //setFontSizes();
        //setSizes();
        setColor(col);
    }
    
    /**
     * Returns the mapping of the points of the Idon's hex shape.
     *//* 
    public HashMap<String, Point2D.Double> getHexPointMap()
    {
        return hexPointMap;
    }*/
    
    /**
     * Creates an idon from a hexagon with additional
     * information
     */
    /*public Idon(Hexagon h, String idea, Color col, int size)
    {
        //this(h);
        
        
        
    }*/
    
   
    private int calcFontSize(double size)
    {
        return (int)(size / 5) + 3;
    }
    
    //private void setSizes()
    //{
        //smallSize = getSideLength() * SMALL_MULT;
        //normalSize = getSideLength() * NORMAL_MULT;
        //largeSize = getSideLength() * LARGE_MULT;
   
        //setFontSizes();
        
        /*if(size == SIZE_LARGE)
        {
            super.changeSize(largeSize);
            setFont(largeFont);
        }
        else if(size == SIZE_SMALL)
        {
            super.changeSize(smallSize);
            setFont(smallFont);
        }
        else if(size == SIZE_NORMAL)
        {
            super.changeSize(normalSize);
            setFont(normalFont);
        }
        else
        {
            throw new IllegalArgumentException("Idon size is not small, " 
                                + " normal or large!");
        }*/
    //}
    
   
    
    /*
     * Getter/setter methods for
     * the Idon's Font object
     */ 
    /*protected void setFont(Font f)
    {
        this.font = f;
    }*/
    protected void setFont()
    {
        this.font = new Font("Serif", Font.PLAIN, 
                             calcFontSize(super.getSideLength()));
    }
        
    public Font getFont()
    {
        return font;
    }
    
    protected void setCoord(Coord c)
    {
        this.coord = c;
    }
    
    /**
     * Returns this Idon's @fillColor
     */ 
    public Color getColor()
    {
        return fillColor;
    }
    
    private void recreateTextRect()
    {
        hasTextRect = false;
        createTextAreaBounds();
        layouts = null;
    }
    
    protected boolean increaseSize()
    {
        if(size == SIZE_NORMAL)
        {
            //super.changeSize(largeSize);
            super.changeSize(Controller.getHexPanel().getLargeSize());
            //setFont(largeFont);
            setFont();
            size = SIZE_LARGE;
        }
        else if(size == SIZE_SMALL)
        {
            //super.changeSize(normalSize);
            super.changeSize(Controller.getHexPanel().getNormalSize());
            //setFont(normalFont);
            setFont();
            size = SIZE_NORMAL;
        }
        else
        {
            return false;
        }
        recreateTextRect();
        return true;
    }
    
    /**
     * Decreases the size of this Idon if the Idon is either SIZE_LARGE
     * or SIZE_NORMAL. 
     */ 
    protected boolean decreaseSize()
    {
        if(size == SIZE_NORMAL)
        {
            //super.changeSize(smallSize);
            super.changeSize(Controller.getHexPanel().getSmallSize());
            //setFont(smallFont);
            setFont();
            size = SIZE_SMALL;
        }   
        else if(size == SIZE_LARGE)
        {
            //super.changeSize(normalSize);
            super.changeSize(Controller.getHexPanel().getNormalSize());
            //setFont(normalFont);
            setFont();
            size = SIZE_NORMAL;
        }
        else
        {
            return false;
        }
        recreateTextRect();
        return true;
    }
    
    /**
     * Returns the size (SIZE_NORMAL, SIZE_SMALL, SIZE_LARGE) of
     * this Idon.
     */ 
    public int getSize()
    {
        return size;
    }
    
    /**
     * Resets this Idon to its position when it was first put at
     * its current Coord.
     */ 
    public void resetPosition()
    {
        setPosition(getOriginalXPos(), getOriginalYPos());    
    }
    
    /*public Position getPosition()
    {
        return new Position(getXPos(), getYPos());
    }*/
    
    /**
     * Sets the Idon's colour.
     * <p>
     * @param c The Color to set this Idon.
     */ 
    public void setColor(Color c)
    {
        if(c == null)
        {
            c = Controller.getColorFromString("default");
        }
        fillColor = c;
        return;
    }
   
    
    /**
     * Deselect the idon in the hex panel
     */ 
    protected void deselect()
    {
        isSelected = false;
        Controller.getHexPanel().repaint();
    }

    /**
     * Returns the Idon's drag state
     */ 
    public boolean getDragState()
    {
        return isBeingDragged;
    }
    
    /**
     * Sets the Idon's drag state. Used by the Controller module.
     */ 
    protected void setDragState(boolean dragged)
    {
        //System.out.println(this + " setDragState: " + dragged);
        isBeingDragged = dragged;
    }
    /*
     * Returns the fill color of
     * this Idon
     */ 
    protected Color getFillColor()
    {
        return fillColor;
    }
    
    /**
     * Deselect the idon in the hex panel
     */ 
    protected void select()
    {
        isSelected = true;
        Controller.getHexPanel().repaint();
    }
    
    /**
     * Complements the value of @isSelected
     */ 
    protected void toggleSelectStatus()
    {
        if(isSelected)
        {
            deselect();
            return;
        }
        select();
    }

    
    /**
     * Return the idea associated with this Idon.
     */
    public String getIdea()
    {
        return idea;
    }
    
    /**
     * Creates the stroke styles : normal,
     * for non-selected Idons, and selected, for
     * selected ones.
     */ 
    private void createStrokeStyles()
    {
        normalStroke   = new BasicStroke(normalWidth);
        selectedStroke = new BasicStroke(selectedWidth);
    }
    
    /**
     * Enters an idea into the idon. 
     */
    /*
     * After setting an idea, the 'layouts' var
     * is set to null to ensure
     */
    public void setIdea(String s)
    {
        //this.idea = s.toUpperCase();
        //System.out.println("setting idea " + s);
        this.idea = s;
        layouts = null;
    }
    
        
    /**
     * Sets up the idon text's rectangular bounding box which is used as 
     * a guide to keep the text within the hexagon's
     * borders. 
     */  
    /*
     * During this, the method stores all of the points of the
     * hexagon shape in the hexPointMap
     */ 
    private void createTextAreaBounds()
    {   
        if(hasTextRect)
        {
            return;
        }     
           
        int pos = 0;
        int count = 0;
        
        
        
        // move these to the main body as public final static....
        final int hex = 6;
        final int quad = 4;

        PathIterator hexPath = getShape().getPathIterator(null);
        
        double coords[] = new double[hex];
        Point2D.Double[] rectPoints = new Point2D.Double[quad];
        
        // Initialise the hexPoint map and add each one during constructino
        // of the text area rectangle
        //hexPointMap = new HashMap<String, Point2D.Double>();
        
        Point2D.Double p;
        
        while(!hexPath.isDone())
        {
            int i = hexPath.currentSegment(coords);
            if(i == PathIterator.SEG_LINETO)
            {
                ++count;
                switch(count)
                {
                    case TopLeft: 
                        p = new Point2D.Double(coords[0], coords[1]);
                        rectPoints[0] = p;    
                        //hexPointMap.put("TL", p);
                    break;
                    case TopRight:
                        p = new Point2D.Double(coords[0], coords[1]);
                        rectPoints[1] = p;
                       // hexPointMap.put("TR", p);
                    break;
                    case BottomLeft:
                        p = new Point2D.Double(coords[0], coords[1]);
                        rectPoints[2] = p;
                       // hexPointMap.put("BL", p); 
                    break;
                    case Northern:
                        p = new Point2D.Double(coords[0], coords[1]);
                       // hexPointMap.put("N", p);
                    break;
                    case Southern: 
                        p = new Point2D.Double(coords[0], coords[1]);
                       // hexPointMap.put("S", p);
                    break;
                    case BottomRight:
                        p = new Point2D.Double(coords[0], coords[1]);
                       //hexPointMap.put("BR", p);
                    break;
                } 
            }
            hexPath.next();
        }
          
        double x = rectPoints[0].getX();
        double y = rectPoints[0].getY();
        
        textRectTopLeft = rectPoints[0];
        
        double rHeight = rectPoints[2].getY() - rectPoints[0].getY();
        double rWidth = rectPoints[1].getX() - rectPoints[0].getX() ;
        
        /*
         * Create the bounding box from the above points:
         */
        textAreaRect = new Rectangle((int)x, (int)y, (int)rWidth, (int)rHeight);
        hasTextRect = true;
        //printHexPoints();
    }
    

    
    /*
     * Returns a string representation of this Idon
     */ 
    public String toString()
    {
        String s = idea + " Coord: " + this.coord.toString() + ", Size: " + size 
                    + ", Selected: " + isSelected;
        return s;
    }
    
    /**
     * Draws the Idon's hexagon shape and fills it
     * with the specific colour. This method is called from
     * the HexPanel.
     */
    public void draw(Graphics2D g)
    {   
        /*
         * Set the stroke style, depending
         * on status of isSelected
         */ 
        this.setStroke(g);
        
        /*
         * Draws the hexagonal shape.
         */
        super.draw(g);
        
        frc = g.getFontRenderContext();
        //drawBackgroundColour(g);
        //drawIdeaText(g);  
    }
        
    /**
     * Sets the stroke type to use when
     * drawing this Idon
     */ 
    private void setStroke(Graphics2D g)
    {
        if(isSelected)
        {
            g.setStroke(selectedStroke);
        }
        else
        {
            g.setStroke(normalStroke);
        }
    }
    
    /**
     * Sets the position of the Idon on
     * the HexPanel.
     */ 
    public void setPosition(double xPos, double yPos)
    {
        super.setPosition(xPos, yPos);
        hasTextRect = false;
        createTextAreaBounds();
    }
    

    
    /**
     * Draws the text layouts inside the 
     * text rectangle
     * 
     * Text is centered horizontally inside the
     * rectangle
     */
    private void drawIdeaText(Graphics2D g)
    {   
        float xText, yText;
        
        if(layouts == null)
        {
            createTextLayouts();
        }
        Color oldCol = g.getColor();
        g.setColor(textColor);
        g.setFont(font);
        int yLevel = 1;
        float xPadding = 2.3f;
        
        for(TextLayout t : layouts)
        {
            xText = ((float)getTextXPos() +
                    (((float)textAreaRect.getWidth() /2)  
                            - t.getVisibleAdvance()/2));
            yText = (float)getTextYPos() + (t.getAscent() * yLevel);
            if(textAreaRect.contains(new Point2D.Double(xText, yText)))
            {
                t.draw(g, (int)xText, (int)yText); 
            }
            ++yLevel;
        }
        g.setColor(oldCol);
    }
    
    /*
     * Creates the text layout objects from the idea String.
     */
    private void createTextLayouts()
    {
        layouts = new ArrayList<TextLayout>();
        
        if(idea.length() == 0)
        {
            return;
        }
        AttributedString attrString = new AttributedString(idea, getFont().getAttributes());
        AttributedCharacterIterator itr = attrString.getIterator();
        breakMeasurer = new LineBreakMeasurer(itr, frc);
        paraStart = itr.getBeginIndex();
        paraEnd = itr.getEndIndex();
        breakMeasurer.setPosition(paraStart);
        
        layouts.clear();
        
        while(breakMeasurer.getPosition() < paraEnd) 
        {
            TextLayout currentLayout = 
                 breakMeasurer.nextLayout((int)textAreaRect.getWidth());
            layouts.add(currentLayout);
        }
    }
    
    /**
     * Debugging method to display null objects
     */ 
    private void isNull(Object o, String name)
    {
        if(o == null)
        {
            System.out.println("Warning: " + name + " is null");
        }
    }
    
    /**
     * Fills the interior of the Idon's shape
     */ 
    private void drawBackgroundColour(Graphics2D g)
    {
        Color oldColor = g.getColor();
        g.setColor(this.fillColor);
        g.fill(getShape());
        g.setColor(oldColor);
    }
    
    /*
     *  Get the X and y positions for the label
     */
    public double getTextXPos()
    {
        return textRectTopLeft.x;
    }
    public double getTextYPos()
    {
        return textRectTopLeft.y;
    }
    
    /**
     * Return's whether or not this
     * Idon is selected 
     */ 
    protected boolean getSelectedStatus()
    {
        return isSelected;
    }
   
    /**
     * Returns the Rectangle
     * bounding the Idon's internal
     * text.
     */ 
    protected Rectangle getTextRect()
    {
        return textAreaRect;
    }    
}
