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


/**
 * Conveniance class used for adding bare
 * Strings to the KeyboardInputPanel's 
 * 
 */ 
public class ObjectString extends Object
{
    public String s;
    
    /**
     * Creates a new ObjectString from an existing String.
     */ 
    public ObjectString(final String s)
    {
        this.s = s;
    }
    public String toString()
    {
        return s;
    }
}
