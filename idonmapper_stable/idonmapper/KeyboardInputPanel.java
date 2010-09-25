package idonmapper;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import net.miginfocom.swing.MigLayout;
import javax.swing.text.*;


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
    private final JComboBox combo;
    private final JPanel panel;
    private final JButton addButton;
    private final DefaultComboBoxModel mod;
    private String prevString;
    private final Locale locale = Controller.LOCALE;
    
    private final JTextField textField;

    private static final int EMPTY = -1;
    private static final Dimension d = new Dimension(400, 25);
    
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
    final SuggestionRenderer renderer;
    
    /*
     * Alphabetically sorted Set of 
     * Strings used as suggestions  for 
     * the user to input to the 
     * HexPanel. 
     * 
     * The suggestions are sent from the 
     * Controller module.
     */ 
    private final Set<String> suggestions = new HashSet<String>();
    
    /*
     * Suggestions that will actually be 
     * shown in the combo box list are
     * stored here.
     */ 
    private final Set<String> visibleSuggestions = new TreeSet<String>();
    
    public KeyboardInputPanel()
    {
        panel = new JPanel(new MigLayout());
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
        addButton = new JButton("OK");
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
        addButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(final ActionEvent ae)
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
    public void	keyReleased(final KeyEvent e)
    {
        dealWithKeyReleased(e);
    } 
    
    /*
     * Processes KeyReleased events fired
     * by the KeyListener on the JComboBox's
     * text field.
     */ 
    private void dealWithKeyReleased(final KeyEvent e)
    {
        final String current = getCurrentTextFieldString();
        
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
        if(prevString != null && current.equals(prevString))
        {
            prevString = current;
            updateVisibleSuggestions(current);
        }              
        else
        {
            prevString = current;
        }
    } 
    
    /**
     * This is a public method because it deals with AWTKeyEvents.
     * It should not be used directly by the programmer.
     */ 
    public void	keyPressed(final KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_TAB)
        {
            tabToHexPanel();    
            return;
        } 
    } 
    
    /*
     * Moves focus to the HexPanel.
     */ 
    private void tabToHexPanel()
    {
        Controller.getHexPanel().requestFocus();      
    }
    
    /*
     * Unused, unattractive boiler plate.
     */ 
    public void	keyTyped(final KeyEvent e){} 
    
   
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
    public void actionPerformed(final ActionEvent ae)
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
    protected void mouseReleasedOnSuggestion(final MouseEvent e)
    {
        if(SwingUtilities.isLeftMouseButton(e))
        {
            final JList list = renderer.getList();
            final Point p = e.getPoint();
            final int index = list.locationToIndex(p);
            
            if(index != EMPTY)
            {
                /*
                 * Check that cell was clicked
                 */ 
                final Rectangle rect = list.getCellBounds(index, index);
                if(rect.contains(p))
                {
                    //unnecessary-> final Object sugg = mod.getElementAt(index);
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
    private final void performNewIdonCreationAction()
    {   
        if(combo.isPopupVisible())
        {         
            /*
             * Check to see if there is a focused
             * object in the popup menu 
             */ 
            final int in = renderer.getSelectedIndex();
            
            if(in >= 0)
            {
                final Object o = mod.getElementAt(in);
                
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
            final String input = textField.getText();     
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
     * Returns the JPanel so it can be added to the main frame.
     */  
    protected JPanel getPanel()
    {
        return panel;
    }
    
    /*
     * Updates the set of suggestion strings
     * that will appear below the text area of the
     * combo box when the user types the text field. 
     */ 
    private void updateVisibleSuggestions(final String s)
    {
        /*
         * Only update the suggestions if we
         * have received some from the Google
         * Sets via the Controller
         */ 
        if(hasSuggestions && !suggestions.isEmpty())
        {
            createVisibleSuggestions(s);
            addSuggestionsToComboBox(s);
        }
    }
     
    
    /*
     * Adds the set of visible suggestions to the
     * combo box. 
     */
    private void addSuggestionsToComboBox(final String str)
    {
        /*
         * Close the pop up menu prior to changing 
         * its list contents
         */ 
        combo.setPopupVisible(false); 
        final ObjectString user = new ObjectString(str);
        mod.removeAllElements();
        
        mod.insertElementAt(user, 0);
        mod.setSelectedItem(user);
        
        for(final String s : visibleSuggestions)
        {
            /*
             * Check if the suggestion
             * is the same as the user
             * input before adding
             */
            if(!comboContainsString(s) 
                &&
               !s.equals(user.toString().toLowerCase(locale)))
            {
                mod.addElement(new ObjectString(s));
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
    
    private boolean comboContainsString(final String s)
    {
        for(int i = 0; i < mod.getSize(); i++)
        {
            final Object o = mod.getElementAt(i);
            if(o.toString().toLowerCase(locale).equals(s.toLowerCase(locale)))
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
    private void createVisibleSuggestions(final String input)
    {
        /*
         * Clear the list of visible Strings
         * and Start afresh
         */ 
        visibleSuggestions.clear();
        
        if(input == null || input.isEmpty())
        {
            //System.out.println("zero length input: show all suggestions");
            for(final String s : suggestions)
            {
                visibleSuggestions.add(s);
            }
            return;
        }
        for(final String s : suggestions)
        {
            if(s.toLowerCase(locale).startsWith(input.toLowerCase(locale)))
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
    protected void addSuggestions(final Collection<String> suggs)
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
    }
}
