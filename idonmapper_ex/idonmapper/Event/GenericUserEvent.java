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
/**
 * General, all-purpose user
 * event class.
 */ 
public abstract class GenericUserEvent extends Object implements UserEvent
{    
    public GenericUserEvent()
    {}   
    
    /**
     * Empty undo and redo methods. 
     * Implementations exist
     * in the sub classes.
     */ 
    public void undo()
    {   
    }
    public void redo()
    {   
    }
}

