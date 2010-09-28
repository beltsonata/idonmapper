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
 * A coordinate on a HexPanel.
 */
public class Coord implements Comparable
{
    public int x, y;
    
    /*
     * Comparison constants 
     */
    public final static int GREATER = 1, LESSER = -1, EQUAL = 0;
    
    public Coord(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public String toString()
    {
        return "[x: " + x + ", " + "y: " + y + "] ";
    }
    
    /*
     * Equals method to compare two Coords.
     *
    public boolean equals(Object c)
    {
        if(c == null)
        {
            throw new IllegalArgumentException("Coord.equals() --> "
                                             + " null Coord!");
        }
        if(this.hashCode() == c.hashCode())
        {
            return true;
        }
        return false;
    }*
    
    */
    
    /**
     * Compares a Coord against another.
     * 
     * This will return 0 if the this Coord exactly matches, a negative 
     * int if lesser and a positive int if greater than the other
     * Coord.
     */ 
    public int compareTo(Object other)
    {
        Coord c = (Coord)other;
        
        /* 
         * How this works:
         * 
         * A Coord is said to be greater than another if it is further 
         * along the HexPanel than another. 'Further along' being
         * when the HexPanel is read left-to-right from the top-left 
         * to the bottom-right like a book.
         */
        if(this.equals(other))
        {
            return EQUAL;
        }
        else if(this.y > c.y)
        {        
            return GREATER;
        }
        else if(this.y < c.y)
        {
            return LESSER;
        }
        else if(this.y == c.y)
        {
            /*
             * Same row
             */ 
            if(this.x > c.x)
            {
                return GREATER;
            }
        }
        return LESSER;
    }
    
    
}
