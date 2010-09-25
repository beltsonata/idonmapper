package idonmapper;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/*
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
    public static void main(String[] args)
    {
        try
        {
            /*
             * Set native look
             */ 
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
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
