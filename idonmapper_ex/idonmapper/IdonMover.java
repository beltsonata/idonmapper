package idonmapper;

import idonmapper.Event.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;

/**
 * Invoked when the user starts to drag selected Idons on 
 * the HexPanel. 
 */ 
public class IdonMover
{
    private Idon dragged;
    private HashSet<Idon> allSelected;
    private HashMap<Idon, Double> xDists, yDists;
    private HashMap<Idon, Coord> initialPositions;

    
    public IdonMover(Idon dragged, Collection<Idon> allSelected)
    {
        this.dragged = dragged;
        this.allSelected = new HashSet<Idon>(allSelected);
        setDragState(true);
        calculateDistanceFromDragged();
        createInitialPositionMap();
    }
    
    /*
     * Creates a map of the Idons being moved and their current
     * Coordinates on the HexPanel for the purpose of moving Idons and 
     * creating related undo information in the drop() method.
     */ 
    private void createInitialPositionMap()
    {
        initialPositions = new HashMap<Idon, Coord>();
        
        for(Idon i : allSelected)
        {
            initialPositions.put(i, i.getCoord());
        }
    }
    
    /*
     * Sets the internal dragState boolean in 
     * all of the Idons being moved.
     */ 
    private void setDragState(boolean b)
    {
        for(Idon i : allSelected)
        {
            i.setDragState(b);
        }
    }
    
    /**
     * Returns the Idon being dragged by the mouse.
     */ 
    public Idon getDraggedIdon()
    {
        return dragged;
    }
    
    /*
     * Calculates the distance from the centre of the Idon being 
     * dragged to the centre of every other Idon. The x and y coordinate
     * are stored in a separate hash map each. 
     */ 
    private void calculateDistanceFromDragged()
    {
        xDists = new HashMap<Idon, Double>(); 
        yDists = new HashMap<Idon, Double>();

        for(Idon i : allSelected)
        {
            if(!i.equals(dragged))
            {
                //double x = dragged.getOriginalXPos() - i.getOriginalXPos();
                //double y = dragged.getOriginalYPos() - i.getOriginalYPos();
                double x = dragged.getXPos() - i.getXPos();
                double y = dragged.getYPos() - i.getYPos();
                xDists.put(i, x);
                yDists.put(i, y);
            }
        }
    }
    
    
    private void resetIdons()
    {
        for(final Idon i : allSelected)
        {
            i.resetPosition();
        }
        setDragState(false);
        Controller.getHexPanel().scrollRectToVisible(dragged.getShape()
                                                          .getBounds());
    }
    
    
    private boolean areasOverlap(final Area a, final Area b)
    {
        a.intersect(b);
        
        if(a.isEmpty())
        {
            return false;
        }
        return true;
    }
    
    /**
     * Drops the Idons starting with the dragged Idon at the cell 
     * enclosing Point p, or as near to that cell as possible, should it
     * be occupied.
     */ 
    protected void drop(Point p)
    {
        System.out.println("drop...");
        HexPanel hp = Controller.getHexPanel();
        // first make sure all the positions are up to date
        updateDraggedIdons(p);
        
        // keep a list of bad (occupied) coords to try and drop the
        // draggedIdon 
        HashSet<Coord> exclude = new HashSet<Coord>();
        
        // loop until a suitable location is found
        while(!currentDropLocationOK())
        {
            // add the current coordinate to our list of places tried
            Coord badCoord = hp.getCoordFromPoint(p);
            
            if(badCoord == null)
            {
                throw new NullPointerException("Drop Point was outside of "
                           + "all cells");
            }
            
            exclude.add(badCoord);
            Coord next = hp.getNearestEmptyCellToCoord(badCoord, exclude);
            Point p2 = hp.getCellPointFromCoord(next);
            
            // update positions ready for the next iteration
            updateDraggedIdons(p2);
        }
        dropIdonsHere();
        
    }
    
    private void dropIdonsHere()
    {
        System.out.println("drop idons here...");
        HexPanel hp = Controller.getHexPanel();
        
        HashMap<Idon, Coord> dropMap = new HashMap<Idon, Coord>();
        
        
        for(Idon i : allSelected)
        {
            Point p = new Point((int)i.getXPos(), (int)i.getYPos());
            
            Coord c = hp.getCoordFromPoint(p);
            dropMap.put(i, c);            
        }
        
        this.setDragState(false);
        Controller.moveMultipleIdons(initialPositions, dropMap);
        Controller.pushToUndoStack(new IdonsMovedEvent(
                    initialPositions, dropMap));
    }
    
    /*
     * Looks at the current drop location and compares the Area of the
     * Idon's Shape against all Idons that are not being moved.  
     */ 
    private boolean currentDropLocationOK()
    {
        for(Idon i : allSelected)
        {
            for(Idon idon : Controller.getHexPanel().getIdons())
            {
                if(!allSelected.contains(idon))
                {
                    // only look at Idons we aren't moving
                    Area moving = new Area(i.getShape());
                    Area still = new Area(idon.getShape());
                    
                    if(areasOverlap(moving, still))
                    {
                        //System.out.println("overlapping idons: " + i + 
                          //          ", and " + idon);
                        return false;
                    }              
                }
            }
        }
        return true;
    }
    
    /**
     * Drops the Idons starting with the dragged Idon at the cell 
     * enclosing Point p, or as near to that cell as possible, should it
     * be occupied.
     */ 
    /*protected void drop(Point p)
    {                     
        System.out.println("drop()");  
        // We'll need a mapping of where each Idon is to the dragged Idon.
        
        // These will be used to check the dropping position of each
        // point .
        HashMap<Idon, Point> relPoints;
        
        // We'll put every Coord we try that is occupied in here,
        // so we don't re-try any
        HashSet<Coord> dropCoordsTried = new HashSet<Coord>();
        
        // ...and here's where the new (potential) positions will be:
        HashMap<Idon, Coord> dropMap = null;
        
        Coord dropCoord = Controller.getHexPanel().getCoordFromPoint(p);
        
        if(dropCoord == null)
        {
            System.out.println("Dropped outside all cells. ");
            // This needs improvement...
            resetIdons();
            return;
        }  
        if(dropCoord.equals(dragged.getCoord()))
        {
            System.out.println("returning Idons to original positions");
            resetIdons();
            return;
        }
        
        // Find somewhere close to the dropCoord to create a dropMap to
        // drop the Idons, preferably starting at the dropCoord itself...
        // Keep looping until somewhere is found, or we run out of places
        
        while(dropMap == null)
        { 
            // Create relative mapping of points for the Idons (where 
            // each point is the centre of where each Idon will be placed) 
            relPoints = createRelativeDropPoints(p);
                        
            // See if the relative points are ok to drop the Idons
            dropMap = Controller.getHexPanel().getPlacementMap(relPoints,
                                                         dropCoordsTried);
                                                            
            // Reaching here means getPlacementMap failed to find empty
            // positions at this location, so find another starting point.
            // First, add the previous, failed  coord to the exclusion map 
            dropCoordsTried.add(dropCoord);
            
            
            dropCoord = Controller.getHexPanel()
                         .getNearestEmptyCellToCoord(dropCoord, 
                                    dragged.getSize(), dropCoordsTried);                      
                      
            Hexagon h = Controller.getHexPanel().
                                    getHexagonFromCoord(dropCoord);
            
            p = new Point((int)h.getXPos(), (int)h.getYPos());
        }
        
        this.setDragState(false);
        Controller.moveMultipleIdons(initialPositions, dropMap);
        Controller.pushToUndoStack(new IdonsMovedEvent(
                    initialPositions, dropMap));
    
    }*/
    
    /*protected void drop(Point p)
    {
        HashMap<Idon, Point> relPoints;
        
        // create relative drop points for the Idons being moved.
        // Point p refers to the centre of the dragged Idon
        
        // So, we need drop points for the other Idons being dragged,
        // if any
        relPoints = createRelativeDropPoints(p);
        
        // mashed some of getPlacementMap into here, to try and sort out
        // the dropping of Idons onto occupied/overalapping cells
        // ...not working... yet...
        
        HashMap<Idon, Coord> dropCoords = new HashMap<Idon, Coord>(
                                                     relPoints.size());    
        for(Map.Entry<Idon, Point> e : relPoints.entrySet())
        {
            Idon i = e.getKey();
            Point point = e.getValue();
        
            Coord c = Controller.getHexPanel().getCoordFromPoint(point);   
        
            if(c == null)
            {
                System.out.println("drop failed");
                return;
            }
            // ignoring if there's an Idon there already.
            dropCoords.put(i, c);
        }
        Controller.moveMultipleIdons(initialPositions, dropCoords);
        Controller.pushToUndoStack(new IdonsMovedEvent(
                    initialPositions, dropCoords));
    }*/
    
    
    /*
     * Used when the dropCoord is null (mouse point was
     *  outside of all cells).
     *
     * This method finds the nearest Hexagon cell from
     * a collection of Coords and returns that Coord.
     * 
     * Note that the cells represented by the Collection of Coords
     * are not checked for the presence of Idons.  
     *
    private Coord getNearestCoord(Collection<Coord> coords,
                                         Point p)
    {
        double distanceToP = 0, nearestDistance = 0;
        Coord nearestCoord = null;
        Hexagon nearestHex = null;
        
        for(Coord c : coords)
        {
            /*
             * Get a the hexagon from the coord, then its position
             * as a Point
             *
            Hexagon hex = Controller.getHexPanel().getHexagonFromCoord(c);   
            Point hexPoint = new Point((int)hex.getXPos(), 
                                       (int)hex.getYPos());
            /*
             * Get distance between hex and p
             *  
            double distanceFromP = p.distance(hexPoint);
            
            if(nearestCoord == null)
            {
                /*
                 * First point checked, so naturally we have nothing to
                 * compare it to
                 *
                
                nearestCoord = c;
                nearestDistance = distanceFromP;
                nearestHex = hex;
            }
            else
            {
                /*
                 * Check the current distanceFromP to see if its
                 * nearer that our current nearest. If so, replace 
                 * the nearest with .
                 * 
                if(distanceFromP < nearestDistance)
                {
                    nearestCoord = c;
                    nearestDistance = distanceFromP;
                    nearestHex = hex;
                    System.out.println("nearestCoord " + c);
                }
            }
        }
        System.out.print("p was " + p + ", now");
        p = new Point((int)nearestHex.getXPos(), (int)nearestHex.getYPos());
        System.out.println(p);
        
        return nearestCoord;
    }
    */
/*   private HashMap<Idon, Position> createRelativeDropPositions(Point p)
    {
        HashMap<Idon, Position> relPos = new HashMap<Idon, Position>();
        Position dragPos = new Position(p);
        
        relPos.put(dragged, dragPos);
        
        for(Idon i : allSelected)
        {
            if(!i.equals(dragged))
            {
                relPos.put(i, new Position(dragPos.x - xDists.get(i),
                                           dragPos.y - yDists.get(i)));
            }
        }
        return relPos;
    }*/
    
    /*
     * Returns a mapping of where the centre of each Idon would be placed
     * relative to Point p, where p is the centre of the dragged Idon. 
     */ 
    private HashMap<Idon, Point> createRelativeDropPoints(Point p)
    {
        HashMap<Idon, Point> points = new HashMap<Idon, Point>();
        Point dragPoint = new Point((int)p.getX(), (int)p.getY());
        
        points.put(dragged, dragPoint);
        
        for(Idon i : allSelected)
        {
            if(!i.equals(dragged))
            {
                points.put(i, new Point((int)(p.getX() - xDists.get(i)),
                                        (int)(p.getY() - yDists.get(i))));
            }
        }
        return points;
    }
    
    /**
     * Updates the Idons relative to a Point, where the Point is the  
     * center of the dragged Idon in its new position.
     */ 
    protected void updateDraggedIdons(Point p)
    {
        dragged.setPosition(p.getX(), p.getY());
        
        if(allSelected.size() > 1)
        {
            for(Idon i : allSelected)
            {
                if(!i.equals(dragged))
                {
                    double newX, newY;   
                    newX = p.getX() - xDists.get(i);
                    newY = p.getY() - yDists.get(i);
                    i.setPosition(newX, newY);
                }
            }
        }
        Controller.getHexPanel().repaint();
    }
}
