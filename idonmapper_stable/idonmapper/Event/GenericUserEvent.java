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
 * A Generic User Event which is extended to create events when
 * adding, removing and altering Idons on a HexPanel so that the user
 * can undo (Ctrl-Z) and redo (Ctrl-Y) them.
 */ 
public interface GenericUserEvent
{       
    /**
     * Empty undo method. Implement in sub-classes.
     */ 
    void undo();
    
    /**
     * Empty redo method. Implement in sub-classes.
     */ 
    void redo();
}

