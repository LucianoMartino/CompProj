package obj;

import java.awt.image.*;
import game.*;

public class Bait extends Projectile{
	private static BufferedImage baitImg = Util.loading("/art/sBait.png");
	
	public Bait(Game g, double x, double y) {
		super(x, y);
		
		this.img = baitImg;
		this.x = x;
		this.y = y;
		this.width = baitImg.getWidth();
		this.height = baitImg.getHeight();

		vecToMouseX = (Game.getMouseX() + Game.camX) - g.player.getMidX();
		vecToMouseY = (Game.getMouseY() + Game.camY) - g.player.getMidY();

		double length = Math.sqrt(vecToMouseX * vecToMouseX + vecToMouseY * vecToMouseY);
		if(!(length < 0.01)){
			vecToMouseX /= length;
			vecToMouseY /= length;
		}
		
		this.rotation = Util.getAngle(vecToMouseX, vecToMouseY);

		this.midX = x + width/2;
		this.midY = y + height/2;
		
		this.bounds = new Collision((int)x, (int)y, width, height);
	}
	public static double getWidth(){
		return baitImg.getWidth();
	}
	public static double getHeight(){
		return baitImg.getHeight();
	}
}