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

/**
 * Defines an event when an Idon's idea has changed
 */ 
public class IdonStringChangeEvent extends GenericUserEvent
{
    Idon i;
    String oldStr, newStr;
    
    public IdonStringChangeEvent(Idon i, String oldStr, String newStr)
    {
        this.i = i;
        this.oldStr = oldStr;
        this.newStr = newStr;
    }
    public void undo()
    {   
        Controller.setIdonIdea(i, oldStr);
    }
    public void redo()
    {   
        Controller.setIdonIdea(i, newStr);
    }
}
