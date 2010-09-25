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
