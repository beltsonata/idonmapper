package idonmapper;

import idonmapper.Event.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.Point2D;

/**
 * Invoked when the user starts to drag selected Idons on 
 * the HexPanel. 
 */ 
public class IdonMover
{
    private final Idon dragged;
    private final Set<Idon> allSelected;
    private Map<Idon, Double> xDists, yDists;
    private Map<Idon, Coord> initialPositions;
    
    public IdonMover(final Idon dragged, final Collection<Idon> allSelected)
    {
        this.dragged = dragged;
        this.allSelected = new HashSet<Idon>(allSelected);
        setDragState(true);
        createInitialPositionMap();
        calculateDistanceFromDragged();
    }
    
    private void createInitialPositionMap()
    {
        initialPositions = new HashMap<Idon, Coord>();
        
        for(final Idon i : allSelected)
        {
            initialPositions.put(i, i.getCoord());
        }
    }
    
    private void setDragState(final boolean b)
    {
        for(final Idon i : allSelected)
        {
            i.setDragState(b);
        }
    }
    
    public Idon getDragged()
    {
        return dragged;
    }
    
    private void calculateDistanceFromDragged()
    {
        xDists = new HashMap<Idon, Double>(); 
        yDists = new HashMap<Idon, Double>();

        for(final Idon i : allSelected)
        {
            if(!i.equals(dragged))
            {
                final double x = dragged.getOriginalXPos() - i.getOriginalXPos();
                final double y = dragged.getOriginalYPos() - i.getOriginalYPos();
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
    /**
     * Drops the Idons starting at the cell
     * starting at Point p, or as near to
     * that cell as possible, should it
     * be occupied.
     */ 
    protected void drop(Point p)
    {           
        Map<Idon, Point> relPoints;
        final Set<Coord> dropCoordsTried = new HashSet<Coord>();
        Map<Idon, Coord> dropMap = null;
        
        Coord dropCoord = Controller.getHexPanel().getCoordFromPoint(p);
        
        if(dropCoord == null)
        {
            resetIdons();
            return;
        }  
        if(dropCoord.equals(dragged.getCoord()))
        {
            //System.out.println("returning Idons to original positions");
            resetIdons();
            return;
        }
        else 
        {
            if(dropCoord == null)
            {
                throw new NullPointerException();
            }
            
            /*
             * Find somewhere close to the dropCoord to put the
             * dragged Idons
             */ 
            while(dropMap == null)
            { 
                /*
                 * Create relative points from current location
                 */ 
                relPoints = createRelativeDropPoints(p);
                
                /*
                 * Try and match the points to unoccupied places on
                 * the map
                 */ 
                dropMap = Controller.getHexPanel()
                                          .getPlacementMap(relPoints);
                
                                      
                dropCoordsTried.add(dropCoord);
                dropCoord = Controller.getHexPanel()
                                .getNearestEmptyCellToCoord(dropCoord, 
                                                        dropCoordsTried);                        
                final Hexagon h = Controller.getHexPanel().
                                        getHexagonFromCoord(dropCoord);
                
                p = new Point((int)h.getXPos(), (int)h.getYPos());
            }
            
            setDragState(false);
            Controller.moveMultipleIdons(initialPositions, dropMap);
            Controller.pushToUndoStack(
                new IdonsMovedEvent(initialPositions, dropMap));
        }
    }
    
    /*
     * Used when the dropCoord is null (mouse point was
     *  outside of all cells).
     *
     * This method finds the nearest Hexagon cell from
     * a collection of Coords and returns that Coord.
     * 
     * Note that the cells represented by the Collection of Coords
     * are not checked for the presence of Idons.  
     */ 
    private Coord getNearestCoord(final Collection<Coord> coords, Point p)
    {
        double nearestDistance = 0;
        Coord nearestCoord = null;
        Hexagon nearestHex = null;
        
        for(final Coord c : coords)
        {
            /*
             * Get a the hexagon from the coord, then its position
             * as a Point
             */ 
            final Hexagon hex = Controller.getHexPanel().getHexagonFromCoord(c);   
            final Point hexPoint = new Point((int)hex.getXPos(), 
                                       (int)hex.getYPos());
            /*
             * Get distance between hex and p
             */  
            final double distanceFromP = p.distance(hexPoint);
            
            if(nearestCoord == null)
            {
                /*
                 * First point checked, so naturally we have nothing to
                 * compare it to
                 */ 
                
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
                 */ 
                if(distanceFromP < nearestDistance)
                {
                    nearestCoord = c;
                    nearestDistance = distanceFromP;
                    nearestHex = hex;
                    //System.out.println("nearestCoord " + c);
                }
            }
        }
        //System.out.print("p was " + p + ", now");
        p = new Point((int)nearestHex.getXPos(), (int)nearestHex.getYPos());
        //System.out.println(p);
        
        return nearestCoord;
    }
    
    private Map<Idon, Point> createRelativeDropPoints(final Point p)
    {
        final Map<Idon, Point> points = new HashMap<Idon, Point>();
        final Point dragPoint = new Point((int)p.getX(), (int)p.getY());
        
        points.put(dragged, dragPoint);
        
        for(final Idon i : allSelected)
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
     * Updates the Idons relative to a Point,
     * where the Point is used as the new center
     * of the dragged Idon, and other selected Idons
     * are calculted accordinly.
     */ 
    protected void updateDraggedIdons(final Point p)
    {
        dragged.setPosition(p.getX(), p.getY());
        
        if(allSelected.size() > 1)
        {
            for(final Idon i : allSelected)
            {
                if(!i.equals(dragged))
                {
                    i.setPosition(p.getX() - xDists.get(i), 
                                  p.getY() - yDists.get(i));
                }
            }
        }
        Controller.getHexPanel().repaint();
    }
}
