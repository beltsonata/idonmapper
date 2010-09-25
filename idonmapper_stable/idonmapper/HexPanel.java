package idonmapper;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.Locale;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;
import java.io.*;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.search.*;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;

/**
 * A dual-axis coordinate-based Hexagonal grid.
 *
 * Idons are associated with Coord objects and then
 * the associated Hexagon is located on the map. This
 * is then used as the basis for the Idon.
 *
 * The Hexagonal orientation is fixed so that one of its
 */
public class HexPanel extends JPanel implements Scrollable
{
    private List<Hexagon>       cellList;
    private Map<Coord, Hexagon> coordMap;
    private Map<Coord, Idon>    idonMap;
    private List<Coord>         coordList;
    
    // NOTE: arrows have not been implemented at all.
    //final private List<Arrow>         arrowList;

    public static final int INCREASE = 1, DECREASE = -1;

    private static final int TRIP = 3,
                             QUAD = 4;

    private final int rows, columns;

    private static final int ANGLE = 30;

    private static final float HALF = 0.5f;
    
    private static final float StrokeWidth = 1.2f;


    /*
     * hexDist - Distance between the center of 2
     * hex cells.
     */
    private double hexDist, hexSize;

    public static final int SCROLL_UNIT_INCR = 10,
                            BLOCK_MULT = 3;
    public static final int NOWHERE = -1;

    /*
     * A rectangle used by the user to click
     * and drag across cells to select
     * multiple idons
     */
    private Rectangle dragRect = null;

    /*
     *  These constants represent the maximum allowable sizes,
     *  respectively, of:
     *
     *  MAX_HEX_SIZE - the sideLength (vertex) of the Hexagons
     * 					on the grid;
     * 	MAX_COLUMNS	 - the maximum allowable amount
     *  			   of columns on the grid;
     * 	MAX_ROWS	-the maximum allowable amount of rows on the grid;
     *
     */

    public static final double MAX_HEX_SIZE = 150.0,
                               MIN_HEX_SIZE = 30.0,
                               PADDING = 15;
    public static final int MAX_COLUMNS = 200, MAX_ROWS = 200,
                            HEX_SIZE_INCR = 5;

    /*
     * A matcher object to match Patterns sent from the Idon Finder
     * object
     */
    //public Matcher matcher;

    /*
     * HexPanel()
     * Create a HexPanel with the specified number of rows
     * and columns.
     */
    public HexPanel(final double hexSize, final int rows, final int columns)
    {
        super(null, false);
        checkArgs(hexSize, rows, columns);
        this.rows = rows;
        this.columns = columns;
        setHexSizePrivate(hexSize);        
        setBackground(Color.white);

        //arrowList = new ArrayList<Arrow>();
    }
    
    /**
     * Return the size of the side length of the cells on the grid.
     */ 
    protected double getHexSize()
    {
        return hexSize;
    }

    /**
     * Sets the vertex length of the hexagon cells in on
     * the HexPanel. Note that this method is only to be used
     * when creating the hexPanel.
     */
    protected void setHexSize(final double hexSize)
    {
        setHexSizePrivate(hexSize);
    }
    
    /*
     * This is private because originally, non-private method setHexSize()
     * was being called in the constructor, meaning that it could be
     * overidden and possibly cause an error. The protected method above
     * is essentially then a wrapper for this private method.
     */ 
    private final void setHexSizePrivate(final double hexSize)
    {
        this.hexSize = hexSize;

        /*
         * hexDist - Distance between the center of 2
         * hex cells.
         */
        hexDist = hexSize * Math.sqrt(3);
        //r = Math.cos(ANGLE) * hexSize;
        setGridDimensions();
    }
    
    /**
     * Returns the user-drag rectangle
     */
    protected Rectangle getDragRect()
    {
        return dragRect;
    }

    /**
     * Returns a set of the Idons on this
     * HexPanel.
     *
     * Used by the Controller module in the
     * process of saving the application
     * state to file.
     */
    protected Set<Idon> getIdons()
    {
        return new HashSet<Idon>(idonMap.values());
    }
    
    protected Map<Coord, Idon> getIdonMap()
    {
        return idonMap;
    }
    
    /**
     * Replaces the drag rectangle with @r
     */
    protected void setDragRect(final Rectangle r)
    {
        dragRect = r;
    }


    private void checkArgs(final double hexSize, final int rows, final int columns)
    {
        boolean bad = false;

        if(hexSize < 1 || rows < 1 || columns < 1)
        {
            bad = true;
        }
        else if(hexSize > MAX_HEX_SIZE || rows > MAX_ROWS
                || columns > MAX_COLUMNS)
        {
            bad = true;
        }

        if(bad)
        {
            throw new IllegalStateException(
                                "HexPanel received illegal variable(s): \n"
                                 + hexSize + ", " + rows + ", " + columns);
        }
    }

    /*
     * Sets the number of rows and columns of this
     * HexPanel.
     */
    private final void setGridDimensions()
    {
        createHexagonCells();
        setPreferredSize(calculateMinimumSize());
    }

    /**
     * Attempts to return the nearest unoccupied cell
     * coordinate to the top left of JScrollPane's
     * viewport
     */
    protected Coord getNearestUnoccupiedCell()
    {
        final java.util.List<Coord> visEmpty = getVisibleUnoccupiedCells();

        if(!visEmpty.isEmpty())
        {
            return visEmpty.get(0);
        }
        return getUnoccupiedCell();
    }


    /**
     * Returns an ArrayList of
     * all Idons on the HexPanel
     * that are currently selected
     */
    protected List<Idon> getSelectedIdons()
    {
        final List<Idon> sel = new ArrayList<Idon>();

        for(final Idon i : idonMap.values())
        {
            if(i.getSelectedStatus())
            {
                sel.add(i);
            }
        }
        return sel;
    }

    /**
     * Returns an ArrayList of
     * the coordinates all Idons
     * on the HexPanel that are
     * currently selected
     */
    protected List<Coord> getSelectedIdonsCoords()
    {
        final List<Coord> coords = new ArrayList<Coord>();

        for(final Idon i : getSelectedIdons())
        {
            coords.add(i.getCoord());
        }
        return coords;
    }

    /**
     * Returns an arraylist of cells that overlap with
     * @rect on the Hex Panel
     */
    protected List<Idon> getOverlappingIdons(final Rectangle rect)
    {
        if(rect == null)
        {
            throw new NullPointerException("getOverlappingCellCoord: "
                                           + " rectangle sent is null");
        }
        final List<Idon> overlap = new ArrayList<Idon>();

        for(final Map.Entry<Coord, Idon> e : idonMap.entrySet())
        {
            final Coord c = e.getKey();
            final Idon i = e.getValue();
            if(i.intersects(rect))
            {
                overlap.add(i);
            }
        }
        return overlap;
    }

    /**
     * Destroys the user-drag-rectangle
     */
    protected void destroyDragRect()
    {
        dragRect = null;
    }

    /*
     * Sets Idons found in the coordinates listed
     * in @newSelection, if present
     */
    protected void toggleSelectIdonsInList(final java.util.List<Idon> newSelection)
    {
        for(final Idon i : newSelection)
        {
            i.toggleSelectStatus();
        }
    }

    /*
     * Returns a reference to the Coord:Hexagon map.
     */
    protected Map<Coord, Hexagon> getCoordMap()
    {
        return coordMap;
    }

    /**
     * Returns a list containing the coordinates for
     * all empty hex cells on the hex panel adjacent to
     * Coord @c.
     */
    protected Set<Coord> getEmptyNeighbourCoords(final Coord c)
    {
        final Set<Coord> emptyNeighbours = new HashSet<Coord>();
        final Set<Coord> neighbours = getNeighbourCoords(c);

        if(neighbours == null)
        {
            return null;
        }

        for(final Coord cur : getNeighbourCoords(c))
        {
            if(!cellContainsIdon(cur))
            {
                emptyNeighbours.add(cur);
            }
        }
        return emptyNeighbours;
    }


    /*
     * Returns the coordinate on the grid
     * exactly in the middle of the panel,
     * so that the controller module can
     * move the viewport to it at the
     * beginning of the hex panel's creation
     */
    protected Hexagon getMiddleHexagon()
    {
        final int x = rows / 2;
        final int y = columns / 2;

        for(final Coord c : coordList)
        {
            if(c.x == x && c.y == y)
            {
                return getHexagonFromCoord(c);
            }
        }
        return null;
    }


    /**
     * Returns the coordinate to the nearest
     * empty cell to @c, or @c itself, if it is
     * empty. If the hashSet is not null, it will be
     * checked to see if the Coord has already been
     * examined.
     */
    protected Coord getNearestEmptyCellToCoord(final Coord c,
                                               final Set<Coord> exclude)
    {
        if(c == null)
        {
            return null;
        }
        if(getIdonFromCoord(c) == null && !exclude.contains(c))
        {
            return c;
        }

        final Set<Coord> unoccupied = getEmptyNeighbourCoords(c);
        
        if(unoccupied.isEmpty())
        {
            System.out.println("coord " + c.toString() + " has no"
                                  + " empty neighbours!");
            /*
             * Coord c has no empty adjacent
             * cells, so look for the nearest
             * empty cell to all of the
             */
             final Set<Coord> neighbours = getNeighbourCoords(c);

             for(final Coord curCoord : neighbours)
             {
                 return getNearestEmptyCellToCoord(curCoord, exclude);
             }
        }
        else
        {
            final Set<Coord> emptyList = new HashSet<Coord>(unoccupied);

            for(final Coord curCoord : emptyList)
            {
                return curCoord;
            }
        }

        return null;
    }

    /**
     * Returns a list of coordinates for all cells (empty or
     * otherwise) that are adjacent to @c.
     */
    protected Set<Coord> getNeighbourCoords(final Coord c)
    {
        if(c == null)
        {
            return null;
        }
        final Set<Coord> neighbours = new HashSet<Coord>();

        for(final Direction d : Direction.values())
        {
            final Coord adj = d.directionToCoord(c, d);

            /*
             * Only add to the list if the Coord actually
             * exists on the hex panel
             */
            if(isFoundInCoordList(adj))
            {
                neighbours.add(adj);
            }
        }
        return neighbours;
    }

    /**
     * Utility method to output the contents of the panel to a
     * postscript file.
     */
    public void paintToPostScript(final EPSDocumentGraphics2D g2d)
    {
        setUpGraphics2D(g2d);
        paintIdons(g2d);
    }

    private void setUpGraphics2D(final Graphics2D g2)
    {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(StrokeWidth));
        g2.setPaint(new Color(0, 0, 0));
    }

    /*
     * Returns true if a coordinate with the
     * same contents as @c exists in the
     * hex panel's coordinate list
     */
    private boolean isFoundInCoordList(final Coord c)
    {
        if(c == null)
        {
            return false;
        }
        for(final Coord currCoord : coordList)
        {
            if(c.x == currCoord.x && c.y == currCoord.y)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the Coord for an unoccupied cell inside (or near to) the
     * the JScrollPane's viewport, or null if none are found.
     */
    protected List<Coord> getVisibleUnoccupiedCells()
    {
        /*
         * First grab a rectangle of the visible area on the
         * hex panel
         */
        final Rectangle vis = visibleArea();

        if(vis == null)
        {
            throw new IllegalStateException(
                               "getVisibleUnoccupiedCells: no visible" +
                               " area available!");
        }

        final List<Coord> visEmpty = new ArrayList<Coord>();

        /*
         * Next examine all those hexagons on the
         * panel which intersect the visible area
         */
        for(final Hexagon h : cellList)
        {
            if(h.isEnclosedBy(vis))
            {
                 final Coord c = h.getCoord();

                 if(!cellContainsIdon(c))
                 {
                     visEmpty.add(c);
                 }
            }
        }
        return visEmpty;
    }


    /**
     * Returns the Idon which contains Point p, or null.
     */ 
    protected Idon getIdonFromPoint(final Point p)
    {
        for(final Idon i : idonMap.values())
        {
            if(i.getShape().contains(p))
            {
                return i;
            }
        }
        return null;
    }


    public List<Idon> alterIdonSize(final Collection<Idon> idons,
                                    final int change)
    {
        final List<Idon> changed = new ArrayList<Idon>();

        if(change != INCREASE && change != DECREASE)
        {
            throw new IllegalArgumentException("alterIdonSize: wrong "
                                                + "arg");
        }

        for(final Idon i : idons)
        {
            if(change == INCREASE && i.increaseSize())
            {
                changed.add(i);
            }
            else if(change == DECREASE && i.decreaseSize())
            {
                changed.add(i);
            }
        }
        repaint();
        return changed;
    }

    /**
     * Returns the coordinates of the first unoccupied cell on the grid,
     * starting at the top left and working horizontally
     */
    public Coord getUnoccupiedCell()
    {
        for(final Coord c : coordList)
        {
            if(!cellContainsIdon(c))
            {
                return c;
            }
        }
        throw new IllegalStateException("No empty cells left!");
    }

    /**
     * Return the Hexagon at the specified Coordinate.
     */
    protected Hexagon getHexagonFromCoord(final Coord c)
    {
        try
        {
            final Coord hexCoord = getCoordFromList(c.x, c.y);
            return coordMap.get(hexCoord);
        }
        catch(NullPointerException e)
        {
            System.out.println("getHexagonFromCoord returned null!");
            return null;
        }
    }

    /*
     * Creates the hex cells on the hex panel.
     *
     * Note the boolean to tell the method
     * whether or not to destroy existing map
     * data.
     */
    private void createHexagonCells()
    {
        cellList = new ArrayList<Hexagon>(rows*columns);
        coordList = new ArrayList<Coord>(rows*columns);
        coordMap = new HashMap<Coord, Hexagon>();
        idonMap = new HashMap<Coord, Idon>();
        
        int incrOdd = 1;
        int incrEven = 3;
        
        for(int col = 1; col <= columns; ++col)
        {
            if(isOdd(col))
            {
                createRow(col, hexDist/2, hexSize*incrOdd);
                incrOdd += TRIP;
            }
            else
            {
                createRow(col, hexDist, ((hexSize/2) * (col + incrEven)));
                incrEven += QUAD;
            }
        }
        repaint();
    }

    /*
     * Set and return a @Dimension containing the minimum size the panel
     * must be in order to display all of the hex cells, according to the
     * rows and columns.
     */
    public Dimension calculateMinimumSize()
    {
        double x, y = 0;
        final double div = 7.6;
        x = (hexDist * rows) + (hexDist / 2);
        y = (hexDist * columns);
        y -= (hexDist * ((double)columns / div));
        return new Dimension((int)x, (int)y);
    }

    /*
     * Create a row of hexagonal cells that will later be
     * drawn onto the HexPanel.
     *
     * Deals with odd and even rows separately
     */
    private void createRow(final int column, final double xPos, 
                                             final double yPos)
    {
        int incr = 1;

        for(int x = 1; x <= rows; ++x)
        {
            final Coord coord = new Coord(x, column);
            final Hexagon h = new Hexagon(hexSize, (xPos * incr), yPos, coord);
            coordMap.put(coord, h);
            coordList.add(coord);
            cellList.add(h);

            if(isOdd(column))
            {
                incr += 2;
            }
            else
            {
                ++incr;
            }
        }
    }

    private boolean isOdd(final int n)
    {
        if(n % 2 != 0 && n >= 0)
        {
            return true;
        }
        return false;
    }

    /*
     * Returns true if the MouseEvent @e featured
     * button number @n
     */
    private boolean isButton(final MouseEvent e, final int n)
    {
        if(e.getButton() == n)
        {
            return true;
        }
        return false;
    }

    /**
     * Returns a mapping of the Idons surrounding a particular Idon
     * and the Directions in which they occur.
     **/
    protected Map<Direction, Idon> getNeighbours(final Idon idon)
    {
        final Coord current = idon.getCoord();

        final Map<Direction, Idon> map = new HashMap<Direction, Idon>();

        for(final Direction d : Direction.values())
        {
            final Coord c = Direction.directionToCoord(current, d);
            if(containsCoord(c))
            {
                final Idon i = getIdonFromCoord(c);
                if(i != null)
                {
                    map.put(d, i);
                }
            }
        }
        return map;
    }

    /**
     * Returns the Coord object matching x, y.
     */
    protected Coord getCoordFromList(final int x, final int y)
    {
        for(final Coord c : coordList)
        {
            if(c.x == x && c.y == y)
            {
                return c;
            }
        }
        return null;
    }
    
    /**
     * Returns the Coord object matching the the contents of Coord c.
     */
    protected Coord getCoordFromList(final Coord c)
    {
        return getCoordFromList(c.x, c.y);
    }
    
    /**
     * Recursively adds all the idons linked to idon @i on the grid
     * to the Idon HashSet.
     *
     * @param i The idon to use as the start.
     *
     * @param linked The destination for linked Idons.
     */
     // OLD version
    protected void getAllLinked(final Idon i, final Set<Idon> linked)
    {
        if(linked.contains(i))
        {
            return;
        }
        linked.add(i);

        for(final Idon idon : getNeighbours(i).values())
        {
            getAllLinked(idon, linked);
        }
    }


    /*
     * Returns true if the specified Idon is within any of the collections inside
     * collection @all.
     */
    private boolean collectionsContainsIdon(final Set<Set<Idon>> all,
                                            final Idon idon)
    {
        if(all.isEmpty())
        {
            return false;
        }

        final Iterator<Set<Idon>> iter = all.iterator();

        while(iter.hasNext())
        {
            final Set<Idon> hm = iter.next();
            if(hm.contains(idon))
            {
                return true;
            }
        }

        return false;
    }

    public void printNeighbours(final Idon i)
    {
        final Map<Direction, Idon> neighbours = getNeighbours(i);
        System.out.println(i + " neighbours:");

        for(final Map.Entry<Direction, Idon> e : neighbours.entrySet())
        {
            final Direction d = e.getKey();
            final Idon idon = e.getValue();
            System.out.println("\tdirection " + d + ": " + idon);
        }
    }

    /*
     * Returns the Idon found at Coord @c, or null.
     */
    protected Idon getIdonFromCoord(final Coord c)
    {
        if(c == null)
        {
            throw new NullPointerException("getIidonFromCoord: coord"
                                            + " is null!");
        }
        final Coord coord = getCoordFromList(c);
        return idonMap.get(coord);
    }

    /*
     * Returns true if the coordMap
     * Coord contains @c.
     */
    protected boolean containsCoord(final Coord c)
    {
        for(final Map.Entry<Coord, Hexagon> e : coordMap.entrySet())
        {
            final Coord coord = e.getKey();
            if(coord.equals(c))
            {
                return true;
            }
        }
        return false;
    }

    /*
     * If MouseEvent @e is inside a hex cell, return it,
     * or else return null
     */
    private Hexagon getCellFromPoint(final Point p)
    {
        final ListIterator<Hexagon> i = cellList.listIterator();

        while(i.hasNext())
        {
            final Hexagon cell = i.next();

            if(cell.containsPoint(p))
            {
                return cell;
            }
        }
        return null;
    }


    /**
     * Returns the coord for the
     * hex cell at Point p, or null
     * if there isn't one.
     *
     * @param p The point to locate in
     * a Hexagon Cell
     *
     * @return The Coord associated with
     * the Hexagon Cell.
     */
    protected Coord getCoordFromPoint(final Point p)
    {
        final Hexagon h = getCellFromPoint(p);
        if(h == null)
        {
            return null;
        }
        for(final Map.Entry<Coord, Hexagon> e : coordMap.entrySet())
        {
            final Coord c = e.getKey();
            final Hexagon hex = e.getValue();

            if(hex.equals(h))
            {
                return c;
            }
        }
        return null;
    }

    /*
     * Paints the Idons and Hex cells if they intersect
     * the scrollpane's viewport.
     */
    public void paintComponent(final Graphics g)
    {
        super.paintComponent(g);
        final Graphics2D g2 = setUpGraphics(g);

        /*
         * Painting the grid of hex cells
         * eats up quite a bit of processing
         * power, so it has been turned off.
         *
         * Uncomment paintGrid(g2) to display it again.
         */
        //paintGrid(g2);
        paintIdons(g2);
        //paintArrows(g2);
        drawDragRect(g2);
    }

    /*public void paintArrows(final Graphics2D g)
    {
        for(final Arrow arrow : arrowList)
        {
            arrow.draw(g);
        }
    }*/
    /**
     * Painting method that allows the SVG exporter to grab the Idon
     * graphics.
     */
    protected void paintSVG(final Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(StrokeWidth));

        paintIdons(g);
    }

    /**
     * Draws the @dragRect on the hex panel
     */
    private void drawDragRect(final Graphics2D g2)
    {
        if(dragRect == null)
        {
            return;
        }
        g2.setStroke(new BasicStroke(StrokeWidth));
        g2.setPaint(new Color(0, 0, 0));
        g2.draw(dragRect);
    }

    /*
     * Returns a rectangle delimiting the visible area of
     * this HexPanel according to the JScrollPane's viewport.
     */
    protected Rectangle visibleArea()
    {
        return Controller.getHexScroller().getViewport().getViewRect();
    }

    /*
     * Paints the Hexagonal grid (not the Idons) cells using the
     * JXPanel's Graphics2D object .
     *
     * For greater efficiency, Hexagon cells are drawn only if
     * their bounding Rectangles intersect
     * the Controller's current JViewport rectangle.
     */
    private void paintGrid(final Graphics2D g2)
    {
        final Rectangle visible = visibleArea();
        for(final Coord c : coordList)
        {
            final Hexagon h = coordMap.get(c);
            if(h.intersects(visible) && !cellContainsIdon(c))
            {
                h.draw(g2);
            }
        }
    }

    /**
     * Paints the Idons that are visible, according to
     * the visibleArea() Rectangle.
     */
     /*
      * Note that Idons are sorted into separate lists
      * and drawn in different stages according to
      * their properties:
      *
      * 1) Normal, stationary, non-selected
      * 2) Selected Idons
      * 3) Idons being dragged
      *
      * This gives a 'z-order' layering appearance, so
      * (for example) stationary Idons will
      *  appear behind any being dragged.
      */
    private void paintIdons(final Graphics2D g2)
    {
        final Rectangle visible = visibleArea();

        final List<Idon> selected = new ArrayList<Idon>();
        final List<Idon> dragged = new ArrayList<Idon>();

        for(final Idon i : idonMap.values())
        {
            /*
             * Find those Idons intersecting the
             * visible area
             */
            if(i.intersects(visible))
            {
                if(i.getDragState())
                {
                    dragged.add(i);
                }
                else if(i.getSelectedStatus())
                {
                    selected.add(i);
                }
                else
                {
                    /*
                     * Draw normal (unselected, non-moving) first
                     */
                    i.draw(g2);
                }
            }
        }

        /*
         * Draw selected Idons next
         */
        paintIdonList(g2, selected);
        /*
         * Finally draw Idon[s] being dragged on top
         */
        paintIdonList(g2, dragged);
    }

    /*
     * Method used by paintIdons() to paint a list of Idons.
     */ 
    private void paintIdonList(final Graphics2D g2, 
                               final Collection<Idon> idons)
    {
        for(final Idon i : idons)
        {
            i.draw(g2);
        }
    }

    private Graphics2D setUpGraphics(final Graphics g)
    {
        final Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(StrokeWidth));
        g2.setPaint(new Color(0, 0, 0));
        return g2;
    }

    /**
     * Returns true if the cell at Coord @c
     * contains an Idon.
     */
    public boolean cellContainsIdon(final Coord c)
    {
        if(idonMap.containsKey(getCoordFromList(c)))
        {
            return true;
        }
        return false;
    }

    /**
     * Returns a collection of Coords of each visible
     * hexagon on the hex panel. Note that this includes
     * both empty and non-empty idons
     */
    protected Set<Coord> getVisibleHexagonCoords()
    {
        final Rectangle visible = visibleArea();
        final Set<Coord> visCoords = new HashSet<Coord>();

        for(final Coord c : coordList)
        {
            final Hexagon h = getHexagonFromCoord(c);

            if(h.intersects(visible))
            {
                visCoords.add(c);
            }
        }
        return visCoords;
    }

    /**
     * Conveniance method for adding
     * a new Idon which does not include
     * a Color argument. The default
     * Color is used instead.
     */
    protected Idon addIdon(final Coord c, final String s, final int size)
    {
        final Color col = Controller.getColorFromString("default");
        return this.addIdon(c, s, col, size);
    }

    /**
     * Associates a hexagon cell with an idon.
     */
    protected Idon addIdon(final Coord c, final String s, 
                           final Color col, 
                           final int size)
    {
        final Hexagon h = getHexagonFromCoord(c);
        final Idon i = new Idon(h, s, col, size);
        idonMap.put(c, i);

        /*
         * Check that the Idon is visible to the user
         */
         if(!i.isEnclosedBy(visibleArea()))
         {
             /*
              * Scroll the hexScroller so that the
              * idon is in view
              */
              scrollRectToVisible(i.getShape().getBounds());
         }
         return i;
     }

    /**
     * Returns true if the HexPanel contains an Idon
     * with the String @s.
     */
    public boolean containsIdea(final String s)
    {
        for(final Map.Entry<Coord, Idon> e : idonMap.entrySet())
        {
            if(s.equals(e.getValue().getIdea()))
            {
                return true;
            }
        }
        return false;
    }

    /*
     * Print out the idons as a list (for debugging
     * purposes)
     */
    public void printIdons()
    {
        System.out.println("    IDONS:");
        for(final Map.Entry<Coord, Idon> e : idonMap.entrySet())
        {
            final Coord c = e.getKey();
            final Idon i = e.getValue();
            System.out.println(c + "--->   " + i);
        }
    }

    /**
     * Remove a hexagon/idon association
     * from the hashmap.
     */
    protected void removeIdon(final Coord c)
    {
        /*System.out.println("removing " + idonMap.get(c)
            + " from " + c);*/
        idonMap.remove(c);
        repaint();
    }



    private int getIdonCount()
    {
        return idonMap.size();
    }

    private void printOutHashSet(final Set<?> h)
    {
        final Iterator it = h.iterator();
        while(it.hasNext())
        {
            System.out.print(it.next().toString() + ", ");
        }
        System.out.println();
    }

    /**
     * Conveniance method to find out if a Hexagon contains an Idon.
     * <p>
     * @return True if c has no Idon associated with it.
     * @param c The Coord to look up.
     */
    public boolean isEmpty(final Coord c)
    {
        if(getIdonFromCoord(c) == null)
        {
            return true;
        }
        return false;
    }

    /**
     * Deselects any selected Idons
     */
    protected void deselectIdons()
    {
        for(final Idon i : idonMap.values())
        {
            if(i.getSelectedStatus())
            {
                i.deselect();
            }
        }
        repaint();
    }

    /**
     * Returns a HashMap of the Coords of new positions for Idons if
     * the collection of Idons can fit on the HexPanel starting at a
     * specific point.
     */
    public Map<Idon, Coord> getPlacementMap(final Map<Idon, Point> dropPoints)
    {
        //System.out.println("getPlacementMap");
        final Map<Idon, Coord> dropCoords = new HashMap<Idon, Coord>(
                                                     dropPoints.size());
        for(final Map.Entry<Idon, Point> e : dropPoints.entrySet())
        {
            final Idon i = e.getKey();
            final Point p = e.getValue();

            final Coord c = getCoordFromPoint(p);

            if(c == null)
            {
                /*
                 * Outside of the HexPanel cells
                 */
                return null;
            }

            final Idon idon = getIdonFromCoord(c);

            /*
             * If there is no Idon at Coord c, or the
             * Idon at Coord c is in the collection
             * being dragged, the Coord is usable
             * for dropping.
             */
            if(idon == null || dropPoints.containsKey(idon))
            {
                dropCoords.put(i, c);
            }
            else
            {
                return null;
            }
        }

        return dropCoords;
    }

    /**
     * Returns the Coord found by following a path from a specified
     * Coord on the HexPanel, or otherwise null.
     */
    public Coord getCoordFromPath(final Coord start, final List<Direction> path)
    {
        if(path.isEmpty())
        {
            return start;
        }
        Coord c = start;

        for(final Direction d : path)
        {
            c = d.directionToCoord(c, d);

            if(c == null)
            {
                return null;
            }
        }
        return c;
    }

    /**
     * Scrollable implementation method.
     */
    public Dimension getPreferredScrollableViewportSize()
    {
        return getPreferredSize();
    }

    /**
     * Scrollable implementation method.
     */
    public int getScrollableBlockIncrement(final Rectangle visibleRect,
                                    final int orientation, final int direction)
    {
        return SCROLL_UNIT_INCR * BLOCK_MULT;
    }
    public boolean getScrollableTracksViewportHeight()
    {
        return false;
    }
    public boolean getScrollableTracksViewportWidth()
    {
        return false;
    }
    public int getScrollableUnitIncrement(final Rectangle visibleRect,
                                          final int orientation, 
                                          final int direction)
    {
        return SCROLL_UNIT_INCR;
    }

   /**
    * Search for Idons with the given String.
    *
    * @param query The idea to find in the Idons
    *
    * @param start The position to start in the Idon list.
    *
    * @return The position in the sorted Idon list that matches the
    * query, or a negative int on fail.
    */
    public Coord searchForIdon(final String query, final Coord lastFound, 
                               final boolean rev)
    {
        /*
         * Create a sorted list of Coordinates that contain Idons
         */
        final TreeSet<Coord> sortedCoords = new TreeSet<Coord>(idonMap.keySet());
        final Iterator<Coord> it;
        
        if(rev)
        {
            it = sortedCoords.descendingIterator();
            
            while(it.hasNext())
            {
                final Coord c = it.next();
                if(idonMatchesPattern(query, c, lastFound))
                {
                    return c;
                }
            }
        }
        else
        {
            it = sortedCoords.iterator();
            
            while(it.hasNext())
            {
                final Coord c = it.next();
                if(idonMatchesPattern(query, c, lastFound))
                {
                    return c;
                }
            }
        }
        
        return null;
    }
    
    
    
    /*
     * Utility method used by both forward and backward Idon searches.
     */
    private boolean idonMatchesPattern(final String str, final Coord c, 
                                       final Coord lastFound)
    {
        final Idon idon = getIdonFromCoord(c);
        final String idea = idon.getIdea();
        final Locale locale = Controller.LOCALE;
        
        if(idea != null)
        {
            System.out.println("comparing query " + str.toLowerCase(locale)
                                + " with Idon: " + idon);
            if(idea.toLowerCase(locale).startsWith(str.toLowerCase(locale)))
            {
                if(lastFound == null)
                {
                    System.out.println("\t--> match");
                    return true;
                }
                else
                {
                    if(!c.equals(lastFound))
                    {
                        System.out.println("\t--> match");

                        return true;
                    }
                }
            }
        }
        return false;
    }
}
