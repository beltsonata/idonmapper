package idonmapper;

/*
 * Directions used for easily associating
 * adjacent hexagons together.
 */
public enum Direction
{
    NE, E, SE, SW, W, NW;

    Direction()
    {
    }
    
    /**
     * Returns the opposite direction to @dir.
     */
    public static Direction getOpposite(Direction dir)
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
            default:
                break;
        }
        return null;
    }
    
    /**
     * Returns the next direction clockwise from
     * the current one.
     */ 
    public Direction turnRight()
    {
        switch(this)
        {
            case NE :
                return E;
            case E:
                return SE;
            case SE:
                return SW;
            case SW:
                return W;
            case W:
                return NW;
            case NW:
                return NE;
            default:
                break;
        }
        return null;
    }
    /**
     * Returns the next direction anti-clockwise from
     * the current one.
     */ 
    public Direction turnLeft()
    {
        switch(this)
        {
            case NE :
                return NW;
            case NW:
                return W;
            case W:
                return SW;
            case SW:
                return SE;
            case SE:
                return E;
            case E:
                return NE;
            default:
                break;
        }
        return null;
    }
    /**
     * Returns the first Coord on the hex panel away from 
     * another the specified direction. 
     * 
     * <p>
     * 
     * @return  The Coordinate found.
     * @param   c         The source coordinate.
     * @param   direction The direction in which to look.
     */
    public static Coord directionToCoord(Coord c, Direction direction)
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
