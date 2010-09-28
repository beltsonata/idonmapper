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
