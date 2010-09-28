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
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import net.miginfocom.swing.MigLayout;
import javax.swing.text.*;
import org.jdesktop.swingx.*;

/** 
 * GUI component used to capture the user's  The pop up
 * menu it contains provides suggestions based on the
 * ideas and send them to the HexPanel as Idons. 
 */
public class KeyboardInputPanel implements ActionListener, KeyListener                                                    
{ 
    /*
     * An editable combo box for the 
     * user to enter ideas. 
     */ 
    private JComboBox combo;
    private JXPanel panel;
    private JButton addButton;
    private DefaultComboBoxModel mod;
    private String prevString;
    
    private final JTextField textField;

    private final int EMPTY = -1;
    private Dimension d = new Dimension(400, 25);
    
    public static final int TEXT_FIELD_LENGTH = 100;
    
    private boolean hasSuggestions = false;
    
    /*
     * Custom renderer for combo's cells.
     * 
     * This allows keeping track of
     * which of the strings in the
     * pop up menu is highlighted
     * by the user. 
     */ 
    SuggestionRenderer renderer;
    
    /*
     * Alphabetically sorted Set of 
     * Strings used as suggestions  for 
     * the user to input to the 
     * HexPanel. 
     * 
     * The suggestions are sent from the 
     * Controller module.
     */ 
    private Set<String> suggestions = new HashSet<String>();
    
    /*
     * Suggestions that will actually be 
     * shown in the combo box list are
     * stored here.
     */ 
    //private HashSet<String> visibleSuggestions = new HashSet<String>();
    private Set<String> visibleSuggestions = new TreeSet<String>();
    
    public KeyboardInputPanel()
    {
        panel = new JXPanel(new MigLayout());
        combo = new JComboBox();
        combo.setEditable(true);
        combo.setEnabled(true);
        combo.setPreferredSize(d);
        combo.setMinimumSize(d);
        combo.setLightWeightPopupEnabled(true);
        
        mod = new DefaultComboBoxModel();
        combo.setModel(mod);
        combo.putClientProperty("JComboBox.isTableCellEditor", true);
        
        renderer = new SuggestionRenderer(combo);
        
        textField = (JTextField)combo.getEditor().getEditorComponent();
        textField.addActionListener(this);
        textField.addKeyListener(this);
        textField.setFocusTraversalKeysEnabled(false);
        setUpAddButton();
        addButton.setFocusable(false);
        panel.add(addButton);
        panel.add(combo);
        
        /*
         * Setup callbacks to close
         * the menus on mouse click
         */ 
        Controller.setMenuPopUpMouseListener(panel);
        Controller.setMenuPopUpMouseListener(combo);
        Controller.setMenuPopUpMouseListener(addButton);
        Controller.setMenuPopUpMouseListener(textField);
        return;
    }
   
    
    /*
     * Sets up the 'OK' button on the input panel.
     * This is used as an alternative to the 'enter'
     * button, and so also acts as a method to send
     * the contents to the Controller as a new
     * Idon.
     */ 
    private void setUpAddButton()
    {
        addButton = new JButton("OK");
        addButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                performNewIdonCreationAction();
                
            }
        });
    }
    
    
    
    /**
     * KeyListener method overrides.
     * 
     * This is public due to the implementation
     * specifics. 
     */ 
    public void	keyReleased(KeyEvent e)
    {
        dealWithKeyReleased(e);
    } 
    
    /*
     * Processes KeyReleased events fired
     * by the KeyListener on the JComboBox's
     * text field.
     */ 
    private void dealWithKeyReleased(KeyEvent e)
    {
        String current = getCurrentTextFieldString();
        
        if(e.getKeyCode() == KeyEvent.VK_UP)
        {
            //System.out.println("up arrow");
            return;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            //System.out.println("down arrow");
            return;
        }
        
        if(current.length() == 0)
        {
            /*
             * Close the pop up menu while
             * a new set of suggestions
             * is created.
             */ 
            //combo.setPopupVisible(false);         
            mod.removeAllElements();
            updateVisibleSuggestions(current);
            prevString = current;
            return;
        }
        if(prevString != null)
        {
            if(!current.equals(prevString))
            {
                prevString = current;
                updateVisibleSuggestions(current);
            }              
        }
        else
        {
            prevString = current;
        }
    } 
    
    /*
     * 
     */ 
    public void	keyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_TAB)
        {
            tabToHexPanel();    
            return;
        } 
    } 
    
    private void tabToHexPanel()
    {
        Controller.getHexPanel().requestFocus();      
    }
    
    public void	keyTyped(KeyEvent e){} 
    
   
    /**
     * Action Listener for the JComboBox's JTextField
     * 
     * Do not call or override - only public due to
     * implementation requirement.
     */ 
    /* 
     * (Putting an action listener on
     * a JComboBox itself makes it impossible to tell
     * what action is actually taking place.)
     */ 
    public void actionPerformed(ActionEvent ae)
    {        
        performNewIdonCreationAction();
    }
    
    
    /**
     * Deals with mouse released events from the custom
     * cell renderer's mouse listener.
     * 
     * This should not be called apart from by 
     * the custom cell renderer.
     */ 
    protected void mouseReleasedOnSuggestion(MouseEvent e)
    {
        if(SwingUtilities.isLeftMouseButton(e))
        {
            JList list = renderer.getList();
            Point p = e.getPoint();
            int index = list.locationToIndex(p);
            if(index != EMPTY)
            {
                /*
                 * Check that cell was clicked
                 */ 
                Rectangle rect = list.getCellBounds(index, index);
                if(rect.contains(p))
                {
                    Object sugg = mod.getElementAt(index);
                    performNewIdonCreationAction();
                }
            }
        }
    }
    
    /*
     * Utility Method containing the actual
     * implementation of the above actionPerformed()
     * method. 
     */ 
    private void performNewIdonCreationAction()
    {   
        if(combo.isPopupVisible())
        {         
            /*
             * Check to see if there is a focused
             * object in the popup menu 
             */ 
            int in = renderer.getSelectedIndex();
            if(in >= 0)
            {
                Object o = mod.getElementAt(in);
                
                /*
                 * If so, use this text instead of the
                 * contents of the text field
                 */ 
                //System.out.println("selected element " + in);
                Controller.createIdonFromInput(o.toString().trim()); 
                clearComboList();                  
           }
        }
        else
        {
            String input = textField.getText();     
            clearComboList();
            if(input == null)
            {
                return;
            }
            if(input.length() == 0)
            {
                return;
            }
            Controller.createIdonFromInput(input.trim());
        }
        updateVisibleSuggestions(textField.getText());
    }
    
    /**
     * Returns the JXPanel so it can be added to the main frame.
     */  
    protected JXPanel getPanel()
    {
        return panel;
    }
    
    /*
     * Updates the set of suggestion strings
     * that will appear below the text area of the
     * combo box when the user types the text field. 
     */ 
    private void updateVisibleSuggestions(String s)
    {
        /*
         * Only update the suggestions if we
         * have received some from the Google
         * Sets via the Controller
         */ 
        if(hasSuggestions)
        {   
            if(suggestions.size() > 0)
            {
                /*if(s.length() == 0)
                {
                    return;
                }*/
                createVisibleSuggestions(s);
                addSuggestionsToComboBox(s);
            }
        }   
    }
    
    
    
    /*
     * Adds the set of visible suggestions to the
     * combo box. 
     */
    private void addSuggestionsToComboBox(String str)
    {
        /*
         * Close the pop up menu prior to changing 
         * its list contents
         */ 
        combo.setPopupVisible(false); 
        ObjectString user = new ObjectString(str);
        mod.removeAllElements();
        
        mod.insertElementAt(user, 0);
        mod.setSelectedItem(user);
        
        for(String s : visibleSuggestions)
        {
            if(!comboContainsString(s))
            {
                /*
                 * Check if the suggestion
                 * is the same as the user
                 * input before adding
                 */ 
                if(!s.equals(user.toString().toLowerCase()))
                {
                    mod.addElement(new ObjectString(s));
                }
            }
        }
        
        /*
         * The first item in the list (position 0) 
         * will be the contents of the text field,
         * if it is non-empty.
         *
         * 
         * If there is only this item, there is no
         * point in showing the contents of the list 
         * again in the popup menu, so we can leave
         * it down
         */ 
        if(mod.getSize() > 1 && textField.getText().length() > 0)
        {
            combo.setPopupVisible(true);        
        }
        return;
    }
    
    /*
     * Debug method showing the contents of
     * the JComboBox's menu contents.
     */ 
    private void printOutComboContents()
    {
        System.out.println("Combobox contains:");
        for(int i = 0; i < mod.getSize(); i++)
        {
            System.out.println(i + ": " + mod.getElementAt(i).toString());
        }
    }
    
    private boolean comboContainsString(String s)
    {
        for(int i = 0; i < mod.getSize(); i++)
        {
            Object o = mod.getElementAt(i);
            if(o.toString().toLowerCase().equals(s.toLowerCase()))
            {
                return true;
            }
        }
        return false;
        
    }
    
    /*
     * Creates a list of visible suggestions
     * based on input with which to
     * populate the combo box.
     */ 
    private void createVisibleSuggestions(String input)
    {
        /*
         * Clear the list of visible Strings
         * and Start afresh
         */ 
        visibleSuggestions.clear();
        assert(visibleSuggestions.size() == 0);
        
        if(input == null || input.length() == 0)
        {
            //System.out.println("zero length input: show all suggestions");
            for(String s : suggestions)
            {
                visibleSuggestions.add(s);
            }
            return;
        }
        
        for(String s : suggestions)
        {
            if(s.toLowerCase().startsWith(input.toLowerCase()))
            {
                visibleSuggestions.add(s);
            }   
        }
    }
    
    /*
     * Returns the current contents of the jcombo box.
     * Used after a user has altered/added/removed text
     */
    private String getCurrentTextFieldString()
    {
        return textField.getText();
    }
    
    /**
     * Used by the Controller to set the 
     * suggestions set, which will be used
     * to create the drop-down list from
     * the 
     * whilst the user is typing, if a
     * near-match is found.
     * 
     * @param suggs The collection of Strings
     * to add to the suggestions TreeSet.
     */ 
    protected void addSuggestions(Collection<String> suggs)
    {
        suggestions.addAll(suggs);   
        hasSuggestions = true;    
    }
    
    protected void clearSuggestions()
    {
        //System.out.println("clearing suggestions from input panel");
        suggestions.clear();   
        hasSuggestions = false;    
    }

    protected void clearComboList()
    {
        combo.setPopupVisible(false);
        mod.removeAllElements();
        textField.setText(null);
        //mod.setSelectedItem(null);
    }
}
