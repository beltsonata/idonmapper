package idonmapper;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import org.jdesktop.swingx.*;
import net.miginfocom.swing.MigLayout;


/**
 * A custom dialog using  that allows the user to
 * search for Idons on the HexPanel
 */ 
public class IdonFinder 
{
    private JDialog dialog;
    private static boolean MODAL = true;
    private JTextField textField;
    private JCheckBox reverse;
    private static final int MAX_TEXT = 20; 
    public final Dimension size = new Dimension(300, 200);
    
    
    private Coord lastFound = null;
    
    /**
     * Creates the IdonFinder and associates
     * it with the main application window.
     */
    public IdonFinder(JFrame f)
    {
        dialog = new JDialog(f, "Find Idon", MODAL);
        JLabel label = new JLabel("Find");
        reverse = new JCheckBox("Reverse", false); 
        reverse.setFocusTraversalKeysEnabled(false);
        textField = new JTextField(MAX_TEXT);
        textField.setFocusTraversalKeysEnabled(false);

        dialog.getContentPane().setLayout(new MigLayout());
        dialog.getContentPane().add(label, "split 2");
        dialog.getContentPane().add(textField, "span 1");
        dialog.getContentPane().add(reverse, "wrap");
        
        setUpActionListeners();
        dialog.pack();
        dialog.setLocationRelativeTo(f);
    }
    
    /*
     * Creates the component action listeners to deal with input
     * by the user 
     */ 
    private void setUpActionListeners()
    {
        textField.addActionListener(new AbstractAction() 
        {       
            public void actionPerformed(ActionEvent ae)
            { 
                boolean rev = reverse.isSelected();
                Coord c = Controller.getHexPanel().searchForIdon(
                                                    textField.getText(), 
                                                    lastFound, rev);
                if(c == null)
                {
                    System.out.println("not found");
                    lastFound = null;
                    return;
                }
                lastFound = c;
                Idon idon = Controller.getHexPanel().getIdonFromCoord(c);
                Controller.getHexPanel().scrollRectToVisible(
                                            idon.getShape().getBounds());
            }
        });
        KeyAdapter ka = new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if(isEscape(e))
                {
                    //System.out.println("ESCAPE");
                    dialog.setVisible(false); 
                }
            }
        };
        
        textField.addKeyListener(ka);
        reverse.addKeyListener(ka);
    }
    
    private boolean isEscape(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            return true;
        }
        return false;
    }
    
    /**
     * Displays the Find Idon dialog.
     */ 
    protected void findIdon()
    {
        dialog.setVisible(true);    
        dialog.requestFocus();   
    }
 
}
