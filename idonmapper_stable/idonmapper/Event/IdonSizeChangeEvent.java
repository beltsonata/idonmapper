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
package idonmapper.Event;

import idonmapper.Controller;
import idonmapper.Idon;
import java.util.*;

/**
 * Encapsulates information about one or more Idons having their sizes
 * altered. [Note that this is not used in the stable version].
 */ 
public class IdonSizeChangeEvent implements GenericUserEvent
{
    final private List<Idon> idons;
    final private int change;
    
    public IdonSizeChangeEvent(final List<Idon> idons, final int change)
    {
        this.idons = idons;
        this.change = change;
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
        Controller.getHexPanel().alterIdonSize(idons, revert);
    }
    public void redo()
    {   
        Controller.getHexPanel().alterIdonSize(idons, change);
    }
    

    
    public String toString()
    {
        return "IdonSizeChangeEvent";
    }
}
