package idonmapper;

import java.awt.Graphics2D;

public interface HexPanelObject 
{
     void setPosition(double xPos, double yPos);
     double getXPos();
     double getYPos();
     void draw(Graphics2D g);
}
