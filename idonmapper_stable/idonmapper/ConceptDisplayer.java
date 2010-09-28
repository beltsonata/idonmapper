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
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import org.jdesktop.swingx.*;
import net.miginfocom.swing.MigLayout;

/**
 * Displays a dialog window containing a list of the concepts on the
 * HexPanel which the user can select individually to move them into
 * the visible area. 
 */ 
public class ConceptDisplayer
{
    private final JDialog dialog;
    private static JList list;
    private static boolean MODAL = true;
    private JScrollPane scroller;
    private HexPanel hexPanel;
    private static Set<Idon> idons;
    private static Map<ObjectString, Idon> ideaIdonMap;
    private static int CLICKS = 2;
    private static final Dimension dim = new Dimension(200, 100);
    private static final String PROTO = "-----------------------------------------";
    private final Locale locale = Controller.LOCALE;
    
    /**
     * Creates the ConceptDisplayer and associates it with the main
     * window and HexPanel.
     */ 
    public ConceptDisplayer(final JFrame f, final HexPanel hp)
    {      
        dialog = new JDialog(f, "Concepts", MODAL);
        dialog.getContentPane().setLayout(new MigLayout());
        dialog.setLocationRelativeTo(f);
        hexPanel = hp;
        scroller = new JScrollPane();
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    /**
     * Displays the ConceptDisplayer to the user.
     */ 
    public void displayConcepts()
    {
        getIdeas();
        updateList();
        dialog.add(scroller);
        scroller.setMinimumSize(dim);
        addEscapeListener();
        addMouseListener();
        dialog.pack();
        dialog.setVisible(true);
    }
    
    /*
     * Allows the user to hide the displayer with the Escape key.
     */ 
    private void addEscapeListener()
    {
        final ActionListener escaper = new ActionListener() 
        {
            public void actionPerformed(final ActionEvent e)
            {
                dialog.setVisible(false);
            }
        };

        dialog.getRootPane().registerKeyboardAction(escaper,
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                                     JComponent.WHEN_IN_FOCUSED_WINDOW);
      
      
      //below is old code; it doesn't work:
            /*KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        
            Action act = new AbstractAction()
                {         
                    public void actionPerformed(ActionEvent actionEvent) 
                    { 
                        dialog.setVisible(false);
                    } 
                };
            
            JRootPane root = dialog.getRootPane();
            InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(stroke, "ESCAPE");
            root.getActionMap().put("ESCAPE", act);

            */
    }
       
    /*
     * Listens for mouse clicks on the JList. If a user double clicks
     * on an Item, the HexPanel view is moved to make it visible 
     * (if it is not already)
     */ 
    private void addMouseListener()
    {
        final MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) 
            {
                if(e.getClickCount() == CLICKS) 
                {
                    int index = list.locationToIndex(e.getPoint());
                    if(index < 0)
                    {
                        return;
                    }
                    Object o = list.getModel().getElementAt(index);
                   
                    Idon i = ideaIdonMap.get(o);
                    hexPanel.scrollRectToVisible(i.getShape().getBounds());
                }                
            };
        };
        list.addMouseListener(mouseListener);
    }
    
    /*
     * Updates the JList with the current concepts on the HexPanel.
     */ 
    private void updateList()
    {
        list = new JList(ideaIdonMap.keySet().toArray());
        list.setPrototypeCellValue(PROTO);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scroller.setViewportView(list);
    }
    
    /*
     * Extracts the concepts from the Idons on the HexPanel and
     * sorts them by placing them in a TreeSet
     */ 
    private void getIdeas()
    {
        idons = hexPanel.getIdons();
        ideaIdonMap = new HashMap<ObjectString, Idon>();
        
        for(Idon i : idons)
        {
            String s = i.getIdea();
            s = s.toLowerCase(locale);
            ideaIdonMap.put(new ObjectString(s), i);
        }
    }
}
