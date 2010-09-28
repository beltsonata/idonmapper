package idonmapper;

import java.util.*;
import javax.swing.*;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.lang.Math;
import java.awt.geom.*;
import java.awt.font.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;


public class Idon extends Hexagon
{
    protected String idea;
    private Font font;

    /*
     * Idon size variables.
     */
    public static final int SIZE_SMALL = 0, SIZE_NORMAL = 1,
                            SIZE_LARGE = 2;
    private double smallSize, normalSize, largeSize;

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
    private static final float normalWidth = 1.2f,
                               selectedWidth = 4.2f;

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

    private List<TextLayout> layouts = null;

    private Point textRectTopLeft;

    Font smallFont, normalFont, largeFont;

    /**
     * Main Idon constructor.
     */
    public Idon(final double hexSize, final double xPos, final double yPos, 
                final Coord c)
    {
        super(hexSize, xPos, yPos, c);
        createStrokeStyles();
    }

    /**
     * Creates an idon from a hexagon with additional
     * information
     */
    public Idon(final Hexagon h, final String idea, final Color col, 
                final int size)
    {
        this(h);
        this.size = size;
        setSizes();
        setColor(col);
        setIdea(idea);
        createTextAreaBounds();
    }

    private void setFontSizes()
    {
        normalFont = new Font("Serif", Font.PLAIN,
                                            calcFontSize(normalSize));
        smallFont = new Font("Serif", Font.PLAIN,
                                            calcFontSize(smallSize));
        largeFont = new Font("Serif", Font.PLAIN,
                                            calcFontSize(largeSize));
    }

    private int calcFontSize(final double size)
    {
        return (int)(size / 5) + 4;
    }

    private void setSizes()
    {
        normalSize = getSideLength();
        smallSize = normalSize - (normalSize / 2);
        largeSize = normalSize + (normalSize / 2);
        setFontSizes();

        if(size == SIZE_LARGE)
        {
            super.changeSize(largeSize);
            setFont(largeFont);
        }
        else if(size == SIZE_SMALL)
        {
            super.changeSize(smallSize);
            setFont(smallFont);
        }
        else
        {
            setFont(normalFont);
        }
    }

    /**
     * Creates an idon from a hexagon.
     */
    public Idon(final Hexagon h)
    {
        this(h.getSideLength(), h.getXPos(), h.getYPos(), h.getCoord());
    }

    /*
     * Getter/setter methods for
     * the Idon's Font object
     */
    protected final void setFont(final Font f)
    {
        this.font = f;
    }
    public Font getFont()
    {
        return font;
    }

    protected void setCoord(final Coord c)
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
            super.changeSize(largeSize);
            setFont(largeFont);
            size = SIZE_LARGE;
        }
        else if(size == SIZE_SMALL)
        {
            super.changeSize(normalSize);
            setFont(normalFont);
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
            super.changeSize(smallSize);
            setFont(smallFont);
            size = SIZE_SMALL;
        }
        else if(size == SIZE_LARGE)
        {
            super.changeSize(normalSize);
            setFont(normalFont);
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

    /**
     * Sets the Idon's colour.
     * <p>
     * @param c The Color to set this Idon.
     */
    public final void setColor(final Color c)
    {
        if(c == null)
        {
            fillColor = Controller.getColorFromString("default");
        }
        else
        {
            fillColor = c;
        }
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
    protected void setDragState(final boolean dragged)
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
     * Enters an idea into the Idon.
     */
    /*
     * After setting an idea, the 'layouts' var
     * is set to null to ensure
     */
    public final void setIdea(final String s)
    {
        this.idea = s;
        layouts = null;
    }


    /**
     * Sets up the idon text's rectangular
     * bounding box which is used as
     * a guide to keep the text within the hexagon's
     * borders.
     */
    private void createTextAreaBounds()
    {
        if(hasTextRect)
        {
            return;
        }
        
        int count = 0;
        
        final int topLeft = 3;
        final int topRight = 5;
        final int bottomLeft = 2;
        final int hex = 6;
        final int quad = 4;

        final PathIterator rectPath = getShape().getPathIterator(null);

        final double coords[] = new double[hex];
        final Point2D.Double[] rectPoints = new Point2D.Double[quad];

        while(!rectPath.isDone())
        {
            final int i = rectPath.currentSegment(coords);
            if(i == PathIterator.SEG_LINETO)
            {
                ++count;
                switch(count)
                {
                    case bottomLeft:
                        rectPoints[2] =
                            new Point2D.Double(coords[0], coords[1]);
                    break;
                    case topLeft:
                        rectPoints[0] =
                            new Point2D.Double(coords[0], coords[1]);
                    break;
                    case topRight:
                        rectPoints[1] =
                            new Point2D.Double(coords[0], coords[1]);
                    break;
                }
            }
            rectPath.next();
        }

        final double x = rectPoints[0].getX();
        final double y = rectPoints[0].getY();

        textRectTopLeft = new Point((int)x, (int)y);

        final double rHeight = rectPoints[2].getY() - rectPoints[0].getY();
        final double rWidth = rectPoints[1].getX() - rectPoints[0].getX() ;

        /*
         * Create the bounding box from the above points:
         */
        textAreaRect = new Rectangle((int)x, (int)y, (int)rWidth, 
                                     (int)rHeight);
        hasTextRect = true;
    }

    /*
     * Returns a string representation of this Idon
     */
    public String toString()
    {
        final String s = idea + " Coord: " + this.coord.toString()
                         + "Selected: " + isSelected;
        return s;
    }

    /**
     * Draws the Idon's hexagon shape and fills it
     * with the specific colour. This method is called from
     * the HexPanel.
     */
    public void draw(final Graphics2D g)
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
        drawBackgroundColour(g);
        drawIdeaText(g);
        //drawTextRect(g);
    }
    
    private void drawTextRect(final Graphics2D g)
    {
        g.draw(textAreaRect);
    }

    /**
     * Sets the stroke type to use when
     * drawing this Idon
     */
    private void setStroke(final Graphics2D g)
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
    public void setPosition(final double xPos, final double yPos)
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
    private void drawIdeaText(final Graphics2D g)
    {
        if(layouts == null)
        {
            createTextLayouts();
        }
        
        final Color oldCol = g.getColor();
        
        g.setColor(textColor);
        g.setFont(font);
        
        int yLevel = 1;

        for(final TextLayout t : layouts)
        {
            final float xText = ((float)getTextXPos() +
                                (((float)textAreaRect.getWidth() /2)
                                 - t.getVisibleAdvance()/2));
                            
            final float yText = (float)getTextYPos() 
                                + (t.getAscent() * yLevel);
            
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
        
        final AttributedString attrString = 
                  new AttributedString(idea, getFont().getAttributes());
        
        final AttributedCharacterIterator itr = attrString.getIterator();
        
        final LineBreakMeasurer breakMeasurer 
                                     = new LineBreakMeasurer(itr, frc);
        paraStart = itr.getBeginIndex();
        paraEnd = itr.getEndIndex();
        breakMeasurer.setPosition(paraStart);

        layouts.clear();

        while(breakMeasurer.getPosition() < paraEnd)
        {
            final TextLayout currentLayout =
                 breakMeasurer.nextLayout((int)textAreaRect.getWidth());
            layouts.add(currentLayout);
        }
    }

    /**
     * Debugging method to display null objects
     */
    private void isNull(final Object o, final String name)
    {
        if(o == null)
        {
            System.out.println("Warning: " + name + " is null");
        }
    }

    /**
     * Fills the interior of the Idon's shape
     */
    private void drawBackgroundColour(final Graphics2D g)
    {
        final Color oldColor = g.getColor();
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
    protected boolean isSelected()
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
