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
 * User Events - the events that the idonmapper
 * application records in the Controller.userEventStack.
 * 
 * Enables the Controller to perform Undo/Redo
 * functions. 
 * 
 * Note that an incomplete GenericUserEvent template
 * is below for the most used of tasks.
 */
public interface UserEvent 
{        
    void undo();
    void redo();
}
