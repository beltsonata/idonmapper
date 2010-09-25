package idonmapper.Event;

import idonmapper.Controller;
import idonmapper.Coord;
import java.util.*;

public class IdonSizeChangeEvent extends GenericUserEvent
{
    //ArrayList<Idon> idons;
    ArrayList<Coord> idonCoords;
    int change;
    
    /*public IdonSizeChangeEvent(ArrayList<Idon> idons, int change)
    {
        this.idons = idons;
        this.change = change;
        printIdons();
    }*/
    
    public IdonSizeChangeEvent(ArrayList<Coord> coords, int change)
    {
        this.idonCoords = coords;
        this.change = change;
        //printIdons();
    }
    
    private void printIdons()
    {
        /*System.out.println("IdonSizeChangeEvent. Effecting:");
        for(Idon i : idons)
        {
            System.out.print("\t'" + i.getIdea() + "'");
        }
        System.out.println();*/
    }
    
    public void undo()
    {   
        int revert;
        if(change == Controller.getHexPanel().INCREASE)
        {
            revert = Controller.getHexPanel().DECREASE;
        }
        else if(change == Controller.getHexPanel().DECREASE)
        {
            revert = Controller.getHexPanel().INCREASE;
        }
        else
        {
            throw new IllegalStateException();
        }
        
        //Controller.getHexPanel().alterIdonSize(idons, revert);
        Controller.getHexPanel().alterIdonSize(idonCoords, revert);
    }
    public void redo()
    {   
        //Controller.getHexPanel().alterIdonSize(idons, change);
        Controller.getHexPanel().alterIdonSize(idonCoords, change);
        System.out.println("redoing alter idon size event");
    }
    

    
    public String toString()
    {
        return "IdonSizeChangeEvent";
    }
}
