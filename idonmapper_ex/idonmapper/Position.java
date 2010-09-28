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

import java.awt.*;
import java.awt.geom.*;

public class Position
{
    public double x, y;
    
    public Position(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Position(Point p)
    {
        this.x = p.getX();
        this.y = p.getY();
    }
    
    public Point2D.Double point()
    {
        return new Point2D.Double(x, y);
    }
}
