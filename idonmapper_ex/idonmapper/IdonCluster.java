package idonmapper;

import idonmapper.Event.*;
import idonmapper.Coord.*;
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
 * Created when the user shift-clicks on an Idon.
 * 
 * Represents a group of linked Idons on the HexPanel. That is,
 * any Idons connected to the one being clicked on, and any connected
 * to any of them, and so forth. 
 */ 
public class IdonCluster 
{
    /*
     * Reference to the main reference Idon in 
     * the cluster from which others are found.
     */ 
    //private Idon clicked;
    
    /*
     * Set of all the Idons found, including the
     * above clicked Idon.
     */ 
    private HashSet<Idon> cluster;
    
    public IdonCluster(Idon clicked)
    {
        //this.clicked = clicked;
        cluster = new HashSet<Idon>();
        
        /*
         * Find all the linked Idons.
         */ 
        //Controller.getHexPanel().getAllLinked(clicked, cluster);
    }
    
    protected void toggleSelectStatus()
    {
        for(Idon i : cluster)
        {
            i.toggleSelectStatus();
        }
    }
    
    public HashSet<Idon> getIdons()
    {
        return cluster;
    }
    
    public int getSize()
    {
        return cluster.size();
    }
    protected void selectAll()
    {
        for(Idon i : cluster)
        {
            i.select();
        }
    }
   
    
}
