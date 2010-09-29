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
 * Directions used for easily associating
 * adjacent hexagons together.
 */
public enum Direction
{
    NE, E, SE, SW, W, NW;

    Direction(){}
    
    /*
     * Returns the opposite direction to @dir.
     */
    /*public static Direction getOpposite(final Direction dir)
    {
        switch(dir)
        {
            case NE :
                return SW;
            case E:
                return W;
            case SE:
                return NW;
            case SW:
                return NE;
            case W:
                return E;
            case NW:
                return NE;
        }
        return null;
    }*/
    
    
    
    /**
     * Returns the first Coord on the hex panel away from 
     * another Coord on the HexPanel in the specified direction. 
     * 
     * <p>
     * 
     * @return  The Coordinate found.
     * @param   c         The source coordinate.
     * @param   direction The direction in which to look.
     */
    public static Coord directionToCoord(final Coord c, final 
                                         Direction direction)
    {
        /*
         * Odd and even columns behave differently.
         */
        if(c == null)
        {
            System.out.println("directionToCoord - coord is null");
            return null;
        }
        if(c.y % 2 != 0)
        {
            /*
             *  odd columns
             */
            switch(direction)
            {
                case NE :
                    return Controller
                            .getHexPanel().getCoordFromList(c.x, c.y - 1);
                case E:
                    return Controller
                            .getHexPanel().getCoordFromList(c.x + 1, c.y);
                case SE:
                    return Controller
                            .getHexPanel().getCoordFromList(c.x, c.y + 1);
                case SW:
                    return Controller
                            .getHexPanel().getCoordFromList(c.x -1, c.y +1);
                case W:
                    return Controller
                            .getHexPanel().getCoordFromList(c.x - 1, c.y);
                case NW:
                    return Controller
                            .getHexPanel().getCoordFromList(c.x - 1, c.y - 1);
            }
        }
        else
        {
            /* 
             * even columns
             */
            switch(direction)
            {
                case NE :
                    return Controller
                            .getHexPanel().getCoordFromList(c.x + 1, c.y - 1);
                case E:
                    return Controller
                            .getHexPanel().getCoordFromList(c.x + 1, c.y);
                case SE:
                    return Controller
                            .getHexPanel().getCoordFromList(c.x + 1, c.y + 1);
                case SW:
                    return Controller
                            .getHexPanel().getCoordFromList(c.x, c.y + 1);
                case W:
                    return Controller
                            .getHexPanel().getCoordFromList(c.x -1, c.y);
                case NW:
                    return Controller
                            .getHexPanel().getCoordFromList(c.x, c.y -1);
            }
        }
        return null;
    }
}
