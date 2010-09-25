package idonmapper;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 * Conveniance class used to execute the idonmapper
 * application. 
 * 
 * Usage - 
 * 
 * idonmapper.Start
 *  
 */
public class Start
{ 
    /**
     * Starts the application.
     */ 
    public static void main(final String[] args)
    {
        try
        {
            /*
             * Set native look
             */ 
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch(final Exception e)
        {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                Controller.createComponents();               
            }
        });
    }
}
