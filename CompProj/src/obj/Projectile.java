package obj;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import game.*;

public class Projectile {
	protected BufferedImage img;
	protected int width, height;
	protected double x, y;
	protected double midX, midY;
	protected double rotation;
	protected double vecToMouseX, vecToMouseY;
	protected Collision bounds;
	private boolean shouldRemove = false;
	
	public Projectile(double x, double y){
		this.x = x; this.y = y;
		
		//this.vecToMouseX = vecToMouseX; this.vecToMouseY = vecToMouseY;
		this.rotation = Util.getAngle(vecToMouseX, vecToMouseY);
		//this.bounds = bounds;
		//this.img = img;
	
	}
	
	public boolean shouldRemove() {
		return shouldRemove;
	}
	
	public void tick() {
		boolean didCollide = doCollisions();

		if (didCollide) {
			shouldRemove = true;
		} else {
			x += vecToMouseX * 10;
			y += vecToMouseY * 10;
		}
		
		bounds.setBounds((int) x + 5, (int) y + 5, width - 5, height - 5);
		
	}
	
	public void render(Graphics2D g) {
		AffineTransform beforeRotation = g.getTransform();
		g.rotate(rotation, (int) x + width / 2, (int) y + height / 2);
		g.drawImage(img, (int) x, (int) y, null);
		g.setTransform(beforeRotation);
	}
	
	private boolean doCollisions() {
		if(x < 0 ||x > Game.MAP_WIDTH * Wall.WALL_SIZE || y < 0 || y > Game.MAP_HEIGHT * Wall.WALL_SIZE)
			return true;
		else {
			boolean didCollide = false;
			Wall[][] nearby = Game.getNearbyWalls(midX, midY);
			
			for (Wall[] walls : nearby) {
				for (Wall notWalls : walls) {
					if (notWalls == null)
						continue;
					Collision wallCollision = notWalls.getCollision();
	
					if (bounds.doesOverlap(wallCollision))
						didCollide = true;
	
				}
			}
			return didCollide;
		}
	}
}
