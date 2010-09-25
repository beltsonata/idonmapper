package idonmapper;

import idonmapper.Event.*;
import java.io.*;
import net.miginfocom.swing.MigLayout;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.MouseScrollableUI;
import org.jdesktop.swingx.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The master application control module. 
 * Creates the idonmapper components and
 * links them together.
 */
/*
 * Compilation / execution instructions 
   ------------------------------------
  
 * To compile -
  
   javac -cp :lib/miglayout-3.7.2.jar:lib/jericho-html-3.1.jar:lib/jxlayer.jar:lib/swingx-1.6.1.jar: idonmapper/*java
  
 * To run -

   java -cp :lib/miglayout-3.7.2.jar:lib/jericho-html-3.1.jar:lib/jxlayer.jar:lib/swingx-1.6.1.jar:  idonmapper.Start
 
 */ 
public class Controller
{
    private static JXLayer<JScrollPane> hexScrollerWrapper;
    
    /*
     * Suggestions retrieved by the SetRetriever
     */ 
    private static HashSet<String> suggestions = new HashSet<String>();
    

    /*
     * Strings used as the basis for the above
     * suggestions are stored here in sourceInput. 
     * 
     * This list is kept to ensure that no
     * duplicate queries are made.
     */  
    private static volatile HashSet<String> sourceInput = new HashSet<String>();
    
    private static final String APP_NAME = "idonmapper - untitled";
    
    private static String frameTitle = APP_NAME;
    
    private static boolean mouseDraggingIdon = false, 
                           mousePressedOnIdon = false, setUp = false,
                           shiftIsDown = false, controlIsDown = false,
                           altIsDown = false, middleMouseScroll = false,
                           draggingCluster = false;
    
    private static Idon editedIdon = null,
                        lastPressedIdon = null;
    
    private static Dimension d = new Dimension(800, 600);
    
    private static final int VERT_SCROLL_INCR = 10,
                             HORI_SCROLL_INCR = 10,
                             SINGLE = 1,
                             DOUBLE = 2;
    /*
     * MouseEvent  constants
     */ 
    private static final int LEFT_MOUSE_SHIFT_MASK = 
                                            (MouseEvent.BUTTON1_MASK 
                                            + InputEvent.SHIFT_MASK),
                             LEFT_MOUSE_NO_SHIFT_MASK = 
                                             MouseEvent.BUTTON1_MASK,
                             INC_DEC_BUTTONS_FONT_SIZE = 13;
            
    /*
     * Amount of cells to put on the HexPanel
     */ 
    private static int rows = 150, 
                       cols = 150;
                       
    
    /*
     * hexSize - used as the initial
     * size of the hexagonal cells on 
     * the HexPanel
     */                    
    private static final double hexSize = 14;
    
    /*
     * A text editor for Idons placed on the panel
     */ 
    private static IdonTextEditor editor;
    
    /*
     * Idon Cluster - This is used to find groups of connected
     * Idons and toggle their selected status.
     */ 
    private static IdonCluster idonCluster = null;
    private static Point mouseDownPoint = null;
    
    /*
     *  Stack of user events for undo/redo functionality
     */ 
    private static Stack<UserEvent> undoStack, redoStack;
    
    /*
     * Available Idon colours
     */ 
    private static HashMap<String, Color> colors;
    
    /*
     * Main application window
     */ 
    private static JXFrame f;
    
    /*
     * Idonmapper components
     */
    private static HexPanel hexPanel;
    private static HexPanelKeyListener hexKeyListener;
    private static SetRetriever retriever;
    private static KeyboardInputPanel inputPanel;
    private static ArrowPainter arrowPainter;
    private static IdonMover idonMover;
    
    /*
     * Menu and Icon bars, for File actions
     * and un/redo
     */ 
    private static JMenuBar menuBar;
    private static JMenu fileMenu, editMenu;
    private static JFileChooser fileChooser;
    private static JToolBar toolBar;
    private static JButton decreaseSizeButton, increaseSizeButton;
    
    /*
     * Component to locate Idons on the
     * HexPanel
     */ 
    private static IdonFinder idonFinder;
    
    /*
     * Scrollpane for HexPanel 
     */
    private static JScrollPane hexScroller;
                                
    /*
     * Mouse listener for the hex panel
     */ 
    private static HexPanelMouseManager hexMouseListener;

    /*
     * Simple container panel for the inputPanel
     */
    private static JXPanel inputSuggestPanel;
    
    /*
     * 
     */ 
    private static IdonMapFileHandler fileHandler;
    
    /*
     * Boolean to flag when the 
     * user should be prompted to save
     * their Idon Map before committing
     * an action alters the current map
     * (such as load map, create new map,
     * quit the application)
     */ 
    private static boolean hexPanelChanged = false;
    
    /*
     * Getter methods for the GUI components
     */ 
    protected static SetRetriever getSetRetriever()
    {
        assert(retriever != null);
        return retriever;
    }
    public static HexPanel getHexPanel()
    {
        return hexPanel;
    }
    protected static KeyboardInputPanel getInputPanel()
    {
        return inputPanel;
    }
    protected static JScrollPane getHexScroller()
    {
        return hexScroller;
    }
    
    /*
     * Creates the application components 
     */
    protected static void createComponents()
    {
        if(!setUp)
        {
            f = new JXFrame(APP_NAME);
            f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setUpHexPanelComponents();
            setUpSetRetriever();
            setUpColors();
            setUpInputPanel();
            setUpUndoRedo();
            setUpMenus();
            setUpToolBar();
            setUpFrame();
            setUpFileHandler();
            setUp = true;
        }
    }
    
    private static void setUpHexPanelComponents()
    {
        setUpHexPanel();
        setUpHexKeyListener();
        setUpIdonTextEditor();
        scrollHexScrollerToCenter();
        setUpArrowPainter();
    }
    

    
    /*
     * Sets up the IdonMapFileHandler object
     * which allows the user to draw arrows
     * on the HexPanel
     */ 
    private static void setUpArrowPainter()
    {
        arrowPainter = new ArrowPainter();
    }
    
    /*
     * Sets up the IdonMapFileHandler object
     * which allows the saving and retrieving
     * of files. 
     */ 
    private static void setUpFileHandler()
    {
        fileHandler = new IdonMapFileHandler();
    }
    
    /*
     * Constructs the undo and redo 
     * UserEvent stacks.d bea
     */ 
    private static void setUpUndoRedo()
    {
        undoStack = new Stack<UserEvent>();
        redoStack = new Stack<UserEvent>();
    }
    
   
    
    /**
     * Create a map of the Idons 
     * to alter, and the color to
     * set them to.
     */
    private static HashMap<Coord, Color> createColorMap(
                                                Collection<Coord> idons,
                                                Color col)
    {
        HashMap<Coord, Color> colorMap = new HashMap<Coord, Color>();
        for(Coord c : idons)
        {
            colorMap.put(c, col);
        }
        return colorMap;      
    }
    
    /**
     * Returns true if KeyEvent @e matches
     * the undo key 
     */ 
   /* private static boolean isUndoKey(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown())
        {
            return true;
        }
        return false;
    }*/
    
    /**
     * Returns true if KeyEvent @e matches
     * the redo key 
     */ 
  /* private static boolean isRedoKey(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_Y && e.isControlDown())
        {
            return true;
        }
        return false;
    }*/ 
    
    /**
     * Deals with KeyEvents performed on the
     * HexPanel
     */ 
    protected static void hexPanelKeyReleased(KeyEvent e)
    {
        /*if(isDeleteKey(e))
        { 
            deleteSelectedIdons();
        }*/
        /*else if(isShiftKey(e))
        {
            shiftIsDown = false;
        }
        else if(isControlKey(e))
        {
            controlIsDown = false;
        }
        else if(isAltKey(e))
        {
            altIsDown = false;
        }*/
        return;
    }
    
    /*
     * Returns true if @e is the
     * delete key
     */ 
    public static boolean isDeleteKey(KeyEvent e)
    {
       return isKey(e.getKeyCode(), KeyEvent.VK_DELETE);      
    }
    public static boolean isShiftKey(KeyEvent e)
    {
       return isKey(e.getKeyCode(), KeyEvent.VK_SHIFT);
    }
    public static boolean isControlKey(KeyEvent e)
    {
       return isKey(e.getKeyCode(), KeyEvent.VK_CONTROL);
    }
    public static boolean isAltKey(KeyEvent e)
    {
       return isKey(e.getKeyCode(), KeyEvent.VK_ALT);
    }
    
    
    private static boolean isKey(int k1, int k2)
    {
        if(k1 == k2)
        {
            return true;
        }
        return false;
    }
    
    /*private static boolean isNewFileKey(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_N && e.isControlDown())
        {
            return true;
        }
        return false;
    }*/
    
    /**
     * Sets the shiftIsDown boolean
     */ 
    protected static void setShiftDown(boolean b)
    {
        shiftIsDown = b;
    }
    /**
     * Sets the controlIsDown boolean
     */ 
    protected static void setControlDown(boolean b)
    {
        controlIsDown = b;
    }
    /**
     * Sets the altIsDown boolean
     */ 
    protected static void setAltDown(boolean b)
    {
        altIsDown = b;
    }
    /**
     * Responds to calls from the
     * HexPanelKeyListener and deals with undo,
     * redo and shift key presses.
     */ 
    protected static void hexPanelKeyPressed(KeyEvent e)
    {
        if(isDeleteKey(e))
        { 
            deleteSelectedIdons();
            return;
        }
    
        /*
         * Test the Key for a Color change
         * command character
         */ 
        Color col = getColorFromKeyEvent(e);
        if(col != null && allowedToGetHexPanelControl())
        {
            /*
             * If we have a valid Color from the key,
             * create a new hash map of the Idons
             * to change (and to which color) then
             * send it to the Controller.
             *
             * 
             * The Controller will ensure that undo &
             * redo events are created.
             */ 
            pushToUndoStack(setIdonColors(createColorMap(
                          getHexPanel().getSelectedIdonsCoords(), col)));
            return;
        }
    }
    
    /*
     * Returns the Color associated with this 
     * Key
     */ 
    private static Color getColorFromKeyEvent(KeyEvent e)
    {
        Color col = null;
        int k = e.getKeyCode();
        
        if(e.isControlDown())
        {
            return null;
        }
        
        if(k == KeyEvent.VK_B)
        {
            col = getColorFromString("blue");
        }
        else if(k == KeyEvent.VK_R)
        {
            col = getColorFromString("red");            
        }
        else if(k == KeyEvent.VK_Y)
        {
            col = getColorFromString("yellow");            
        }
        else if(k == KeyEvent.VK_O)
        {
            col = getColorFromString("orange");            
        }
        else if(k == KeyEvent.VK_P)
        {
            col = getColorFromString("purple");            
        }
        else if(k == KeyEvent.VK_G)
        {
            col = getColorFromString("green");            
        }
        return col;
    }
    
    /**
     * Associates multiple Coords 
     * on the Hex Panel with Idons 
     */
    public static void addMultipleIdons(HashMap<Coord, Idon> map)
    {
        for(Map.Entry<Coord, Idon> e : map.entrySet())
        {
            Coord c = e.getKey();
            Idon i = e.getValue();
            getHexPanel().addIdon(c, i.getIdea(), i.getColor(), 
                                  i.getSize());
        } 
    }
    
    public static void addIdonFromInputPanel(Coord c, String idea, 
                                             Color col, int size)
    {
        HashMap<Coord, Idon> map = new HashMap<Coord, Idon>();
        getHexPanel().addIdon(c, idea, col, size);
        
        Idon i = getHexPanel().getIdonFromCoord(c);
        map.put(c, i);
        pushToUndoStack(new IdonsAddedEvent(map));
    }
    
    /*
     * Utility method to reset variables associated with
     * the dragging
     */ 
    private static void clearMouseDragVars()
    {
        mouseDraggingIdon = false;
        mousePressedOnIdon = false;
        idonMover = null;        
    }
    
 
    
    /**
     * Getter and setter methods for
     * boolean associated with the mouse's
     * dragging state. 
     */ 
    protected static boolean getMouseDragState()
    {
        return mouseDraggingIdon;
    }
   
    
    /**
     * Main method with which to add 
     * single Idons to the Hex Panel
     */ 
    public static void addIdonToHexPanel(Coord c, String idea, 
                                         Color col, int size)
    {
        Idon i = getHexPanel().addIdon(c, idea, col, size);
        HashMap<Coord, Idon> map = new HashMap<Coord, Idon>();
        map.put(i.getCoord(), i);
                
        hexPanelChanged = true;
        return;
    }
    
    /**
     * Main method with which to remove Idons
     * to the Hex Panel
     */
    public static void removeIdonsFromHexPanel(HashMap<Coord, Idon> map)
    { 
        for(Map.Entry<Coord, Idon> e : map.entrySet())
        {
            Coord c = e.getKey();
            getHexPanel().removeIdon(c);   
        }
        hexPanelChanged = true;
        return;
    }
    
    /**
     * Pushes a UserEvent onto the 
     * Controller's undo stack
     */ 
    public static void pushToUndoStack(UserEvent e)
    {
        System.out.println("pushToUndoStack: " + e);
        undoStack.push(e);
    }
    
    /**
     * Pushes a UserEvent onto the 
     * Controller's undo stack
     */ 
    public static void pushToRedoStack(UserEvent e)
    {
        System.out.println("pushToRedoStack: " + e);
        redoStack.push(e);
    }
    
    /*
     * Returns true if the MouseEvent represents a left mouse
     * button press whilst holding Ctrl but not Shift 
     */ 
    private static boolean isLeftMouseButtonWithCtrl(MouseEvent e)
    {
        int on = InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
        int off = InputEvent.SHIFT_DOWN_MASK;
        
        if(isMouseEvent(e, on, off))
        {
            return true;
        }
        return false;
    }
    
    /*
     * Returns true if the MouseEvent represents a left mouse
     * button press whilst holding Shift but not Ctrl
     */ 
    private static boolean isLeftMouseButtonWithShift(MouseEvent e)
    {
        int on = InputEvent.BUTTON1_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
        int off = InputEvent.CTRL_DOWN_MASK;
        if(isMouseEvent(e, on, off))
        {
            return true;
        }
        return false;
    }
    
    
    private static boolean isMouseEvent(MouseEvent e, int on, int off)
    {
        if((e.getModifiersEx() & (on | off)) == on)
        {
            return true;
        }
        return false;
    }
    
    /**
     * Deals with mouse events sent
     * from the HexPanelMouseManager when
     * a mouse button is l
     */
    protected static void hexPanelMousePressed(MouseEvent e)
    {
        HexPanel hp = getHexPanel();
        getHexPanel().requestFocusInWindow();
        mouseDownPoint = e.getPoint();

        Idon i = hexPanel.getIdonFromPoint(mouseDownPoint);

        if(i == null)
        {
            mousePressedOnIdon = false;
        }
        else
        {
            mousePressedOnIdon = true;
        }

        /*
         * Deal with double clicks from left mouse button.
         */
        if(leftButtonDoubleClick(e))
        {
            //System.out.println("double click");
            /*
             * If the cell contains an idon, let
             * the user edit the text it contains
             * unless the user is holding Ctrl.
             */
            if(mousePressedOnIdon)
            {

                System.out.println("\ton " + lastPressedIdon.getIdea());

                /*
                 * Only allow one Idon to be
                 * edited at a time
                 */
                if(!editor.isEditingIdon())
                {
                    if(!e.isControlDown())
                    {
                        System.out.println();
                        getIdonTextEditor().editIdon(lastPressedIdon);
                        editedIdon = lastPressedIdon;
                    }
                }
            }


        }
        else if(leftMouseSinglePress(e))
        {
            //System.out.println("left mouse down on hex panel");
            closeMenuPopup();

            if(editor.isEditingIdon())
            {
                /*
                 * If the user altered the contents of the Idon,
                 * check to see if it should be sent to Google
                 * Sets.
                 */
                editor.stopEditing();

            }


            /*
             * Request component focus, so that
             * keyboard events will be delivered
             * to the HexPanel
             */
            hp.requestFocus();

            if(!mousePressedOnIdon)
            {
                if(!isLeftMouseButtonWithCtrl(e))
                {
                    /*
                     * User did not hold control
                     */
                    hp.deselectIdons();
                }
            }
            else
            {
                lastPressedIdon = i;

                /*if(isLeftMouseButtonWithShift(e))
                {
                    idonCluster = new IdonCluster(i);
                    //idonCluster.toggleSelectStatus();
                    idonCluster.selectAll();
                }
                else*/ if(!isLeftMouseButtonWithCtrl(e))
                {
                    hexPanel.deselectIdons();
                    i.toggleSelectStatus();
                }
                else
                {
                    i.toggleSelectStatus();
                }

                /*
                 * Mouse pressed on
                 * occupied cell.
                 *
                 * If an Idon *is* being edited,
                 * it will carry on being edited
                 * until the user presses on a
                 * *separate* one, as these mouse
                 * events will not reach it due
                 * to the JTextPane being visible.
                 */



            }
        }
    }
    private static boolean isValidLeftMouseDraggingEvent(MouseEvent e)
    {
        if(SwingUtilities.isLeftMouseButton(e)) 
        {
            return true;
        }
        return false;
    }
    /*
     * Utility method - Returns true if the MouseEvent 
     * @e contains a bit mask of a left mouse button
     * wwithout the shift key modifier
     */ 
    /*private static boolean isIdonDrag(MouseEvent e)
    {
        
        if((e.getModifiers() & LEFT_MOUSE_NO_SHIFT_MASK)
                 == LEFT_MOUSE_NO_SHIFT_MASK)
        {
            return true;
        }
        return false;
    }*/
    
    /*
     * Utility method - Returns true if the MouseEvent 
     * @e contains a bit mask of a left mouse button, 
     * with  the shift key modifier
     */ 
   /* private static boolean isIdonClusterDrag(MouseEvent e)
    {
        if((e.getModifiers() & LEFT_MOUSE_SHIFT_MASK) 
                == LEFT_MOUSE_SHIFT_MASK)
        {
            //System.out.println("cluster");
            return true;
        }
        return false;
    }*/
   
    
    /**
     * Defines the procedure for dealing with
     * mouse dragged events sent from the HexPanelMouseManager.
     */ 
    protected static void hexPanelMouseDragged(MouseEvent e)
    {
        if(editor.isEditingIdon())
        {
            return;
        }
        if(!mouseDraggingIdon
           && isValidLeftMouseDraggingEvent(e))
        {
            if(!mousePressedOnIdon)
            {
                /*
                 * Draw a rectangle starting from the mouse
                 * point which the user can use to select
                 * multiple Idons
                 */ 
                if(getHexPanel().getDragRect() == null)
                {
                    if(mouseDownPoint != null)
                    {
                        getHexPanel()
                            .setDragRect(new Rectangle(mouseDownPoint));   
                    }
                }
                else
                {
                    /*
                     * Already created first point of rectangle,
                     * so work out the difference between 
                     * the current mouse point and the start
                     * of dragRect and replace with a new rectangle
                     */ 
                    Point newPoint = e.getPoint();
                    Rectangle dragRect = getHexPanel().getDragRect();
                    if(dragRect == null)
                    {
                        throw new NullPointerException("DragRect"
                                                     + " is null");
                    }
                    updateDragRect(newPoint);
                }
                return;
            }
            
            /*
             * Mouse pressed on Idon
             */ 
            if(lastPressedIdon.getSelectedStatus() == true)
            {                         
                /*
                 * If selected, move this and any other
                 * selected Idons
                 */
                idonMover = new IdonMover(lastPressedIdon, 
                                getHexPanel().getSelectedIdons());
                mouseDraggingIdon = true;
                return;
            }
        }
        else if(mouseDraggingIdon && isValidLeftMouseDraggingEvent(e))
        {
            /*
             * Already dragging an Idon,
             * so update its position
             */ 
            idonMover.updateDraggedIdons(e.getPoint());
            updateView();
        }        
    }
    
    /*
     * Updates the hex panel's scroll pane so 
     * that the dragged Idon is in view.
     * 
     * This isn't great - it works
     * based on mouseDragged calls from the
     * HexPanelMouseManager and so doesn't
     * scroll unless the user is still
     * dragging the mouse
     */ 
    private static void updateView()
    {
        if(!mouseDraggingIdon)
        {
            throw new IllegalStateException("attempting to update" +
                             " visible area when not dragging an idon" );
        }
        
        hexPanel.scrollRectToVisible(idonMover.getDraggedIdon()
                                                .getShape().getBounds());
    }
    

    
    /*
     * Updates the user-drag rectangle (used to
     * drag-select on the HexPanel)
     */ 
    private static void updateDragRect(Point newPoint)
    {
        getHexPanel().getDragRect()
                     .setFrameFromDiagonal(mouseDownPoint, newPoint);
        getHexPanel().repaint();
    }
    
    /**
     * Moves a mapping of Idons from one set of Coords to another
     * on the HexPanel.
     */ 
    /*public static void moveMultipleIdons(HashMap<Idon, Position> here,
                                         HashMap<Idon, Position> there)
    {
        
        for(Map.Entry<Idon, Position> e : here.entrySet())
        {
            Idon i = e.getKey();
            Position pos = e.getValue();
            
            i.setPosition(there.get(i));
        }
        
        
    }*/
    public static void moveMultipleIdons(HashMap<Idon, Coord> here,
                                           HashMap<Idon, Coord> there)
    {    
        
        /*
         * Make a copy of the old positions ('here') in a concurrent
         * HashMap so we can remove elements
         */ 
        ConcurrentHashMap<Idon, Coord> hereCopy = new ConcurrentHashMap
                                                    <Idon, Coord>(here);        
        
        /*
         * Loops until all of the Idons have been moved. If a cell is
         * occupied, returns and attempts to move another.
         */ 
        //while(!hereCopy.isEmpty())
        //{
            for(Map.Entry<Idon, Coord> e : hereCopy.entrySet())
            {
                Idon i = e.getKey();
                System.out.println("trying to move " + i + " to " +
                            there.get(i));
                if(moveIdon(e.getValue(), there.get(i)))
                {
                    hereCopy.remove(i);
                }
            }
       //}
    }
    
    /**
     * Receieves mouse events from the HexPanelMouseListener and
     * responds appropriately.
     */
    protected static void hexPanelMouseReleased(MouseEvent e)
    { 
        HexPanel hp = getHexPanel();
        
        if(SwingUtilities.isLeftMouseButton(e))
        {
            if(mouseDraggingIdon)
            {
                dropIdons(e.getPoint());
                return;
            }
           /* if(arrowPainter.isPainting())
            {
                
                arrowPainter.setEndPoint(e.getPoint());
                return;
            }*/
            
            /*
             * Check to see if the user
             * has been drag-selecting  
             */ 
            if(hp.getDragRect() == null)
            {
                return;
            }
            ArrayList<Idon> newSelection = 
                         hp.getOverlappingIdons(hp.getDragRect());
            hp.toggleSelectIdonsInList(newSelection);
            
            /*
             * Destroy @dragRect and repaint
             * the hex panel
             */ 
            hp.destroyDragRect();
            hp.repaint();
        } 
    }
    
    /*
     * Instructs the IdonMover to drop any Idons it is carrying.
     */ 
    private static void dropIdons(Point p)
    {
        idonMover.drop(p);
        clearMouseDragVars();
        hexPanel.repaint();
    }
    
    /*
     * 
     */ 
    protected static void setEditedIdon(Idon i)
    {
        editedIdon = i;
    }
    
    protected static Idon getEditedIdon()
    {
        return editedIdon;
    }
    
    /**
     * 
     */ 
    public static UserEvent popUndoStack()
    {
        return popEventStack(undoStack);
    }
    public static UserEvent popRedoStack()
    {
        return popEventStack(redoStack);
    }
    
    /*
     * Generic pop method for both
     * undo and redo event stacks
     */ 
    private static UserEvent popEventStack(Stack<UserEvent> stk)
    {
        try
        {
            UserEvent ue = stk.pop();
            return ue;
        }
        catch(EmptyStackException e)
        {
            return null;
        }        
    }
    /**
     * Sets up the available colours for
     * the Idons on the Hex Panel
     */ 
    private static void setUpColors()
    {
       /*
        * Note that these RGB values were taken
        * from the examples here: 
        * 
        * http://gucky.uni-muenster.de/cgi-bin/rgbtab-en
        */  
        colors = new HashMap<String, Color>();
        colors.put("yellow", Color.yellow);
        colors.put("blue", new Color(100, 149, 237));
        colors.put("red", new Color(255, 10, 10));
        colors.put("orange", new Color(255,140, 0));
        colors.put("purple", new Color(147, 112, 219));
        colors.put("green", new Color(144, 238, 144));
    }
    
    
    
    
    /**
     * Returns the IdonTextEditor object
     */ 
    public static IdonTextEditor getIdonTextEditor()
    {
        return editor;   
    }
    
    private static void setUpIdonTextEditor()
    {
        editor = new IdonTextEditor();
        hexPanel.add(editor);
    }
    
    
    /**
     * Changes Idon @i's idea to the contents of @newStr
     */ 
    public static void setIdonIdea(Idon i, String newStr)
    {
        i.setIdea(newStr);
        Controller.getHexPanel().repaint();
        return;
    }
    
    /**
     * Returns the Color with 
     * the key string @name,
     * or null if not found.
     */ 
    protected static Color getColorFromString(String name)
    {
        if(name.equals("default"))
        {
            return colors.get("yellow");
        }
        return colors.get(name);
    }
    private static void setUpHexKeyListener()
    {
        hexKeyListener = new HexPanelKeyListener();
    }
    private static void setUpHexPanelMouseManager()
    {
        hexMouseListener = new HexPanelMouseManager();
    }
    private static void setUpHexPanel()
    {
        hexPanel = new HexPanel(hexSize, rows, cols);
        hexPanel.setLayout(null);
        setUpHexPanelMouseManager();
        setUpHexScroller();
    }
    private static void setUpSetRetriever()
    {
        //System.out.println("setting up set retriever");
        retriever = new SetRetriever();
    }
    private static void setUpInputPanel()
    {
        //System.out.println("setting up input panel");
        inputPanel = new KeyboardInputPanel();
    }

    /**
     * Sets up the HexPanel's JScrollPane 
     * for scrolling purposes
     */ 
    private static void setUpHexScroller()
    {
        hexScroller = new JScrollPane(hexPanel);
        hexScroller.getVerticalScrollBar()
                        .setUnitIncrement(VERT_SCROLL_INCR);
        hexScroller.getHorizontalScrollBar()
                        .setUnitIncrement(HORI_SCROLL_INCR);
        
        /*
         * Set the viewport to use blitting mode
         * 
         * See the following for alleged performance benefit:
         * http://www.javaperformancetuning.com/tips/user_perception.shtml#REF26
         */ 
        hexScroller.getViewport()
                        .setScrollMode(JViewport.BLIT_SCROLL_MODE);
        
        /*
         * Adjust the scrollSize to be *slightly* larger than the 
         * hex panel
         */ 
        Dimension scrollerSize = hexPanel.calculateMinimumSize();
        scrollerSize.width  += hexPanel.PADDING;
        scrollerSize.height += hexPanel.PADDING;
        hexScroller.setPreferredSize(scrollerSize);
        //scrollHexScrollerToCenter();
        return;
    }

    /*
     * Sets up the GUI toolbar and
     * the buttons on top of it. 
     */ 
    private static void setUpToolBar()
    {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        setUpHexSizeButtons();
    }
    
    /*
     * Method invoked by the Controller
     * to increment the Hexagons'
     * side length.
     */ 
    private static void increaseIdonSize()
    {
        ArrayList<Coord> changed = 
                hexPanel.alterIdonSize(hexPanel.getSelectedIdonsCoords(), 
                                            HexPanel.INCREASE);
        /*ArrayList<Idon> changed = 
                     hexPanel.alterIdonSize(hexPanel.getSelectedIdons(), 
                                            HexPanel.INCREASE);*/
        pushToUndoStack(new IdonSizeChangeEvent(changed, 
                                                HexPanel.INCREASE));
   
    }
    
    private static void decreaseIdonSize()
    {
        ArrayList<Coord> changed = 
                hexPanel.alterIdonSize(hexPanel.getSelectedIdonsCoords(), 
                                            HexPanel.DECREASE);

        
        /*ArrayList<Idon> changed = 
                     hexPanel.alterIdonSize(hexPanel.getSelectedIdons(), 
                                            HexPanel.DECREASE);*/
        pushToUndoStack(new IdonSizeChangeEvent(changed, 
                                                HexPanel.DECREASE));

    }
    
    /*
     * Creates the '--' and '+' buttons on the
     * toolbar with which the user can increase
     * & decrease the size of the hex cells.
     */ 
    private static void setUpHexSizeButtons()
    {
        Action actionInc = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(allowedToGetHexPanelControl())
                {
                    increaseIdonSize();
                }
            }                
        };
        
        Font sizeFont = new Font("Dialog", Font.BOLD, 
                                 INC_DEC_BUTTONS_FONT_SIZE);
        increaseSizeButton = new JButton("+");
        increaseSizeButton.setFont(sizeFont);
        increaseSizeButton.setFocusable(false);
        increaseSizeButton.addActionListener(actionInc);
        
        /*
         * Create the keyboard short cut 
         */ 
        KeyStroke ctrlPlus = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS,
                                                  InputEvent.CTRL_MASK);
        increaseSizeButton.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW)
                                .put(ctrlPlus, "INCREASE_SIZE");
        increaseSizeButton.getActionMap().put("INCREASE_SIZE", actionInc);
        
        /*
         * Repeat for decrease button
         */ 
        Action actionDec = new AbstractAction()
        {
            public void actionPerformed(ActionEvent ae)
            {
                if(allowedToGetHexPanelControl())
                {
                    decreaseIdonSize();
                }
            }
        }; 
        
        decreaseSizeButton = new JButton("--");
        decreaseSizeButton.setFont(sizeFont);
        decreaseSizeButton.setFocusable(false);
        decreaseSizeButton.addActionListener(actionDec);
        
        KeyStroke ctrlMinus = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
                                                  InputEvent.CTRL_MASK);
        decreaseSizeButton.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW)
                                .put(ctrlMinus, "DECREASE_SIZE");
        decreaseSizeButton.getActionMap().put("DECREASE_SIZE", 
                                                    actionDec);
        setMenuPopUpMouseListener(decreaseSizeButton);
        
        /*
         * Add both to the toolbar
         */ 
        toolBar.add(decreaseSizeButton);
        toolBar.add(increaseSizeButton);
    }
 
    
    /**
     * 
     */
    public static boolean allowedToGetHexPanelControl()
    {
        if(!mouseDraggingIdon)
        {
            if(!editor.isEditingIdon())
            {
                return true;
            }
        }
        //System.out.println("not allowed hex panel control");
        return false;
    }
    
    /*
     * Sets up the context menu items.
     */ 
    private static void setUpMenus()
    {   
        menuBar = new JMenuBar();
        
        /*
         * Set up the file chooser for opening/saving 
         */ 
        setUpFileChooser();
        setUpFileMenu();
        setUpEditMenu();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
    }
    
    private static void setUpFileMenu()
    {
        fileMenu = new JMenu("File");   
        setUpNewFileAction();
        setUpOpenFileAction();
        setUpSaveFileAction();
        //setUpExportSVGFileAction();
        setUpExportPostScript();
        setUpQuitAction();
    }
    
    private static void setUpFileChooser()
    {
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }
    
    /*
     * Sets up the edit menu items
     * and their shortcuts.
     */ 
    private static void setUpEditMenu()
    {
        editMenu = new JMenu("Edit");
        JMenuItem undo = new JMenuItem("Undo");
        
        undo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                undo();
            }
        });
        
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 
                                                   Event.CTRL_MASK));
        JMenuItem redo = new JMenuItem("Redo");
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 
                                                   Event.CTRL_MASK));
        redo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                redo();
            }
        });
        editMenu.add(undo);
        editMenu.add(redo);
        
        setUpFind();
    }
    
   /*
    * Add the 'Find' option to the
    * Edit menu and attach its 
    */ 
    private static void setUpFind()
    {
        
        JMenuItem find = new JMenuItem("Find Idon");
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, 
                                                   Event.CTRL_MASK));
                                                   
        idonFinder = new IdonFinder(f);
        find.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {                
                if(Controller.allowedToGetHexPanelControl())
                {
                    idonFinder.findIdon();
                }
            }
        });
        editMenu.add(find);
    }
    
    /**
     * Utility method to close the JMenus.
     */ 
    protected static void closeMenuPopup()
    {
        fileMenu.getPopupMenu().setVisible(false);
        menuBar.setSelected(null);
        fileMenu.setSelected(false);
        editMenu.getPopupMenu().setVisible(false);
        editMenu.setSelected(false);
    }
    
    
    
    private static void setUpQuitAction()
    {
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                 exit();
            }
        });
        quit.setAccelerator(KeyStroke.getKeyStroke
                                     (KeyEvent.VK_Q, Event.CTRL_MASK));
        fileMenu.add(quit);
    } 
    
    /*
     * Defines the application exit procedure 
     */ 
    private static void exit()
    {
        if(hexPanelChanged)
        {
            int res = promptUserToSave();
            if(res == JOptionPane.CANCEL_OPTION)
            {
                System.out.println("exit: save cancelled");
                return;
            } 
        }
        System.exit(0);
    }
    
    /*
     * Check the user input from a JOptionPane
     * 
     */ 
    private static boolean isCancelled(int i)
    {
        if(i == JOptionPane.CANCEL_OPTION)
        {
            return true;
        }
        return false;
    }
    private static boolean isNegative(int i)
    {
        if(i == JOptionPane.NO_OPTION)
        {
            return true;
        }
        return false;
    }
    
    /*
     * Utility method to set up the action associated with
     * the 'New' menu item.
     */ 
    private static void setUpNewFileAction()
    {
        JMenuItem newMap = new JMenuItem("New Map");
        newMap.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                newFile();
            }
        });
        newMap.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
        fileMenu.add(newMap);
    }
    
    /*
     * Defines the procedure to create a new file
     * when the user has clicked the relevant
     * menu item or typed the relevant keyboard
     * shortcut.
     */ 
    private static void newFile()
    {
        if(hexPanelChanged)
        {
            int res = promptUserToSave();
            if(isCancelled(res))
            {
                return;
            } 
        }
        
        /*
         * We can reset the hexPanel by setting the hexSize to
         * its original value and by setting the 'destroy' boolean
         * as 'true'
         */ 
        hexPanel.setHexSize(hexSize);
        clearSuggestionData();
        hexPanel.repaint();
        hexPanelChanged = false;
    }
    
    /*
     * Utility method to clear all
     * suggestions and source input
     * from which suggestions were based
     */ 
    private static void clearSuggestionData()
    {
        if(suggestions != null)
        {
            suggestions.clear();
        }
        if(suggestions != null)
        {
            sourceInput.clear();
        }
        inputPanel.clearSuggestions();
    }
    
    /*
     * Utility method to set up the action associated with
     * the 'Save' menu item.
     */ 
    private static void setUpSaveFileAction()
    {
        JMenuItem save = new JMenuItem("Save Map");
        save.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                saveFile();
            }
        });
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
                                                   Event.CTRL_MASK));
        fileMenu.add(save);
    }
    
    /*
     * Note: This is not being added to the menu as
     * the exported SVG files are not correct!
     */ 
    private static void setUpExportSVGFileAction()
    {
        JMenuItem exportSVG = new JMenuItem("Export SVG");
        exportSVG.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                exportAsSvg();
            }
        });
        fileMenu.add(exportSVG);
    }
    private static void setUpExportPostScript()
    {
        JMenuItem exportPS = new JMenuItem("Export Postscript");
        exportPS.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                exportPostScript();
            }
        });
        fileMenu.add(exportPS);
    }
    
    private static void exportPostScript()
    {
        int response = fileChooser.showSaveDialog(f);
        
        if(response == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            try
            {
                if(!file.createNewFile())
                {
                    /*
                     * This file already exists
                     */ 
                    if(popUpOverwriteFile() == true)
                    {
                        PostscriptExporter pe = new PostscriptExporter();
                        pe.setUp(file);
                        if(!pe.save())
                        {
                            popUpSaveFileFailed(); 
                        }
                    }
                }
            }
            catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }

        
    }
    
    /*
     * Attempts to export the current HexPanel view
     * as an SVG file.
     */ 
    private static void exportAsSvg()
    {
        int response = fileChooser.showSaveDialog(f);
        
        if(response == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            try
            {
                if(!file.createNewFile())
                {
                    /*
                     * This file already exists
                     */ 
                    if(popUpOverwriteFile() == true)
                    {
                       /* if(!SVGExporter.exportSVG(hexPanel, file))
                        {
                            popUpSaveFileFailed(); 
                        }*/
                    }
                }
            }
            catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
    
    /*
     * Creates an IdonMap object from the current application
     * state information.
     */ 
    private static IdonMap createIdonMap(String name)
    {
        //System.out.println("createIdonMap: name " + name);
        return new IdonMap(name, hexPanel.getHexSize(), 
                           hexPanel.getIdons(), suggestions, 
                           hexPanel.visibleArea());
            
    }

    
    /*
     * Defines the procedure to save an
     * Idon Map to file.
     */
    private static void saveFile()
    {
        int response = fileChooser.showSaveDialog(f);
        if(response == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            System.out.println("chosen file: " + file);
            
            try
            {
                if(!file.createNewFile())
                {
                    /*
                     * This file already exists
                     */ 
                    if(popUpOverwriteFile() == true)
                    {
                        if(!fileHandler.exportFile(createIdonMap(
                                                 file.getName()), file))
                        {
                            popUpSaveFileFailed(); 
                        }
                    }
                }
                else
                {
                    /*
                     * Write to the file
                     */ 
                    if(!fileHandler.exportFile(createIdonMap(
                                                file.getName()), file))
                    {
                        popUpSaveFileFailed();
                    }
                    else
                    {
                        // save successful 
                        hexPanelChanged = false;
                    }
                }
            }
            catch(IOException e)
            {
                popUpSaveFileFailed();
                return;
            }
        }            
    }
    
    /*
     * Shows an option dialog prompting the
     * user to save the current application 
     * state to a file.
     */ 
    private static boolean popUpOverwriteFile()
    {
        int result = JOptionPane.showOptionDialog(
                                f, "File Exists.\n"
                                + "Overwrite it?", "Warning",
                                   JOptionPane.YES_NO_OPTION,
                                   JOptionPane.WARNING_MESSAGE, 
                                   null, null, null);
                                   
        if(result == JFileChooser.APPROVE_OPTION)
        {
            return true;
        }
        return false;
    }
    
    private static void popUpSaveFileFailed()
    {
        JOptionPane.showMessageDialog(f, "Could not write to file.",
                                        "I/O Error", 
                                     JOptionPane.ERROR_MESSAGE);
    }
    
    /*
     * Create an anonymous mouse listener
     * for a component so that the
     * JMenu  close when the mouse is 
     * pressed on it.
     */ 
    protected static void setMenuPopUpMouseListener(JComponent c)
    {
        c.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                Controller.closeMenuPopup();
            }
        });
    }
    
    /*
     * Utility method to set up the action associated with
     * the 'Open' menu item.
     */ 
    private static void setUpOpenFileAction()
    {
        JMenuItem open = new JMenuItem("Open Map");
        open.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                openFile();
            }
        });
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
                                                   Event.CTRL_MASK));
        fileMenu.add(open);
    }
    
    /*
     * Defines the procedure to open a file
     * when the user has clicked the relevant
     * menu item or typed the relevant
     * keyboard shortcut.
     */ 
    private static void openFile()
    {
        if(hexPanelChanged)
        {
            int res = promptUserToSave();
            if(isCancelled(res))
            {
                return;
            } 
        }
        
        int response = fileChooser.showOpenDialog(f);
        if(response == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            
            if(file == null)
            {
                popUpLoadFileFailed();
                return;
            }
            
            IdonMap map = fileHandler.importFile(file);   
            if(map == null)
            {
                popUpLoadFileFailed();
                return;
            }
            
            /*
             * Set frame title to the loaded
             * map title
             */ 
            setFrameTitle(map.getMapName());
            clearSuggestionData();
            suggestions = map.getSuggestions();            
            importIdonMapToHexPanel(map);
            hexPanel.scrollRectToVisible(map.getViewRect());
        }
    }
    
    /*
     * Imports an IdonMap object (which was itself
     * imported from a file)
     */ 
    private static void importIdonMapToHexPanel(IdonMap map)
    {
        hexPanel.setHexSize(map.getCellSize());
        
        for(Idon idon : map.getIdons())
        {
            addIdonToHexPanel(idon.getCoord(), idon.getIdea(), 
                              idon.getColor(), idon.getSize());
        }
        
        map = null;
        
        hexPanelChanged = false;
        mouseDraggingIdon = false;
        mousePressedOnIdon = false;
        
        /*
         * Clear both stacks
         */ 
        undoStack.clear();
        redoStack.clear();
    }
    /*
     * Utility method to display a dialog
     * informing the user that an attempt 
     * to load a file failed.
     */ 
    private static void popUpLoadFileFailed()
    {
        JOptionPane.showMessageDialog(f, "Unable to load IdonMap from "
                              + "file.\nPlease check the file exists "
                              + "and that\nit contains valid xml data.", 
                                "File Error",
                                JOptionPane.ERROR_MESSAGE);
    }
    
    private static void setFrameTitle(String title)
    {
        f.setTitle(title);
    }
    
    /*
     * Creates a dialog window prompting the
     * user to save the current Idon Map. 
     */ 
    private static int promptUserToSave()
    {        
        int choice = JOptionPane.showConfirmDialog(
                       f, "You have unsaved data.\n"
                       + "\nWould you like to save?", 
                       "Warning",
                       JOptionPane.YES_NO_CANCEL_OPTION,
                       JOptionPane.QUESTION_MESSAGE);                   
        if(choice == JFileChooser.APPROVE_OPTION)
        {
            saveFile();
        }
        return choice;
    }
    
    /*
     * Scrolls to the middle of the hex panel
     * to maximise the possible directions in which
     * user-created maps can grow.
     */ 
    protected static void scrollHexScrollerToCenter()
    {
        Hexagon h = getHexPanel().getMiddleHexagon();
        
        if(h == null)
        {
            throw new NullPointerException(
                "scrollHexScrollerToCenter couldn't get a middle hex");
        }
        Rectangle r = h.getShape().getBounds();
        getHexPanel().scrollRectToVisible(r);
        return;        
    }

    /*
     * Sets up the main GUI window and adds the components to its
     * content pane.
     */
    private static void setUpFrame()
    {
        MigLayout frameLayout = new MigLayout();
        f.getContentPane().setLayout(frameLayout);
        f.setPreferredSize(d);
        f.setJMenuBar(menuBar);
        f.setJMenuBar(menuBar);
        f.getContentPane().add(inputPanel.getPanel(), "split 2");
        f.getContentPane().add(toolBar, "wrap");
        
        /*
         * By wrapping the hexPanel's JScrollPane in a JXLayer,
         * we can use middle mouse drag-scrolling navigate it.
         */ 
        JXLayer<JScrollPane> hexScrollerWrapper =
                 new JXLayer<JScrollPane>(hexScroller, new
                                          MouseScrollableUI());
        f.getContentPane().add(hexScrollerWrapper);
        
        /*
         * Define the what to do when the user tries to quit
         */ 
        f.addWindowListener(new WindowAdapter() 
        {
            public void windowClosing(WindowEvent e) 
            {
                exit();
            }
        });
        
       
        f.pack();
        f.setVisible(true);
        
        /*
         * Center on the screen
         */
        f.setLocationRelativeTo(null);
    }
    
    /*
     * Creates a new Idon object from a String 
     * inputted from the KeyBoardInputPanel,
     * then attempts to query Google Sets
     * with it. 
     */ 
    protected static void createIdonFromInput(String input)
    {
        // Put the Idon somewhere in the current view of the HexPanel,
        // or at least near to it.
        
        Coord coord = hexPanel.getNearestUnoccupiedCell();
        HashSet<Coord> exclude = new HashSet<Coord>();
        coord = hexPanel.getNearestEmptyCellToCoord(coord, exclude);
        
        if(coord == null)
        {
            throw new NullPointerException("No unoccupied cell found!");
            // Uh oh. The Panel should have more cells!
        }
        
        addIdonFromInputPanel(coord, input, Color.yellow, Idon.SIZE_NORMAL);
        
        if(isValidSetQuery(input))
        {
            getSetResultsFromString(input);
        }
        hexPanelChanged = true;
        hexPanel.repaint();
    }
    
    /**
     * Returns false if the query is invalid, i.e. it
     * will not be possible to encode it and retrieve
     * set data from it.
     */ 
    public static boolean isValidSetQuery(String s)
    {
        if(s.length() == 0)
        {
            return false;
        }
        if(!Character.isLetterOrDigit(s.charAt(0)))
        {
            return false;
        }
        return true;
    }
    
    /*
     * Attempts to retrieve Strings from Google
     * Sets based upon a query using soley String
     * @s.
     */ 
    protected static void getSetResultsFromString(String s)
    {
        if(sourceInput == null)
        {
            sourceInput = new HashSet<String>();
        }
        
        if(!sourceInput.contains(s.toLowerCase()))
        {
            /*System.out.println("looking for " + s + 
                " in sourceInput set, size " + sourceInput.size());
            System.out.println("getting Set results for '" + s + "'.");*/
            
           
            suggestions.addAll(getSetRetriever().requestSet(s));
            sendToKeyboardInputPanel(suggestions);
            
            /*
             * Add String s to the source input set 
             * so that it is not queries again
             */ 
            sourceInput.add(s.toLowerCase());
        }
    }
    
    /*
     * Adds a HashSet of strings to the KeyboardInputPanel
     * for it to use as a drop down list of suggestions.
     */ 
    private static void sendToKeyboardInputPanel(HashSet<String> ideas)
    {
        getInputPanel().addSuggestions(ideas);    
    }
    
    /**
     * Moves Idon @i from @oldPos to @newPos. 
     */ 
   /* public static boolean moveIdon(Idon i, Position oldPos, Position newPos)
    {
        if(oldPos == null)
        {
            throw new NullPointerException("moveIdon: oldPos is null!");
        }
        if(newPos == null)
        {
            throw new NullPointerException("moveIdon: oldPos is null!");
        }
        
        Point2D.Double newPoint = new Point2D.Double(oldPos.x, oldPos.y); 
        Idon idon = hexPanel.getIdonFromPoint(newPoint);
        
        if(idon != null)
        {
            System.out.println("moveIdon: point is inside idon");
            return false;
        }
        
        i.setPosition(newPos);
        hexPanelChanged = true;
        return true;
    }*/
    public static boolean moveIdon(Coord oldPos, Coord newPos)
    {      
        if(oldPos == null)
        {
            throw new NullPointerException("moveIdon: oldPos is null!");
        }
        if(newPos == null)
        {
            throw new NullPointerException("moveIdon: oldPos is null!");
        }
        
        Idon i = hexPanel.getIdonFromCoord(oldPos);
        Idon destIdon = hexPanel.getIdonFromCoord(newPos);
        
        if(i == null)
        {
            throw new NullPointerException("cannot moveIdon: idon at "
                                          + oldPos + " is null! ");
        }
        if(destIdon != null)
        {
            return false;
        }
        
     
                          
        HexPanel hp = getHexPanel();
           
        boolean selectedStatus = i.getSelectedStatus();
        
        
        hp.removeIdon(oldPos);   
        
        hp.addIdon(newPos, i.getIdea(), i.getColor(), i.getSize());     
        i = hp.getIdonFromCoord(newPos);
        
        if(i == null)
        {
            throw new NullPointerException("moveIdon failed: new position "
                            + "coord does NOT contain an Idon!");
        }
        
         
        if(selectedStatus)
        {
            i.select();
        }
        hexPanelChanged = true;
        return true;
    } 
    
    /**
     * Method to the selected Idons from 
     * the HexPanel.
     */ 
    protected static void deleteSelectedIdons()
    {
        ArrayList<Coord> coords = Controller.getHexPanel()
                                            .getSelectedIdonsCoords();
        if(coords.size() == 0)
        {
            return;   
        }
        
        HashMap<Coord, Idon> map = new HashMap<Coord, Idon>();
        
        for(Coord c : coords)
        {
            map.put(c, Controller.getHexPanel().getIdonFromCoord(c));
        }
        Controller.removeIdonsFromHexPanel(map);
        pushToUndoStack(new IdonsDeletedEvent(map));
    }
    
    /**
     * Un-does the last user action
     */ 
    protected static void undo()
    {
        
        UserEvent e = popUndoStack();
        
        if(e == null)
        {
            return;
        }
        System.out.println("undo : " + e);
        e.undo();
        pushToRedoStack(e);
        getHexPanel().repaint();
    }
    
    /**
     * Re-does the laste user action
     */ 
    protected static void redo()
    {
        
        UserEvent e = popRedoStack();
        if(e == null)
        {
            return;
        }
        System.out.println("redo : " + e);
        e.redo();
        pushToUndoStack(e);
        getHexPanel().repaint();
    }
    
    /**
     * Sets the Idons associated with
     * the coords in the HashMap to the
     * associated Color on the HexPanel.
     * 
     * Returns an IdonsColorChangedEvent 
     * which can then be dealt with
     * (i.e. sent to the undo or redo
     * stack). 
     */ 
    public static IdonsColorChangedEvent setIdonColors(HashMap<Coord, 
                                                       Color> newColorMap)
    {       
        /*
         * Take a copy of the old colors 
         * before setting the new 
         * colorMap
         */
        HashMap<Coord, Color> oldColorMap = new HashMap<Coord, Color>();
        
        for(Map.Entry<Coord, Color> e : newColorMap.entrySet())
        {
            Coord c = e.getKey();
            Color newCol = e.getValue();
            Idon i = getHexPanel().getIdonFromCoord(c);
            if(i == null)
            {
                throw new IllegalStateException();
            } 
            oldColorMap.put(c, i.getColor());
            i.setColor(newCol);
        }
        
        getHexPanel().repaint();
        
        return new IdonsColorChangedEvent(oldColorMap, newColorMap);
    }
    
    /**
     * Returns true if the MouseEvent @e
     * is a first button double click
     */ 
    public static boolean leftButtonDoubleClick(MouseEvent e)
    {
        if(e.getButton() == MouseEvent.BUTTON1 && 
           e.getClickCount() == DOUBLE)
        {
            return true;
        }
        return false;
    }
    
    /**
     * Returns true if MouseEvent @e
     * represents a single left
     * mouse button click
     */ 
    public static boolean leftMouseSinglePress(MouseEvent e)
    {
        if(SwingUtilities.isLeftMouseButton(e) 
           && e.getClickCount() == SINGLE)
        {
            return true;
        }
        return false;
    }
    
    

    /**
     * Returns true if MouseEvent @e
     * represents double right
     * mouse button clicks
     */ 
    public static boolean rightMouseDoubleClick(MouseEvent e)
    {
        if(SwingUtilities.isRightMouseButton(e)
           && e.getClickCount() == DOUBLE)
        {
            return true;
        }
        return false;
    }
    /**
     * Returns true if MouseEvent @e
     * represents double right
     * mouse button clicks
     */ 
    public static boolean rightMouseSingleClick(MouseEvent e)
    {
        if(SwingUtilities.isRightMouseButton(e)
           && e.getClickCount() == SINGLE)
        {
            return true;
        }
        return false;
    }
    
    
}
